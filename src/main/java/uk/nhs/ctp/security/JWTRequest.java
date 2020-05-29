package uk.nhs.ctp.security;

import java.util.Collection;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class JWTRequest {
  String username;
  String supplierId;
  @Singular
  Collection<String> roles;
  Long secondsUntilExpiry;
}
