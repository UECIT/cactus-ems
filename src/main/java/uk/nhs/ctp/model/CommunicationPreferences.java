package uk.nhs.ctp.model;

import lombok.Data;
import uk.nhs.ctp.enums.CommunicationMode;
import uk.nhs.ctp.enums.CommunicationProficiency;
import uk.nhs.ctp.enums.Language;

@Data
public class CommunicationPreferences {
  private boolean preferred;
  private boolean interpreterRequired;
  private Language language;
  private CommunicationMode communicationMode;
  private CommunicationProficiency communicationProficiency;
}
