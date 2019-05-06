package uk.nhs.ctp.service.dto;

import org.hl7.fhir.dstu3.model.Annotation;

public class NotesDTO {
	private String text;

	public NotesDTO(Annotation note) {
		this.setText(note.getText());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
