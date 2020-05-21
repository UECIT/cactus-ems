package uk.nhs.ctp.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import uk.nhs.ctp.enums.ReferencingType;

@Data
public class NewCdssSupplierDTO {

	private String name;
	private String baseUrl;
	private List<ServiceDefinitionDTO> serviceDefinitions = new ArrayList<>();
	private ReferencingType inputParamsRefType;
	private ReferencingType inputDataRefType;

}
