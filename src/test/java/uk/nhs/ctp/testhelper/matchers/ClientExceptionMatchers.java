package uk.nhs.ctp.testhelper.matchers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientExceptionMatchers {

  public static Matcher<HttpClientErrorException> hasStatusCode(final HttpStatus status) {
    return new FunctionMatcher<>(
        e -> e.getStatusCode() == status,
        "has HTTP status code " + status);
  }

}
