package uk.nhs.ctp.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXBException;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.DoSService;
import uk.nhs.ctp.service.dto.DoSRequestDTO;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.dto.ReportType;
import uk.nhs.ctp.service.dto.ReportsDTO;

@Service
public class DoSReportService implements Reportable {
	
	@Autowired
	private DoSService dosService;
	
	@Autowired 
	private CaseRepository caseRepository;
	
	@Value("${ems.search.distance:25}")
	private int searchDistance;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public ReportsDTO generate(ReportRequestDTO request) throws JAXBException, JsonProcessingException {
		
		Cases caseEntity = caseRepository.findOne(request.getCaseId());
		
		List<HealthcareServiceDTO> dosResponse =
				dosService.getDoS(request.getReferralRequest().getId());

		
		return new ReportsDTO(mapper.writeValueAsString(createDoSRequest(caseEntity, request.getReferralRequest())), 
				mapper.writeValueAsString(dosResponse), ReportType.DOS, ContentType.JSON);
	}
	
	private DoSRequestDTO createDoSRequest(Cases caseEntity, ReferralRequest referralRequest) {
		DoSRequestDTO dosRequest = new DoSRequestDTO();
		
		dosRequest.deriveGender(caseEntity.getGender());
		dosRequest.setPostcode(caseEntity.getAddress());
		dosRequest.setSearchDistance(searchDistance);
		dosRequest.setSymptomDiscriminatorInt(Integer.parseInt(getServiceRequestedCode(referralRequest, "SD")));
		dosRequest.setSymptomGroup(Integer.parseInt(getServiceRequestedCode(referralRequest, "SG")));
		
		return dosRequest;
	}
	
	private String getServiceRequestedCode(ReferralRequest referralRequest, String system) {
		Optional<CodeableConcept> optional = referralRequest.getServiceRequested().stream()
				.filter(service -> system.equals(service.getCoding().get(0).getSystem())).findFirst();
		
		return optional.isPresent() ? optional.get().getCoding().get(0).getCode() : "0";
	}

}
