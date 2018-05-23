package com.tantum.app.tantum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.tantum.app.tantum.algorithm.Algorithm;
import com.tantum.app.tantum.helper.Helper;
import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Course;
import com.tantum.app.tantum.models.History;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Period;
import com.tantum.app.tantum.models.SemesterHistory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlgorirmoTest {

	private static Constraints constraints = new Constraints();

	private static Algorithm alg;

	private static SemesterHistory semesterHistory;

	@BeforeClass
	public static void before() {
		constraints.setPeriods(Arrays.asList(Period.values()));
		constraints.setCreditMax(30);
		constraints.setCreditMin(0);
		constraints.setEquivalent(true);
		constraints.setSubjectsWanted(Arrays.asList());
		constraints.setSubjectsNotWanted(Arrays.asList());

		String c = Helper.course_test;
		String h = Helper.class_history_test;

		Gson g = new Gson();
		Course curso = g.fromJson(c, Course.class);
		History history = g.fromJson(h, History.class);

		semesterHistory = history.getSemesters().stream().reduce((x, y) -> {
			x.getSubjects().addAll(y.getSubjects());
			return x;
		}).orElse(new SemesterHistory("", Arrays.asList()));

		alg = new Algorithm(curso);
		alg.rankDisciplinas();
	}

	@Before
	public void beforeEach() {
		constraints.setPeriods(Arrays.asList(Period.values()));
		constraints.setCreditMax(30);
		constraints.setCreditMin(0);
		constraints.setEquivalent(true);
		constraints.setSubjectsWanted(Arrays.asList());
		constraints.setSubjectsNotWanted(Arrays.asList());
	}

	@Test
	public void testConstraints() {
		assertNotNull(constraints);
		assertNotNull(constraints.getPeriods());
		assertNotNull(constraints.getCreditMin());
		assertNotNull(constraints.getCreditMax());
		assertNotNull(constraints.getSubjectsNotWanted());
		assertNotNull(constraints.getSubjectsWanted());
		assertNotNull(constraints.isEquivalent());

		assertFalse(constraints.getPeriods().isEmpty());
		assertTrue(constraints.getCreditMax() > 0);
	}

	@Test
	public void testAllPeriods() {
		// given

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		assertFalse(result.getNextSemesters().isEmpty());

	}

	@Test
	public void testNoPeriods() {
		// given
		constraints.setPeriods(Arrays.asList());

	}

}
