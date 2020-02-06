package uk.nhs.ctp.service.dto;

import java.util.Date;
import lombok.Data;

@Data
public class PatientDTO {

  private String id;
  private String title;
  private String firstName;
  private String lastName;
  private Date dateOfBirth;
  private String address;
  private String gender;
  private String homePhone;
  private String mobile;
  private String postalCode;
  private String email;
  private String language;

}
