package uk.nhs.ctp.service.report.org.hl7.v3;

public interface ClassCodeAware<CODE extends CV> {

	void setStandardIndustryClassCode(CODE code);
}
