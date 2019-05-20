package uk.nhs.ctp.service.report.org.hl7.v3;

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;

public interface ContentAware {

    TemplateContent getContentId();
    
    void setContentId(TemplateContent value);
}
