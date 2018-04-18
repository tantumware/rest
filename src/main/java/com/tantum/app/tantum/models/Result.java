package com.tantum.app.tantum.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Result<R> {

	protected boolean success;

	protected R result;

}
