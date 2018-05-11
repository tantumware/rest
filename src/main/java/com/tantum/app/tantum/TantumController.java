package com.tantum.app.tantum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.tantum.app.tantum.algoritmo.Algoritmo;
import com.tantum.app.tantum.helper.Helper;
import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Course;
import com.tantum.app.tantum.models.Estatisticas;
import com.tantum.app.tantum.models.History;
import com.tantum.app.tantum.models.LoginDTO;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Result;
import com.tantum.app.tantum.models.Semester;
import com.tantum.app.tantum.models.SemesterHistory;
import com.tantum.app.tantum.models.SemestersDTO;
import com.tantum.app.tantum.models.Subject;
import com.tantum.app.tantum.models.SubjectsDTO;

@Slf4j
@RequestMapping("/v1/")
@CrossOrigin
@RestController
@ControllerAdvice
public class TantumController {

	@RequestMapping(path = "test", method = RequestMethod.GET)
	public String test() {
		return "This is a Test, REST API is Working";
	}

	@RequestMapping(path = "login", method = RequestMethod.GET)
	public LoginDTO login(@RequestParam(value = "token", required = true) String token) {
		// return new LoginDTO(this.loginService.doAuthenticate(token), token);
		return new LoginDTO(true, token);
	}

	@RequestMapping(path = "calculate-semester", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SemestersDTO calculateSemester(@RequestParam(value = "token", required = true) String token,
			@RequestBody(required = true) Constraints constraints) {

		String c = Helper.course_test;
		String h = Helper.class_history_test;

		Gson g = new Gson();
		Course curso = g.fromJson(c, Course.class);
		History history = g.fromJson(h, History.class);
		SemesterHistory xx = history.getSemesters().stream().reduce((x, y) -> {
			x.getSubjects().addAll(y.getSubjects());
			return x;
		}).orElse(new SemesterHistory("", Arrays.asList()));
		Algoritmo a = new Algoritmo(curso);
		a.rankDisciplinas();
		NextSemestersDTO result = a.calculateSemesters(constraints, xx.getSubjects());

		log.info("calculate-semester");
		return new SemestersDTO(true, result);
	}

	@RequestMapping(path = "schedule/{semester}", method = RequestMethod.GET)
	public SubjectsDTO schedule(@RequestParam(value = "token", required = true) String token,
			@PathVariable String semester) {

		String c = Helper.course_test;
		String h = Helper.class_history_test;

		Gson g = new Gson();
		Course curso = g.fromJson(c, Course.class);
		History history = g.fromJson(h, History.class);

		List<Subject> subjects = new ArrayList<>();

		for (SemesterHistory sh : history.getSemesters()) {
			for (String code : sh.getSubjects()) {
				curso.getSubjects()
						.stream()
						.filter(s -> s.getCodigo().equals(code))
						.findFirst()
						.ifPresent(subjects::add);
			}
		}

		SubjectsDTO disciplinasDTO = new SubjectsDTO(true, new Semester(subjects));
		disciplinasDTO.setSemestre(semester);
		log.info("/schedule/" + semester);
		return disciplinasDTO;
	}

	@RequestMapping(path = "subjects", method = RequestMethod.GET) // all subjects
	public SubjectsDTO disciplinas() {
		String c = Helper.course_test;
		String h = Helper.class_history_test;

		Gson g = new Gson();
		Course curso = g.fromJson(c, Course.class);
		History history = g.fromJson(h, History.class);

		List<Subject> subjects = curso.getSubjects().stream().filter(s -> {
			for (SemesterHistory sh : history.getSemesters()) {
				if (sh.getSubjects().contains(s.getCodigo())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());

		SubjectsDTO disciplinasDTO = new SubjectsDTO(true, new Semester(subjects));
		log.info("subjects");
		return disciplinasDTO;
	}

	@RequestMapping(path = "statictics", method = RequestMethod.GET)
	public Estatisticas estatisticas(@RequestParam(value = "token", required = true) String token) {
		List<String> semesters = Arrays.asList("2015-1", "2015-2", "2016-1", "2016-2", "2017-1", "2017-2");
		List<Double> semestersIA = Arrays.asList(4.0, 6.0, 7.5, 5.0, 6.0, 7.0);
		List<Double> courseIA = Arrays.asList(6.0, 4.0, 6.5, 4.0, 5.0, 6.0);

		return new Estatisticas(2, semesters, semestersIA, courseIA);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Result<String> handleMissingToken(MissingServletRequestParameterException ex) {
		String name = ex.getParameterName();
		return new Result<String>(false, name + " parameter is missing") {
		};
	}

}
