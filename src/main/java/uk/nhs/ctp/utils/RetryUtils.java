package uk.nhs.ctp.utils;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import java.net.ConnectException;
import java.time.Duration;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.Timeout;
import net.jodah.failsafe.function.CheckedSupplier;

@UtilityClass
@Slf4j
public class RetryUtils {

  private final int MAX_RETRIES = 3;

  public <T> T retry(CheckedSupplier<T> runnable) {
    var retryPolicy = new RetryPolicy<>()
        .handle(ConnectException.class, FhirClientConnectionException.class)
        .withMaxRetries(MAX_RETRIES)
        .onRetry(e -> log.warn("Failure #{}: {}. Retrying.", e.getAttemptCount(), e.getLastFailure().getMessage()));
    return Failsafe.with(retryPolicy).get(runnable);
  }

}
