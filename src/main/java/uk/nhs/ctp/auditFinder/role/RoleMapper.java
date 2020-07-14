package uk.nhs.ctp.auditFinder.role;

import uk.nhs.ctp.model.SupplierAccountDetails;

public interface RoleMapper {

  void setupSupplierRoles(String supplierId, SupplierAccountDetails accountDetails);

}
