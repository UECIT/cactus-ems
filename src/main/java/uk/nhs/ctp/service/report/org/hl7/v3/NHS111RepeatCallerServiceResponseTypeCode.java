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
 * <p>Java class for NHS111RepeatCallerServiceResponseType_code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NHS111RepeatCallerServiceResponseType_code">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="RC"/>
 *     &lt;enumeration value="NRC"/>
 *     &lt;enumeration value="PRC"/>
 *     &lt;enumeration value="IIQ"/>
 *     &lt;enumeration value="ER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NHS111RepeatCallerServiceResponseType_code")
@XmlEnum
public enum NHS111RepeatCallerServiceResponseTypeCode {

    RC,
    NRC,
    PRC,
    IIQ,
    ER;

    public String value() {
        return name();
    }

    public static NHS111RepeatCallerServiceResponseTypeCode fromValue(String v) {
        return valueOf(v);
    }

}
