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
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EMSControllerComponentTest {

  @Autowired
  private EmsController emsController;

  @Autowired
  private EmsSupplierRepository emsRepository;

  @MockBean
  private TokenAuthenticationService authenticationService;

  @Test
  @Transactional
  public void createEMS() {
    var request = newEMS();
    when(authenticationService.requireSupplierId()).thenReturn("mockSupplierId");

    EmsSupplier returned = emsController.createorUpdateEMS(request);

    EmsSupplier stored = emsRepository.getOne(returned.getId());

    assertThat(returned.getName(), is(request.getName()));
    assertThat(returned.getBaseUrl(), is(request.getBaseUrl()));
    assertThat(returned.getSupplierId(), is("mockSupplierId"));
    assertThat(returned.getId(), notNullValue());
    assertThat(returned.getAuthToken(), is("token"));
    assertThat(stored, is(returned));
  }

  @Test
  @Transactional
  public void updateEMS() {
    var request = newEMS();
    when(authenticationService.requireSupplierId()).thenReturn("mockSupplierId");
    EmsSupplier created = emsController.createorUpdateEMS(request);

    created.setName("Updated");
    created.setAuthToken("Updated");
    created.setBaseUrl("Updated");

    EmsSupplier updated = emsController.createorUpdateEMS(request);
    EmsSupplier stored = emsRepository.getOne(updated.getId());

    assertThat(updated.getName(), is(created.getName()));
    assertThat(updated.getBaseUrl(), is(created.getBaseUrl()));
    assertThat(updated.getSupplierId(), is("mockSupplierId"));
    assertThat(updated.getId(), is(created.getId()));
    assertThat(updated.getAuthToken(), is(created.getAuthToken()));
    assertThat(stored, is(updated));
  }

  private EmsSupplier newEMS() {
    var request = new EmsSupplier();
    request.setName("Test Supplier");
    request.setBaseUrl("base.url.com/fhir");
    request.setAuthToken("token");
    return request;
  }

}