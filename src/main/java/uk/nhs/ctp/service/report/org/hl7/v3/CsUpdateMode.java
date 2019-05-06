//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.15 at 03:22:47 PM GMT 
//


package uk.nhs.ctp.service.report.org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cs_UpdateMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="cs_UpdateMode">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="added"/>
 *     &lt;enumeration value="altered"/>
 *     &lt;enumeration value="removed"/>
 *     &lt;enumeration value="unchanged"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "cs_UpdateMode")
@XmlEnum
public enum CsUpdateMode {

    @XmlEnumValue("added")
    ADDED("added"),
    @XmlEnumValue("altered")
    ALTERED("altered"),
    @XmlEnumValue("removed")
    REMOVED("removed"),
    @XmlEnumValue("unchanged")
    UNCHANGED("unchanged");
    private final String value;

    CsUpdateMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CsUpdateMode fromValue(String v) {
        for (CsUpdateMode c: CsUpdateMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
