package uk.nhs.ctp.transform;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.ConditionDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.testhelper.matchers.FhirMatchers;

@RunWith(MockitoJUnitRunner.class)
public class ConditionDTOTransformerTest {

  @Mock
  GenericResourceLocator genericResourceLocator;

  @Mock
  ReferenceService referenceService;

  @Test
  public void dtoIncludesCodeElements() {
    CodeableConcept code = new CodeableConcept()
        .addCoding(new Coding()
            .setSystem("system")
            .setCode("code")
        );

    Observation observation = new Observation().setCode(code);
    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    when(genericResourceLocator.findResource(
        argThat(FhirMatchers.referenceTo("Observation/1")),
        argThat(equalTo(condition.getIdElement())))
    ).thenReturn(observation);

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.contains("Observation - code (system) = null"));
  }

  @Test
  public void dtoIncludesCodeDisplay() {
    CodeableConcept code = new CodeableConcept()
        .addCoding(new Coding()
            .setSystem("system")
            .setCode("code")
            .setDisplay("display")
        );

    Observation observation = new Observation().setCode(code);
    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    when(genericResourceLocator.findResource(
        argThat(FhirMatchers.referenceTo("Observation/1")),
        argThat(equalTo(condition.getIdElement())))
    ).thenReturn(observation);

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.contains("Observation - display = null"));
  }

  @Test
  public void dtoIncludesConceptText() {
    CodeableConcept code = new CodeableConcept()
        .addCoding(new Coding()
            .setSystem("system")
            .setCode("code")
            .setDisplay("display")
        )
        .setText("text");

    Observation observation = new Observation().setCode(code);
    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    when(genericResourceLocator.findResource(
        argThat(FhirMatchers.referenceTo("Observation/1")),
        argThat(equalTo(condition.getIdElement())))
    ).thenReturn(observation);

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.contains("Observation - text = null"));

  }

  @Test
  public void dtoIncludesValue() {
    CodeableConcept code = new CodeableConcept()
        .setText("text");

    CodeableConcept value = new CodeableConcept()
        .addCoding(new Coding()
            .setDisplay("value")
        );

    Observation observation = new Observation()
        .setCode(code)
        .setValue(value);
    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    when(genericResourceLocator.findResource(
        argThat(FhirMatchers.referenceTo("Observation/1")),
        argThat(equalTo(condition.getIdElement())))
    ).thenReturn(observation);

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.contains("Observation - text = value"));
  }
}
