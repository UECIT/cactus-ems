//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.15 at 03:22:47 PM GMT 
//


package uk.nhs.ctp.service.report.org.hl7.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * Mailing and home or office addresses. A sequence of address parts, such as street or post office Box, city, postal code, country, etc.
 * 
 * <p>Java class for AD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AD">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}ANY">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="delimiter">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DEL" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="country">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CNT" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="state">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STA" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="county">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CPA" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="city">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CTY" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="postalCode">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ZIP" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="precinct">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="PRE" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="streetAddressLine">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="houseNumber">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="BNR" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="houseNumberNumeric">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="BNN" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="direction">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DIR" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="streetName">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STR" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="streetNameBase">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STB" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="streetNameType">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STTYP" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="additionalLocator">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ADL" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="unitID">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="UNID" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="unitType">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="UNIT" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="carrier">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CAR" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="censusTract">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{urn:hl7-org:v3}ADXP">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CEN" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="addressKey">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ADDRK" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="desc">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DESC" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="useablePeriod" type="{urn:hl7-org:v3}IVL_TS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="use" type="{urn:hl7-org:v3}set_cs_PostalAddressUse" />
 *       &lt;attribute name="isNotOrdered" type="{urn:hl7-org:v3}bl" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AD", propOrder = {
    "content"
})
public class AD {

    @XmlElementRefs({
        @XmlElementRef(name = "streetAddressLine", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "useablePeriod", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "city", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "delimiter", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "houseNumberNumeric", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unitID", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "streetNameBase", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "censusTract", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "precinct", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "streetNameType", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "addressKey", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "id", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "postalCode", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "streetName", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "desc", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "additionalLocator", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "unitType", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "country", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "carrier", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "state", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "county", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "houseNumber", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "direction", namespace = "urn:hl7-org:v3", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "use")
    protected List<CsPostalAddressUse> use;
    @XmlAttribute(name = "isNotOrdered")
    protected Boolean isNotOrdered;

    /**
     * Mailing and home or office addresses. A sequence of address parts, such as street or post office Box, city, postal code, country, etc.Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AD.StreetAddressLine }{@code >}
     * {@link JAXBElement }{@code <}{@link IVLTS }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.City }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Delimiter }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.HouseNumberNumeric }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.UnitID }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.StreetNameBase }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.CensusTract }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Precinct }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.StreetNameType }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.AddressKey }{@code >}
     * {@link JAXBElement }{@code <}{@link II }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.PostalCode }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.StreetName }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Desc }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.AdditionalLocator }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.UnitType }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Country }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Carrier }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.State }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.County }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.HouseNumber }{@code >}
     * {@link JAXBElement }{@code <}{@link AD.Direction }{@code >}
     * {@link String }
     * 
     * 
     */
    public List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    /**
     * Gets the value of the use property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the use property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CsPostalAddressUse }
     * 
     * 
     */
    public List<CsPostalAddressUse> getUse() {
        if (use == null) {
            use = new ArrayList<CsPostalAddressUse>();
        }
        return this.use;
    }

    /**
     * Gets the value of the isNotOrdered property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsNotOrdered() {
        return isNotOrdered;
    }

    /**
     * Sets the value of the isNotOrdered property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsNotOrdered(Boolean value) {
        this.isNotOrdered = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ADL" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AdditionalLocator
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ADDRK" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    public static class AddressKey {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "partType")
        protected CsAddressPartType partType;

        /**
         * Gets the value of the content property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the value of the content property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setContent(String value) {
            this.content = value;
        }

        /**
         * Gets the value of the partType property.
         * 
         * @return
         *     possible object is
         *     {@link CsAddressPartType }
         *     
         */
        public CsAddressPartType getPartType() {
            if (partType == null) {
                return CsAddressPartType.ADDRK;
            } else {
                return partType;
            }
        }

        /**
         * Sets the value of the partType property.
         * 
         * @param value
         *     allowed object is
         *     {@link CsAddressPartType }
         *     
         */
        public void setPartType(CsAddressPartType value) {
            this.partType = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CAR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Carrier
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CEN" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class CensusTract
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CTY" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class City
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CNT" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Country
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="CPA" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class County
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DEL" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Delimiter
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DESC" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    public static class Desc {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "partType")
        protected CsAddressPartType partType;

        /**
         * Gets the value of the content property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the value of the content property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setContent(String value) {
            this.content = value;
        }

        /**
         * Gets the value of the partType property.
         * 
         * @return
         *     possible object is
         *     {@link CsAddressPartType }
         *     
         */
        public CsAddressPartType getPartType() {
            if (partType == null) {
                return CsAddressPartType.DESC;
            } else {
                return partType;
            }
        }

        /**
         * Sets the value of the partType property.
         * 
         * @param value
         *     allowed object is
         *     {@link CsAddressPartType }
         *     
         */
        public void setPartType(CsAddressPartType value) {
            this.partType = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="DIR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Direction
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="BNR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HouseNumber
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="BNN" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HouseNumberNumeric
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="ZIP" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PostalCode
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="PRE" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Precinct
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STA" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class State
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StreetAddressLine
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STR" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StreetName
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STB" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StreetNameBase
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="STTYP" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StreetNameType
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="UNID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UnitID
        extends ADXP
    {


    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{urn:hl7-org:v3}ADXP">
     *       &lt;attribute name="partType" type="{urn:hl7-org:v3}cs_AddressPartType" fixed="UNIT" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UnitType
        extends ADXP
    {


    }

}
