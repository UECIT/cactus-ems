package uk.nhs.ctp.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.enums.CdsApiVersion.Converter;

@Getter
@RequiredArgsConstructor
@Convert(converter = Converter.class)
public enum CdsApiVersion {
  @JsonProperty("1.1")
  ONE_ONE("1.1"),
  @JsonProperty("2.0")
  TWO("2.0");

  private final String version;

  public static class Converter implements
      AttributeConverter<CdsApiVersion, String> {

    @Override
    public String convertToDatabaseColumn(CdsApiVersion supportedVersion) {
      if (supportedVersion == null) {
        return null;
      }
      return supportedVersion.getVersion();
    }

    @Override
    public CdsApiVersion convertToEntityAttribute(String version) {
      if (version == null) {
        return null;
      }
      return Stream.of(CdsApiVersion.values())
          .filter(c -> c.getVersion().equals(version))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
  }
}
