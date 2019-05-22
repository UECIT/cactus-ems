package uk.nhs.ctp.service.report.org.hl7.v3;

import java.util.List;

public interface DetailedEntity<NAME extends EN> extends Entity<NAME> {

	void setAddr(AD address);
	
	List<TEL> getTelecom();
}
