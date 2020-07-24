package uk.nhs.ctp.auditFinder;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.audit.model.AuditEntry;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cactus.common.elasticsearch.ElasticSearchClient;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.auditFinder.model.AuditInteraction;
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

    auditFinder.findAllEncountersByOperationTypeAndInteractionId(OperationType.SERVICE_SEARCH,"76");

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
            + " { term : { additionalProperties.interactionId : { value : \"76\" } } }"
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
        "76");

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findAllEmsEncountersByCaseId_buildsRequest() throws IOException {
    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(eq("test-supplier-audit"), any(SearchSourceBuilder.class)))
        .thenReturn(emptyList());

    auditFinder.findAllEmsEncountersByCaseId("76");

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
        + " { term : { additionalProperties.interactionId : { value : \"76\" } } }"
        + "] } }")));
  }

  @Test
  public void findAllEmsEncountersByCaseId_returnsAudits() throws IOException {
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

    var audits = auditFinder.findAllEmsEncountersByCaseId("76");

    assertThat(audits, contains(auditSession, auditSessionWithEntry));
  }

  @Test
  public void findAllEmsEncountersByCaseId_withFailedParsing_shouldFail() throws IOException {
    var auditSessionJson = "{ \"requestUrl\": \"/test-url\" }";

    when(authService.requireSupplierId()).thenReturn("test-supplier");
    when(elasticSearchClient.search(
        argThat(is("test-supplier-audit")),
        any(SearchSourceBuilder.class)))
        .thenReturn(singletonList(ElasticSearchFixtures.buildSearchHit(auditSessionJson)));
    when(objectMapper.readValue(anyString(), eq(AuditSession.class)))
      .thenThrow(new JsonParseException(null, "Failed to parse"));

    expectedException.expect(JsonParseException.class);
    auditFinder.findAllEmsEncountersByCaseId("76");
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
            + " { exists : { field : additionalProperties.interactionId } },"
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

  @Test
  public void groupInteractions_withEmptyList_returnsEmpty() {
    assertThat(auditFinder.groupInteractions(emptyList()), empty());
  }

  @Test
  public void groupInteractions_withInvalidOperationType_fails() {
    final var CREATION_DATE = Instant.parse("2019-08-22T12:11:54Z");

    var encounter1Audit1 = AuditSession.builder()
        .createdDate(CREATION_DATE)
        .additionalProperty("operation", "invalid_operation_type")
        .additionalProperty("interactionId", "1")
        .build();

    var auditSessions = List.of(encounter1Audit1);

    expectedException.expect(IllegalArgumentException.class);
    auditFinder.groupInteractions(auditSessions);
  }

  @Test
  public void groupInteractions_returnsGroups() {
    final var CREATION_DATE_1 = Instant.parse("2019-08-22T12:11:54Z");
    final var CREATION_DATE_2 = Instant.parse("2020-07-23T13:12:55Z");

    var encounter1Audit1 = AuditSession.builder()
        .createdDate(CREATION_DATE_1)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var encounter1Audit2 = AuditSession.builder()
        .createdDate(CREATION_DATE_2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "1")
        .build();
    var encounter2Audit = AuditSession.builder()
        .createdDate(CREATION_DATE_2)
        .additionalProperty("operation", "encounter")
        .additionalProperty("interactionId", "2")
        .build();
    var serviceSearch1Audit = AuditSession.builder()
        .createdDate(CREATION_DATE_1)
        .additionalProperty("operation", "service_search")
        .additionalProperty("interactionId", "1")
        .build();

    var auditSessions = List.of(
        encounter1Audit1,
        encounter1Audit2,
        encounter2Audit,
        serviceSearch1Audit);

    var interactionGroups = auditFinder.groupInteractions(auditSessions);

    var expectedInteractionGroups = new Object[] {
        new AuditInteraction(OperationType.ENCOUNTER, "1", CREATION_DATE_1.toString()),
        new AuditInteraction(OperationType.ENCOUNTER, "2", CREATION_DATE_2.toString()),
        new AuditInteraction(OperationType.SERVICE_SEARCH, "1", CREATION_DATE_1.toString())
    };

    assertThat(interactionGroups, containsInAnyOrder(expectedInteractionGroups));
  }

  @Test
  public void objectMapper_readValue_canDeserialiseAudit() throws IOException {
    var auditFile = getClass().getClassLoader().getResource("exampleAudit.json");
    var auditJson = IOUtils.toString(Objects.requireNonNull(auditFile), StandardCharsets.UTF_8);

    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    var audit = mapper.readValue(auditJson, AuditSession.class);

    assertThat(audit.getCreatedDate(), is(Instant.parse("2020-06-11T16:36:52.587218Z")));
    assertThat(audit.getRequestUrl(), is("/case/"));
    assertThat(audit.getResponseStatus(), is("200"));
    assertThat(audit.getAdditionalProperties().get("interactionId"), is("57"));
    assertThat(audit.getEntries(), hasSize(28));
  }
}