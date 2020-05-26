package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

  @Autowired
  private UserController userController;

  @Test
  public void shouldResponseWithAccountDetails() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("testid");
    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotResponseError() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();

    ResponseEntity<SupplierAccountDetails> response = userController.signup(request);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }
}