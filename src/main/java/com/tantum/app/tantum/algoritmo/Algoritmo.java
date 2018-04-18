package com.tantum.app.tantum.algoritmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.impl.FixedBoolVarImpl;

import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Curso;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Periodo;
import com.tantum.app.tantum.models.Semester;
import com.tantum.app.tantum.models.Subject;

import lombok.Getter;

@Getter
public class Algoritmo {

	private Map<String, Subject> disciplinas;

	private Map<String, Integer> rank;

	private Map<Integer, Map<String, List<String>>> curriculo;

	private Map<String, Semester> semesters = new HashMap<>();

	private List<Subject> subjectsWantedError;

	public Algoritmo(Curso curso) {
		this.disciplinas = curso.getDisciplinas().stream()
				.collect(Collectors.toMap(Subject::getCodigo, Function.identity()));
		// recebe a lista de disciplinas do curso e monta do map
		this.curriculo = new HashMap<>();

		curso.getDisciplinas().stream().forEach(d -> {
			if (!this.curriculo.containsKey(d.getFase())) {
				this.curriculo.put(d.getFase(), new HashMap<>());
			}
			this.curriculo.get(d.getFase()).put(d.getCodigo(), d.getRequisitos());
		});
	}

	public void rankDisciplinas() {
		this.rank = new HashMap<>();

		int qtFases = this.curriculo.size();
		// para cada materia da fase, começando pela ultima fase
		for (int i = qtFases; i >= 1; i--) {
			for (String materia : this.curriculo.get(i).keySet()) {
				// controle da disciplina
				if (this.rank.containsKey(materia)) {
					// se ja existe a disciplina, soma 1
					Integer aux = this.rank.get(materia);
					aux++;
					this.rank.replace(materia, aux);
				} else {
					// senao adiciona com valor 1
					this.rank.put(materia, 1);
				}

				// controle dos requisitos
				// para cada requisito da materia
				for (String requisito : this.curriculo.get(i).get(materia)) {
					if (this.rank.containsKey(requisito)) {
						// se ja existe o requisito, soma 1
						Integer aux = this.rank.get(requisito);
						aux++;
						this.rank.replace(requisito, aux);
					} else {
						// senao passa o valor da materia
						this.rank.put(requisito, this.rank.get(materia));
					}
				}
			}
		}
	}

	private List<String> getDisciplinasByRank() {
		return this.rank.entrySet()
				.stream()
				.sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}

	private Solver applyConstraints(Constraints constraints, List<String> subjectsHistory, Subject subject, List<Subject> currentSubjects) {
		Model model = new Model();
		Solver solver = model.getSolver();

		model.addClauseTrue(model.boolVar("carga horaria maxima", validateClassHourLoad(constraints, currentSubjects, subject)));
		model.addClauseTrue(model.boolVar("horarios", validateHorario(currentSubjects, subject)));
		model.addClauseTrue(model.boolVar("periodo", validadePeriodo(constraints, subject)));
		// TODO fazer essa validação e as que ele quer mas removendo elas depois do
		// primeiro semestre
		model.addClauseTrue(model.boolVar("disciplinas wanted", validateSubjectsWanted(constraints, subject)));
		model.addClauseFalse(model.boolVar("disciplias excluidas", validateDisciplinaExcluida(constraints, subject)));
		model.addClauseTrue(model.boolVar("requisitos", validateRequisitos(subject, subjectsHistory)));

		return solver;
	}

	public NextSemestersDTO calculateSemesters(Constraints constraints, List<String> _subjectsHistory) {
		List<String> rankDisciplinas = getDisciplinasByRank();
		rankDisciplinas.removeAll(_subjectsHistory);
		List<String> subjectsHistory = new ArrayList<>(_subjectsHistory);
		List<Subject> subjects = null;

		int i = 0;
		while (!rankDisciplinas.isEmpty()) {
			i++;
			if (this.semesters.isEmpty()) {
				subjects = calculateFirstSemester(constraints, subjectsHistory, rankDisciplinas);
				
				// clear subjects wanted and not wanted cuz its only for the next semester
				constraints.setSubjectsWanted(Arrays.asList());
				constraints.setSubjectsNotWanted(Arrays.asList());
			} else {
				subjects = calculateSemester(constraints, subjectsHistory, new ArrayList<>(), rankDisciplinas);
			}

			if (subjects.isEmpty()) {
				break;
			}

			this.semesters.put(getSemesterYear(i), new Semester(subjects));

			subjects.stream().map(Subject::getCodigo).forEach(codigo -> {
				rankDisciplinas.remove(codigo);
				subjectsHistory.add(codigo);
			});
		}
		return new NextSemestersDTO(this.semesters, this.subjectsWantedError, new ArrayList<>(), rankDisciplinas.stream().map(this.disciplinas::get).collect(Collectors.toList()));
	}

