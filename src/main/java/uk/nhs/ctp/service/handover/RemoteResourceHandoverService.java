package uk.nhs.ctp.service.handover;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import uk.nhs.ctp.service.dto.HandoverRequestDTO;

@Service
public class RemoteResourceHandoverService extends HandoverService {

	private Map<Class<?>, Function<ReferralRequest, String>> urlFunctions = new HashMap<>();
	
	public RemoteResourceHandoverService() {
		urlFunctions.put(ProcedureRequest.class, (rr) -> rr.getBasedOnFirstRep().getReference());
		urlFunctions.put(Provenance.class, (rr) -> rr.getRelevantHistoryFirstRep().getReference());
	}

	@Autowired
	private FhirContext fhirContext;
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Resource> T getResource(HandoverRequestDTO request, Class<T> resourceClass) {
		IGenericClient client = fhirContext.newRestfulGenericClient(request.getRemoteUrl());
		ReferralRequest referralRequest = 
				client.read().resource(ReferralRequest.class).withUrl(request.getResourceUrl()).execute();
		
		return resourceClass.equals(ReferralRequest.class) ? 
			(T)referralRequest :
			client.read().resource(resourceClass).withUrl(
					urlFunctions.get(resourceClass).apply(referralRequest)).execute();
	}

}
