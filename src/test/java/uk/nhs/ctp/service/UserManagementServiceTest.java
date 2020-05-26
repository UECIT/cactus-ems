package uk.nhs.ctp.service;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementServiceTest {

  @InjectMocks
  private UserManagementService userManagementService;

  @Test
  public void createNewSupplierUser() {
    ReflectionTestUtils.setField(userManagementService, "ems", "http://ems.com");
    ReflectionTestUtils.setField(userManagementService, "cdss", "http://cdss.com");
    ReflectionTestUtils.setField(userManagementService, "dos", "http://dos.com");

    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("suppliedid");

    SupplierAccountDetails returned = userManagementService.createNewSupplierUser(request);

    SupplierAccountDetails expected = SupplierAccountDetails.builder()
        .username("suppliedid")
        .endpoints(EndpointDetails.builder()
            .ems("http://ems.com")
            .cdss("http://cdss.com")
            .dos("http://dos.com")
            .build())
        .build();

    assertThat(returned, sameBeanAs(expected)
      .with("jwt", any(String.class))
      .with("password", any(String.class)));
  }
}