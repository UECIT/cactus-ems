//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.15 at 03:22:47 PM GMT 
//


package uk.nhs.ctp.service.report.org.hl7.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for COCD_TP145222GB02.HealthCareFacility complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="COCD_TP145222GB02.HealthCareFacility">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element name="code" type="{urn:hl7-org:v3}CV.NPfIT.Codedplain" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II.NPfIT.oid.required" maxOccurs="3"/>
 *         &lt;element name="templateId">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{urn:hl7-org:v3}II">
 *                 &lt;attribute name="root" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{urn:hl7-org:v3}uid">
 *                       &lt;enumeration value="2.16.840.1.113883.2.1.3.2.4.18.2"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="extension" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{urn:hl7-org:v3}st">
 *                       &lt;enumeration value="COCD_TP145222GB02#HealthCareFacility"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="location" type="{urn:hl7-org:v3}COCD_TP145222GB02.Place" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="classCode" use="required" type="{urn:hl7-org:v3}cs" fixed="ISDLOC" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCD_TP145222GB02.HealthCareFacility", propOrder = {
    "code",
    "id",
    "templateId",
    "location"
})
public class COCDTP145222GB02HealthCareFacility {

    protected CVNPfITCodedplain code;
    @XmlElement(required = true)
    protected List<IINPfITOidRequired> id;
    @XmlElement(required = true)
    protected COCDTP145222GB02HealthCareFacility.TemplateId templateId;
    @XmlElementRef(name = "location", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    protected JAXBElement<COCDTP145222GB02Place> location;
    @XmlAttribute(name = "classCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String classCode;
    @XmlAttribute(name = "nullFlavor")
    protected List<String> nullFlavor;
    @XmlAttribute(name = "updateMode")
    protected CsUpdateMode updateMode;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link CVNPfITCodedplain }
     *     
     */
    public CVNPfITCodedplain getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CVNPfITCodedplain }
     *     
     */
    public void setCode(CVNPfITCodedplain value) {
        this.code = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IINPfITOidRequired }
     * 
     * 
     */
    public List<IINPfITOidRequired> getId() {
        if (id == null) {
            id = new ArrayList<IINPfITOidRequired>();
        }
        return this.id;
    }

    /**
     * Gets the value of the templateId property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP145222GB02HealthCareFacility.TemplateId }
     *     
     */
    public COCDTP145222GB02HealthCareFacility.TemplateId getTemplateId() {
        return templateId;
    }

    /**
     * Sets the value of the templateId property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP145222GB02HealthCareFacility.TemplateId }
     *     
     */
    public void setTemplateId(COCDTP145222GB02HealthCareFacility.TemplateId value) {
        this.templateId = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link COCDTP145222GB02Place }{@code >}
     *     
     */
    public JAXBElement<COCDTP145222GB02Place> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link COCDTP145222GB02Place }{@code >}
     *     
     */
    public void setLocation(JAXBElement<COCDTP145222GB02Place> value) {
        this.location = value;
    }

    /**
     * Gets the value of the classCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassCode() {
        if (classCode == null) {
            return "ISDLOC";
        } else {
            return classCode;
        }
    }

    /**
     * Sets the value of the classCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassCode(String value) {
        this.classCode = value;
    }

    /**
     * Gets the value of the nullFlavor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nullFlavor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNullFlavor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNullFlavor() {
        if (nullFlavor == null) {
            nullFlavor = new ArrayList<String>();
        }
        return this.nullFlavor;
    }

    /**
     * Gets the value of the updateMode property.
     * 
     * @return
     *     possible object is
     *     {@link CsUpdateMode }
     *     
     */
    public CsUpdateMode getUpdateMode() {
        return updateMode;
    }

    /**
     * Sets the value of the updateMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CsUpdateMode }
     *     
     */
    public void setUpdateMode(CsUpdateMode value) {
        this.updateMode = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}II">
     *       &lt;attribute name="root" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{urn:hl7-org:v3}uid">
     *             &lt;enumeration value="2.16.840.1.113883.2.1.3.2.4.18.2"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="extension" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{urn:hl7-org:v3}st">
     *             &lt;enumeration value="COCD_TP145222GB02#HealthCareFacility"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TemplateId
        extends II
    {


    }

}
