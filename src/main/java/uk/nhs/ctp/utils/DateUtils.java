package uk.nhs.ctp.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import lombok.NonNull;

public class DateUtils {

  public static int calculateAge(@NonNull Date dateOfBirth) {
    LocalDate localDate;
    if (dateOfBirth instanceof java.sql.Date) {
      localDate = ((java.sql.Date) dateOfBirth).toLocalDate();
    }
    else {
      localDate = dateOfBirth.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }

    return Period.between(localDate, LocalDate.now()).getYears();
  }

  public static String ageCodeFromDate(@NonNull Date dateOfBirth) {
    return calculateAge(dateOfBirth) >= 16 ? "adult" : "child";
  }

}
