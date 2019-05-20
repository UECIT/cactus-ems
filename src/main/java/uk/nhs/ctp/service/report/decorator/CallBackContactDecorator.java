package uk.nhs.ctp.service.report.decorator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Workgroup;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Participant;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class CallBackContactDecorator implements AmbulanceDecorator {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.setCallBackContact(CreateCallBackContact(request));
	}
	
	private JAXBElement<POCDMT200001GB02Participant> CreateCallBackContact(ReportRequestDTO request) {
		POCDMT200001GB02Participant callBackContact = new POCDMT200001GB02Participant();
		
		CareConnectPatient fhirPatient = 
				ResourceProviderUtils.getResource(request.getBundle(), CareConnectPatient.class);
		
		
		callBackContact.getTypeCode().add("CALLBCK");
		callBackContact.getContextControlCode().add("OP");
		
		TemplateContent contentTemplate = new TemplateContent();
		contentTemplate.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		contentTemplate.setExtension("COCD_TP145212GB02#Workgroup");
		callBackContact.setContentId(contentTemplate);
		
		COCDTP145212GB02Workgroup workGroup = new COCDTP145212GB02Workgroup();
		workGroup.setClassCode(workGroup.getClassCode());
		
		CVNPfITCodedplain code = new CVNPfITCodedplain();
			code.setCode("01");
			code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.266");
			code.setDisplayName("EMS-Poc");
		workGroup.setCode(code);
		
		IINPfITOidRequiredAssigningAuthorityName assigningAuthorityName = new IINPfITOidRequiredAssigningAuthorityName();
		assigningAuthorityName.setNullFlavor(CsNullFlavor.NI);
		workGroup.setId(assigningAuthorityName);
		
		TEL phone = new TEL();
		phone.getUse().add(CsTelecommunicationAddressUse.EC);
		phone.setValue(fhirPatient.getTelecomFirstRep().getValue());
		workGroup.getTelecom().add(phone);
		
		COCDTP145212GB02Person assignedPerson = new COCDTP145212GB02Person();
		assignedPerson.setClassCode(assignedPerson.getClassCode());
		assignedPerson.setDeterminerCode(assignedPerson.getDeterminerCode());
		
		PN personName = new PN();
		personName.getUse().add(CsEntityNameUse.L); // TODO should be mapped via terminology server from fhirPatient.getNameFirstRep().getUse()
		personName.getContent().add(fhirPatient.getNameFirstRep().getNameAsSingleString());
		assignedPerson.setName(personName);
		
		workGroup.setAssignedPerson(new JAXBElement<COCDTP145212GB02Person>(new QName("assignedPerson"), COCDTP145212GB02Person.class, assignedPerson));
		
		callBackContact.setCOCDTP145212GB02Workgroup(workGroup);
		
		
		return new JAXBElement<POCDMT200001GB02Participant>(new QName("callBackContact"), POCDMT200001GB02Participant.class, callBackContact);
	}

}
