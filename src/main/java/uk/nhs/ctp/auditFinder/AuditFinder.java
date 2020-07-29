package uk.nhs.ctp.auditFinder;

import static java.util.stream.Collectors.toUnmodifiableList;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cactus.common.elasticsearch.ElasticSearchClient;
import uk.nhs.cactus.common.security.TokenAuthenticationService;

@Service
@RequiredArgsConstructor
public class AuditFinder {

  private static final int MAX_RETURNED_AUDITS = 300;

  private static final String OWNER_FIELD = "@owner.keyword";
  private static final String TIMESTAMP_FIELD = "@timestamp";
  private static final String SUPPLIER_ID_FIELD = "additionalProperties.supplierId";
  private static final String INTERACTION_ID_FIELD = "additionalProperties.interactionId.keyword";
  private static final String OPERATION_FIELD = "additionalProperties.operation";

  private static final String AUDIT_SUFFIX = "-audit";

  @Value("${service.name}")
  private String emsName;

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
        .must(QueryBuilders.termQuery(OWNER_FIELD, emsName))
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
        .sort(new FieldSortBuilder(TIMESTAMP_FIELD).order(SortOrder.DESC));
  }
  @SneakyThrows
  private AuditSession asAudit(String source) {
    return mapper.readValue(source, AuditSession.class);
  }
}
