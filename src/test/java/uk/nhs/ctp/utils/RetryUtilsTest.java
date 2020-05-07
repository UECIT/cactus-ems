package uk.nhs.ctp.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RetryUtilsTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldRetryOnFail() {
    AtomicInteger count = new AtomicInteger(0);

    exception.expect(FhirClientConnectionException.class);

    try {
      RetryUtils.retry(() -> {
        count.incrementAndGet();
        throw new FhirClientConnectionException(new ConnectException());
      });
    } finally {
      assertThat(count.get(), is(4)); //3 retires executes 4 times
    }
  }

  @Test
  public void shouldPassFirstTime() {

  }

  @Test
  public void shouldPassAfterTwoFailsOneSuccess() {

  }

}