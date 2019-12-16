package uk.nhs.ctp.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.dstu3.model.Coding;

@Data
@NoArgsConstructor
public class ExtensionDTO {
	private String system;
	private String code;
	private String display;

	public ExtensionDTO(Coding code2) {
		this.setSystem(code2.getSystem());
		this.setCode(code2.getCode());
		this.setDisplay(code2.getDisplay());
	}

}
