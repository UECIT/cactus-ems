package uk.nhs.ctp.service.handover.decorator;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.entities.AuditEntry;

public interface AuditDataDecorator<RESOURCE extends Resource> {

	RESOURCE decorate(Bundle bundle, AuditEntry auditEntry);

}
