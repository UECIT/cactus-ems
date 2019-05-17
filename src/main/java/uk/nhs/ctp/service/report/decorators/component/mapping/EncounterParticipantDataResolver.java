package uk.nhs.ctp.service.report.decorators.component.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Resource;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public class EncounterParticipantDataResolver<RESOURCE extends Resource> {

	private List<EncounterParticipantDataMapper<RESOURCE>> encounterParticipantDataMappers;
	
	public void resolve(Encounter encounter, COCDTP146232GB01EncompassingEncounter encompassingEncounter) {
		List<Resource> resources = new ArrayList<>();
		resources.add(ResourceProviderUtils.getResource(encounter.getSubjectTarget(), CareConnectPatient.class));
		resources.addAll(encounter.getParticipant().stream().map(
				EncounterParticipantComponent::getIndividualTarget).collect(Collectors.toList()));
		
		resources.stream().forEach(resource -> {
			
			Optional<EncounterParticipantDataMapper<RESOURCE>> optional = 
					encounterParticipantDataMappers.stream().filter(
							mapper -> mapper.getResourceClass().equals(resource.getClass())).findFirst();
			
			if (optional.isPresent()) {
				EncounterParticipantDataMapper<RESOURCE> mapper = optional.get();
				mapper.map(mapper.getResourceClass().cast(resource), encompassingEncounter);
			}
		});
	}
}
