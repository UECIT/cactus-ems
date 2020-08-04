package uk.nhs.ctp.tkwvalidation;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.fixtures.AuditSelectorFixtures.mixedMethodAudits;

import java.time.Instant;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.audit.model.AuditEntry;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

@RunWith(MockitoJUnitRunner.class)
public class AuditSelectorTest {
  private static final Instant CREATED_AT_1 = Instant.parse("2019-06-05T09:12:20Z");
  private static final Instant CREATED_AT_2 = Instant.parse("2020-07-06T10:23:31Z");

  @Mock
  private FhirMessageAuditTransformer auditTransformer;

  @InjectMocks
  private AuditSelector auditSelector;

  @Test
  public void selectAudits_withServiceSearchAudits_shouldSelectServiceSearchPaths() {
    var entry = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("validResponseBody")
        .responseHeaders("content-type: [application/fhir+json]")
        .requestUrl("http://valid.com/request/url")
        .dateOfEntry(CREATED_AT_2)
        .build();

    var audit = AuditSession.builder()
        .entry(entry)
        .requestMethod("GET")
        .requestUrl("http://valid.com/request/base")
        .responseBody("validResponseBody2")
        .responseHeaders("content-type: [application/fhir+json]")
        .additionalProperty("interactionId", "6")
        .createdDate(CREATED_AT_1)
        .build();

    var audits = singletonList(audit);

    var auditMessage = FhirMessageAudit.builder().filePath("1").moment(CREATED_AT_1).build();
    var entryMessage = FhirMessageAudit.builder().filePath("2").moment(CREATED_AT_2).build();
    when(auditTransformer.from(audit, "service_search6", false))
        .thenReturn(auditMessage);
    when(auditTransformer.from(entry, "service_search6", false))
        .thenReturn(entryMessage);

    var messageAudits = auditSelector.selectAudits(audits, OperationType.SERVICE_SEARCH);

    assertThat(messageAudits, contains(auditMessage, entryMessage));
  }

  @Test
  public void selectAudits_withEncounterAudits_shouldSelectEncounterPaths() {
    var getEntry = AuditEntry.builder()
        .requestMethod("GET")
        .responseHeaders("content-type: [application/fhir+json]")
        .responseBody("validResponseBody1")
        .requestUrl("http://valid.com/request/url1")
        .dateOfEntry(CREATED_AT_1)
        .build();
    var postEntry = AuditEntry.builder()
        .requestMethod("POST")
        .requestHeaders("content-type: [application/fhir+json]")
        .responseHeaders("content-type: [application/fhir+xml]")
        .requestBody("validRequestBody2")
        .responseBody("validResponseBody2")
        .requestUrl("http://valid.com/request/url2")
        .dateOfEntry(CREATED_AT_2)
        .build();

    var audit = AuditSession.builder()
        .entry(getEntry)
        .entry(postEntry)
        .additionalProperty("interactionId", "6")
        .requestMethod("GET")
        .responseHeaders("content-type: [application/fhir+json]")
        .requestUrl("http://valid.com/request/base")
        .responseBody("validResponseBody3")
        .createdDate(CREATED_AT_1)
        .build();

    var audits = singletonList(audit);

    var auditMessage = FhirMessageAudit.builder().filePath("1").moment(CREATED_AT_1).build();
    var getEntryMessage = FhirMessageAudit.builder().filePath("2").moment(CREATED_AT_1).build();
    var postEntryMessage = FhirMessageAudit.builder().filePath("3").moment(CREATED_AT_2).build();
    when(auditTransformer.from(audit, "encounter6", false))
        .thenReturn(auditMessage);
    when(auditTransformer.from(getEntry, "encounter6", false))
        .thenReturn(getEntryMessage);
    when(auditTransformer.from(postEntry, "encounter6", true))
        .thenReturn(postEntryMessage);

    var messageAudits = auditSelector.selectAudits(audits, OperationType.ENCOUNTER);

    assertThat(messageAudits, containsInAnyOrder(auditMessage, getEntryMessage, postEntryMessage));
  }

