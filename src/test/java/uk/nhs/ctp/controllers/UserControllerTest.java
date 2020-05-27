package uk.nhs.ctp.controllers;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.repos.UserRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

  @Autowired
  private UserController userController;

  @Autowired
  private UserRepository userRepository;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @After
  public void cleanup() {
    var createdUser = userRepository.findByUsername("admin_testid");
    if (createdUser != null) {
      userRepository.delete(createdUser);
    }
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldResponseWithAccountDetails() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");
    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    var body = SupplierAccountDetails.builder()
        .username("admin_testid")
        .endpoints(EndpointDetails.builder()
            .cdss("http://localhost:8080/fhir")
            .ems("http://localhost:8083/fhir")
            .emsUi("http://localhost:4200")
            .dos("http://localhost:8085/fhir")
            .logs("http://elastic.search")
            .build())
        .build();
    assertThat(response.getBody(), sameBeanAs(body)
        .with("password", any(String.class))
        .with("jwt", any(String.class)));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldNotResponseError() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();

    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

  @Test
  @WithMockUser
  public void shouldFailRole() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");

    expectedException.expect(AccessDeniedException.class);
    userController.signup(request);
  }
}