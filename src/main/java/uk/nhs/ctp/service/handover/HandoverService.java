package uk.nhs.ctp.service.handover;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.uhn.fhir.parser.IParser;
import resources.CareConnectPatient;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.service.dto.HandoverRequestDTO;
import uk.nhs.ctp.service.factory.DocumentBundleFactory;
import uk.nhs.ctp.service.handover.decorator.AuditDataDecorator;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;
import uk.nhs.ctp.service.handover.decorator.bundle.CarePlanBundleDecorator;
import uk.nhs.ctp.service.handover.decorator.bundle.CompositionBundleDecorator;
import uk.nhs.ctp.service.handover.decorator.bundle.PatientBundleDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.AuthorRequesterDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.FlagSupportingInfoDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.ProcedureRequestBasedOnDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.ProvenanceRelevantHistoryDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.SubjectDecorator;

public abstract class HandoverService {
	
	@Autowired 
	private AuditService auditService;
	
	@Autowired
	private IParser fhirParser;
	
	@Autowired
	private ProcedureRequestBasedOnDecorator procedureRequestBasedOnDecorator;
	
	@Autowired
	private ProvenanceRelevantHistoryDecorator provenanceReleventHistoryDecorator;
	
	@Autowired
	private SubjectDecorator subjectDecorator;
	
	@Autowired 
	private FlagSupportingInfoDecorator flagSupportingInfoDecorator; 
	
	@Autowired
	private AuthorRequesterDecorator authorRequesterDecorator;
	
	@Autowired
	private DocumentBundleFactory documentBundleFactory;
	
	@Autowired
	private CompositionBundleDecorator compositionBundleDecorator;
	
	@Autowired 
	private PatientBundleDecorator patientBundleDecorator;
	
	@Autowired 
	private CarePlanBundleDecorator carePlanBundleDecorator;
	
	@Autowired
	private List<ResourceDecorator<ReferralRequest>> referralRequestDecorators;
	
	@Autowired
	private List<AuditDataDecorator<? extends Resource>> auditDataDecorators;
	
	public abstract <T extends Resource> T getResource(HandoverRequestDTO request, Class<T> resourceClass);
	
	public String getHandoverMessage(HandoverRequestDTO request) throws MalformedURLException, JsonProcessingException {
		List<AuditEntry> auditEntries = 
				auditService.getAuditEntries(request.getCaseId(), AuditEntryType.INTIALREQUEST, AuditEntryType.REQUEST);
		
		AuditEntry latestEntry = auditEntries.get(auditEntries.size() - 1);
		request.setResourceBundle(fhirParser.parseResource(Bundle.class, latestEntry.getContained()));
		
		Bundle documentBundle = documentBundleFactory.create("resourceBundle", true);
		ReferralRequest referralRequest = getResource(request, ReferralRequest.class);
		referralRequest.setRecipient(new ArrayList<>());
		
		// Add patient
		subjectDecorator.decorate(referralRequest, latestEntry);
		
		referralRequestDecorators.stream().forEach(decorator -> decorator.decorate(referralRequest));
		authorRequesterDecorator.decorate(referralRequest);
		
		if (referralRequest.hasBasedOn()) {
			procedureRequestBasedOnDecorator.decorate(
					referralRequest, getResource(request, ProcedureRequest.class));
		}

		if (referralRequest.hasRelevantHistory()) {
			provenanceReleventHistoryDecorator.decorate(
					referralRequest, getResource(request, Provenance.class));
		}

		
		compositionBundleDecorator.decorate(documentBundle, (CareConnectPatient) referralRequest.getSubject().getResource());
		
		patientBundleDecorator.decorate(documentBundle, (CareConnectPatient) referralRequest.getSubject().getResource());
		
		carePlanBundleDecorator.decorate(documentBundle, request.getResourceBundle());
		
		auditEntries.stream().forEach(auditEntry -> {
			auditDataDecorators.stream().forEach(decorator -> 
				decorator.decorate(documentBundle, auditEntry));
		});
		
		flagSupportingInfoDecorator.decorate(referralRequest, (CareConnectPatient) referralRequest.getSubject().getResource());
		referralRequest.addSupportingInfo(new Reference(documentBundle));

		return fhirParser.encodeResourceToString(referralRequest);
	}
}
