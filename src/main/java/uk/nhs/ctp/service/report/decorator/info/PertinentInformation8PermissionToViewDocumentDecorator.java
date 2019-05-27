package uk.nhs.ctp.service.report.decorator.info;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Consent;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146050GB01PermissionToView;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146050GB01PermissionToView.Code;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146050GB01PermissionToView.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.QTY;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation9;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation9.SeperatableInd;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class PertinentInformation8PermissionToViewDocumentDecorator implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation9 permissionToView = new REPCMT200001GB02PertinentInformation9();
		
		Consent fhirConsent = ResourceProviderUtils.getResource(request.getReferralRequest().getContained(), Consent.class);
		
		permissionToView.setTypeCode(permissionToView.getTypeCode());
		
		SeperatableInd permissionToViewSeperatableInd = new SeperatableInd();
		permissionToViewSeperatableInd.setValue(false);
		permissionToView.setSeperatableInd(permissionToViewSeperatableInd);
		
		TemplateContent permissionToViewTemplateContent = new TemplateContent();
		permissionToViewTemplateContent.setNullFlavor(CsNullFlavor.NA);
		permissionToView.setContentId(permissionToViewTemplateContent);
		
		permissionToView.setCOCDTP146050GB01PermissionToView(createPermissionToViewContent(fhirConsent));
		
		document.setPertinentInformation8(new JAXBElement<REPCMT200001GB02PertinentInformation9>(new QName("pertinentInformation8"), REPCMT200001GB02PertinentInformation9.class, permissionToView));
	}

	private COCDTP146050GB01PermissionToView createPermissionToViewContent(Consent fhirConsent) {
		COCDTP146050GB01PermissionToView permissionToViewContent = new COCDTP146050GB01PermissionToView();
		permissionToViewContent.setClassCode(permissionToViewContent.getClassCode());
		permissionToViewContent.setMoodCode(permissionToViewContent.getMoodCode());
		
		Code code = new Code();
		code.setCode("PTV");
		code.setDisplayName("Permission To View");
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.220");
		permissionToViewContent.setCode(code);
		
		permissionToViewContent.setEffectiveTime(createEffectiveTime(fhirConsent));
		
		TemplateId permissionToViewContentTemplateId = new TemplateId();
		permissionToViewContentTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		permissionToViewContentTemplateId.setExtension("COCD_TP146050GB01#PermissionToView");
		permissionToViewContent.setTemplateId(permissionToViewContentTemplateId);
		
		CV codedValue = new CV();
		codedValue.setCode("01");
		codedValue.setDisplayName("Permission to view obtained");
		codedValue.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.524");
		permissionToViewContent.setValue(codedValue);
		
		return permissionToViewContent;
	}

	private IVLTS createEffectiveTime(Consent fhirConsent) {
		IVLTS effectiveTime = new IVLTS();
		TS low = new TS();
		low.setValue(fhirConsent.getPeriod().getStart().toString());
		effectiveTime.getRest().add(new JAXBElement<QTY>(new QName("urn:hl7-org:v3", "low"), QTY.class, low));
		
		TS high = new TS();
		high.setValue(fhirConsent.getPeriod().getEnd().toString());
		effectiveTime.getRest().add(new JAXBElement<QTY>(new QName("urn:hl7-org:v3", "high"), QTY.class, high));
		return effectiveTime;
	}

}
