package com.tantum.app.tantum.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estatisticas {

	private int semestresRestantes;

	private List<String> semesters;

	private List<Double> semestersIA;

	private List<Double> courseIA;

}
