package uk.nhs.ctp.service;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import javax.persistence.EntityExistsException;
import org.elasticsearch.indices.InvalidIndexNameException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.security.JWTHandler;
import uk.nhs.cactus.common.security.JWTRequest;
import uk.nhs.ctp.elasticsearch.role.RoleMapper;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.security.CognitoService;
import uk.nhs.ctp.service.dto.ChangePasswordDTO;
import uk.nhs.ctp.service.dto.NewUserDTO;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private CognitoService cognitoService;

  @Mock
  private JWTHandler jwtHandler;

  @Mock
  private RoleMapper roleMapper;

  @InjectMocks
  private UserManagementService userManagementService;

  @Before
  public void setup() {
    when(passwordEncoder.encode(anyString())).then(inv -> inv.getArguments()[0]);
  }

  @Test
  public void createNewSupplierUser() {
    var jwtRequest = JWTRequest.builder()
        .username("supplier_id")
        .supplierId("supplier_id")
        .role("ROLE_SUPPLIER_ADMIN")
        .build();
    when(jwtHandler.generate(argThat(is(jwtRequest))))
        .thenReturn("random_jwt_value");
    ReflectionTestUtils.setField(userManagementService, "ems", "http://ems.com");
    ReflectionTestUtils.setField(userManagementService, "emsUi", "http://ems-ui.com");
    ReflectionTestUtils.setField(userManagementService, "cdss", "http://cdss.com");
    ReflectionTestUtils.setField(userManagementService, "cdss2", "http://cdss2.com");
    ReflectionTestUtils.setField(userManagementService, "dos", "http://dos.com");
    ReflectionTestUtils.setField(userManagementService, "logs", "http://elastic.com");
    ReflectionTestUtils.setField(userManagementService, "fhirServer", "http://fhir-place.com");
    ReflectionTestUtils.setField(userManagementService, "blobServer", "http://blob-palace.com");

    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("suPpliEr_iD");

    SupplierAccountDetails returned = userManagementService.createNewSupplierUser(request);

    SupplierAccountDetails expected = SupplierAccountDetails.builder()
        .username("supplier_id")
        .jwt("random_jwt_value")
        .endpoints(EndpointDetails.builder()
            .ems("http://ems.com")
            .emsUi("http://ems-ui.com")
            .cdss("http://cdss.com")
            .fhirServer("http://fhir-place.com")
            .blobServer("http://blob-palace.com")
            .cdss2("http://cdss2.com")
            .dos("http://dos.com")
            .logs("http://elastic.com")
            .build())
        .build();

    assertThat(returned, sameBeanAs(expected)
        .with("password", any(String.class)));
    verify(cognitoService).signUp("supplier_id", returned);
    verify(roleMapper).setupSupplierRoles("supplier_id", returned.getUsername());
  }

  @Test
  public void createNewSupplierUser_alreadyExists() {
    when(userRepository.save(Matchers.any(UserEntity.class)))
        .thenThrow(new EntityExistsException());

    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("supplier_id");

    expectedException.expect(EntityExistsException.class);
    userManagementService.createNewSupplierUser(request);

    verifyZeroInteractions(cognitoService, roleMapper);
  }

  @Test
  public void createNewSupplierUser_invalidIndexFails() {
    RegisterSupplierRequest request = new RegisterSupplierRequest();
    request.setSupplierId("contains <invalid>#charcters?");

    expectedException.expect(InvalidIndexNameException.class);
    userManagementService.createNewSupplierUser(request);

    verifyZeroInteractions(cognitoService, userRepository, roleMapper);
  }

  private NewUserDTO getTestUser() {
    var newUser = new NewUserDTO();
    newUser.setRole("ROLE_TEST");
    newUser.setUsername("test_username");
    newUser.setPassword("insecure password");
    newUser.setName("test name");
    newUser.setEnabled(true);
    newUser.setSupplierId("thesupplier");
    return newUser;
  }

  @Test
  public void createUser_success() {
    userManagementService.createUser(getTestUser());

    var expectedUser = new UserEntity();
    expectedUser.setRole("ROLE_TEST");
    expectedUser.setUsername("test_username");
    expectedUser.setPassword("insecure password");
    expectedUser.setName("test name");
    expectedUser.setEnabled(true);
    expectedUser.setSupplierId("thesupplier");

    verify(userRepository).save(argThat(sameBeanAs(expectedUser)));
  }

  @Test
  public void createUser_failWithSameUsername() {
    var existingUser = new UserEntity();
    existingUser.setUsername("test_username");
    when(userRepository.findAll()).thenReturn(Collections.singletonList(existingUser));

    expectedException.expect(EMSException.class);
    userManagementService.createUser(getTestUser());
  }

  @Test
  public void reset_success() {

    var existingUser = new UserEntity();
    existingUser.setUsername("test_username");
    when(userRepository.findByUsername("test_username")).thenReturn(existingUser);

    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
    changePasswordDTO.setUsername("test_username");
    changePasswordDTO.setNewPassword("new password");
    userManagementService.resetPassword(changePasswordDTO);

    var expectedUser = new UserEntity();
    expectedUser.setUsername("test_username");
    expectedUser.setPassword("new password");

    verify(userRepository).save(argThat(sameBeanAs(expectedUser)));
  }
}