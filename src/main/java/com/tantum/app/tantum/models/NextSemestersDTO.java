package com.tantum.app.tantum.models;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NextSemestersDTO {

	private Map<String, Semester> nextSemesters;

	private List<Subject> subjectsWantedError;

	private List<Subject> subjectsNotWantedError;

	private List<Subject> subjectsNotSelected;

}
