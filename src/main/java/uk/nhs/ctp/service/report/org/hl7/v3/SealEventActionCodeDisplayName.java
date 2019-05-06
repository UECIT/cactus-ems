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
 * <p>Java class for SealEventActionCode_displayName.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SealEventActionCode_displayName">
 *   &lt;restriction base="{urn:hl7-org:v3}st">
 *     &lt;enumeration value="Information has been sealed"/>
 *     &lt;enumeration value="Information has been sealed and locked"/>
 *     &lt;enumeration value="Information that was sealed and locked is now sealed"/>
 *     &lt;enumeration value="Information has been unsealed"/>
 *     &lt;enumeration value="An additional workgroup now has access to some sealed information"/>
 *     &lt;enumeration value="The new GP workgroup now has access to some sealed information"/>
 *     &lt;enumeration value="A replacement workgroup now has access to some sealed information due to organizational change"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SealEventActionCode_displayName")
@XmlEnum
public enum SealEventActionCodeDisplayName {

    @XmlEnumValue("Information has been sealed")
    INFORMATION_HAS_BEEN_SEALED("Information has been sealed"),
    @XmlEnumValue("Information has been sealed and locked")
    INFORMATION_HAS_BEEN_SEALED_AND_LOCKED("Information has been sealed and locked"),
    @XmlEnumValue("Information that was sealed and locked is now sealed")
    INFORMATION_THAT_WAS_SEALED_AND_LOCKED_IS_NOW_SEALED("Information that was sealed and locked is now sealed"),
    @XmlEnumValue("Information has been unsealed")
    INFORMATION_HAS_BEEN_UNSEALED("Information has been unsealed"),
    @XmlEnumValue("An additional workgroup now has access to some sealed information")
    AN_ADDITIONAL_WORKGROUP_NOW_HAS_ACCESS_TO_SOME_SEALED_INFORMATION("An additional workgroup now has access to some sealed information"),
    @XmlEnumValue("The new GP workgroup now has access to some sealed information")
    THE_NEW_GP_WORKGROUP_NOW_HAS_ACCESS_TO_SOME_SEALED_INFORMATION("The new GP workgroup now has access to some sealed information"),
    @XmlEnumValue("A replacement workgroup now has access to some sealed information due to organizational change")
    A_REPLACEMENT_WORKGROUP_NOW_HAS_ACCESS_TO_SOME_SEALED_INFORMATION_DUE_TO_ORGANIZATIONAL_CHANGE("A replacement workgroup now has access to some sealed information due to organizational change");
    private final String value;

    SealEventActionCodeDisplayName(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SealEventActionCodeDisplayName fromValue(String v) {
        for (SealEventActionCodeDisplayName c: SealEventActionCodeDisplayName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
