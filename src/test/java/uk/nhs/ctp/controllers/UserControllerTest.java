package uk.nhs.ctp.controllers;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.security.CognitoService;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class UserControllerTest {

  @Autowired
  private UserController userController;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private CognitoService cognitoService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @After
  public void cleanup() {
    var createdUser = userRepository.findByUsername("testid");
    if (createdUser != null) {
      userRepository.delete(createdUser);
    }
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldRespondWithAccountDetails() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");
    request.setEmail("testemail");
    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    var body = SupplierAccountDetails.builder()
        .username("testid")
        .email("testemail")
        .endpoints(EndpointDetails.builder()
            .cdss("http://localhost:8080/fhir")
            .cdss2("http://localhost:8080/fhir")
            .ems("http://localhost:8083/fhir")
            .emsUi("http://localhost:4200")
            .fhirServer("http://localhost:8084/fhir")
            .blobServer("http://localhost:8084/blob")
            .dos("http://localhost:8085/fhir")
            .logs("http://elastic.search")
            .build())
        .build();
    assertThat(response.getBody(), sameBeanAs(body)
        .with("password", any(String.class))
        .with("jwt", any(String.class)));
    verify(cognitoService).signUp("testid",  response.getBody());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldNotResponseError() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();

    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    verifyZeroInteractions(cognitoService);
  }

  @Test
  @WithMockUser
  public void shouldFailRole() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");

    expectedException.expect(AccessDeniedException.class);
    userController.signup(request);
    verifyZeroInteractions(cognitoService);
  }
}