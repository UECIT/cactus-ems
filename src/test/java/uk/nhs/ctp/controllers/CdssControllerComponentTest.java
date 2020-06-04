package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import javax.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.CdssSupplier.SupportedVersion;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.dto.NewCdssSupplierDTO;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CdssControllerComponentTest {

  @Autowired
  private CdssController cdssController;

  @Autowired
  private CdssSupplierRepository cdssRepository;

  @MockBean
  private TokenAuthenticationService authenticationService;

  @Test
  @Transactional
  public void createsCdss() {
    var request = newSupplierRequest();
    when(authenticationService.requireSupplierId()).thenReturn("mockSupplierId");

    CdssSupplier returned = cdssController.createCdssSupplier(request);

    CdssSupplier stored = cdssRepository.getOne(returned.getId());

    assertThat(returned.getInputParamsRefType(), is(request.getInputParamsRefType()));
    assertThat(returned.getInputDataRefType(), is(request.getInputDataRefType()));
    assertThat(returned.getName(), is(request.getName()));
    assertThat(returned.getBaseUrl(), is(request.getBaseUrl()));
    assertThat(returned.getSupplierId(), is("mockSupplierId"));
    assertThat(returned.getId(), notNullValue());
    assertThat(stored, is(returned));
  }

  private NewCdssSupplierDTO newSupplierRequest() {
    var request = new NewCdssSupplierDTO();
    request.setName("Test Supplier");
    request.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    request.setInputDataRefType(ReferencingType.BY_REFERENCE);
    request.setBaseUrl("base.url.com/fhir");
    request.setSupportedVersion(SupportedVersion.TWO);
    return request;
  }

}