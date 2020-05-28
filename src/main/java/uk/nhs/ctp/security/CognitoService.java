package uk.nhs.ctp.security;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.utils.PasswordUtil;

@Service
@Slf4j
public class CognitoService {

  @Value("${cognito.client.id}")
  private String clientId;

  @Value("${cognito.user.pool}")
  private String userPool;

  @Value("${ems.app.client.secret}")
  private String clientSecret;

  private static final String SUPPLIER_ID_ATTRIBUTE = "custom:supplierId";
  private static final String EMAIL_ATTRIBUTE = "email";
  private static final String USERNAME_PROPERTY = "USERNAME";
  private static final String PASSWORD_PROPERTY = "PASSWORD";
  private static final String SECRET_HASH_PROPERTY = "SECRET_HASH";

  public void signUp(String supplierId, SupplierAccountDetails accountDetails) {
    if (clientId == null || userPool == null || clientSecret == null) {
      log.warn("No client id or user pool set, skipping creating user in cognito");
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
    var adminInitiateAuthRequest = new AdminInitiateAuthRequest()
        .withAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
        .withAuthParameters(Map.ofEntries(
            Map.entry(USERNAME_PROPERTY, username),
            Map.entry(PASSWORD_PROPERTY, tempPassword),
            Map.entry(SECRET_HASH_PROPERTY, calculateSecretHash(clientId, clientSecret, username))
        ))
        .withUserPoolId(userPool)
        .withClientId(clientId);
    // Login as admin
    var adminInitiateAuthResult = cognitoIdentityProvider
        .adminInitiateAuth(adminInitiateAuthRequest);
    log.debug("{}", adminInitiateAuthResult);
    log.debug("{}", adminInitiateAuthResult.getChallengeName());
    String token = adminInitiateAuthResult.getAuthenticationResult().getAccessToken();
    // Change the temporary password to the one we return
    var changePasswordRequest = new ChangePasswordRequest()
        .withAccessToken(token)
        .withPreviousPassword(tempPassword)
        .withProposedPassword(accountDetails.getPassword());
    log.info("updating password for user: {}", username);
    cognitoIdentityProvider.changePassword(changePasswordRequest);
  }

  /**
   * Taken from https://docs.aws.amazon.com/cognito/latest/developerguide/signing-up-users-in-your-app.html#cognito-user-pools-computing-secret-hash
   * @param userPoolClientId
   * @param userPoolClientSecret
   * @param userName
   * @return
   */
  private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
    final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    SecretKeySpec signingKey = new SecretKeySpec(
        userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
        HMAC_SHA256_ALGORITHM);
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);
      mac.update(userName.getBytes(StandardCharsets.UTF_8));
      byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (Exception e) {
      throw new RuntimeException("Error while calculating ");
    }
  }

}
