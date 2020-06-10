package uk.nhs.ctp.service.fhir;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.search.SearchParameters;
import uk.nhs.ctp.service.search.SearchParametersTransformer;

@RunWith(MockitoJUnitRunner.class)
public class SwitchTriggerResolverTest {

  private SwitchTriggerResolver switchTriggerResolver;

  @Mock
  private CdssService cdssService;

  @Mock
  private SearchParametersTransformer searchParametersTransformer;

  @Before
  public void setup() {
    this.switchTriggerResolver = new SwitchTriggerResolver(
        cdssService,
        searchParametersTransformer
    );
  }

  @Test
  public void transformIntoSearchParameters() {

    GuidanceResponse guidanceResponse = new GuidanceResponse();
    List<DataRequirement> dataRequirements = Collections.singletonList(new DataRequirement());
    guidanceResponse.setDataRequirement(dataRequirements);

    SettingsDTO settingsDTO = new SettingsDTO();
    settingsDTO.setSetting(new CodeDTO());

    SearchParameters searchParameters = SearchParameters.builder()
        .experimental("true")
        .status("active")
        .build();

    CdssSupplierDTO supplier = new CdssSupplierDTO();
    supplier.setId(4L);
    ServiceDefinitionDTO serviceDefinition = new ServiceDefinitionDTO();
    serviceDefinition.setServiceDefinitionId("theredirect");
    supplier.setServiceDefinitions(Collections.singletonList(serviceDefinition));
    when(searchParametersTransformer.transform(dataRequirements, settingsDTO, "3"))
        .thenReturn(searchParameters);
    when(cdssService.queryServiceDefinitions(searchParameters))
        .thenReturn(Collections
            .singletonList(supplier));

    String newServiceDef = switchTriggerResolver
        .getSwitchTrigger(guidanceResponse, settingsDTO, "3");

    assertThat(newServiceDef, is("4/theredirect"));
  }

}