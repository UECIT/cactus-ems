package uk.nhs.ctp.service.report.decorator.info;

import java.util.List;

import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.org.hl7.v3.CD;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02ClinicalDiscriminator;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation10;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation10.SeperatableInd;

@Component
public class PertinentInformation9ClinicalDiscriminatorDocumentDecorator implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		List<REPCMT200001GB02PertinentInformation10> clinicalDiscriminators = document.getPertinentInformation9();
		// TODO Populate data.
		REPCMT200001GB02PertinentInformation10 clinicalDiscriminator = new REPCMT200001GB02PertinentInformation10();
		
		clinicalDiscriminator.setTypeCode(clinicalDiscriminator.getTypeCode());
		
		SeperatableInd clinicalDiscriminatorSeperatableInd = new SeperatableInd();
		clinicalDiscriminatorSeperatableInd.setValue(false);
		clinicalDiscriminator.setSeperatableInd(clinicalDiscriminatorSeperatableInd);
		
		clinicalDiscriminator.setPertinentClinicalDiscriminator(createPertinentClinicalDiscriminator(request));
		
		clinicalDiscriminators.add(clinicalDiscriminator);
		
	}

	private REPCMT200001GB02ClinicalDiscriminator createPertinentClinicalDiscriminator(ReportRequestDTO request) {
		REPCMT200001GB02ClinicalDiscriminator pertinentClinicalDiscriminator = new REPCMT200001GB02ClinicalDiscriminator();
		pertinentClinicalDiscriminator.setClassCode(pertinentClinicalDiscriminator.getClassCode());
		pertinentClinicalDiscriminator.setMoodCode(pertinentClinicalDiscriminator.getMoodCode());
		
		CD codedData = new CD();
		codedData.setCode("CD");
		codedData.setDisplayName("Clinical Discriminator");
		codedData.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.540");
		pertinentClinicalDiscriminator.setCode(codedData);

		pertinentClinicalDiscriminator.setValue(getReasonReferenceCode(request));
		
		return pertinentClinicalDiscriminator;
	}

	private CV getReasonReferenceCode(ReportRequestDTO request) {
		CV codedValue = new CV();
		
		try {
			CareConnectObservation observation = (CareConnectObservation) request.getReferralRequest().getReasonReferenceFirstRep().getResource();
			codedValue.setCode(observation.getCode().getCodingFirstRep().getCode());
			codedValue.setDisplayName(observation.getCode().getCodingFirstRep().getDisplay());
			codedValue.setCodeSystem("2.16.840.1.113883.6.96");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return codedValue;
	}

}
