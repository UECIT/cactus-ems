package uk.nhs.ctp.tkwvalidation;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uk.nhs.ctp.testhelper.fixtures.AuditSelectorFixtures.mixedMethodAudits;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.hamcrest.Matcher;
import org.junit.Test;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.testhelper.matchers.FunctionMatcher;
import uk.nhs.ctp.tkwvalidation.model.FhirMessageAudit;

public class AuditSelectorTest {
  private static final Instant CREATED_AT_1 = Instant.parse("2019-06-05T09:12:20Z");
  private static final Instant CREATED_AT_2 = Instant.parse("2020-07-06T10:23:31Z");

  private final AuditSelector auditSelector = new AuditSelector();

  @Test
  public void selectAudits_withServiceSearchAudits_shouldSelectServiceSearchPaths() {
    var entry = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("validResponseBody")
        .requestUrl("http://valid.com/request/url")
        .dateOfEntry(CREATED_AT_2)
        .build();

    var audits = singletonList(AuditSession.builder()
        .entry(entry)
        .requestMethod("GET")
        .requestUrl("http://valid.com/request/base")
        .responseBody("validResponseBody2")
        .createdDate(CREATED_AT_1)
        .build());

    var messageAudits = auditSelector.selectAudits(audits, OperationType.SERVICE_SEARCH);

    assertThat(messageAudits, contains(
        isEntry(
            "service_search/valid.com/request/base",
            null,
            "validResponseBody2",
            CREATED_AT_1),
        isEntry(
          "service_search/valid.com/request/url",
          null,
          "validResponseBody",
          CREATED_AT_2)));
  }

  @Test
  public void selectAudits_withEncounterAudits_shouldSelectEncounterPaths() {
    var getEntry = AuditEntry.builder()
        .requestMethod("GET")
        .responseBody("validResponseBody1")
        .requestUrl("http://valid.com/request/url1")
        .dateOfEntry(CREATED_AT_1)
        .build();
    var postEntry = AuditEntry.builder()
        .requestMethod("POST")
        .requestBody("validRequestBody2")
        .responseBody("validResponseBody2")
        .requestUrl("http://valid.com/request/url2")
        .dateOfEntry(CREATED_AT_2)
        .build();

    var audits = singletonList(AuditSession.builder()
        .entry(getEntry)
        .entry(postEntry)
        .additionalProperty("caseId", "6")
        .requestMethod("GET")
        .requestUrl("http://valid.com/request/base")
        .responseBody("validResponseBody3")
        .createdDate(CREATED_AT_1)
        .build());

    var messageAudits = auditSelector.selectAudits(audits, OperationType.ENCOUNTER);

    assertThat(messageAudits, containsInAnyOrder(
        isEntry("encounter6/valid.com/request/url1",
            null, "validResponseBody1", CREATED_AT_1),
        isEntry("encounter6/valid.com/request/url2",
            "validRequestBody2", "validResponseBody2", CREATED_AT_2),
        isEntry("encounter6/valid.com/request/base",
            null, "validResponseBody3", CREATED_AT_1)));
  }

  @Test
  public void selectAudits_withEncounterAudits_shouldOnlySelectGetAndPostEntries()  {
    var messageAudits = auditSelector.selectAudits(mixedMethodAudits(CREATED_AT_1), OperationType.ENCOUNTER);

    assertThat(messageAudits, containsInAnyOrder(
        isEntry("encounter_get/get",
            null, "{ response get }", CREATED_AT_1),
        isEntry("encounter_get/get/inside/get",
            null, "{ response get inside get }", CREATED_AT_1),
        isEntry("encounter_get/post/inside/get",
            "{ request post inside get }", "{ response post inside get }", CREATED_AT_1),

        isEntry("encounter_post/post",
            "{ request post }", "{ response post }", CREATED_AT_1),
        isEntry("encounter_post/get/inside/post",
            null, "{ response get inside post }", CREATED_AT_1),
        isEntry("encounter_post/post/inside/post",
            "{ request post inside post }", "{ response post inside post }", CREATED_AT_1),

        isEntry("encounter_put/get/inside/put",
            null, "{ response get inside put }", CREATED_AT_1),
        isEntry("encounter_put/post/inside/put",
            "{ request post inside put }", "{ response post inside put }", CREATED_AT_1)
    ));
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
        .dateOfEntry(date2)
        .build();
    var laterEntry = AuditEntry.builder()
        .requestMethod("GET")
        .requestUrl("http://later/entry")
        .responseBody("laterEntryBody")
        .dateOfEntry(date3)
        .build();
    var earlierAudit = AuditSession.builder()
        .requestMethod("GET")
        .requestUrl("http://earlier/session")
        .responseBody("earlierSessionBody")
        .createdDate(date1)
        .entry(laterEntry)
        .entry(earlierEntry)
        .additionalProperty("caseId", "_earlier")
        .build();
    var laterAudit = AuditSession.builder()
        .requestMethod("GET")
        .requestUrl("http://later/session")
        .responseBody("laterSessionBody")
        .createdDate(date4)
        .additionalProperty("caseId", "_later")
        .build();

    var audits = List.of(laterAudit, earlierAudit);

    var messageAudits = auditSelector.selectAudits(audits, OperationType.ENCOUNTER);

    assertThat(messageAudits, contains(
        isEntry("encounter_earlier/earlier/session", null, "earlierSessionBody", date1),
        isEntry("encounter_earlier/earlier/entry", null, "earlierEntryBody", date2),
        isEntry("encounter_earlier/later/entry", null, "laterEntryBody", date3),
        isEntry("encounter_later/later/session", null, "laterSessionBody", date4)
    ));
  }

  private Matcher<FhirMessageAudit> isEntry(
      String path,
      String requestBody,
      String responseBody,
      Instant instant) {
    return new FunctionMatcher<>(messageAudit ->
        path.equals(messageAudit.getFilePath())
            && Objects.equals(requestBody, messageAudit.getRequestBody())
            && Objects.equals(responseBody, messageAudit.getResponseBody())
            && instant.equals(messageAudit.getMoment()),
        "is entry with path " + path);
  }
}