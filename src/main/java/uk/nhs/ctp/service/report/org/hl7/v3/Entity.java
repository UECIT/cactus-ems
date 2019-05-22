package uk.nhs.ctp.service.report.org.hl7.v3;

public interface Entity<NAME extends EN> {

	void setClassCode(String classCode);
	String getClassCode();
	
	void setDeterminerCode(String determinerCode);
	String getDeterminerCode();
	
	void setName(NAME name);
}
