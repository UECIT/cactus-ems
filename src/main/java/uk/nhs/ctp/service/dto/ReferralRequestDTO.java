package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@NoArgsConstructor
@Data
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
	private String contextReference;
	private String resourceId;
	
	public ReferralRequestDTO(ReferralRequest referralRequest) {
		super();

		if (referralRequest.hasStatus()) {
			setStatus(referralRequest.getStatus().toCode());
		}

		if (referralRequest.hasPriority()) {
			setPriority(referralRequest.getPriority().toCode());
		}

		transformOccurrence(referralRequest);
		transformServiceRequested(referralRequest);

		if (referralRequest.hasSpecialty()) {
			setSpecialty(referralRequest.getSpecialty().getCodingFirstRep().getCode());
		}
		if (referralRequest.hasRecipient()) {
			setRecipient(referralRequest.getRecipientFirstRep().getReference());
		}

		if (referralRequest.hasDescription()) {
			setDescription(referralRequest.getDescription());
		}

		if (referralRequest.hasReasonReference()) {
			setReasonReference(referralRequest.getReasonReferenceFirstRep().getDisplay());
		}

		if (referralRequest.hasNote()) {
			setNote(referralRequest.getNoteFirstRep().getText());
		}

		transformSupportingInfo(referralRequest);

		if (referralRequest.hasRelevantHistory()) {
			setRelevantHistory(referralRequest.getRelevantHistoryFirstRep().getDisplay());
		}

		if (referralRequest.hasContext()) {
			setContextReference(referralRequest.getContext().getReference());
		}

		setResourceId(referralRequest.getId());
	}

	private void transformSupportingInfo(ReferralRequest referralRequest) {
		if (referralRequest.hasSupportingInfo()) {
			for (Reference supportingInfo : referralRequest.getSupportingInfo()) {
				if (supportingInfo.hasDisplay()) {
					addSupportingInfo(supportingInfo.getDisplay());
				} else if (!supportingInfo.isEmpty()) {
					Observation observation = ResourceProviderUtils.castToType(supportingInfo.getResource(),
							Observation.class);
					addSupportingInfo(observation.getCode().getCodingFirstRep().getDisplay());
				}
			}
		}
	}

	private void transformServiceRequested(ReferralRequest referralRequest) {
		if (referralRequest.hasServiceRequested()) {
			Coding srCoding = referralRequest.getServiceRequestedFirstRep().getCodingFirstRep();
			String serviceRequestedCode = srCoding.getCode();
			String serviceRequestedSystem = srCoding.getSystem();

			List<CodingDTO> codeList = new ArrayList<>();

			for (CodeableConcept coding : referralRequest.getServiceRequested()) {
				codeList.add(new CodingDTO(coding));
			}

			setServiceRequested(codeList);
			setServiceRequestedSystem(serviceRequestedSystem);
			setServiceRequestedCode(serviceRequestedCode);
		}
	}

	private void transformOccurrence(ReferralRequest referralRequest) {

		if (referralRequest.hasOccurrenceDateTimeType()) {
			setOccurence(referralRequest.getOccurrenceDateTimeType().getValueAsString());
		}
		else if (referralRequest.hasOccurrencePeriod()) {
			setOccurence("Start: " + referralRequest.getOccurrencePeriod().getStart().toGMTString()
					+ " - End: " + referralRequest.getOccurrencePeriod().getEnd().toGMTString());
		}
		else {
			setOccurence(null);
		}
	}

	public void addSupportingInfo(String supportingInfo) {
		this.supportingInfo.add(supportingInfo);
	}
}