	private List<Subject> calculateFirstSemester(Constraints constraints, List<String> subjectsHistory, List<String> _subjectsRemaining) {
		this.subjectsWantedError = new ArrayList<>();
		List<Subject> currentSubjects = new ArrayList<>();
		List<String> subjectsRemaining = new ArrayList<>(_subjectsRemaining);

		for (String s : constraints.getSubjectsWanted()) {
			Subject subject = this.disciplinas.get(s);
			Solver solver = applyConstraints(constraints, subjectsHistory, subject, currentSubjects);
			boolean solve = solver.solve();
			if (solve) {
				currentSubjects.add(subject);
			} else {
				this.subjectsWantedError.add(subject);
			}
		}

		constraints.getSubjectsNotWanted().forEach(subjectsRemaining::remove);
		currentSubjects.stream().map(Subject::getCodigo).forEach(subjectsRemaining::remove);

		return calculateSemester(constraints, subjectsHistory, currentSubjects, subjectsRemaining);
	}

	private List<Subject> calculateSemester(Constraints constraints, List<String> subjectsHistory, List<Subject> _currentSubjects, List<String> subjectsRemaining) {
		List<Subject> currentSubjects = new ArrayList<>(_currentSubjects);

		for (String d : subjectsRemaining) {
			Subject disciplina = this.disciplinas.get(d);
			Solver solver = applyConstraints(constraints, subjectsHistory, disciplina, currentSubjects);
			boolean solve = solver.solve();
			if (solve) {
				currentSubjects.add(disciplina);
			} else {
				if (!checkCargaHorariaOk(solver)) {
					break;
				}
			}
		}

		return currentSubjects;
	}

	// TODO arrumar essa coisa aqui
	private String getSemesterYear(int index) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);

		int n = Math.round(index / 2);

		year = year + n;
		if (month >= 7) {
			return String.valueOf(year) + "-2";
		}

		return String.valueOf(year) + "-1";
	}

	private boolean validateRequisitos(Subject disciplina, List<String> subjects) {
		return subjects.containsAll(disciplina.getRequisitos());
	}

	/**
	 * Verifica se a constraint da carga horária está ok
	 *
	 * @param solver
	 * @return boolean
	 */
	private boolean checkCargaHorariaOk(Solver solver) {
		return Stream.of(solver.getModel().getVars())
				.filter(v -> v.getName().equals("carga horaria maxima"))
				.map(v -> ((FixedBoolVarImpl) v).getValue() == 1 ? true : false)
				.findAny()
				.orElse(false);
	}

	/**
	 * Valida as disciplinas que não devem ser pegas
	 *
	 * @param settings
	 * @param disciplina
	 * @return boolean
	 */
	private boolean validateDisciplinaExcluida(Constraints settings, Subject subject) {
		return settings.getSubjectsNotWanted().contains(subject.getCodigo());
	}
	

	private boolean validateSubjectsWanted(Constraints constraints, Subject subject) {
		return constraints.getSubjectsWanted().contains(subject.getCodigo());
	}
	
	private boolean validateClassHourLoad(Constraints constraints, List<Subject> currentSubjects, Subject subject) {
		int cargaHoraria = currentSubjects.stream().mapToInt(Subject::getAulas).sum() + subject.getAulas();
		
		return cargaHoraria <= constraints.getCreditMax();		
	}

	/**
	 * Valida o períoo escolhido pelo aluno (matutino, vespertivo e noturno)
	 *
	 * @param settings
	 * @param disciplina
	 * @return boolean
	 */
	private boolean validadePeriodo(Constraints settings, Subject disciplina) {
		return disciplina.getHorarios()
				.stream()
				.map(Periodo::getPeriodoByHorario)
				.allMatch(settings.getPeriods()::contains);
	}

	/**
	 * Valida o horário das disciplinas para nao haver choque de horários
	 *
	 * @param semestre
	 * @param currentDisciplina
	 * @return boolean
	 */
	private boolean validateHorario(List<Subject> currentSubjects, Subject currentDisciplina) {
		for (Subject s : currentSubjects) {
			for (String time : s.getHorarios()) {
				if (currentDisciplina.getHorarios().contains(time)) {
					return false;
				}
			}
		}
		return true;
	}
}
