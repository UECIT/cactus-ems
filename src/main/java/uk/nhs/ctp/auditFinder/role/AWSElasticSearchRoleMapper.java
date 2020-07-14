package uk.nhs.ctp.auditFinder.role;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;
import uk.nhs.ctp.model.SupplierAccountDetails;

@Service
@RequiredArgsConstructor
@Profile("!dev")
@Slf4j
public class AWSElasticSearchRoleMapper implements RoleMapper {

  private final ElasticSearchClient esClient;

  @Override
  public void setupSupplierRoles(String supplierId, SupplierAccountDetails accountDetails) {

    try{
      esClient.mapRole(supplierId, "Cognito/{userpool}/" + accountDetails.getUsername());
    } catch (IOException e) {
      log.error("Error occurred creating elasticsearch roles for user {}", accountDetails.getUsername(), e);
    }

  }
}
