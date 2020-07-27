package uk.nhs.ctp.auditFinder;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.audit.model.AuditEntry;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cactus.common.elasticsearch.ElasticSearchClient;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.testhelper.fixtures.ElasticSearchFixtures;

@RunWith(MockitoJUnitRunner.class)
public class AuditFinderTest {

  @Mock
  private ElasticSearchClient elasticSearchClient;

  @Mock
  private TokenAuthenticationService authService;

  @Mock
  private ObjectMapper objectMapper;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private AuditFinder auditFinder;

  @Test
  public void findAllEncountersByOperationTypeAndInteractionId_buildsRequest() throws IOException {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(emptyList());

    auditFinder.findAllEncountersByOperationTypeAndInteractionId(OperationType.SERVICE_SEARCH,"interactionId");

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0), hasToString(equalToJSON(
        "{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
            + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
            + " { term : { additionalProperties.operation : { value : service_search } } },"
            + " { term : { additionalProperties.interactionId.keyword : { value : interactionId } } }"
            + "] } }")));
  }

  @Test
  public void findAllEncountersByOperationTypeAndInteractionId_returnsAudits() throws IOException {
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
        .map(ElasticSearchFixtures::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var audits = auditFinder.findAllEncountersByOperationTypeAndInteractionId(
        OperationType.ENCOUNTER,
        "caseId");

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findAllEmsEncountersByCaseId_buildsRequest() throws IOException {
    ReflectionTestUtils.setField(auditFinder, "emsName", "validEmsName");

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(emptyList());

    auditFinder.findAllEmsEncountersByCaseId("interactionId");

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0), hasToString(equalToJSON(
        "{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
        + " { term : { @owner.keyword : { value : validEmsName } } },"
        + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
        + " { term : { additionalProperties.interactionId.keyword : { value : interactionId } } }"
        + "] } }")));
  }

  @Test
  public void findAllEmsEncountersByCaseId_returnsAudits() throws IOException {
    ReflectionTestUtils.setField(auditFinder, "emsName", "validEmsName");

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
        .map(ElasticSearchFixtures::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var audits = auditFinder.findAllEmsEncountersByCaseId("caseId");

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findAllEmsEncountersByCaseId_withFailedParsing_shouldFail() throws IOException {
    ReflectionTestUtils.setField(auditFinder, "emsName", "validEmsName");

    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(
        argThat(is("test-supplier-audit")),
        any(SearchSourceBuilder.class)))
        .thenReturn(singletonList(ElasticSearchFixtures.buildSearchHit(auditSessionJson)));
    when(objectMapper.readValue(anyString(), eq(AuditSession.class)))
      .thenThrow(new JsonParseException(null, "Failed to parse"));

    expectedException.expect(JsonParseException.class);
    auditFinder.findAllEmsEncountersByCaseId("caseId");
  }

  @Test
  public void findInteractions_buildsRequest() throws Exception {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(emptyList());

    auditFinder.findInteractions();

    var searchSourceCaptor = ArgumentCaptor.forClass(SearchSourceBuilder.class);
    verify(elasticSearchClient).search(eq("test-supplier-audit"), searchSourceCaptor.capture());

    var searchSource = searchSourceCaptor.getValue();

    assertThat(searchSource.sorts(), hasSize(1));
    assertThat(searchSource.sorts().get(0),
        hasToString(equalToJSON("{ @timestamp : {order : asc } }")));
    assertThat(searchSource.query(), hasToString(equalToJSON(
        "{ bool : { must : ["
            + " { term : { additionalProperties.supplierId : { value : test-supplier } } },"
            + " { exists : { field : additionalProperties.interactionId.keyword } },"
            + " { exists : { field : additionalProperties.operation } }"
            + "] } }")));
  }

  @Test
  public void findInteractions_returnsAudits() throws IOException {
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
        .map(ElasticSearchFixtures::buildSearchHit)
        .collect(Collectors.toUnmodifiableList());

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(searchHits);
    when(objectMapper.readValue(auditSessionJson, AuditSession.class))
        .thenReturn(auditSession);
    when(objectMapper.readValue(auditSessionWithEntryJson, AuditSession.class))
        .thenReturn(auditSessionWithEntry);

    var interactions = auditFinder.findInteractions();

    assertThat(interactions, contains(auditSession, auditSessionWithEntry));
  }
}