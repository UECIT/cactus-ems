package uk.nhs.ctp.service.isvalid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.IdentifierType;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;

@RunWith(MockitoJUnitRunner.class)
public class CdssValidityServiceTest {

  @InjectMocks
  private CdssValidityService cdssValidityService;

  @Mock
  private GenericResourceLocator resourceLocator;

  @Mock
  private CdssSupplierRepository cdssSupplierRepository;

  @Mock
  private TokenAuthenticationService authService;

  @Mock
  private IsValidOperationService isValidOperationService;

  private static final String SUPPLIER_ID = "supplierId";

  @Before
  public void setup() {
    when(authService.requireSupplierId()).thenReturn(SUPPLIER_ID);
  }

  @Test
  public void isValid_empty_noRegisteredGp() {
    String patientId = "some.patient.id";
    Reference gpRef = new Reference("Practitioner/notanorg");
    Patient patientNoGpOrg = new Patient()
        .addGeneralPractitioner(gpRef);

    when(resourceLocator.findResource(patientId))
        .thenReturn(patientNoGpOrg);

    Map<String, Boolean> results = cdssValidityService.checkValidity(patientId);

    assertThat(results, not(hasEntry(anything(), anything())));
    verifyZeroInteractions(authService, cdssSupplierRepository);
    verify(resourceLocator, never()).findResource(gpRef, new IdType(patientId));
  }

  @Test
  public void isValid_empty_noOdsCode() {
    String patientId = "some.patient.id";
    Reference gpRef = new Reference("Practitioner/notanorg");
    Patient patientNoGpOrg = new Patient()
        .addGeneralPractitioner(gpRef);

    Organization gp = new Organization()
        .addIdentifier(new Identifier().setSystem(IdentifierType.SDSR.getSystem()).setValue("no_oc"));

    when(resourceLocator.findResource(patientId))
        .thenReturn(patientNoGpOrg);
    when(resourceLocator.findResource(eq(gpRef),
        argThat(new FunctionMatcher<>(id -> id.getValue().equals(patientId), patientId))))
        .thenReturn(gp);

    Map<String, Boolean> results = cdssValidityService.checkValidity(patientId);

    assertThat(results, not(hasEntry(anything(), anything())));
    verifyZeroInteractions(authService, cdssSupplierRepository);
  }

  @Test
  public void isValid_returnsResults_allCdssSuppliers() {
    String patientId = "some.patient.id";
    Reference gpRef = new Reference("Organization/isanorg");
    Patient patient = new Patient()
        .addGeneralPractitioner(gpRef);
    patient.setId(patientId);
    Identifier odsCode = new Identifier()
        .setSystem(IdentifierType.OC.getSystem())
        .setValue("someoc");
    Organization gp = new Organization()
        .addIdentifier(odsCode);

    when(resourceLocator.findResource(patientId))
        .thenReturn(patient);
    when(resourceLocator.findResource(eq(gpRef),
        argThat(new FunctionMatcher<>(id -> id.getValue().equals(patientId), patientId))))
        .thenReturn(gp);
    CdssSupplier supplier1 = new CdssSupplier();
    supplier1.setBaseUrl("supplier1.base.url");
    CdssSupplier supplier2 = new CdssSupplier();
    supplier2.setBaseUrl("supplier2.base.url");
    when(cdssSupplierRepository.findAllBySupplierId(SUPPLIER_ID))
        .thenReturn(List.of(supplier1, supplier2));
    when(isValidOperationService.invokeIsValid(supplier1, odsCode, patient))
        .thenReturn(Boolean.TRUE);
    when(isValidOperationService.invokeIsValid(supplier2, odsCode, patient))
        .thenReturn(Boolean.FALSE);

    Map<String, Boolean> results = cdssValidityService.checkValidity(patientId);

    assertThat(results, hasEntry("supplier1.base.url", true));
    assertThat(results, hasEntry("supplier2.base.url", false));
  }

}