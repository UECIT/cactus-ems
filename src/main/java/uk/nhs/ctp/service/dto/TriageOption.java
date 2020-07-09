package uk.nhs.ctp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageOption {

	private String system;
	private String code;
	private String display;
	private TriageExtension extension;

	public TriageOption(String code, String display) {
		this.code = code;
		this.display = display;
		this.extension = null;
	}

	public TriageOption(String system, String code, String display) {
		this(code, display);
		this.system = system;
	}

}
