package com.tantum.app.tantum.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubjectsDTO extends Result<Semester> {

	public SubjectsDTO(boolean success, Semester result) {
		super(success, result);
	}

	private String semestre;

	private Boolean morning;

	private Boolean afternoon;

	private Boolean night;

}
