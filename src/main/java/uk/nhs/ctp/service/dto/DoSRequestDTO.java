package uk.nhs.ctp.service.dto;

import dos.wsdl.GenderType;

public class DoSRequestDTO {

	private String postcode;
	private int disposition;
	private int symptomGroup;
	private int symptomDiscriminatorInt;
	private int searchDistance;
	private GenderType gender;
	private String service;
	
	public String getRESTUrl() {
		StringBuilder url = new StringBuilder(
				"https://uat.pathwaysdos.nhs.uk/app/controllers/api/v1.0/services/byClinicalTerm/"); // 12/bs11se/20/0/1/M/0/360=14014/5"
		// /caseId123/15/WF32LL/ /1/F/0/360=14023/10 move caseid???
		url.append("caseId123"); // caseId
		url.append("/");
		url.append(this.getPostcode().replaceAll(" ", "")); // postcode
		url.append("/");
		url.append(this.getSearchDistance()); // searchDistance
		url.append("/");
		url.append("0"); // gpPracticeId
		url.append("/");
		url.append(1); // age
		url.append("/");
		url.append(this.getGender()); // gender
		url.append("/");
		url.append(this.getDisposition()); // disposition
		url.append("/");
		url.append("360=" + this.getSymptomDiscriminatorInt()); // symptomGroupDiscriminatorCombos
		url.append("/");
		url.append(10); // numberPerType
		return url.toString();
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public int getDisposition() {
		return disposition;
	}

	public void setDisposition(int disposition) {
		this.disposition = disposition;
	}

	public int getSymptomGroup() {
		return symptomGroup;
	}

	public void setSymptomGroup(int symptomGroup) {
		this.symptomGroup = symptomGroup;
	}

	public int getSymptomDiscriminatorInt() {
		return symptomDiscriminatorInt;
	}

	public void setSymptomDiscriminatorInt(int symptomDiscriminatorInt) {
		this.symptomDiscriminatorInt = symptomDiscriminatorInt;
	}

	public int getSearchDistance() {
		return searchDistance;
	}

	public void setSearchDistance(int searchDistance) {
		this.searchDistance = searchDistance;
	}

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	
	public void deriveGender(String gender) {
		String initial = gender.substring(0, 1).toUpperCase();
		setGender("M".equals(initial) || "F".equals(initial) ? GenderType.valueOf(initial) : GenderType.I);
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

}
