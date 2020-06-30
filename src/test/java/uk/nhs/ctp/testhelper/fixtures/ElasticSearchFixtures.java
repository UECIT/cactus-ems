package uk.nhs.ctp.testhelper.fixtures;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;

@UtilityClass
public class ElasticSearchFixtures {

  public SearchHit minimumSearchHit() {
    return buildSearchHit("{}");
  }

  @SneakyThrows
  public SearchHit buildSearchHit(String auditJson) {
    var encoder = StandardCharsets.UTF_8.newEncoder();
    var byteBuffer = encoder.encode(CharBuffer.wrap(auditJson));
    var bytesReference = BytesReference.fromByteBuffers(new ByteBuffer[]{ byteBuffer });
    return SearchHit.createFromMap(Map.of("_source", bytesReference));
  }

}
