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

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;


/**
 * <p>Java class for POCD_MT200001GB02.Participant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="POCD_MT200001GB02.Participant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:hl7-org:v3}InfrastructureRootElements"/>
 *         &lt;element ref="{NPFIT:HL7:Localisation}contentId"/>
 *         &lt;element name="functionCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;group ref="{urn:hl7-org:v3}NPFIT-000086_Role"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:hl7-org:v3}InfrastructureRootAttributes"/>
 *       &lt;attribute name="typeCode" use="required" type="{urn:hl7-org:v3}ParticipationType" />
 *       &lt;attribute name="contextControlCode" use="required" type="{urn:hl7-org:v3}ContextControl" fixed="OP" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POCD_MT200001GB02.Participant", propOrder = {
    "contentId",
    "functionCode",
    "cocdtp145214GB01AssociatedEntity",
    "cocdtp145007UK03RelatedEntity",
    "cocdtp145212GB02Workgroup"
})
public class POCDMT200001GB02Participant implements ContentAware {

    @XmlElement(namespace = "NPFIT:HL7:Localisation", required = true)
    protected TemplateContent contentId;
    protected CE functionCode;
    @XmlElement(name = "COCD_TP145214GB01.AssociatedEntity")
    protected COCDTP145214GB01AssociatedEntity cocdtp145214GB01AssociatedEntity;
    @XmlElement(name = "COCD_TP145007UK03.RelatedEntity")
    protected COCDTP145007UK03RelatedEntity cocdtp145007UK03RelatedEntity;
    @XmlElement(name = "COCD_TP145212GB02.Workgroup")
    protected COCDTP145212GB02Workgroup cocdtp145212GB02Workgroup;
    @XmlAttribute(name = "typeCode", required = true)
    protected List<String> typeCode;
    @XmlAttribute(name = "contextControlCode", required = true)
    protected List<String> contextControlCode;
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
     * Gets the value of the functionCode property.
     * 
     * @return
     *     possible object is
     *     {@link CE }
     *     
     */
    public CE getFunctionCode() {
        return functionCode;
    }

    /**
     * Sets the value of the functionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CE }
     *     
     */
    public void setFunctionCode(CE value) {
        this.functionCode = value;
    }

    /**
     * Gets the value of the cocdtp145214GB01AssociatedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP145214GB01AssociatedEntity }
     *     
     */
    public COCDTP145214GB01AssociatedEntity getCOCDTP145214GB01AssociatedEntity() {
        return cocdtp145214GB01AssociatedEntity;
    }

    /**
     * Sets the value of the cocdtp145214GB01AssociatedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP145214GB01AssociatedEntity }
     *     
     */
    public void setCOCDTP145214GB01AssociatedEntity(COCDTP145214GB01AssociatedEntity value) {
        this.cocdtp145214GB01AssociatedEntity = value;
    }

    /**
     * Gets the value of the cocdtp145007UK03RelatedEntity property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP145007UK03RelatedEntity }
     *     
     */
    public COCDTP145007UK03RelatedEntity getCOCDTP145007UK03RelatedEntity() {
        return cocdtp145007UK03RelatedEntity;
    }

    /**
     * Sets the value of the cocdtp145007UK03RelatedEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP145007UK03RelatedEntity }
     *     
     */
    public void setCOCDTP145007UK03RelatedEntity(COCDTP145007UK03RelatedEntity value) {
        this.cocdtp145007UK03RelatedEntity = value;
    }

    /**
     * Gets the value of the cocdtp145212GB02Workgroup property.
     * 
     * @return
     *     possible object is
     *     {@link COCDTP145212GB02Workgroup }
     *     
     */
    public COCDTP145212GB02Workgroup getCOCDTP145212GB02Workgroup() {
        return cocdtp145212GB02Workgroup;
    }

    /**
     * Sets the value of the cocdtp145212GB02Workgroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link COCDTP145212GB02Workgroup }
     *     
     */
    public void setCOCDTP145212GB02Workgroup(COCDTP145212GB02Workgroup value) {
        this.cocdtp145212GB02Workgroup = value;
    }

    /**
     * Gets the value of the typeCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypeCode() {
        if (typeCode == null) {
            typeCode = new ArrayList<String>();
        }
        return this.typeCode;
    }

    /**
     * Gets the value of the contextControlCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contextControlCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContextControlCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getContextControlCode() {
        if (contextControlCode == null) {
            contextControlCode = new ArrayList<String>();
        }
        return this.contextControlCode;
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

}
