package com.tantum.app.tantum;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class TantumErrorController implements ErrorController {

	// https://stackoverflow.com/questions/31134333/this-application-has-no-explicit-mapping-for-error

	private static final String ERROR_PATH = "/error";

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

}
