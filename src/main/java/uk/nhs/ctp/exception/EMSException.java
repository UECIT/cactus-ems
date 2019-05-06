package uk.nhs.ctp.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class EMSException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;
	protected final List<String> errors = new ArrayList<>();

	public EMSException(HttpStatus httpStatus, String message, Throwable throwable) {
		super(message, throwable);

		if (throwable != null) populateErrors(throwable);
		this.httpStatus = httpStatus;
	}

	public EMSException(HttpStatus httpStatus, String message) {
		this(httpStatus, message, null);
	}

	public ExceptionResponse getResponseBody() {
		return new ExceptionResponse(getMessage(), errors);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	private void populateErrors(Throwable throwable) {
		if (throwable.getCause() != null) populateErrors(throwable.getCause());
		errors.add(throwable.getMessage());
	}

}
