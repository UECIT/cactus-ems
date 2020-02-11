package uk.nhs.ctp.service.dto;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.dstu3.model.Reference;

@Data
public class CarePlanDTO {
	private String id;
	private String title;
	private String description;
	private String category;
	private String system;
	private String code;
	private String text;
	private String status;

	private ArrayList<ActivityDTO> activities;
	private ArrayList<SupportingInfoDTO> supportingInfo;
	private ArrayList<NotesDTO> notes;
	
	public CarePlanDTO(CarePlan carePlan) {
		super();

		setId(carePlan.getId());

		if (carePlan.getStatus() != null) {
			this.setStatus(carePlan.getStatus().toCode());
		}

		if (carePlan.getTitle() != null) {
			this.setTitle(carePlan.getTitle());
		}

		// Add an "ActivityDTO" to the "CarePlanDTO" and populate with the information below.
		ArrayList<ActivityDTO> activities = new ArrayList<>();
		for (CarePlanActivityComponent carePlanActivityComponent : carePlan.getActivity()) {
			ActivityDTO activityDTO = new ActivityDTO(carePlanActivityComponent);
			activities.add(activityDTO);
		}

		this.setActivities(activities);

		if (carePlan.getDescription() != null) {
			this.setDescription(carePlan.getDescription());
		}

		if (carePlan.getText() != null) {
			this.setText(carePlan.getText().getDiv().getContent());
		}

		if (carePlan.getSupportingInfo() != null) {
			ArrayList<SupportingInfoDTO> supportingInfo = new ArrayList<>();

			for (Reference info : carePlan.getSupportingInfo()) {
				SupportingInfoDTO supportingInfoDTO = new SupportingInfoDTO(info);
				supportingInfo.add(supportingInfoDTO);
			}

			this.setSupportingInfo(supportingInfo);
		}

		if (carePlan.getNote() != null) {
			ArrayList<NotesDTO> notes = new ArrayList<>();

			for (Annotation note : carePlan.getNote()) {
				NotesDTO notesDTO = new NotesDTO(note);
				notes.add(notesDTO);
			}

			this.setNotes(notes);
		}
	}

}
