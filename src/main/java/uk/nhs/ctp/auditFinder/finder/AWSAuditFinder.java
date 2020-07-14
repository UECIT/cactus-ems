package uk.nhs.ctp.auditFinder.finder;

import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.stream.Collectors.toUnmodifiableList;
import static uk.nhs.ctp.auditFinder.Constants.AUDIT_SUFFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditSession;
import uk.nhs.ctp.auditFinder.ElasticSearchClient;
import uk.nhs.ctp.auditFinder.model.OperationType;

@Service
@RequiredArgsConstructor
@Profile("!dev")
public class AWSAuditFinder implements AuditFinder {

  private static final int MAX_RETURNED_AUDITS = 100;

  private static final String EMS_NAME = "ems.cactus-staging";

  private static final String OWNER_FIELD = "@owner.keyword";
  private static final String TIMESTAMP_FIELD = "@timestamp";
  private static final String SUPPLIER_ID_FIELD = "additionalProperties.supplierId";
  private static final String CASE_ID_FIELD = "additionalProperties.caseId";
  private static final String OPERATION_FIELD = "additionalProperties.operation";
  private static final String REQUEST_ID_FIELD = "requestId.keyword";

  private final ElasticSearchClient esClient;
  private final TokenAuthenticationService authenticationService;
  private final ObjectMapper mapper;

  @Override
  public Optional<AuditSession> findByAuditId(String auditId) {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(REQUEST_ID_FIELD, auditId));

    var source = buildSingularSource(query);

    return search(supplierId, source).collect(toOptional());
  }

  @Override
  public List<AuditSession> findAllEncountersByCaseId(String caseId) {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(CASE_ID_FIELD, caseId));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
  }

  @Override
  public List<AuditSession> findAllEmsEncountersByCaseId(String caseId) {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(OWNER_FIELD, EMS_NAME))
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(CASE_ID_FIELD, caseId));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
  }

  @Override
  public List<AuditSession> findAllEncounters() {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.existsQuery(CASE_ID_FIELD));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
  }

  @Override
  public List<AuditSession> findAllServiceSearches() {
    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(OPERATION_FIELD, OperationType.SERVICE_SEARCH.getName()));

    var source = buildSearchSource(query);

    return search(supplierId, source).collect(toUnmodifiableList());
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

  private SearchSourceBuilder buildSingularSource(BoolQueryBuilder query) {
    return new SearchSourceBuilder()
        .query(query)
        .size(1);
  }

  @SneakyThrows
  private AuditSession asAudit(String source) {
    return mapper.readValue(source, AuditSession.class);
  }
}
