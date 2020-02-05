package uk.nhs.ctp.model;

import java.util.List;
import lombok.Data;

@Data
public class Address {
  public enum Use {
    HOME, WORK, TEMP, OLD;

    public String toCode() {
      return name().toLowerCase();
    }
  }

  private List<String> lines;
  private String city;
  private String postcode;
  private Use use;
}
