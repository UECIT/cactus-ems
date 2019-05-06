package uk.nhs.ctp.service.report.dos.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "type", "odsCode", "address", "postcode", "easting", "northing", "phone", "web",
		"openingTimes", "referralInstructions", "capacity", "endpoints", "patientDistance" })
public class Service {

	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("type")
	private Type type;
	@JsonProperty("odsCode")
	private String odsCode;
	@JsonProperty("address")
	private List<String> address = null;
	@JsonProperty("postcode")
	private String postcode;
	@JsonProperty("easting")
	private String easting;
	@JsonProperty("northing")
	private String northing;
	@JsonProperty("phone")
	private Phone phone;
	@JsonProperty("web")
	private String web;
	@JsonProperty("openingTimes")
	private OpeningTimes openingTimes;
	@JsonProperty("referralInstructions")
	private ReferralInstructions referralInstructions;
	@JsonProperty("capacity")
	private Capacity capacity;
	@JsonProperty("endpoints")
	private List<Object> endpoints = null;
	@JsonProperty("patientDistance")
	private String patientDistance;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("type")
	public Type getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(Type type) {
		this.type = type;
	}

	@JsonProperty("odsCode")
	public String getOdsCode() {
		return odsCode;
	}

	@JsonProperty("odsCode")
	public void setOdsCode(String odsCode) {
		this.odsCode = odsCode;
	}

	@JsonProperty("address")
	public List<String> getAddress() {
		return address;
	}

	@JsonProperty("address")
	public void setAddress(List<String> address) {
		this.address = address;
	}

	@JsonProperty("postcode")
	public String getPostcode() {
		return postcode;
	}

	@JsonProperty("postcode")
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@JsonProperty("easting")
	public String getEasting() {
		return easting;
	}

	@JsonProperty("easting")
	public void setEasting(String easting) {
		this.easting = easting;
	}

	@JsonProperty("northing")
	public String getNorthing() {
		return northing;
	}

	@JsonProperty("northing")
	public void setNorthing(String northing) {
		this.northing = northing;
	}

	@JsonProperty("phone")
	public Phone getPhone() {
		return phone;
	}

	@JsonProperty("phone")
	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	@JsonProperty("web")
	public String getWeb() {
		return web;
	}

	@JsonProperty("web")
	public void setWeb(String web) {
		this.web = web;
	}

	@JsonProperty("openingTimes")
	public OpeningTimes getOpeningTimes() {
		return openingTimes;
	}

	@JsonProperty("openingTimes")
	public void setOpeningTimes(OpeningTimes openingTimes) {
		this.openingTimes = openingTimes;
	}

	@JsonProperty("referralInstructions")
	public ReferralInstructions getReferralInstructions() {
		return referralInstructions;
	}

	@JsonProperty("referralInstructions")
	public void setReferralInstructions(ReferralInstructions referralInstructions) {
		this.referralInstructions = referralInstructions;
	}

	@JsonProperty("capacity")
	public Capacity getCapacity() {
		return capacity;
	}

	@JsonProperty("capacity")
	public void setCapacity(Capacity capacity) {
		this.capacity = capacity;
	}

	@JsonProperty("endpoints")
	public List<Object> getEndpoints() {
		return endpoints;
	}

	@JsonProperty("endpoints")
	public void setEndpoints(List<Object> endpoints) {
		this.endpoints = endpoints;
	}

	@JsonProperty("patientDistance")
	public String getPatientDistance() {
		return patientDistance;
	}

	@JsonProperty("patientDistance")
	public void setPatientDistance(String patientDistance) {
		this.patientDistance = patientDistance;
	}

	/*
	 * Inner classes
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "id", "name" })
	/**
	 * Type
	 * 
	 * @author wilcockl
	 *
	 */
	public class Type {

		@JsonProperty("id")
		private String id;
		@JsonProperty("name")
		private String name;

		@JsonProperty("id")
		public String getId() {
			return id;
		}

		@JsonProperty("id")
		public void setId(String id) {
			this.id = id;
		}

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "public", "nonPublic", "fax" })
	/**
	 * Phone
	 * 
	 * @author wilcockl
	 *
	 */
	public class Phone {

		@JsonProperty("public")
		private String publicPhone;
		@JsonProperty("nonPublic")
		private String nonPublic;
		@JsonProperty("fax")
		private String fax;

		@JsonProperty("public")
		public String getPublicPhone() {
			return publicPhone;
		}

		@JsonProperty("public")
		public void setPublicPhone(String publicPhone) {
			this.publicPhone = publicPhone;
		}

		@JsonProperty("nonPublic")
		public String getNonPublic() {
			return nonPublic;
		}

		@JsonProperty("nonPublic")
		public void setNonPublic(String nonPublic) {
			this.nonPublic = nonPublic;
		}

		@JsonProperty("fax")
		public String getFax() {
			return fax;
		}

		@JsonProperty("fax")
		public void setFax(String fax) {
			this.fax = fax;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "callHandler", "other" })
	/**
	 * ReferralInstructions
	 * 
	 * @author wilcockl
	 *
	 */
	public class ReferralInstructions {

		@JsonProperty("callHandler")
		private String callHandler;
		@JsonProperty("other")
		private String other;

		@JsonProperty("callHandler")
		public String getCallHandler() {
			return callHandler;
		}

		@JsonProperty("callHandler")
		public void setCallHandler(String callHandler) {
			this.callHandler = callHandler;
		}

		@JsonProperty("other")
		public String getOther() {
			return other;
		}

		@JsonProperty("other")
		public void setOther(String other) {
			this.other = other;
		}
	}

}
