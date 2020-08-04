package uk.nhs.ctp.model;

import java.util.List;
import lombok.Data;

@Data
public class Organisation {
  private String id;
  private String name;
  private List<Identifier> identifiers;
}
