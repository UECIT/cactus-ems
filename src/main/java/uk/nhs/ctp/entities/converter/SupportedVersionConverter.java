package uk.nhs.ctp.entities.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import uk.nhs.ctp.entities.CdssSupplier.SupportedVersion;

@Converter(autoApply = true)
public class SupportedVersionConverter implements AttributeConverter<SupportedVersion, String> {

  @Override
  public String convertToDatabaseColumn(SupportedVersion supportedVersion) {
    if (supportedVersion == null) {
      return null;
    }
    return supportedVersion.getVersion();
  }

  @Override
  public SupportedVersion convertToEntityAttribute(String version) {
    if (version == null) {
      return null;
    }
    return Stream.of(SupportedVersion.values())
        .filter(c -> c.getVersion().equals(version))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
