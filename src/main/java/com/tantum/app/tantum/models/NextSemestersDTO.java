package com.tantum.app.tantum.models;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NextSemestersDTO {

	private LinkedHashMap<String, Semester> nextSemesters;

	private List<Subject> subjectsWantedError;

	private List<Subject> subjectsNotWantedError;

	private List<Subject> subjectsNotSelected;

}
