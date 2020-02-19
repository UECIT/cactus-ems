package uk.nhs.ctp.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Builder and utility class for working with the common application log context fields.
 */
@Value
@Builder
@Slf4j
public class LogContext {

  public static final String EVALUATE = "evaluate";

  String task, encounter, request, cds;

  /**
   * Apply the values in this Context to the MDC while executing the provided task
   */
  public <T> T wrap(Callable<T> callable) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    MapType objectType = objectMapper.getTypeFactory()
        .constructMapType(HashMap.class, String.class, String.class);
    Map<String, String> fields = objectMapper.convertValue(this, objectType);
    fields.forEach(MDC::put);
    Instant start = Instant.now();
    log.info("START: [{}]", task);
    try {
      return callable.call();
    } catch (Exception e) {
      log.error("Uncaught exception in [{}]", task, e);
      throw e;
    } finally {
      log.info("FINISH: [{}] took {} ms", task,
          Duration.between(start, Instant.now()).toMillis());
      fields.keySet().forEach(MDC::remove);
    }
  }

}
