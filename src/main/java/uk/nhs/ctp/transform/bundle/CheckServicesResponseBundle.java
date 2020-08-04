package uk.nhs.ctp.transform.bundle;

import lombok.Value;
import org.hl7.fhir.dstu3.model.Parameters;

@Value
public class CheckServicesResponseBundle {
  String baseUrl;
  Parameters responseParameters;
}
