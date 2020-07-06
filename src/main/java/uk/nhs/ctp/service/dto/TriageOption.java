package uk.nhs.ctp.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageOption {

	private String code;
	private String display;
	private TriageExtension extension;

	public TriageOption(String code, String display) {
		this.code = code;
		this.display = display;
		this.extension = null;
	}

}
