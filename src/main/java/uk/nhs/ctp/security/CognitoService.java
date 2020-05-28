package uk.nhs.ctp.security;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.utils.PasswordUtil;

@Service
@Slf4j
public class CognitoService {

  @Value("${cognito.user.pool}")
  private String userPool;

  private static final String SUPPLIER_ID_ATTRIBUTE = "custom:supplierId";
  private static final String EMAIL_ATTRIBUTE = "email";
  private static final String SUPPLIER_GROUP_NAME = "cactus_admin_suppliers";

  public void signUp(String supplierId, SupplierAccountDetails accountDetails) {
    if (isEmpty(userPool)) {
      log.warn("No user pool set, skipping creating user in cognito");
      return;
    }

    var cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
        .withRegion(Regions.EU_WEST_2)
        .build();

    var tempPassword = PasswordUtil.getStrongPassword();
    String username = accountDetails.getUsername();
    log.info("creating user in cognito with username: {}", username);
    var adminCreateUserRequest = new AdminCreateUserRequest()
        .withUserPoolId(userPool)
        .withUsername(username)
        .withTemporaryPassword(tempPassword)
        .withUserAttributes(
            new AttributeType()
              .withName(SUPPLIER_ID_ATTRIBUTE)
              .withValue(supplierId),
            new AttributeType()
              .withName(EMAIL_ATTRIBUTE)
              .withValue(accountDetails.getEmail())
        );
    // Create the user
    cognitoIdentityProvider.adminCreateUser(adminCreateUserRequest);

    var setPasswordRequest = new AdminSetUserPasswordRequest()
        .withUserPoolId(userPool)
        .withUsername(username)
        .withPassword(accountDetails.getPassword())
        .withPermanent(TRUE);
    // Set the password to the permanent one
    log.info("confirming password");
    cognitoIdentityProvider.adminSetUserPassword(setPasswordRequest);

    // Add user to the suppliers group
    var addToGroupRequest = new AdminAddUserToGroupRequest()
        .withUserPoolId(userPool)
        .withUsername(username)
        .withGroupName(SUPPLIER_GROUP_NAME);
    log.info("adding user '{}' to supplier group '{}'", username, SUPPLIER_GROUP_NAME);
    cognitoIdentityProvider.adminAddUserToGroup(addToGroupRequest);
  }
}
