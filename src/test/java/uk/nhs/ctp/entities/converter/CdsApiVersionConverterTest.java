package uk.nhs.ctp.entities.converter;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.enums.CdsApiVersion.Converter;

public class CdsApiVersionConverterTest {

  public Converter converter;

  @Rule
  public ExpectedException expected = ExpectedException.none();

  @Before
  public void setup() {
    converter = new Converter();
  }

  @Test
  public void convertToDatabaseColumn_whenNull() {
    String returned = converter.convertToDatabaseColumn(null);
    assertThat(returned, nullValue());
  }

  @Test
  public void convertToDatabaseColumn_withValue() {
    String returned = converter.convertToDatabaseColumn(CdsApiVersion.TWO);
    assertThat(returned, is("2.0"));
  }

  @Test
  public void convertToEntity_whenNull() {
    CdsApiVersion supportedVersion = converter.convertToEntityAttribute(null);
    assertThat(supportedVersion, nullValue());
  }

  @Test
  public void convertToEntity_unsupportedVersion() {
    expected.expect(IllegalArgumentException.class);
    converter.convertToEntityAttribute("not a version");
  }

  @Test
  public void convertToEntity_supportedVersion() {
    CdsApiVersion returned = converter.convertToEntityAttribute("1.1");
    assertThat(returned, is(CdsApiVersion.ONE_ONE));
  }

}