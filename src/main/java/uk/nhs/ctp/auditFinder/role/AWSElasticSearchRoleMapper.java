package uk.nhs.ctp.auditFinder.role;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.security.user.privileges.Role.IndexPrivilegeName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;
import uk.nhs.ctp.auditFinder.role.PutRoleRequest.IndexPermissions;

@Service
@RequiredArgsConstructor
@Profile("!dev")
@Slf4j
public class AWSElasticSearchRoleMapper implements RoleMapper {

  private final ElasticSearchClient esClient;

  @Value("${cognito.user.pool}")
  private String userPool;

  private static final String COGNITO_PREXIX = "Cognito/";
  private static final String ROLE_SUFFIX = "_role";

  @Override
  public void setupSupplierRoles(String supplierId, String username) {

    PutRoleRequest roleRequest = PutRoleRequest.builder()
        .indexPermission(IndexPermissions.builder()
            .allowedAction(IndexPrivilegeName.READ)
            .indexPattern(supplierId + "-*")
            .build())
        .build();

    String cognitoUser = COGNITO_PREXIX + userPool + "/" + username;
    String roleName = supplierId + ROLE_SUFFIX;

    PutRoleMappingRequest roleMappingRequest = PutRoleMappingRequest.builder()
        .user(cognitoUser)
        .build();

    try{
      esClient.mapRole(roleName, roleRequest, roleMappingRequest);
    } catch (IOException e) {
      log.error("Error occurred creating elasticsearch roles for user {}", username, e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
