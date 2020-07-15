package uk.nhs.ctp.auditFinder.role;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class PutRoleMappingRequest {

  @Singular
  List<String> users;

}
