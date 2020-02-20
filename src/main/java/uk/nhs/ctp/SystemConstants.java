package uk.nhs.ctp;

import org.apache.commons.lang3.time.FastDateFormat;

public final class SystemConstants {

	/*
	 * Constants for building FHIR resources
	 */

	public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	public static final String DESCRIPTION_ID = "descriptionId";
	public static final String DESCRIPTION_DISPLAY = "descriptionDisplay";
	public static final String INPUT_DATA = "inputData";
	public static final String OUTPUT_DATA = "outputData";
	public static final String INPUT_PARAMETERS = "inputParameters";
	public static final String CONTEXT = "context";
	public static final String PATIENT = "patient";

	// initiatingPerson, userType, userLanguage, userTaskContext, receivingPerson,
	// recipientType, recipientLanguage, setting, encounter
	public static final String INITIATINGPERSON = "initiatingPerson";
	public static final String USERTYPE = "userType";
	public static final String USERLANGUAGE = "userLanguage";
	public static final String USERTASKCONTEXT = "userTaskContext";
	public static final String RECEIVINGPERSON = "receivingPerson";
	public static final String RECIPIENTTYPE = "recipientType";
	public static final String RECIPIENTLANGUAGE = "recipientLanguage";
	public static final String SETTING = "setting";
	public static final String ENCOUNTER = "encounter";

	public static final String REQUEST_ID = "requestId";
	public static final String APPLICATION_FHIR_JSON = "application/fhir+json; charset=utf-8";
	public static final String SERVICE_DEFINITION = "ServiceDefinition";
	public static final String EVALUATE = "$evaluate";
	public static final String QUESTIONNAIRE = "Questionnaire";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_NHS = "ROLE_NHS";
	public static final String ROLE_CDSS = "ROLE_CDSS";

	public static final String AUTH_TOKEN = "RGF2aWRXYXRlcnM6TkhTRGlnaXRhbFBPQw==";

}
