package com.tantum.app.tantum;

import static com.tantum.app.tantum.models.Period.AFTERNOON;
import static com.tantum.app.tantum.models.Period.MORNING;
import static com.tantum.app.tantum.models.Period.NIGHT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.Gson;
import com.tantum.app.tantum.algorithm.Algorithm;
import com.tantum.app.tantum.helper.Helper;
import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Course;
import com.tantum.app.tantum.models.History;
import com.tantum.app.tantum.models.NextSemestersDTO;
import com.tantum.app.tantum.models.Period;
import com.tantum.app.tantum.models.Semester;
import com.tantum.app.tantum.models.SemesterHistory;
import com.tantum.app.tantum.models.Subject;

@RunWith(JUnit4.class)
public class AlgorirmoTest {

	private static Constraints constraints = new Constraints();

	private static Algorithm alg;

	private static SemesterHistory semesterHistory;

	private static Course curso;

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
		curso = g.fromJson(c, Course.class);
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

		alg = new Algorithm(curso);
		alg.rankDisciplinas();
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
		constraints.setPeriods(Arrays.asList(Period.values()));

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertFalse(result.getNextSemesters().isEmpty());
		assertTrue(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
	}

	@Test
	public void testNoPeriods() {
		// given
		constraints.setPeriods(Arrays.asList());

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertTrue(result.getNextSemesters().isEmpty());
		assertFalse(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
	}

	@Test
	public void testMorningPeriod() {
		// given
		constraints.setPeriods(Arrays.asList(MORNING));

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertFalse(result.getNextSemesters().isEmpty());
		assertFalse(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
		for (Semester s : result.getNextSemesters().values()) {
			for (Subject subject : s.getSubjects()) {
				boolean b = subject.getHorarios().stream().map(Period::getPeriodByTime).allMatch(MORNING::equals);
				assertTrue(b);
			}
		}
	}

	@Test
	public void testMorningAfternoon() {
		// given
		constraints.setPeriods(Arrays.asList(AFTERNOON));

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertFalse(result.getNextSemesters().isEmpty());
		assertFalse(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
		for (Semester s : result.getNextSemesters().values()) {
			for (Subject subject : s.getSubjects()) {
				boolean b = subject.getHorarios().stream().map(Period::getPeriodByTime).allMatch(AFTERNOON::equals);
				assertTrue(b);
			}
		}
	}

	@Test
	public void testMorningNight() {
		// given
		constraints.setPeriods(Arrays.asList(NIGHT));

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertTrue(result.getNextSemesters().isEmpty());
		assertFalse(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
	}

	@Test
	public void testCreditMax() {
		// given
		constraints.setCreditMax(2);

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertFalse(result.getNextSemesters().isEmpty());
		assertTrue(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
		for (Semester s : result.getNextSemesters().values()) {
			int totalCredits = s.getSubjects().stream().mapToInt(Subject::getAulas).sum();
			assertTrue(constraints.getCreditMax() >= totalCredits);
		}
	}

	@Test
	public void testCreditMax0() {
		// given
		constraints.setCreditMax(0);

		// when
		NextSemestersDTO result = alg.calculateSemesters(constraints, semesterHistory.getSubjects());

		// then
		assertTrue(result.getNextSemesters().isEmpty());
		assertFalse(result.getSubjectsNotSelected().isEmpty());
		assertTrue(result.getSubjectsNotWantedError().isEmpty());
		assertTrue(result.getSubjectsWantedError().isEmpty());
	}

}
