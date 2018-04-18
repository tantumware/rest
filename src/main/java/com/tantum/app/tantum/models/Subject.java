package com.tantum.app.tantum.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Subject {

	private String nome;

	private String codigo;

	private int fase;

	private int aulas;

	private boolean obrigatoria;

	private List<String> horarios;

	private List<String> requisitos;

}
