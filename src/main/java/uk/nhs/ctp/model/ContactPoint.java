package uk.nhs.ctp.model;

import lombok.Data;

@Data
public class ContactPoint {
  public enum System {
    PHONE;

    public String toCode() {
      return name().toLowerCase();
    }
  }

  public enum Use {
    HOME,
    WORK,
    TEMP,
    OLD,
    MOBILE;

    public String toCode() {
      return name().toLowerCase();
    }
  }

  private String value;
  private System system;
  private Use use;
}
