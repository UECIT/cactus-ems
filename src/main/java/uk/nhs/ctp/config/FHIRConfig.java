package uk.nhs.ctp.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.ctp.security.SupplierTokenResolver;

@Configuration
@RequiredArgsConstructor
public class FHIRConfig {

  private final List<IClientInterceptor> clientInterceptors;
  private final SupplierTokenResolver tokenResolver;

  @Bean
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu3();

    List<Class<? extends Resource>> profiles = Arrays.asList(
//        CareConnectEncounter.class,
        CareConnectLocation.class,
        CareConnectOrganization.class,
        CareConnectPatient.class,
        CareConnectPractitioner.class,
        CareConnectRelatedPerson.class);

    for (Class<? extends Resource> profileClass : profiles) {
      ResourceDef resourceDef = profileClass.getAnnotation(ResourceDef.class);
      String profile = resourceDef.profile();
      fhirContext.setDefaultTypeForProfile(profile, profileClass);
    }

    fhirContext.registerCustomType(CoordinateResource.class);

    return fhirContext;
  }

  @PostConstruct
  private void configureClientInterceptors() {
    ApacheRestfulClientFactory factory = new ApacheRestfulClientFactory() {
      @Override
      public synchronized IGenericClient newGenericClient(String theServerBase) {
        IGenericClient client = super.newGenericClient(theServerBase);

        // Injected interceptors
        for (IClientInterceptor interceptor : clientInterceptors) {
          client.registerInterceptor(interceptor);
        }

        // Authentication
        tokenResolver.resolve(theServerBase)
            .map(BearerTokenAuthInterceptor::new)
            .ifPresent(client::registerInterceptor);

        return client;
      }
    };

    FhirContext fhirContext = fhirContext();
    factory.setFhirContext(fhirContext);
    fhirContext.setRestfulClientFactory(factory);
  }
}
