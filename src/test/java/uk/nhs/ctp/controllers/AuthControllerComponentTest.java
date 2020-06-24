package uk.nhs.ctp.controllers;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class AuthControllerComponentTest {

  @Autowired
  private AuthController authController;

  @Autowired
  private EmsSupplierRepository emsSupplierRepository;

  @MockBean
  private TokenAuthenticationService authenticationService;

  @Test
  @Transactional
  public void createEMS() {
    var ems = new EmsSupplier();
    ems.setBaseUrl("baseUrl");
    ems.setAuthToken("<emsAuthToken>");
    ems.setName("testEms");
    emsSupplierRepository.saveAndFlush(ems);

    var auth = new PreAuthenticatedAuthenticationToken(null, null, emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    var token = authController.exchangeToken("baseUrl");

    assertThat(token, is("<emsAuthToken>"));
  }
}
