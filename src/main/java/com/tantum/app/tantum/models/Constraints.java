package com.tantum.app.tantum.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Constraints {

	private List<Periodo> periods;

	private int creditMax;

	private int creditMin;

	private boolean equivalent;

	private List<String> subjectsWanted;

	private List<String> subjectsNotWanted;

}
