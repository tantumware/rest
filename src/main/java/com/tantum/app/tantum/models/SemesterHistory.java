package com.tantum.app.tantum.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SemesterHistory {
	
	private String semester;
	
	private List<String> subjects;

}
