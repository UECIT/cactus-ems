package uk.nhs.ctp.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import uk.nhs.ctp.exception.EMSException;

@UtilityClass
public class ErrorHandlingUtils {

	public void checkEntityExists(Object object, String objectDescription) {
		if (object == null) {
			throw new EMSException(HttpStatus.NOT_FOUND, objectDescription + " not found");
		}
	}
}
