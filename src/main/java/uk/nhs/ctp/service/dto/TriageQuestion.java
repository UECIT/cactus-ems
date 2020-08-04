package uk.nhs.ctp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TriageQuestion {

	private String questionnaireId;
	private String question;
	private String questionId;
	private List<TriageOption> options = new ArrayList<>();
	private boolean repeats;
	private boolean required;
	private String questionType;
	private ExtensionDTO extension;

	@JsonInclude(value = Include.NON_NULL)
	private TriageOption response;

	private List<TriageQuestion> subQuestions;

	private String responseString;
	private String responseInteger;
	private String responseDecimal;
	private String responseBoolean;
	private String responseDate;
	private String responseAttachment;
	private String responseAttachmentInitial;
	private String responseAttachmentType;
	private Coordinates responseCoordinates;
	
	private String enableWhenQuestionnaireId;
	private boolean enableWhenAnswer;

	public void addOption(String sys, String code, String display) {
		addOption(new TriageOption(sys, code, display));
	}

	public void addOption(TriageOption option) {
		if (this.options == null) {
			this.options = new ArrayList<>();
		}
		this.options.add(option);
	}
}
