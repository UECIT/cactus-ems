package uk.nhs.ctp.service.report.org.hl7.v3;

public interface PertinentInformation<B extends BL, F extends Flag> {

    B getSeperatableInd();
    void setSeperatableInd(B bl);

    String getTypeCode();
    void setTypeCode(String typeCode);
    
    F getFlag();
    void setFlag(F flag);
    
//    List<String> getNullFlavor();
//
//    CsUpdateMode getUpdateMode();
}
