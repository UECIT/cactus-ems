package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.IsEqualJSON.equalToJSON;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperation;
import ca.uhn.fhir.rest.gclient.IOperationUnnamed;
import ca.uhn.fhir.rest.gclient.IOperationUntyped;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInputAndPartialOutput;
import ca.uhn.fhir.rest.gclient.IRead;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import ca.uhn.fhir.rest.gclient.IReadTyped;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.AuditThreadStore;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.enums.IdentifierType;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.dto.NewCdssSupplierDTO;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class CdssControllerComponentTest {

  @Autowired
  private CdssController cdssController;

  @Autowired
  private CdssSupplierRepository cdssRepository;

  @Autowired
  private AuditService auditService;

  @Autowired
  private AuditThreadStore auditThreadStore;

  @MockBean
  @Qualifier("restTemplate")
  private RestTemplate restTemplate;

  @MockBean
  private TokenAuthenticationService authenticationService;

  @SpyBean //only mock the client, we want a real context
  private FhirContext spyContext;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient mockClient;

  private static final String MOCK_SUPPLIER_ID = "mockSupplierId";

  @Before
  public void setup() {
    when(authenticationService.requireSupplierId()).thenReturn(MOCK_SUPPLIER_ID);
    when(authenticationService.getCurrentSupplierId()).thenReturn(Optional.of(MOCK_SUPPLIER_ID));

    auditThreadStore.setCurrentSession(AuditSession.builder().build());
  }

  @Test
  @Transactional
  public void createsCdss() {
    var request = newSupplierRequest();

    CdssSupplier returned = cdssController.createCdssSupplier(request);

    CdssSupplier stored = cdssRepository.getOne(returned.getId());

    assertThat(returned.getInputParamsRefType(), is(request.getInputParamsRefType()));
    assertThat(returned.getInputDataRefType(), is(request.getInputDataRefType()));
    assertThat(returned.getName(), is(request.getName()));
    assertThat(returned.getBaseUrl(), is(request.getBaseUrl()));
    assertThat(returned.getSupplierId(), is(MOCK_SUPPLIER_ID));
    assertThat(returned.getId(), notNullValue());
    assertThat(returned.getAuthToken(), is("token"));
    assertThat(returned.getSupportedVersion(), is(CdsApiVersion.TWO));
    assertThat(stored, is(returned));
  }

  @Test
  @Transactional
  public void queriesCdssForServiceDefinition() {
    CdssSupplier testSupplier = new CdssSupplier();
    testSupplier.setSupplierId(MOCK_SUPPLIER_ID);
    testSupplier.setBaseUrl("mock.base.url");

    final Long supplierId = cdssRepository.save(testSupplier).getId();
    final String serviceId = "serviceDef";

    ServiceDefinition expected = new ServiceDefinition()
        .setTitle("A Service Definition")
        .setDescription("It does a thing")
        .setExperimental(true);

    doReturn(mockClient).when(spyContext)
        .newRestfulGenericClient("mock.base.url");
    when((Object) mockClient.read()
          .resource(ServiceDefinition.class)
          .withId(serviceId)
          .execute())
        .thenReturn(expected);

    String serviceDefString = cdssController.proxyServiceDefinition(supplierId, serviceId);

    assertThat(serviceDefString,
        equalToJSON("{\"resourceType\":\"ServiceDefinition\","
            + "\"title\":\"A Service Definition\","
            + "\"experimental\":true,"
            + "\"description\":\"It does a thing\"}"));
  }

  @Test
  @Transactional
  public void queriesCdssForImages() {
    CdssSupplier testSupplier = new CdssSupplier();
    testSupplier.setSupplierId(MOCK_SUPPLIER_ID);
    testSupplier.setBaseUrl("mock.base.url/fhir");

    final Long supplierId = cdssRepository.save(testSupplier).getId();
    final String imageid = "img.png";
    final byte[] image = "some encoded image".getBytes();

    when(restTemplate.getForObject("mock.base.url/image/img.png", byte[].class))
        .thenReturn(image);

    ResponseEntity<byte[]> response = cdssController.proxyImage(supplierId, imageid);
    assertThat(response.getBody(), is(image));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getHeaders().getContentType(), is(MediaType.IMAGE_PNG));
  }

  @Test
  @Transactional
  @SuppressWarnings("unchecked")
  public void queriesCdssValidity() {
    CdssSupplier testSupplier = new CdssSupplier();
    testSupplier.setSupplierId(MOCK_SUPPLIER_ID);
    testSupplier.setBaseUrl("mock.base.url/fhir");
    cdssRepository.save(testSupplier);

    String patientId = "http://some.patient.location/Patient/34";
    String gpRef = "http://some.org.location/Organization/11";
    doReturn(mockClient).when(spyContext)
        .newRestfulGenericClient(any());
    Patient patientWithGp = new Patient()
        .addGeneralPractitioner(new Reference(gpRef));
    Organization orgWithCode = new Organization()
        .addIdentifier(new Identifier()
          .setSystem(IdentifierType.OC.getSystem())
          .setValue("98765"));
    var mockIread = mock(IRead.class);
    when(mockClient.read()).thenReturn(mockIread);
    var mockReadType = mock(IReadTyped.class);
    when(mockIread.resource(anyString())).thenReturn(mockReadType);
    var mockExeReadPatient = mock(IReadExecutable.class);
    var mockExeReadOrg = mock(IReadExecutable.class);
    when(mockReadType.withId(new IdType(patientId))).thenReturn(mockExeReadPatient);
    when(mockReadType.withId(new IdType(gpRef))).thenReturn(mockExeReadOrg);
    when(mockExeReadPatient.execute()).thenReturn(patientWithGp);
    when(mockExeReadOrg.execute()).thenReturn(orgWithCode);
    Parameters returnParams = new Parameters().addParameter(new ParametersParameterComponent()
      .setName("return")
      .setValue(new BooleanType(true)));
    mockIsValid(returnParams);

    Map<String, Boolean> results = cdssController.invokeIsValid(patientId);

    assertThat(results, hasEntry("mock.base.url/fhir", Boolean.TRUE));
  }

  @SuppressWarnings("unchecked")
  private void mockIsValid(Parameters returnParams) {
    var mockOp = mock(IOperation.class);
    when(mockClient.operation()).thenReturn(mockOp);
    var mockOpUnnamed = mock(IOperationUnnamed.class);
    when(mockOp.onType(ServiceDefinition.class))
        .thenReturn(mockOpUnnamed);
    var mockOpUntyped = mock(IOperationUntyped.class);
    when(mockOpUnnamed.named("$isValid"))
        .thenReturn(mockOpUntyped);
    var partialOp = mock(IOperationUntypedWithInputAndPartialOutput.class);
    when(mockOpUntyped.withParameters(any()))
        .thenReturn(partialOp);
    when(partialOp.execute())
        .thenReturn(returnParams);
  }

  private NewCdssSupplierDTO newSupplierRequest() {
    var request = new NewCdssSupplierDTO();
    request.setName("Test Supplier");
    request.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    request.setInputDataRefType(ReferencingType.BY_REFERENCE);
    request.setBaseUrl("base.url.com/fhir");
    request.setSupportedVersion(CdsApiVersion.TWO);
    request.setAuthToken("token");
    return request;
  }

}