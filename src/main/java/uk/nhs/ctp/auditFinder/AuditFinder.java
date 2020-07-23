package uk.nhs.ctp.auditFinder;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.audit.model.AuditProperties;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cactus.common.elasticsearch.ElasticSearchClient;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.auditFinder.model.AuditInteraction;

@Service
@RequiredArgsConstructor
public class AuditFinder {

  private static final int MAX_RETURNED_AUDITS = 100;

  private static final String EMS_NAME = "ems.cactus-staging";

  private static final String OWNER_FIELD = "@owner.keyword";
  private static final String TIMESTAMP_FIELD = "@timestamp";
  private static final String SUPPLIER_ID_FIELD = "additionalProperties.supplierId";
  private static final String INTERACTION_ID_FIELD = "additionalProperties.interactionId";
  private static final String OPERATION_FIELD = "additionalProperties.operation";

  private static final String AUDIT_SUFFIX = "-audit";

  private final ElasticSearchClient esClient;
  private final TokenAuthenticationService authenticationService;
  private final ObjectMapper mapper;

  public List<AuditSession> findAllEncountersByOperationTypeAndInteractionId(
      OperationType operationType, String interactionId) {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(OPERATION_FIELD, operationType.getName()))
        .must(QueryBuilders.termQuery(INTERACTION_ID_FIELD, interactionId));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
  }

  public List<AuditSession> findAllEmsEncountersByCaseId(String caseId) {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(OWNER_FIELD, EMS_NAME))
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(INTERACTION_ID_FIELD, caseId));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
  }

  public List<AuditSession> findInteractions() {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.existsQuery(INTERACTION_ID_FIELD))
        .must(QueryBuilders.existsQuery(OPERATION_FIELD));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());

  }

  public List<AuditInteraction> groupInteractions(List<AuditSession> auditSessions) {
    return auditSessions.stream()
        .collect(groupingBy(this::getKey, minBy(comparing(AuditSession::getCreatedDate))))
        .entrySet()
        .stream()
        .map(pair -> new AuditInteraction(
            OperationType.fromName(pair.getKey().getFirst()),
            pair.getKey().getSecond(),
            pair.getValue().map(AuditSession::getCreatedDate).orElseThrow()))
        .collect(toUnmodifiableList());
  }

  private Pair<String, String> getKey(AuditSession interactionAudit) {
    var properties = interactionAudit.getAdditionalProperties();
    return Pair.of(properties.get(AuditProperties.OPERATION_TYPE), properties.get(INTERACTION_ID));
  }

  @SneakyThrows
  private Stream<AuditSession> search(String supplierId, SearchSourceBuilder source) {
    return esClient.search(supplierId + AUDIT_SUFFIX, source)
        .stream()
        .map(SearchHit::getSourceAsString)
        .map(this::asAudit);
  }

  private SearchSourceBuilder buildSearchSource(BoolQueryBuilder query) {
    return new SearchSourceBuilder()
        .query(query)
        .size(MAX_RETURNED_AUDITS)
        .sort(new FieldSortBuilder(TIMESTAMP_FIELD).order(SortOrder.ASC));
  }
  @SneakyThrows
  private AuditSession asAudit(String source) {
    return mapper.readValue(source, AuditSession.class);
  }
}
