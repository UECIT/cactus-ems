package uk.nhs.ctp.audit;

import static java.util.function.Predicate.not;
import static org.apache.commons.lang3.StringUtils.strip;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class AuditParser {
  public Map<String, Collection<String>> getHeadersFrom(String headers) {
    return Stream.of(headers.split("\n"))
        .map(header -> header.split(":"))
        .filter(headerParts -> headerParts.length == 2)
        .collect(Collectors.toUnmodifiableMap(
            headerParts -> headerParts[0],
            headerParts -> getHeaderValueFrom(headerParts[1])
        ));
  }

  public Collection<String> getHeaderValueFrom(String headerValue) {
    return Stream.of(strip(headerValue, "[ ]").split(","))
        .map(String::strip)
        .filter(not(String::isEmpty))
        .collect(Collectors.toUnmodifiableList());
  }
}
