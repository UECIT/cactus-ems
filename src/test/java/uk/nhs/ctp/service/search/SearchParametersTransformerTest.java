package uk.nhs.ctp.service.search;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.PatientService;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;

@RunWith(MockitoJUnitRunner.class)
public class SearchParametersTransformerTest {

  private SearchParametersTransformer searchParametersTransformer;

  @Mock
  private PatientService patientService;

  @Before
  public void setup() {
    searchParametersTransformer = new SearchParametersTransformer(patientService);
  }

  @Test
  public void createSearchParametersFromTriggers() {

    List<String> triggers = ImmutableList.of("coughing", "sneezing", "breathing");

    SearchParameters searchParameters =
        searchParametersTransformer.transform(triggers, null, null);

    assertThat(searchParameters.getTypeCode(),
        containsInAnyOrder("coughing", "sneezing", "breathing"));

  }

  @Test
  public void createSearchParametersFromSettings() {

    CodeDTO jurisdiction = new CodeDTO();
    jurisdiction.setCode("GB");
    CodeDTO userType = new CodeDTO();
    userType.setCode("patient");
    CodeDTO setting = new CodeDTO();
    setting.setCode("online");
    CodeDTO task = new CodeDTO();
    task.setCode("example");
    SettingsDTO settingsDTO = new SettingsDTO();
    settingsDTO.setJurisdiction(jurisdiction);
    settingsDTO.setUserType(userType);
    settingsDTO.setSetting(setting);
    settingsDTO.setUserTaskContext(task);

    SearchParameters searchParameters =
        searchParametersTransformer.transform(emptyList(), settingsDTO, null);

    assertThat(searchParameters.getQuery(), is("triage"));
    assertThat(searchParameters.getJurisdiction(), is("GB"));
    assertThat(searchParameters.getContextValueCode(),
        containsInAnyOrder("user$patient", "setting$online", "task$example"));

  }

  @Test
  public void createSearchParametersFromChildPatient() {

    long patientId = 3L;
    PatientEntity patientEntity = new PatientEntity();

    LocalDate fifteenYearsOld = LocalDate.now().minusYears(15);
    patientEntity.setDateOfBirth(Date.from(fifteenYearsOld.atStartOfDay()
      .atZone(ZoneId.systemDefault())
      .toInstant()));
    patientEntity.setGender("male");

    when(patientService.findById(patientId)).thenReturn(patientEntity);

    SearchParameters searchParameters = searchParametersTransformer
        .transform(emptyList(), null, patientId);

    assertThat(searchParameters.getContextValueCode(), containsInAnyOrder("gender$male", "age$child"));

  }

  @Test
  public void createSearchParametersFromAdultPatient() {

    long patientId = 3L;
    PatientEntity patientEntity = new PatientEntity();

    LocalDate twentyThreeYearsOld = LocalDate.now().minusYears(23);
    patientEntity.setDateOfBirth(Date.from(twentyThreeYearsOld.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()));
    patientEntity.setGender("female");

    when(patientService.findById(patientId)).thenReturn(patientEntity);

    SearchParameters searchParameters = searchParametersTransformer
        .transform(emptyList(), null, patientId);

    assertThat(searchParameters.getContextValueCode(), containsInAnyOrder("gender$female", "age$adult"));

  }

}