package uk.nhs.ctp.builder;


import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Type;

public class ParametersBuilder {

  private final Parameters parameters;

  public ParametersBuilder() {
    parameters = new Parameters();
  }

  public ParametersBuilder add(String name, Resource resource) {
    if (resource != null) {
      add(name).setResource(resource);
    }
    return this;
  }
  public ParametersBuilder add(String name, Type value) {
    if (value != null) {
      add(name).setValue(value);
    }
    return this;
  }

  public Parameters build() {
    return parameters;
  }

  private ParametersParameterComponent add(String name) {
    return parameters.addParameter().setName(name);
  }
}