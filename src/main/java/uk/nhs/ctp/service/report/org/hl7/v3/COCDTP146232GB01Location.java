//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.15 at 03:22:47 PM GMT 
//


package uk.nhs.ctp.service.report.org.hl7.v3;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;


/**
 * <p>Java class for COCD_TP146232GB01.Location complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="COCD_TP146232GB01.Location">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element ref="{NPFIT:HL7:Localisation}contentId"/>
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
 *                       &lt;enumeration value="COCD_TP146232GB01#location"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;group ref="{urn:hl7-org:v3}NPFIT-000099_Role"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="typeCode" use="required" type="{urn:hl7-org:v3}cs" fixed="LOC" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COCD_TP146232GB01.Location", propOrder = {
    "contentId",
    "templateId",
    "cocdtp145222GB02HealthCareFacility"
})
public class COCDTP146232GB01Location {

    @XmlElement(namespace = "NPFIT:HL7:Localisation", required = true)
    protected TemplateContent contentId;
    @XmlElement(required = true)
    protected COCDTP146232GB01Location.TemplateId templateId;
    @XmlElement(name = "COCD_TP145222GB02.HealthCareFacility")
    protected COCDTP145222GB02HealthCareFacility cocdtp145222GB02HealthCareFacility;
    @XmlAttribute(name = "typeCode", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String typeCode;
    @XmlAttribute(name = "nullFlavor")
    protected List<String> nullFlavor;
    @XmlAttribute(name = "updateMode")
    protected CsUpdateMode updateMode;

    /**
     * Gets the value of the contentId property.
     * 
     * @return
     *     possible object is
     *     {@link TemplateContent }
     *     
     */
    public TemplateContent getContentId() {
        return contentId;
    }

    /**
     * Sets the value of the contentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link TemplateContent }
     *     
     */
    public void setContentId(TemplateContent value) {
        this.contentId = value;
    }

    /**
     * Gets the value of the templateId property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP146232GB01Location.TemplateId }
     *     
     */
    public COCDTP146232GB01Location.TemplateId getTemplateId() {
        return templateId;
    }

    /**
     * Sets the value of the templateId property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP146232GB01Location.TemplateId }
     *     
     */
    public void setTemplateId(COCDTP146232GB01Location.TemplateId value) {
        this.templateId = value;
    }

    /**
     * Gets the value of the cocdtp145222GB02HealthCareFacility property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP145222GB02HealthCareFacility }
     *     
     */
    public COCDTP145222GB02HealthCareFacility getCOCDTP145222GB02HealthCareFacility() {
        return cocdtp145222GB02HealthCareFacility;
    }

    /**
     * Sets the value of the cocdtp145222GB02HealthCareFacility property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP145222GB02HealthCareFacility }
     *     
     */
    public void setCOCDTP145222GB02HealthCareFacility(COCDTP145222GB02HealthCareFacility value) {
        this.cocdtp145222GB02HealthCareFacility = value;
    }

    /**
     * Gets the value of the typeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeCode() {
        if (typeCode == null) {
            return "LOC";
        } else {
            return typeCode;
        }
    }

    /**
     * Sets the value of the typeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeCode(String value) {
        this.typeCode = value;
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
     *             &lt;enumeration value="COCD_TP146232GB01#location"/>
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
