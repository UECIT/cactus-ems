package uk.nhs.ctp.exception;

import java.util.List;

public class ExceptionResponse {

	private final String message;
	private final List<String> errors;

	public ExceptionResponse(String message, List<String> errors) {
		this.message = message;
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getErrors() {
		return errors;
	}
}
