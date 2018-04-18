package com.tantum.app.tantum.models;

public enum Turno {

	MATUTINO, VESPERTINO, NOTURNO;

	// d.hh:mm-c
	public static Turno getTurnoByHorario(String horario) {
		Integer hora = Integer.parseInt(horario.substring(2, 4));
		if (hora < 12) {
			return MATUTINO;
		}

		if (hora > 18) {
			return NOTURNO;
		}

		return VESPERTINO;
	}
}
