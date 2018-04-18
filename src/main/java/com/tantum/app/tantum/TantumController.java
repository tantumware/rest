package com.tantum.app.tantum;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.tantum.app.tantum.algoritmo.Algoritmo;
import com.tantum.app.tantum.helper.Helper;
import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Curso;
import com.tantum.app.tantum.models.Estatisticas;
import com.tantum.app.tantum.models.History;
import com.tantum.app.tantum.models.Login;
import com.tantum.app.tantum.models.LoginDTO;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Semester;
import com.tantum.app.tantum.models.SemesterHistory;
import com.tantum.app.tantum.models.SemestersDTO;
import com.tantum.app.tantum.models.SubjectsDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/")
@CrossOrigin
@RestController
public class TantumController {

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public LoginDTO login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
		boolean userOk = StringUtils.isNotBlank(username);
		boolean passwordOk = StringUtils.isNotBlank(password);

		Login login = new Login(userOk, passwordOk);

		LoginDTO loginDto = new LoginDTO(userOk && passwordOk, login);

		log.info("Feito login com: " + username + "/" + password);
		return loginDto;
	}

	@RequestMapping(path = "/calculate-semester", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SemestersDTO calculateSemester(@RequestBody(required = true) Constraints constraints) {
		String c = Helper.readJson("course-test.json");
		String h = Helper.readJson("class-history-test.json");

		Gson g = new Gson();
		Curso curso = g.fromJson(c, Curso.class);
		History history = g.fromJson(h, History.class);
		SemesterHistory xx = history.getSemesters().stream().reduce((x, y) -> {
			x.getSubjects().addAll(y.getSubjects());
			return x;
		}).get();
		Algoritmo a = new Algoritmo(curso);
		a.rankDisciplinas();
		NextSemestersDTO result = a.calculateSemesters(constraints, xx.getSubjects());

		log.info("calculate-semester");
		return new SemestersDTO(true, result);
	}

	@RequestMapping(path = "/schedule/{semester}", method = RequestMethod.GET)
	public SubjectsDTO schedule(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @PathVariable String semester) {
		String c = Helper.readJson("course-test.json");

		Gson g = new Gson();
		Semester s = g.fromJson(c, Semester.class);

		SubjectsDTO disciplinasDTO = new SubjectsDTO(true, s);
		disciplinasDTO.setSemestre(semester);
		log.info("/schedule/" + semester);
		return disciplinasDTO;
	}

	@RequestMapping(path = "/subjects", method = RequestMethod.GET) // all subjects
	public SubjectsDTO disciplinas() {
		String c = Helper.readJson("course-test.json");

		Gson g = new Gson();
		Semester s = g.fromJson(c, Semester.class);

		SubjectsDTO disciplinasDTO = new SubjectsDTO(true, s);
		log.info("subjects");
		return disciplinasDTO;
	}

	@RequestMapping(path = "/statictics", method = RequestMethod.GET)
	public Estatisticas estatisticas(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
		List<String> semesters = Arrays.asList("2015-1", "2015-2", "2016-1", "2016-2", "2017-1", "2017-2");
		List<Double> semestersIA = Arrays.asList(4.0, 6.0, 7.5, 5.0, 6.0, 7.0);
		List<Double> courseIA = Arrays.asList(6.0, 4.0, 6.5, 4.0, 5.0, 6.0);

		Estatisticas e = new Estatisticas(2, semesters, semestersIA, courseIA);
		return e;
	}

	@RequestMapping(path = "/semestre-atual", method = RequestMethod.GET) // current semester
	public Semester semestreAtual(@RequestParam(value = "token") String token) {
		return new Semester();
	}

}
