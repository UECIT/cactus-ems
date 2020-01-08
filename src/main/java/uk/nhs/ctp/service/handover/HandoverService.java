package uk.nhs.ctp.service.handover;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.service.dto.HandoverRequestDTO;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.FlagSupportingInfoDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.ProcedureRequestBasedOnDecorator;
import uk.nhs.ctp.service.handover.decorator.referral.ProvenanceRelevantHistoryDecorator;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public abstract class HandoverService {
	
	@Autowired 
	private AuditService auditService;
	
	@Autowired
	private IParser fhirParser;
	
	@Autowired
	private ProcedureRequestBasedOnDecorator procedureRequestBasedOnDecorator;
	
	@Autowired
	private ProvenanceRelevantHistoryDecorator provenanceRelevantHistoryDecorator;
	
	@Autowired 
	private FlagSupportingInfoDecorator flagSupportingInfoDecorator;
	
	@Autowired
	private List<ResourceDecorator<ReferralRequest, AuditEntry>> referralRequestAuditDecorators;
	
	@Autowired
	private List<ResourceDecorator<Bundle, AuditEntry>> bundleAuditDecorators;
	
	@Autowired
	private List<ResourceDecorator<Bundle, CareConnectPatient>> bundlePatientDecorators;
	
	@Autowired
	private List<ResourceDecorator<Bundle, CareConnectCarePlan>> bundleCarePlanDecorators;

	public abstract <T extends Resource> T getResource(HandoverRequestDTO request, Class<T> resourceClass);
	
	public String getHandoverMessage(HandoverRequestDTO request) throws MalformedURLException, JsonProcessingException {
		List<AuditEntry> auditEntries = 
				auditService.getAuditEntries(request.getCaseId(), AuditEntryType.INTIALREQUEST, AuditEntryType.REQUEST, AuditEntryType.RESULT);
		
		AuditEntry latestEntry = auditEntries.get(auditEntries.size() - 1);
		request.setResourceBundle(fhirParser.parseResource(Bundle.class, latestEntry.getContained()));

		Bundle documentBundle = new Bundle();
		documentBundle.setId("#resourceBundle");
		documentBundle.setType(BundleType.COLLECTION);

		ReferralRequest referralRequest = getResource(request, ReferralRequest.class);
		referralRequest.setRecipient(new ArrayList<>());
		
		referralRequestAuditDecorators
				.forEach(decorator -> decorator.decorate(referralRequest, latestEntry));
		procedureRequestBasedOnDecorator.decorate(referralRequest, getResource(request, ProcedureRequest.class));
		provenanceRelevantHistoryDecorator.decorate(referralRequest, getResource(request, Provenance.class));
		flagSupportingInfoDecorator.decorate(referralRequest, ResourceProviderUtils.getResource(
				referralRequest.getSubject().getResource(), CareConnectPatient.class));

		bundlePatientDecorators.forEach(decorator -> decorator.decorate(documentBundle,
				ResourceProviderUtils.getResource(referralRequest.getSubject().getResource(), CareConnectPatient.class)));
		
		bundleCarePlanDecorators.forEach(decorator -> decorator.decorate(documentBundle,
				ResourceProviderUtils.getResource(request.getResourceBundle(), CareConnectCarePlan.class)));
		
		auditEntries.forEach(auditEntry -> {
			bundleAuditDecorators.forEach(decorator -> decorator.decorate(documentBundle, auditEntry));
		});
		
		referralRequest.addSupportingInfo(new Reference(documentBundle));
		referralRequest.addContained(documentBundle);

		return fhirParser.encodeResourceToString(referralRequest);
	}
}
