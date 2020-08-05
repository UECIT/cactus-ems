package uk.nhs.ctp.service.fhir;

import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.RequestGroup;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.SettingsDTO;

public interface ResponseResolver {

	CdssResult resolve(GuidanceResponse response, CdssSupplier cdssSupplier, SettingsDTO settings, String patientId);

	// TODO: These should be refactored into separate components
	RequestGroup getResult(GuidanceResponse guidanceResponse);
	String getQuestionnaireId(GuidanceResponse guidanceResponse);
	
}
