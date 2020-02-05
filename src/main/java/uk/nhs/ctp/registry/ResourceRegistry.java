package uk.nhs.ctp.registry;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@RequiredArgsConstructor
public class ResourceRegistry<T> implements Registry<T> {

  private final ObjectMapper mapper;
  private final Class<T> type;

  public List<T> getAll() {

    var resolver = new PathMatchingResourcePatternResolver();
    var locationPattern = '/' + type.getSimpleName().toLowerCase() + "/*";

    try {
      var resources = resolver.getResources(locationPattern);
      return Arrays.stream(resources)
          .map(readValue(mapper))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw notFound(e);
    }

  }

  private Function<Resource, T> readValue(ObjectMapper mapper) {
    return resource -> {
      try {
        return mapper.readValue(resource.getInputStream(), type);
      } catch (IOException e) {
        throw notFound(e);
      }
    };
  }

  private ResourceNotFoundException notFound(IOException e) {
    return new ResourceNotFoundException("Resources not found: " + e.getMessage());
  }
}
