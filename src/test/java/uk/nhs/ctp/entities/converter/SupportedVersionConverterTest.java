package uk.nhs.ctp.entities.converter;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.entities.CdssSupplier.SupportedVersion;

public class SupportedVersionConverterTest {

  public SupportedVersionConverter converter;

  @Rule
  public ExpectedException expected = ExpectedException.none();

  @Before
  public void setup() {
    converter = new SupportedVersionConverter();
  }

  @Test
  public void convertToDatabaseColumn_whenNull() {
    String returned = converter.convertToDatabaseColumn(null);
    assertThat(returned, nullValue());
  }

  @Test
  public void convertToDatabaseColumn_withValue() {
    String returned = converter.convertToDatabaseColumn(SupportedVersion.TWO);
    assertThat(returned, is("2.0"));
  }

  @Test
  public void convertToEntity_whenNull() {
    SupportedVersion supportedVersion = converter.convertToEntityAttribute(null);
    assertThat(supportedVersion, nullValue());
  }

  @Test
  public void convertToEntity_unsupportedVersion() {
    expected.expect(IllegalArgumentException.class);
    converter.convertToEntityAttribute("not a version");
  }

  @Test
  public void convertToEntity_supportedVersion() {
    SupportedVersion returned = converter.convertToEntityAttribute("1.1");
    assertThat(returned, is(SupportedVersion.ONE_ONE));
  }

}