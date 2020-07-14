package uk.nhs.ctp.auditFinder.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.SupplierAccountDetails;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class LocalRoleMapper implements RoleMapper {

  @Override
  public void setupSupplierRoles(String supplierId, SupplierAccountDetails accountDetails) {
    log.info("Local profile does not configure ES.");
    log.info("SupplierAccountDetails: {}", accountDetails);
  }
}
