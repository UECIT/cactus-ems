package uk.nhs.ctp.service.report.decorator.mapping.template.textsection;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocItem;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocList;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleToIntegratedUrgentCareTextSectionTemplateMapper extends AbstractIntegratedUrgentCareTextSectionTemplateMapper<Bundle> {
	
	@Override
	public void map(Bundle bundle, COCDTP146246GB01Section1 section) {
		StrucDocText text = new StrucDocText();
		
		List<QuestionnaireResponse> questionnaireResponses = 
				ResourceProviderUtils.getResources(bundle, QuestionnaireResponse.class);

		List<CareConnectCarePlan> carePlans = 
				ResourceProviderUtils.getResources(bundle, CareConnectCarePlan.class);
		
		StrucDocList list = new StrucDocList();
		list.getItem().add(new StrucDocItem("The following interview..."));

		questionnaireResponses.stream().forEach(response ->
			addQuestionnaireResponse(list, response));
		
		list.getItem().add(new StrucDocItem("Resulted in the following care advice..."));

		carePlans.stream().forEach(plan -> addCarePlan(list, plan));

		text.getContent().add(new JAXBElement<StrucDocList>(
				new QName("urn:hl7-org:v3", "list"), StrucDocList.class, list));
		
		section.setTitle("Triage Notes");
		section.setText(text);
	}
	
	@Override
	public Class<Bundle> getResourceClass() {
		return Bundle.class;
	}

	private void addQuestionnaireResponse(StrucDocList list, QuestionnaireResponse response) {
		response.getItem().stream().forEach(item -> {
			StrucDocItem listItem = new StrucDocItem();
			Type type = item.getAnswerFirstRep().getValue();
			
			listItem.getContent().add(item.getText() + " -> " + 
					(type instanceof Coding ? ((Coding)type).getDisplay() : type.primitiveValue()));

			list.getItem().add(listItem);
		});
	}
	
	private void addCarePlan(StrucDocList list, CareConnectCarePlan carePlan) {
		carePlan.getActivity().stream().forEach(activity -> 
				list.getItem().add(new StrucDocItem(activity.getDetail().getDescription())));

		carePlan.getNote().stream().forEach(
				note -> list.getItem().add(new StrucDocItem(note.getText())));
	}
}
