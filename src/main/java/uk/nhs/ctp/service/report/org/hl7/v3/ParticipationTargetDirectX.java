//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.15 at 03:22:47 PM GMT 
//


package uk.nhs.ctp.service.report.org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipationTargetDirect_X.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ParticipationTargetDirect_X">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="DIR"/>
 *     &lt;enumeration value="BBY"/>
 *     &lt;enumeration value="DON"/>
 *     &lt;enumeration value="PRD"/>
 *     &lt;enumeration value="SBJ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ParticipationTargetDirect_X")
@XmlEnum
public enum ParticipationTargetDirectX {

    DIR,
    BBY,
    DON,
    PRD,
    SBJ;

    public String value() {
        return name();
    }

    public static ParticipationTargetDirectX fromValue(String v) {
        return valueOf(v);
    }

}
