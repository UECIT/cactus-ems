package uk.nhs.ctp.testhelper.fixtures;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticSearchFixtures {

  @SneakyThrows
  public static List<SearchHit> encounterSearchHits(ClassLoader classLoader) {
    var auditFile = classLoader.getResource("stubbyEncounterAudit.json");
    var auditJson = IOUtils.toString(Objects.requireNonNull(auditFile), StandardCharsets.UTF_8);
    return List.of(buildSearchHit(auditJson));
  }

  @SneakyThrows
  public static List<SearchHit> serviceDefinitionSearchHits(ClassLoader classLoader) {
    var auditFile = classLoader.getResource("stubbyServiceSearchAudit.json");
    var auditJson = IOUtils.toString(Objects.requireNonNull(auditFile), StandardCharsets.UTF_8);
    return List.of(buildSearchHit(auditJson));
  }

  @SneakyThrows
  public static SearchHit buildSearchHit(String auditJson) {
    var encoder = StandardCharsets.UTF_8.newEncoder();
    var byteBuffer = encoder.encode(CharBuffer.wrap(auditJson));
    var bytesReference = BytesReference.fromByteBuffers(new ByteBuffer[]{ byteBuffer });
    return SearchHit.createFromMap(Map.of("_source", bytesReference));
  }

}
