package uk.nhs.ctp.controllers;

import org.elasticsearch.indices.InvalidIndexNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.exception.ExceptionResponse;

@ControllerAdvice
public class EMSControllerAdvice extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EMSControllerAdvice.class);

	@ExceptionHandler(EMSException.class)
	protected ResponseEntity<ExceptionResponse> handleEMSException(EMSException exception) {
		return new ResponseEntity<>(exception.getResponseBody(), exception.getHttpStatus());
	}

	@ExceptionHandler(InvalidIndexNameException.class)
	protected ResponseEntity<ExceptionResponse> handleBadRequest(Throwable throwable) {
		return handleEMSException(new EMSException(HttpStatus.BAD_REQUEST, "Bad Request", throwable));
	}

	@ExceptionHandler(Throwable.class)
	protected ResponseEntity<ExceptionResponse> handleUnknownException(Throwable throwable) throws Throwable {
		LOG.error("Unknown Exception", throwable);
		return handleEMSException(
				new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", throwable));
	}
}