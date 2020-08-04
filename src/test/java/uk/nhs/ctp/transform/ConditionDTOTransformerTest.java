package uk.nhs.ctp.transform;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.ConditionDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;

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
    when(genericResourceLocator.findResource(any(Reference.class), any(IdType.class)))
        .thenReturn(observation);

    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator, referenceService);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.hasItem(Matchers.containsString("code")));
    assertThat(dto.getEvidence(), Matchers.hasItem(Matchers.containsString("system")));
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
    when(genericResourceLocator.findResource(any(Reference.class), any(IdType.class)))
        .thenReturn(observation);

    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator, referenceService);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.hasItem(Matchers.containsString("display")));
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
    when(genericResourceLocator.findResource(any(Reference.class), any(IdType.class)))
        .thenReturn(observation);

    Condition condition = new Condition()
        .setClinicalStatus(ConditionClinicalStatus.ACTIVE)
        .setVerificationStatus(ConditionVerificationStatus.CONFIRMED)
        .setOnset(new DateTimeType(new Date()))
        .addEvidence(new ConditionEvidenceComponent()
            .addDetail(new Reference("Observation/1"))
        );
    condition.setId("Condition/1");

    ConditionDTOTransformer conditionDTOTransformer =
        new ConditionDTOTransformer(genericResourceLocator, referenceService);

    ConditionDTO dto = conditionDTOTransformer.transform(condition);

    assertThat(dto.getEvidence(), Matchers.hasItem(Matchers.containsString("text")));
  }
}
