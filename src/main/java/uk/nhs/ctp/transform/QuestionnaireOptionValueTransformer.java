package uk.nhs.ctp.transform;

import java.util.function.Function;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.TriageExtension;
import uk.nhs.ctp.service.dto.TriageOption;

@Component
public class QuestionnaireOptionValueTransformer
    implements Transformer<QuestionnaireItemOptionComponent, TriageOption> {

  @Override
  public TriageOption transform(QuestionnaireItemOptionComponent optionComponent) {
    TriageOption option = new TriageOption();

    if (optionComponent.hasExtension()) {
      Extension extension = optionComponent.getExtensionFirstRep();
      TriageExtension triageExtension = new TriageExtension(extension.getUrl(),
          extension.getValue().primitiveValue());
      option.setExtension(triageExtension);
    }

    if (optionComponent.hasValueCoding()) {
      Coding optionCode = optionComponent.getValueCoding();
      option.setCode(optionCode.getCode());
      option.setDisplay(optionCode.getDisplay());
      option.setSystem(optionCode.getSystem());
      return option;
    }

    //non-code types are mangled into coding-like objects for display on the UI
    else if (optionComponent.hasValueStringType()) {
      setValue(optionComponent, option, QuestionnaireItemOptionComponent::getValueStringType);
      return option;
    }
    else if (optionComponent.hasValueIntegerType()) {
      setValue(optionComponent, option, QuestionnaireItemOptionComponent::getValueIntegerType);
      return option;
    }
    else if (optionComponent.hasValueDateType()) {
      setValue(optionComponent, option, QuestionnaireItemOptionComponent::getValueDateType);
      return option;
    }
    else if (optionComponent.hasValueTimeType()) {
      setValue(optionComponent, option, QuestionnaireItemOptionComponent::getValueTimeType);
      return option;
    }
    throw new FHIRException("Option did not have any value");
  }

  private void setValue(QuestionnaireItemOptionComponent comp, TriageOption option,
      Function<QuestionnaireItemOptionComponent, ? extends PrimitiveType<?>> typeExtract) {
    String valueAsString = typeExtract.apply(comp).getValueAsString();
    option.setSystem(comp.getValue().fhirType()); // use fhir type as system (see CDSCT-64)
    option.setCode(valueAsString);
    option.setDisplay(valueAsString);
  }

}
