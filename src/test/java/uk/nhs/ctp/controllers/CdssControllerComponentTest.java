package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.IsEqualJSON.equalToJSON;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import javax.transaction.Transactional;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
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

  @MockBean
  private TokenAuthenticationService authenticationService;

  @SpyBean //only mock the client, we want a real context
  private FhirContext fhirContext;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient spyClient;

  private static final String MOCK_SUPPLIER_ID = "mockSupplierId";

  @Before
  public void setup() {
    when(authenticationService.requireSupplierId()).thenReturn(MOCK_SUPPLIER_ID);
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

    doReturn(spyClient).when(fhirContext)
        .newRestfulGenericClient("mock.base.url");
    when((Object) spyClient.read()
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