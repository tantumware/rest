package com.tantum.app.tantum.models;

public class LoginDTO extends Result<Login> {

	public LoginDTO(boolean success, Login login) {
		super(success, login);
	}

}
