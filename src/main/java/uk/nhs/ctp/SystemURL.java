package uk.nhs.ctp;

public final class SystemURL {

	/*
	 * Specification URLS for FHIR resources
	 */

	// Structure Definition Extensions
	public static final String SD_EXT_SCT_DESC_ID = "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-coding-sctdescid";

	// ValueSets
	public static final String VS_ERROR_WARNING_CODE = "https://fhir.nhs.uk/STU3/ValueSet/Spine-ErrorOrWarningCode-1";

	// Code System Constants
	public static final String SNOMED = "http://snomed.info/sct";
	public static final String DATA_DICTIONARY = "https://www.datadictionary.nhs.uk/";
	public static final String OPERATION_OUTCOME = "https://www.hl7.org/fhir/operationoutcome.html";
	public static final String SERVICE_DEFINITION_EVALUATE = "http://hl7.org/fhir/OperationDefinition/ServiceDefinition-evaluate";
	public static final String QUESTIONNAIRE = "https://www.hl7.org/fhir/questionnaire.html";

}
