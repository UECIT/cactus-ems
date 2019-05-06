package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;

import uk.nhs.ctp.utils.ResourceProviderUtils;

public class ReferralRequestDTO {

	private String status;
	private String priority;
	private String serviceRequestedSystem;
	private String serviceRequestedCode;
	private List<CodingDTO> serviceRequested;
	private String occurence;
	private String specialty;
	private String recipient;
	private String description;
	private String reasonReference;
	private List<String> supportingInfo = new ArrayList<>();
	private String note;
	private String relevantHistory;
	private String resourceId;

	public ReferralRequestDTO() {
		
	}
	
	@SuppressWarnings("deprecation")
	public ReferralRequestDTO(ReferralRequest referralRequest) {
		super();

		if (referralRequest.getStatus() != null) {
			this.setStatus(referralRequest.getStatus().toCode());
		}

		if (referralRequest.getPriority() != null) {
			this.setPriority(referralRequest.getPriority().toCode());
		}

		try {
			this.setOccurence(referralRequest.getOccurrenceDateTimeType().getValueAsString());
		} catch (Exception e) {
			try {
				this.setOccurence("Start: " + referralRequest.getOccurrencePeriod().getStart().toGMTString()
						+ " - End: " + referralRequest.getOccurrencePeriod().getEnd().toGMTString());
			} catch (Exception f) {
				this.setOccurence(null);
			}
		}

		if (!referralRequest.getServiceRequestedFirstRep().isEmpty()) {
			String serviceRequestedCode = referralRequest.getServiceRequestedFirstRep().getCodingFirstRep().getCode();
			String serviceRequestedSystem = referralRequest.getServiceRequestedFirstRep().getCodingFirstRep()
					.getSystem();

			List<CodingDTO> codeList = new ArrayList<CodingDTO>();

			for (CodeableConcept coding : referralRequest.getServiceRequested()) {
				codeList.add(new CodingDTO(coding));
			}

			this.setServiceRequested(codeList);

			this.setServiceRequestedSystem(serviceRequestedSystem);
			this.setServiceRequestedCode(serviceRequestedCode);
		}

		if (!referralRequest.getSpecialty().isEmpty()) {
			String specialityCode = referralRequest.getSpecialty().getCodingFirstRep().getCode();
			this.setSpecialty(specialityCode);
		}

		if (!referralRequest.getRecipientFirstRep().isEmpty()) {
			this.setRecipient(referralRequest.getRecipientFirstRep().getReference());
		}

		if (!referralRequest.getDescription().isEmpty()) {
			this.setDescription(referralRequest.getDescription());
		}

		if (!referralRequest.getReasonReferenceFirstRep().isEmpty()) {
			this.setReasonReference(referralRequest.getReasonReferenceFirstRep().getDisplay());
		}

		if (!referralRequest.getNoteFirstRep().isEmpty()) {
			this.setNote(referralRequest.getNoteFirstRep().getText());
		}

		if (!referralRequest.getSupportingInfoFirstRep().isEmpty()) {
			for (Reference supportingInfo : referralRequest.getSupportingInfo()) {
				if (supportingInfo.getDisplay() != null) {
					this.addSupportingInfo(supportingInfo.getDisplay());
				} else if (supportingInfo.getResource() != null) {
					Observation observation = ResourceProviderUtils.castToType(supportingInfo.getResource(),
							Observation.class);
					this.addSupportingInfo(observation.getCode().getCodingFirstRep().getDisplay());
				}
			}
		}

		if (!referralRequest.getRelevantHistoryFirstRep().isEmpty()) {
			this.setRelevantHistory(referralRequest.getRelevantHistoryFirstRep().getDisplay());
		}

		this.setResourceId(referralRequest.getId());
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getServiceRequestedSystem() {
		return serviceRequestedSystem;
	}

	public void setServiceRequestedSystem(String serviceRequestedSystem) {
		this.serviceRequestedSystem = serviceRequestedSystem;
	}

	public String getServiceRequestedCode() {
		return serviceRequestedCode;
	}

	public void setServiceRequestedCode(String serviceRequestedCode) {
		this.serviceRequestedCode = serviceRequestedCode;
	}

	public String getOccurence() {
		return occurence;
	}

	public void setOccurence(String occurence) {
		this.occurence = occurence;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getSupportingInfo() {
		return supportingInfo;
	}

	public void setSupportingInfo(List<String> supportingInfo) {
		this.supportingInfo = supportingInfo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getRelevantHistory() {
		return relevantHistory;
	}

	public void setRelevantHistory(String relevantHistory) {
		this.relevantHistory = relevantHistory;
	}

	public List<CodingDTO> getServiceRequested() {
		return serviceRequested;
	}

	public void setServiceRequested(List<CodingDTO> serviceRequested) {
		this.serviceRequested = serviceRequested;
	}

	public void addSupportingInfo(String supportingInfo) {
		this.supportingInfo.add(supportingInfo);
	}

	public String getReasonReference() {
		return reasonReference;
	}

	public void setReasonReference(String reasonReference) {
		this.reasonReference = reasonReference;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
