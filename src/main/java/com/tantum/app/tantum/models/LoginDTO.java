package com.tantum.app.tantum.models;

public class LoginDTO extends Result<String> {

	public LoginDTO(boolean success, String token) {
		super(success, token);
	}

}
