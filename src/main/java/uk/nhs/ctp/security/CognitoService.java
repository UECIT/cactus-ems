package uk.nhs.ctp.security;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.model.SupplierAccountDetails;

@Service
public class CognitoService {

  private static final String SUPPLIER_ID_ATTRIBUTE = "custom:supplierId";

  public void signUp(String supplierId, SupplierAccountDetails accountDetails) {
    var cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
        .withRegion(Regions.EU_WEST_2)
        .build();

    SignUpRequest signUpRequest = new SignUpRequest()
        .withClientId("")
        .withUsername(accountDetails.getUsername())
        .withPassword(accountDetails.getPassword())
        .withUserAttributes(new AttributeType()
            .withName(SUPPLIER_ID_ATTRIBUTE)
            .withValue(supplierId));
    cognitoIdentityProvider.signUp(signUpRequest);
  }

}
