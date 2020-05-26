package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

  @Autowired
  private UserController userController;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  @WithMockUser(roles = "ADMIN")
  public void shouldResponseWithAccountDetails() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");
    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
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