  @Test
  public void selectAudits_withEncounterAudits_shouldOnlySelectGetAndPostEntries()  {
    var getSession = FhirMessageAudit.builder().filePath("get").moment(CREATED_AT_1).build();
    var get_getEntry = FhirMessageAudit.builder().filePath("get_get").moment(CREATED_AT_1).build();
    var get_postEntry = FhirMessageAudit.builder().filePath("get_post").moment(CREATED_AT_1).build();
    var postSession = FhirMessageAudit.builder().filePath("post").moment(CREATED_AT_1).build();
    var post_getEntry = FhirMessageAudit.builder().filePath("post_get").moment(CREATED_AT_1).build();
    var post_postEntry = FhirMessageAudit.builder().filePath("post_post").moment(CREATED_AT_1).build();
    var put_getEntry = FhirMessageAudit.builder().filePath("put_get").moment(CREATED_AT_1).build();
    var put_postEntry = FhirMessageAudit.builder().filePath("put_post").moment(CREATED_AT_1).build();
    when(auditTransformer.from(argThat(isSessionFor("http://get")), eq("encounter_get"), eq(false)))
        .thenReturn(getSession);
    when(auditTransformer.from(argThat(isEntryFor("http://get/inside/get")), eq("encounter_get"), eq(false)))
        .thenReturn(get_getEntry);
    when(auditTransformer.from(argThat(isEntryFor("http://post/inside/get")), eq("encounter_get"), eq(true)))
        .thenReturn(get_postEntry);
    when(auditTransformer.from(argThat(isSessionFor("http://post")), eq("encounter_post"), eq(true)))
        .thenReturn(postSession);
    when(auditTransformer.from(argThat(isEntryFor("http://get/inside/post")), eq("encounter_post"), eq(false)))
        .thenReturn(post_getEntry);
    when(auditTransformer.from(argThat(isEntryFor("http://post/inside/post")), eq("encounter_post"), eq(true)))
        .thenReturn(post_postEntry);
    when(auditTransformer.from(argThat(isEntryFor("http://get/inside/put")), eq("encounter_put"), eq(false)))
        .thenReturn(put_getEntry);
    when(auditTransformer.from(argThat(isEntryFor("http://post/inside/put")), eq("encounter_put"), eq(true)))
        .thenReturn(put_postEntry);

    var messageAudits = auditSelector.selectAudits(
        mixedMethodAudits(CREATED_AT_1),
        OperationType.ENCOUNTER);

    assertThat(messageAudits, containsInAnyOrder(
        getSession,
        get_getEntry,
        get_postEntry,
        postSession,
        post_getEntry,
        post_postEntry,
        put_getEntry,
        put_postEntry));
  }

  @Test
  public void selectAudits_withEncounterAudits_shouldSelectEntriesInOrder() {
    var date1 = CREATED_AT_1.plus(2, SECONDS);
    var date2 = date1.plus(2, SECONDS);
    var date3 = date2.plus(2, SECONDS);
    var date4 = date3.plus(2, SECONDS);

    var earlierEntry = AuditEntry.builder()
        .requestMethod("GET")
        .requestUrl("http://earlier/entry")
        .responseBody("earlierEntryBody")
        .responseHeaders("content-type: [application/fhir+json]")
        .dateOfEntry(date2)
        .build();
    var laterEntry = AuditEntry.builder()
        .requestMethod("GET")
        .requestUrl("http://later/entry")
        .responseBody("laterEntryBody")
        .responseHeaders("content-type: [application/fhir+json]")
        .dateOfEntry(date3)
        .build();
    var earlierAudit = AuditSession.builder()
        .requestMethod("GET")
        .requestUrl("http://earlier/session")
        .responseBody("earlierSessionBody")
        .responseHeaders("content-type: [application/fhir+json]")
        .createdDate(date1)
        .entry(laterEntry)
        .entry(earlierEntry)
        .additionalProperty("interactionId", "_earlier")
        .build();
    var laterAudit = AuditSession.builder()
        .requestMethod("GET")
        .requestUrl("http://later/session")
        .responseBody("laterSessionBody")
        .responseHeaders("content-type: [application/fhir+json]")
        .createdDate(date4)
        .additionalProperty("interactionId", "_later")
        .build();

    var audits = List.of(laterAudit, earlierAudit);

    var earlierEntryMessage = FhirMessageAudit.builder().moment(date2).build();
    var laterEntryMessage = FhirMessageAudit.builder().moment(date3).build();
    var earlierAuditMessage = FhirMessageAudit.builder().moment(date1).build();
    var laterAuditMessage = FhirMessageAudit.builder().moment(date4).build();
    when(auditTransformer.from(earlierAudit, "encounter_earlier", false))
        .thenReturn(earlierAuditMessage);
    when(auditTransformer.from(earlierEntry, "encounter_earlier", false))
        .thenReturn(earlierEntryMessage);
    when(auditTransformer.from(laterEntry, "encounter_earlier", false))
        .thenReturn(laterEntryMessage);
    when(auditTransformer.from(laterAudit, "encounter_later", false))
        .thenReturn(laterAuditMessage);

    var messageAudits = auditSelector.selectAudits(audits, OperationType.ENCOUNTER);

    assertThat(
        messageAudits,
        contains(earlierAuditMessage, earlierEntryMessage, laterEntryMessage, laterAuditMessage));
  }

  private static Matcher<AuditSession> isSessionFor(String url) {
    return new FunctionMatcher<>(
        s -> url.equals(s.getRequestUrl()),
        "matches AuditSession with url " + url);
  }

  private static Matcher<AuditEntry> isEntryFor(String url) {
    return new FunctionMatcher<>(
        s -> url.equals(s.getRequestUrl()),
        "matches AuditEntry with url " + url);
  }
}