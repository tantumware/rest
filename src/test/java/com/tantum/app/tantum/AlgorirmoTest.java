package com.tantum.app.tantum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tantum.app.tantum.models.Constraints;
import com.tantum.app.tantum.models.Periodo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlgorirmoTest {

	private static Constraints constraints = new Constraints();

	@BeforeClass
	public static void before() {
		constraints.setPeriods(Arrays.asList(Periodo.values()));
		constraints.setCreditMax(30);
		constraints.setCreditMin(0);
		constraints.setEquivalent(true);
		constraints.setSubjectsWanted(Arrays.asList());
		constraints.setSubjectsNotWanted(Arrays.asList());
	}

	@Test
	public void testConstraints() {
		assertNotNull(constraints);
		assertFalse(constraints.getPeriods().isEmpty());
	}

}
