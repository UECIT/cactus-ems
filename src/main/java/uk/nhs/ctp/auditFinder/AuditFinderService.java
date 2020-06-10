package uk.nhs.ctp.auditFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditSession;

@Service
@RequiredArgsConstructor
public class AuditFinderService {

  private static final int MAX_RETURNED_AUDITS = 100;

  private static final String EMS_NAME = "ems.cactus-staging";

  private static final String OWNER_FIELD = "@owner";
  private static final String TIMESTAMP_FIELD = "@timestamp";
  private static final String SUPPLIER_ID_FIELD = "additionalProperties.supplierId";
  private static final String CASE_ID_FIELD = "additionalProperties.caseId";

  private final ElasticSearchClient esClient;
  private final TokenAuthenticationService authenticationService;
  private final ObjectMapper mapper;

  public List<AuditSession> findAll(Long caseId) throws IOException {
    // TODO CDSCT-164: require non-empty ES client
    if (ObjectUtils.isEmpty(esClient)) {
      return Collections.emptyList();
    }

    var supplierId = authenticationService.requireSupplierId();

    var query = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(OWNER_FIELD, EMS_NAME))
        .must(QueryBuilders.termQuery(SUPPLIER_ID_FIELD, supplierId))
        .must(QueryBuilders.termQuery(CASE_ID_FIELD, caseId.toString()));

    var source = new SearchSourceBuilder()
        .query(query)
        .size(MAX_RETURNED_AUDITS)
        .sort(new FieldSortBuilder(TIMESTAMP_FIELD).order(SortOrder.ASC));

    return esClient.search(supplierId + "-audit", source)
        .stream()
        .map(SearchHit::getSourceAsString)
        .map(this::asAudit)
        .collect(Collectors.toUnmodifiableList());
  }

  @SneakyThrows
  private AuditSession asAudit(String source) {
    return mapper.readValue(source, AuditSession.class);
  }
}
