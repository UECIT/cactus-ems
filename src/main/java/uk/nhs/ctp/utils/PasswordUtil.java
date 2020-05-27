package uk.nhs.ctp.utils;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

@UtilityClass
public class PasswordUtil {

  public String getStrongPassword() {
    List<CharacterRule> rules = Arrays.asList(
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        new CharacterRule(EnglishCharacterData.LowerCase, 1),
        new CharacterRule(EnglishCharacterData.Digit, 1)
    );
    PasswordGenerator passwordGenerator = new PasswordGenerator();
    return passwordGenerator.generatePassword(12, rules);
  }

}
