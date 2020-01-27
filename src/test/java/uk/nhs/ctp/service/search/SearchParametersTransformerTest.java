package uk.nhs.ctp.service.search;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementDateFilterComponent;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;

@RunWith(MockitoJUnitRunner.class)
public class SearchParametersTransformerTest {

  private SearchParametersTransformer searchParametersTransformer;

  @Mock
  private PatientRepository patientService;

  @Before
  public void setup() {
    searchParametersTransformer = new SearchParametersTransformer(patientService);
  }

  @Test
  public void createSearchParametersFromTriggers() {

    DataRequirement observationRequirement = createObservationRequirement("coughing", "absent",
        LocalDateTime.of(2001, 2, 3, 4, 5, 6));
    DataRequirement patientRequirement = createPatientRequirement(LocalDate.of(2004, 2,1));

    SearchParameters searchParameters =
        searchParametersTransformer.transform(ImmutableList.of(observationRequirement, patientRequirement), null, null);

    assertThat(searchParameters.getObservationTriggers(),
        contains("CareConnectObservation$code$system|coughing$value$system|absent$effective$2001-02-03T04:05:06"));
    assertThat(searchParameters.getPatientTriggers(),
        contains("CareConnectPatient$birthDate$2004-02-01"));
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
        containsInAnyOrder("user$http://hl7.org/fhir/valueset-provider-taxonomy.html|patient",
            "setting$http://hl7.org/fhir/valueset-provider-taxonomy.html|online", "task$cdss/supplier/stub|example"));

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

    assertThat(searchParameters.getContextValueCode(),
        containsInAnyOrder("gender$http://hl7.org/fhir/administrative-gender|male", "age$http://snomed.info/sct|67822003"));
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

    assertThat(searchParameters.getContextValueCode(),
        containsInAnyOrder("gender$http://hl7.org/fhir/administrative-gender|female", "age$http://snomed.info/sct|133936004"));
  }

  private DataRequirement createObservationRequirement(String code, String value, LocalDateTime date) {
    DataRequirement dataRequirement = new DataRequirement();
    dataRequirement.setType("CareConnectObservation");

    DataRequirementCodeFilterComponent codeFilter = new DataRequirementCodeFilterComponent();
    codeFilter.setPath("code");
    codeFilter.setValueCoding(singletonList(new Coding("system", code, code)));
    dataRequirement.addCodeFilter(codeFilter);

    DataRequirementCodeFilterComponent valueFilter = new DataRequirementCodeFilterComponent();
    valueFilter.setPath("value");
    valueFilter.setValueCoding(singletonList(new Coding("system", value, value)));
    dataRequirement.addCodeFilter(valueFilter);

    DataRequirementDateFilterComponent effectiveFilter = new DataRequirementDateFilterComponent();
    effectiveFilter.setPath("effective");
    effectiveFilter.setValue(new DateTimeType(Date.from(date.atZone(ZoneId.systemDefault()).toInstant())));
    dataRequirement.addDateFilter(effectiveFilter);

    return dataRequirement;
  }

  private DataRequirement createPatientRequirement(LocalDate date) {
    DataRequirement dataRequirement = new DataRequirement();
    dataRequirement.setType("CareConnectPatient");

    DataRequirementDateFilterComponent birthDateFilter = new DataRequirementDateFilterComponent();
    birthDateFilter.setPath("birthDate");
    birthDateFilter.setValue(new DateTimeType(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())));
    dataRequirement.addDateFilter(birthDateFilter);

    return dataRequirement;
  }

}