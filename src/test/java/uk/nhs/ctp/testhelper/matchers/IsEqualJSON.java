package uk.nhs.ctp.testhelper.matchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * A Matcher for comparing JSON.
 * See https://www.javacodegeeks.com/2018/03/junit-hamcrest-matcher-for-json.html for source.
 * Example usage:
 * <pre>
 * assertThat(new String[] {"foo", "bar"}, equalToJSON("[\"foo\", \"bar\"]"));
 * assertThat(new String[] {"foo", "bar"}, equalToJSONInFile("/tmp/foo.json"));
 * </pre>
 */
@RequiredArgsConstructor
public class IsEqualJSON extends DiagnosingMatcher<Object> {

  private final String expectedJSON;
  private final JSONCompareMode jsonCompareMode;

  public IsEqualJSON(final String expectedJSON) {
    this(expectedJSON, JSONCompareMode.LENIENT);
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText(expectedJSON);
  }

  @Override
  protected boolean matches(final Object actual,
      final Description mismatchDescription) {
    final String actualJSON = toJSONString(actual);
    final JSONCompareResult result = JSONCompare.compareJSON(expectedJSON,
        actualJSON,
        jsonCompareMode);
    if (!result.passed()) {
      mismatchDescription.appendText(result.getMessage());
    }
    return result.passed();
  }

  @SneakyThrows
  private static String toJSONString(final Object o) {
      return o instanceof String ?
          (String) o : new ObjectMapper().writeValueAsString(o);
  }

  @SneakyThrows
  private static String getFileContents(final Path path) {
      return Files.readString(path);
  }

  @Factory
  public static IsEqualJSON equalToJSON(final String expectedJSON) {
    return new IsEqualJSON(expectedJSON);
  }

  @Factory
  public static IsEqualJSON equalToJSONInFile(final Path expectedPath) {
    return equalToJSON(getFileContents(expectedPath));
  }

  @Factory
  public static IsEqualJSON equalToJSONInFile(final String expectedFileName) {
    return equalToJSONInFile(Paths.get(expectedFileName));
  }
}