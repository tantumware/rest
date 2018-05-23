package com.tantum.app.tantum.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Period {

	MORNING, AFTERNOON, NIGHT;

	// 3.0820-2 : d.hhmm-n
	public static Period getPeriodByTime(String time) {
		int hour = Integer.valueOf(time.substring(2, 4));

		if (hour <= 12) {
			return MORNING;
		}

		if (hour >= 18) {
			return NIGHT;
		}

		return AFTERNOON;
	}

	@JsonValue
	public int jsonValue() {
		return super.ordinal();
	}

}
