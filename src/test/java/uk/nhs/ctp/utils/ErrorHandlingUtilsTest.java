package uk.nhs.ctp.utils;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Before;
import org.junit.Test;

import uk.nhs.ctp.exception.EMSException;

public class ErrorHandlingUtilsTest {
	
	Observation observationNotNull, observationNull, observationNotInstantiated;
	
	@Before
	public void setup() {
		observationNotNull = new Observation();
		observationNull = null;
	}

	@Test(expected = EMSException.class)
	public void testExceptionThrownWhenObjectIsNull() {
		ErrorHandlingUtils.checkEntityExists(observationNull, "Observation"); 
	}
	
	@Test(expected = EMSException.class)
	public void testExceptionThrownWhenObjectIsNotInstantiated() {
		ErrorHandlingUtils.checkEntityExists(observationNotInstantiated, "Observation"); 
	}
	
	@Test
	public void testNoExceptionThrownWhenObjectIsNotNull() {
		ErrorHandlingUtils.checkEntityExists(observationNotNull, "Observation"); 
	}

}
