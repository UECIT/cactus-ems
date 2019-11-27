package uk.nhs.ctp.service.dto;

import java.util.ArrayList;

import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.dstu3.model.Reference;

public class CarePlanDTO {
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
	
	public CarePlanDTO() {
		
	}

	public CarePlanDTO(CarePlan carePlan) {
		super();

		if (carePlan.getStatus() != null) {
			this.setStatus(carePlan.getStatus().toCode());
		}

		if (carePlan.getTitle() != null) {
			this.setTitle(carePlan.getTitle());
		}

		// Add an "ActivityDTO" to the "CarePlanDTO" and populate with the information below.
		ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>();
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
			ArrayList<SupportingInfoDTO> supportingInfo = new ArrayList<SupportingInfoDTO>();

			for (Reference info : carePlan.getSupportingInfo()) {
				SupportingInfoDTO supportingInfoDTO = new SupportingInfoDTO(info);
				supportingInfo.add(supportingInfoDTO);
			}

			this.setSupportingInfo(supportingInfo);
		}

		if (carePlan.getNote() != null) {
			ArrayList<NotesDTO> notes = new ArrayList<NotesDTO>();

			for (Annotation note : carePlan.getNote()) {
				NotesDTO notesDTO = new NotesDTO(note);
				notes.add(notesDTO);
			}

			this.setNotes(notes);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<ActivityDTO> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<ActivityDTO> activities) {
		this.activities = activities;
	}

	public ArrayList<SupportingInfoDTO> getSupportingInfo() {
		return supportingInfo;
	}

	public void setSupportingInfo(ArrayList<SupportingInfoDTO> supportingInfo) {
		this.supportingInfo = supportingInfo;
	}

	public ArrayList<NotesDTO> getNotes() {
		return notes;
	}

	public void setNotes(ArrayList<NotesDTO> notes) {
		this.notes = notes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
