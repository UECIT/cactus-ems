package uk.nhs.ctp.elasticsearch.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class PutRoleRequest {

  @JsonProperty("index_permissions")
  @Singular
  List<IndexPermissions> indexPermissions;

  @Value
  @Builder
  public static class IndexPermissions {
    @JsonProperty("allowed_actions")
    @Singular
    List<String> allowedActions;
    @JsonProperty("index_patterns")
    @Singular
    List<String> indexPatterns;
  }
}
