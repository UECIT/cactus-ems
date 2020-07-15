package uk.nhs.ctp.audit;

import static org.apache.commons.lang3.StringUtils.strip;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditParser {
  public static Map<String, Collection<String>> getHeadersFrom(String headers) {
    return Stream.of(headers.split("\n"))
        .map(header -> header.split(": "))
        .filter(headerParts -> headerParts.length == 2)
        .collect(Collectors.toUnmodifiableMap(
            headerParts -> headerParts[0],
            headerParts -> getHeaderValueFrom(headerParts[1])
        ));
  }

  public static Collection<String> getHeaderValueFrom(String headerValue) {
    return Stream.of(strip(headerValue, "[]").split(","))
        .collect(Collectors.toUnmodifiableList());
  }
}
