package uk.nhs.ctp;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SystemURL {

	// Structure Definition Extensions
	public final String SD_EXT_SCT_DESC_ID = "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-coding-sctdescid";
	public final String NHS_NUMBER = "https://fhir.nhs.uk/Id/nhs-number";

	// ValueSets
	public final String VS_ERROR_WARNING_CODE = "https://fhir.nhs.uk/STU3/ValueSet/Spine-ErrorOrWarningCode-1";

	// Code System Constants
	public final String SNOMED = "http://snomed.info/sct";
	public final String DATA_DICTIONARY = "https://www.datadictionary.nhs.uk/";
	public final String OPERATION_OUTCOME = "https://www.hl7.org/fhir/operationoutcome.html";
	public final String SERVICE_DEFINITION_EVALUATE = "http://hl7.org/fhir/OperationDefinition/ServiceDefinition-evaluate";
	public final String QUESTIONNAIRE = "https://www.hl7.org/fhir/questionnaire.html";
	public final String CS_PROVIDER_TAXONOMY = "http://hl7.org/fhir/valueset-provider-taxonomy.html";
	public final String CS_CDS_STUB = "cdss/supplier/stub";
	public final String CS_GENDER = "http://hl7.org/fhir/administrative-gender";

	public final String APPT_DOCUMENT_IDENTIFIER = "https://tools.ietf.org/html/rfc4122";
}
