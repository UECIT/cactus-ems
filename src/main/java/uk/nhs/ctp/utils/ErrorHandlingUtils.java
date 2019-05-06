package uk.nhs.ctp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.exception.EMSException;

@Component
public class ErrorHandlingUtils {

	public static void checkEntityExists(Object object, String objectDescription) {
		if (object == null) {
			throw new EMSException(HttpStatus.NOT_FOUND, objectDescription + "not found");
		}
	}
}
