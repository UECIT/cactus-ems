package uk.nhs.ctp.model;

import lombok.Data;
import uk.nhs.ctp.enums.IdentifierType;

@Data
public class Identifier {
  private String issuerId;
  private String system;
  private String value;
  private IdentifierType type;
}
