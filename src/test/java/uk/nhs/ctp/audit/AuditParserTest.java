package uk.nhs.ctp.audit;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.junit.Test;

public class AuditParserTest {

  private final AuditParser auditParser = new AuditParser();

  @Test(expected = NullPointerException.class)
  public void getHeadersFrom_withNull_shouldFail() {
    //noinspection ConstantConditions
    auditParser.getHeadersFrom(null);
  }

  @Test
  public void getHeadersFrom_withEmptyString_shouldReturnEmpty() {
    var headers = auditParser.getHeadersFrom("");

    assertThat(headers.entrySet(), empty());
  }

  @Test
  public void getHeadersFrom_withInvalidHeaders_shouldReturnEmpty() {
    var headersString = "header1: [something]:somethingElse"
        + "\nheader2";

    var headers = auditParser.getHeadersFrom(headersString);

    assertThat(headers.entrySet(), empty());
  }

  @Test
  public void getHeadersFrom_shouldReturnHeaders() {
    var headersString = "header1: [value1]"
        + "\nheader2:[value2.1,value2.2]";

    var headers = auditParser.getHeadersFrom(headersString);

    assertThat(headers, allOf(
        hasEntry(is("header1"), Matchers.contains("value1")),
        hasEntry(is("header2"), Matchers.contains("value2.1", "value2.2"))
    ));
  }

  @Test(expected = NullPointerException.class)
  public void getHeaderValueFrom_withNull_shouldFail() {
    auditParser.getHeaderValueFrom(null);
  }

  @Test
  public void getHeaderValueFrom_withEmpty_shouldReturnEmpty() {
    var values = auditParser.getHeaderValueFrom("");

    assertThat(values, empty());
  }

  @Test
  public void getHeaderValueFrom_shouldReturnValues() {
    var headerValueString = "[value1,value2, value3]";

    var values = auditParser.getHeaderValueFrom(headerValueString);

    assertThat(values, contains("value1", "value2", "value3"));
  }
}