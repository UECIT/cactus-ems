package uk.nhs.ctp.utils;

import static org.junit.Assert.assertEquals;
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
		assertEquals(1, parameterSetOnce.size());
		assertTrue(parameterSetOnce.contains(testParameterOne));
		
		assertNotNull(ResourceProviderUtils.getParameterByName(parameterSetOnce, "test1"));
	}
	
	@Test(expected = BaseServerResponseException.class)
	public void testGetParameterByNameThrowsExceptionWhenParameterSetMoreThanOnce() {
		assertEquals(3, parameterSetTwice.size());
		assertTrue(parameterSetTwice.contains(testParameterTwoA));
		assertTrue(parameterSetTwice.contains(testParameterTwoB));
		
		ResourceProviderUtils.getParameterByName(parameterSetTwice, "test2");

	}
	
	@Test
	public void testGetParametersByNameReturnsParametersWhenParameterSetMoreThanOnce() {
		assertEquals(3, parameterSetTwice.size());
		assertTrue(parameterSetTwice.contains(testParameterTwoA));
		assertTrue(parameterSetTwice.contains(testParameterTwoB));

		assertEquals(2, ResourceProviderUtils.getParametersByName(parameterSetTwice, "test2").size());
	}
	
	@Test
	public void testGetParameterByNameReturnsNullIfParameterDoesNotExist() {
		assertEquals(2, parameterDoesNotExist.size());
		assertFalse(parameterDoesNotExist.contains(testParameterOne));

		assertNull(ResourceProviderUtils.getParameterByName(parameterDoesNotExist, "test1"));
	}
	
	@Test
	public void testGetParametersByNameReturnsEmptyListIfParameterDoesNotExist() {
		assertEquals(2, parameterDoesNotExist.size());
		assertFalse(parameterDoesNotExist.contains(testParameterOne));
		
		assertTrue(ResourceProviderUtils.getParametersByName(parameterDoesNotExist, "test1").isEmpty());
	}
	

}
