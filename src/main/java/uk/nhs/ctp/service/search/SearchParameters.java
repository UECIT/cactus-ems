package uk.nhs.ctp.service.search;

import static java.util.Collections.singletonList;

import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder(toBuilder = true)
@Getter
public class SearchParameters {

  private static final String SP_QUERY = "_query";
  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_TYPE_CODE = "type-code";
  private static final String SP_CONTEXT_VALUE = "context-value";
  private static final String SP_CONTEXT_QUANTITY = "context-quantity";
  private static final String SP_JURISDICTION = "jurisdiction";

  private String query;
  private String jurisdiction;
  @Singular("type")
  private List<String> typeCode;
  private List<String> contextValueCode;
  private List<String> contextQuantityCode;

  public MultiValueMap<String, String> toMultiValueMap() {
    LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(SP_QUERY, query);
    if (CollectionUtils.isNotEmpty(typeCode)) {
      map.put(SP_TYPE_CODE, typeCode);
    }
    if (CollectionUtils.isNotEmpty(contextValueCode)) {
      map.put(SP_CONTEXT_VALUE, contextValueCode);
    }
    if (CollectionUtils.isNotEmpty(contextQuantityCode)) {
      map.put(SP_CONTEXT_QUANTITY, contextQuantityCode);
    }
    if (StringUtils.isNotEmpty(jurisdiction)) {
      map.put(SP_JURISDICTION, singletonList(jurisdiction));
    }
    return map;
  }

  public static class SearchParametersBuilder {
    public SearchParametersBuilder contextValue(String context, String code) {
      if (this.contextValueCode == null) {
        this.contextValueCode = new ArrayList<>();
      }

      this.contextValueCode.add(context + "$" + code);
      return this;
    }

    public SearchParametersBuilder contextQuantity(String context,
        ParamPrefixEnum prefix, int value) {
      if (this.contextQuantityCode == null) {
        this.contextQuantityCode = new ArrayList<>();
      }

      this.contextQuantityCode.add(context + "$" + prefix.getValue() + value);
      return this;
    }
  }

}
