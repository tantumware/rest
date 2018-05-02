package com.tantum.app.tantum.algoritmo;

import static com.tantum.app.tantum.algoritmo.ConstraintsNames.credit_max;
import static com.tantum.app.tantum.algoritmo.ConstraintsNames.period;
import static com.tantum.app.tantum.algoritmo.ConstraintsNames.requisites;
import static com.tantum.app.tantum.algoritmo.ConstraintsNames.subjects_not_wanted;
import static com.tantum.app.tantum.algoritmo.ConstraintsNames.subjects_wanted;
import static com.tantum.app.tantum.algoritmo.ConstraintsNames.times;

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

import lombok.Getter;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.impl.FixedBoolVarImpl;

import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Course;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Periodo;
import com.tantum.app.tantum.models.Semester;
import com.tantum.app.tantum.models.Subject;

@Getter
public class Algoritmo {

	private Map<String, Subject> subjects;

	private Map<String, Integer> rank;

	private Map<Integer, Map<String, List<String>>> course;

	private Map<String, Semester> semesters = new HashMap<>();

	private List<Subject> subjectsWantedError;

	public Algoritmo(Course curso) {
		this.subjects = curso.getSubjects().stream()
				.collect(Collectors.toMap(Subject::getCodigo, Function.identity()));
		// recebe a lista de disciplinas do curso e monta do map
		this.course = new HashMap<>();

		curso.getSubjects().stream().forEach(d -> {
			if (!this.course.containsKey(d.getFase())) {
				this.course.put(d.getFase(), new HashMap<>());
			}
			this.course.get(d.getFase()).put(d.getCodigo(), d.getRequisitos());
		});
	}

	public void rankDisciplinas() {
		this.rank = new HashMap<>();

		int qtFases = this.course.size();
		// para cada materia da fase, começando pela ultima fase
		for (int i = qtFases; i >= 1; i--) {
			for (String materia : this.course.get(i).keySet()) {
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
				for (String requisito : this.course.get(i).get(materia)) {
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

	private List<String> getSubjectsByRank() {
		return this.rank.entrySet()
				.stream()
				.sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}

	private Solver applyConstraints(Constraints constraints, List<String> subjectsHistory, Subject subject, List<Subject> currentSubjects) {
		Model model = new Model();
		Solver solver = model.getSolver();

		model.addClauseTrue(model.boolVar(credit_max, this.validateClassHourLoad(constraints, currentSubjects, subject)));
		model.addClauseTrue(model.boolVar(times, this.validateTime(currentSubjects, subject)));
		model.addClauseTrue(model.boolVar(period, this.validadePeriod(constraints, subject)));
		model.addClauseTrue(model.boolVar(subjects_wanted, this.validateSubjectsWanted(constraints, subject)));
		model.addClauseFalse(model.boolVar(subjects_not_wanted, this.validateSubjectsNotWanted(constraints, subject)));
		model.addClauseTrue(model.boolVar(requisites, this.validateRequisites(subject, subjectsHistory)));

		return solver;
	}

	public NextSemestersDTO calculateSemesters(Constraints constraints, List<String> subjectsHistory) {
		List<String> rankDisciplinas = this.getSubjectsByRank();
		rankDisciplinas.removeAll(subjectsHistory);
		List<String> newSubjectsHistory = new ArrayList<>(subjectsHistory);
		List<Subject> subjectsSemester = null;

		int i = 0;
		while (!rankDisciplinas.isEmpty()) {
			i++;
			if (this.semesters.isEmpty()) {
				subjectsSemester = this.calculateFirstSemester(constraints, newSubjectsHistory, rankDisciplinas);

				// clear subjects wanted and not wanted cuz its only for the next semester
				constraints.setSubjectsWanted(Arrays.asList());
				constraints.setSubjectsNotWanted(Arrays.asList());
			} else {
				subjectsSemester = this.calculateSemester(constraints, newSubjectsHistory, new ArrayList<>(), rankDisciplinas);
			}

			if (subjectsSemester.isEmpty()) {
				break;
			}

			this.semesters.put(this.getSemesterYear(i), new Semester(subjectsSemester));

			subjectsSemester.stream().map(Subject::getCodigo).forEach(codigo -> {
				rankDisciplinas.remove(codigo);
				newSubjectsHistory.add(codigo);
			});
		}
		return new NextSemestersDTO(this.semesters, this.subjectsWantedError, new ArrayList<>(), rankDisciplinas.stream().map(this.subjects::get).collect(Collectors.toList()));
	}

	private List<Subject> calculateFirstSemester(Constraints constraints, List<String> subjectsHistory, List<String> subjectsRemaining) {
		this.subjectsWantedError = new ArrayList<>();
		List<Subject> currentSubjects = new ArrayList<>();
		List<String> newSubjectsRemaining = new ArrayList<>(subjectsRemaining);

		for (String s : constraints.getSubjectsWanted()) {
			Subject subject = this.subjects.get(s);
			Solver solver = this.applyConstraints(constraints, subjectsHistory, subject, currentSubjects);
			boolean solve = solver.solve();
			if (solve) {
				currentSubjects.add(subject);
			} else {
				this.subjectsWantedError.add(subject);
			}
		}

		constraints.getSubjectsNotWanted().forEach(newSubjectsRemaining::remove);
		currentSubjects.stream().map(Subject::getCodigo).forEach(newSubjectsRemaining::remove);

		return this.calculateSemester(constraints, subjectsHistory, currentSubjects, newSubjectsRemaining);
	}

	private List<Subject> calculateSemester(Constraints constraints, List<String> subjectsHistory, List<Subject> currentSubjects, List<String> subjectsRemaining) {
		List<Subject> newCurrentSubjects = new ArrayList<>(currentSubjects);

		for (String d : subjectsRemaining) {
			Subject disciplina = this.subjects.get(d);
			Solver solver = this.applyConstraints(constraints, subjectsHistory, disciplina, newCurrentSubjects);
			boolean solve = solver.solve();
			if (solve) {
				newCurrentSubjects.add(disciplina);
			} else {
				if (!this.checkCargaHorariaOk(solver)) {
					break;
				}
			}
		}

		return newCurrentSubjects;
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

	private boolean validateRequisites(Subject disciplina, List<String> subjects) {
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
				.filter(v -> v.getName().equals(credit_max))
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
	private boolean validateSubjectsNotWanted(Constraints settings, Subject subject) {
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
	private boolean validadePeriod(Constraints settings, Subject disciplina) {
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
	private boolean validateTime(List<Subject> currentSubjects, Subject currentDisciplina) {
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
