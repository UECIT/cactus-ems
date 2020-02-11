package uk.nhs.ctp.service.dto;

import lombok.Data;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;

@Data
public class ActivityDTO {

  private String description;
  private String display;
  private String system;
  private String code;
  private String text;

  public ActivityDTO(CarePlanActivityComponent carePlanActivityComponent) {
    this.setDescription(carePlanActivityComponent.getDetail().getDescription());
    this.setDisplay(
        carePlanActivityComponent.getDetail().getCategory().getCodingFirstRep().getDisplay());
    this.setSystem(
        carePlanActivityComponent.getDetail().getCode().getCodingFirstRep().getSystem());
    this.setCode(carePlanActivityComponent.getDetail().getCode().getCodingFirstRep().getCode());
    this.setText(carePlanActivityComponent.getDetail().getCode().getText());
  }

}
