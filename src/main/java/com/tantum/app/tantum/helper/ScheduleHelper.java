package com.tantum.app.tantum.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleHelper {

	private static List<String> schedule = new ArrayList<>();

	static {
		schedule.addAll(Arrays.asList("0730", "0820", "0910", "1010", "1100", "1330", "1420", "1510", "1620", "1710", "1830", "1920", "2020", "2110"));
	}

	/**
	 * Recebe um horario no formato d.hhmm-n
	 *
	 * onde n é a quantidade de aulas
	 *
	 * @param horario
	 * @return
	 */
	public static List<String> getNexts(String classTime) {
		int index = schedule.indexOf(getUnformatedTime(classTime));
		int qt = Integer.valueOf(classTime.substring(7));

		return schedule.subList(index, index + qt);
	}

	public static String getUnformatedTime(String classTime) {
		return classTime.substring(2, 6);
	}

	public static String getFormatedTime(String classTime) {
		String unformatedTime = getUnformatedTime(classTime);

		StringBuilder sb = new StringBuilder();

		sb.append(unformatedTime.substring(0, 2));
		sb.append(":");
		sb.append(unformatedTime.substring(2, 4));

		return sb.toString();
	}

}
