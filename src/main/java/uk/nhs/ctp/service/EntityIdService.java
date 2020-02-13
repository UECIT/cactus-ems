package uk.nhs.ctp.service;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.EntityId;
import uk.nhs.ctp.repos.EntityIdRepository;

@Service
@AllArgsConstructor
public class EntityIdService {

  private EntityIdRepository entityIdRepository;

  public Long nextId(ResourceType resourceType) {
    String name = resourceType.getPath();
    if (!entityIdRepository.exists(name)) {
      entityIdRepository.save(new EntityId(name));
      return EntityId.INITIAL_VALUE;
    }

    return entityIdRepository.incrementAndGet(name);
  }

}
