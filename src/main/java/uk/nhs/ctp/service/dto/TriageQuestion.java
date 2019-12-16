package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.hl7.fhir.dstu3.model.Extension;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class TriageQuestion {

	private String questionnaireId;
	private String question;
	private String questionId;
	private List<TriageOption> options;
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

	public String getResponseInteger() {
		return responseInteger;
	}

	public void setResponseInteger(String responseInteger) {
		this.responseInteger = responseInteger;
	}

	public String getResponseDecimal() {
		return responseDecimal;
	}

	public void setResponseDecimal(String responseDecimal) {
		this.responseDecimal = responseDecimal;
	}

	public String getResponseBoolean() {
		return responseBoolean;
	}

	public void setResponseBoolean(String responseBoolean) {
		this.responseBoolean = responseBoolean;
	}

	public String getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(String responseDate) {
		this.responseDate = responseDate;
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public String getQuestion() {
		return question;
	}

	public List<TriageOption> getOptions() {
		if (this.options == null) {
			this.options = new ArrayList<>();
		}
		return options;
	}

	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setOptions(List<TriageOption> options) {
		this.options = options;
	}

	public void addOption(String code, String display) {
		if (this.options == null) {
			this.options = new ArrayList<>();
		}
		this.options.add(new TriageOption(code, display));
	}

	public void addOption(String code, String display, Extension extension) {
		if (this.options == null) {
			this.options = new ArrayList<>();
		}
		this.options.add(new TriageOption(code, display,
				new TriageExtension(extension.getUrl(), extension.getValue().primitiveValue())));
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public TriageOption getResponse() {
		return response;
	}

	public void setResponse(TriageOption response) {
		this.response = response;
	}

	public boolean isRepeats() {
		return repeats;
	}

	public void setRepeats(boolean repeats) {
		this.repeats = repeats;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getResponseAttachment() {
		return responseAttachment;
	}

	public void setResponseAttachment(String responseAttachment) {
		this.responseAttachment = responseAttachment;
	}

	public String getResponseAttachmentInitial() {
		return responseAttachmentInitial;
	}

	public void setResponseAttachmentInitial(String responseAttachmentInitial) {
		this.responseAttachmentInitial = responseAttachmentInitial;
	}

	public String getResponseAttachmentType() {
		return responseAttachmentType;
	}

	public void setResponseAttachmentType(String responseAttachmentType) {
		this.responseAttachmentType = responseAttachmentType;
	}

	public List<TriageQuestion> getSubQuestions() {
		return subQuestions;
	}

	public void setSubQuestions(List<TriageQuestion> subQuestions) {
		this.subQuestions = subQuestions;
	}

	public ExtensionDTO getExtension() {
		return extension;
	}

	public void setExtension(ExtensionDTO extension) {
		this.extension = extension;
	}

	public String getEnableWhenQuestionnaireId() {
		return enableWhenQuestionnaireId;
	}

	public void setEnableWhenQuestionnaireId(String enableWhenQuestionnaireId) {
		this.enableWhenQuestionnaireId = enableWhenQuestionnaireId;
	}

	public boolean getEnableWhenAnswer() {
		return enableWhenAnswer;
	}

	public void setEnableWhenAnswer(boolean enableWhenAnswer) {
		this.enableWhenAnswer = enableWhenAnswer;
	}

	public Coordinates getResponseCoordinates() {
		return responseCoordinates;
	}

	public void setResponseCoordinates(Coordinates responseCoordinates) {
		this.responseCoordinates = responseCoordinates;
	}
}
