package uk.nhs.ctp.model;

import java.util.List;
import lombok.Data;
import uk.nhs.ctp.enums.Qualification;

@Data
public class Practitioner {
  private String id;
  private HumanName name;
  private Address address;
  private List<Identifier> identifiers;
  private CommunicationPreferences communicationPreferences;
  private Qualification qualification;
  private List<ContactPoint> contact;
}
