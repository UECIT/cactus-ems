package uk.nhs.ctp.elasticsearch.role;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.elasticsearch.client.security.user.privileges.Role.IndexPrivilegeName;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.ctp.elasticsearch.ElasticSearchRoleClient;
import uk.nhs.ctp.elasticsearch.role.PutRoleRequest.IndexPermissions;

@RunWith(MockitoJUnitRunner.class)
public class AWSElasticSearchRoleMapperTest {

  @InjectMocks
  private AWSElasticSearchRoleMapper roleMapper;

  @Mock
  private ElasticSearchRoleClient esRoleClient;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String USER_POOL = "some.user.pool";

  @Before
  public void setup() {
    ReflectionTestUtils.setField(roleMapper, "userPool", USER_POOL);
  }

  @Test
  public void shouldMapRole() throws Exception {
    String supplierId = "supplierID";
    String username = "someUsername";

    PutRoleRequest expectedRoleRequest = PutRoleRequest.builder()
        .indexPermission(IndexPermissions.builder()
            .allowedAction(IndexPrivilegeName.READ)
            .indexPattern("supplierID-*")
            .build())
        .build();

    PutRoleMappingRequest expectedMappingRequest = PutRoleMappingRequest
        .builder()
        .user("Cognito/" + USER_POOL + "/someUsername")
        .build();

    roleMapper.setupSupplierRoles(supplierId, username);

    verify(esRoleClient).mapRole(
        "supplierID_role",
        expectedRoleRequest,
        expectedMappingRequest);
  }

  @Test
  public void shouldMapRole_failsRethrows() throws Exception {
    String supplierId = "supplierID";
    String username = "someUsername";

    doThrow(new IOException("something went wrong"))
        .when(esRoleClient).mapRole(any(), any(), any());

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("something went wrong");
    roleMapper.setupSupplierRoles(supplierId, username);
  }

}