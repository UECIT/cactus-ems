package uk.nhs.ctp.auditFinder;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.IsEqualJSON.equalToJSON;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.finder.AWSAuditFinder;

@RunWith(MockitoJUnitRunner.class)
public class AWSAuditFinderTest {

  @Mock
  private ElasticSearchClient elasticSearchClient;

  @Mock
  private TokenAuthenticationService authService;

  @Mock
  private ObjectMapper objectMapper;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private AWSAuditFinder auditFinder;

  @Test
  public void findAll_withCaseIdAndSupplierId_buildsRequest() throws IOException {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(Collections.emptyList());

    auditFinder.findAll(76L);

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0), hasToString(equalToJSON(
        "{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
        + " { term : { @owner.keyword : { value : ems.cactus-staging } } },"
        + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
        + " { term : { additionalProperties.caseId : { value : \"76\" } } }"
        + "] } }")));
  }

  @Test
  public void findAll_withCaseIdAndSupplierId_returnsAudits() throws IOException {
    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";
    var auditSession = AuditSession.builder().requestUrl("/test-url").build();
    var auditSessionWithEntryJson = "{"
        + " \"requestUrl\": \"/test-url-2\","
        + " \"entries\": ["
        + " { \"requestUrl\": \"/test-url-3\" }"
        + "] }";
    var auditSessionWithEntry = AuditSession.builder()
        .requestUrl("/test-url-2")
        .entry(AuditEntry.builder().requestUrl("/test-url-3").build())
        .build();

    var searchHits = Stream.of(auditSessionJson, auditSessionWithEntryJson)
        .map(this::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var audits = auditFinder.findAll(76L);

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findAll_withFailedParsing_shouldFail() throws IOException {
    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(
        argThat(is("test-supplier-audit")),
        any(SearchSourceBuilder.class)))
        .thenReturn(Collections.singletonList(buildSearchHit(auditSessionJson)));
    when(objectMapper.readValue(anyString(), eq(AuditSession.class)))
      .thenThrow(new JsonParseException(null, "Failed to parse"));

    expectedException.expect(JsonParseException.class);
    auditFinder.findAll(76L);
  }

  @Test
  public void findEncounters_buildsRequest() throws Exception {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(Collections.emptyList());

    auditFinder.findAllEncounters();

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0),
        hasToString(equalToJSON("{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
            + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
            + " { exists : { field : additionalProperties.caseId } }"
            + "] } }")));
  }

  @Test
  public void findEncounter_returnsAudits() throws IOException {
    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";
    var auditSession = AuditSession.builder().requestUrl("/test-url").build();
    var auditSessionWithEntryJson = "{"
        + " \"requestUrl\": \"/test-url-2\","
        + " \"entries\": ["
        + " { \"requestUrl\": \"/test-url-3\" }"
        + "] }";
    var auditSessionWithEntry = AuditSession.builder()
        .requestUrl("/test-url-2")
        .entry(AuditEntry.builder().requestUrl("/test-url-3").build())
        .build();

    var searchHits = Stream.of(auditSessionJson, auditSessionWithEntryJson)
        .map(this::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var audits = auditFinder.findAllEncounters();

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findServiceSearches_buildsRequest() throws Exception {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(Collections.emptyList());

    auditFinder.findAllServiceSearches();

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0),
        hasToString(equalToJSON("{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
            + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
            + " { term : { additionalProperties.operation : { value : service_search } } }"
            + "] } }")));
  }

  @Test
  public void findServiceSearches_returnsAudits() throws IOException {
    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";
    var auditSession = AuditSession.builder().requestUrl("/test-url").build();
    var auditSessionWithEntryJson = "{"
        + " \"requestUrl\": \"/test-url-2\","
        + " \"entries\": ["
        + " { \"requestUrl\": \"/test-url-3\" }"
        + "] }";
    var auditSessionWithEntry = AuditSession.builder()
        .requestUrl("/test-url-2")
        .entry(AuditEntry.builder().requestUrl("/test-url-3").build())
        .build();

    var searchHits = Stream.of(auditSessionJson, auditSessionWithEntryJson)
        .map(this::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var audits = auditFinder.findAllServiceSearches();

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void objectMapper_readValue_canDeserialiseAudit() throws IOException {
    var auditFile = getClass().getClassLoader().getResource("exampleAudit.json");
    var auditJson = IOUtils.toString(Objects.requireNonNull(auditFile), StandardCharsets.UTF_8);

    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    var audit = mapper.readValue(auditJson, AuditSession.class);

    assertThat(audit.getRequestUrl(), is("/case/"));
    assertThat(audit.getResponseStatus(), is("200"));
    assertThat(audit.getAdditionalProperties().get("caseId"), is("57"));
    assertThat(audit.getEntries(), hasSize(28));
  }

  @SneakyThrows
  private SearchHit buildSearchHit(String audit) {
    var encoder = StandardCharsets.UTF_8.newEncoder();
    var byteBuffer = encoder.encode(CharBuffer.wrap(audit));
    var bytesReference = BytesReference.fromByteBuffers(new ByteBuffer[]{ byteBuffer });
    return SearchHit.createFromMap(Map.of("_source", bytesReference));
  }
}