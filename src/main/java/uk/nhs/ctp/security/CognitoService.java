package uk.nhs.ctp.security;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.utils.PasswordUtil;

@Service
public class CognitoService {

  @Value("${cognito.client.id}")
  private String clientId;

  @Value("${cognito.user.pool}")
  private String userPool;

  private static final String SUPPLIER_ID_ATTRIBUTE = "custom:supplierId";
  private static final String EMAIL_ATTRIBUTE = "email";

  public void signUp(String supplierId, SupplierAccountDetails accountDetails) {
    if (clientId == null || userPool == null) {
      return;
    }

    var cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
        .withRegion(Regions.EU_WEST_2)
        .build();

    var tempPassword = PasswordUtil.getStrongPassword();
    var adminCreateUserRequest = new AdminCreateUserRequest()
        .withUsername(accountDetails.getUsername())
        .withTemporaryPassword(tempPassword)
        .withUserAttributes(
            new AttributeType()
              .withName(SUPPLIER_ID_ATTRIBUTE)
              .withValue(supplierId),
            new AttributeType()
              .withName(EMAIL_ATTRIBUTE)
              .withValue(accountDetails.getEmail())
        );
    cognitoIdentityProvider.adminCreateUser(adminCreateUserRequest);
    var adminInitiateAuthRequest = new AdminInitiateAuthRequest()
        .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
        .withUserPoolId(userPool)
        .withClientId(clientId);
    var adminInitiateAuthResult = cognitoIdentityProvider
        .adminInitiateAuth(adminInitiateAuthRequest);
    String token = adminInitiateAuthResult.getAuthenticationResult().getAccessToken();
    var changePasswordRequest = new ChangePasswordRequest()
        .withAccessToken(token)
        .withPreviousPassword(tempPassword)
        .withProposedPassword(accountDetails.getPassword());
    cognitoIdentityProvider.changePassword(changePasswordRequest);
  }

}
