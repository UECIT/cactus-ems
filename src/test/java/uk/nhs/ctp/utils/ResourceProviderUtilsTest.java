package uk.nhs.ctp.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class ResourceProviderUtilsTest {
	
	List<ParametersParameterComponent> parameterSetOnce, parameterSetTwice, parameterDoesNotExist;
	ParametersParameterComponent testParameterOne, testParameterTwoA, testParameterTwoB;
	
	@Before
	public void setup() {
		parameterSetOnce = new ArrayList<>();
		parameterSetTwice = new ArrayList<>();
		parameterDoesNotExist = new ArrayList<>();
		
		testParameterOne = new ParametersParameterComponent(new StringType("test1"));
		testParameterOne.setValue(new StringType("Test Parameter 1"));
		
		testParameterTwoA = new ParametersParameterComponent(new StringType("test2"));
		testParameterTwoA.setValue(new StringType("Test Parameter 2A"));
		
		testParameterTwoB = new ParametersParameterComponent(new StringType("test2"));
		testParameterTwoB.setValue(new StringType("Test Parameter 2B"));
		
		parameterSetOnce.add(testParameterOne);
		
		parameterSetTwice.add(testParameterOne);
		parameterSetTwice.add(testParameterTwoA);
		parameterSetTwice.add(testParameterTwoB);
		
		parameterDoesNotExist.add(testParameterTwoA);
		parameterDoesNotExist.add(testParameterTwoB);
	}

	@Test
	public void testGetParameterByNameReturnsParameterWhenParameterSetOnce() {
		assertTrue(parameterSetOnce.size() == 1);
		assertTrue(parameterSetOnce.contains(testParameterOne));
		
		assertNotNull(ResourceProviderUtils.getParameterByName(parameterSetOnce, "test1"));
	}
	
	@Test(expected = BaseServerResponseException.class)
	public void testGetParameterByNameThrowsExceptionWhenParameterSetMoreThanOnce() {
		assertTrue(parameterSetTwice.size() == 3);
		assertTrue(parameterSetTwice.contains(testParameterTwoA));
		assertTrue(parameterSetTwice.contains(testParameterTwoB));
		
		ResourceProviderUtils.getParameterByName(parameterSetTwice, "test2");

	}
	
	@Test
	public void testGetParametersByNameReturnsParametersWhenParameterSetMoreThanOnce() {
		assertTrue(parameterSetTwice.size() == 3);
		assertTrue(parameterSetTwice.contains(testParameterTwoA));
		assertTrue(parameterSetTwice.contains(testParameterTwoB));
		
		assertTrue(ResourceProviderUtils.getParametersByName(parameterSetTwice, "test2").size() == 2);
	}
	
	@Test
	public void testGetParameterByNameReturnsNullIfParameterDoesNotExist() {
		assertTrue(parameterDoesNotExist.size() == 2);
		assertFalse(parameterDoesNotExist.contains(testParameterOne));

		assertNull(ResourceProviderUtils.getParameterByName(parameterDoesNotExist, "test1"));
	}
	
	@Test
	public void testGetParametersByNameReturnsEmptyListIfParameterDoesNotExist() {
		assertTrue(parameterDoesNotExist.size() == 2);
		assertFalse(parameterDoesNotExist.contains(testParameterOne));
		
		assertTrue(ResourceProviderUtils.getParametersByName(parameterDoesNotExist, "test1").isEmpty());
	}
	

}
