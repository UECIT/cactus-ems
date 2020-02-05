package uk.nhs.ctp.model;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class HumanName {
  private String prefix;
  private String given;
  private String family;

  public String getFullName() {
    return Stream.of(prefix, given, family)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining(" "));
  }
}
