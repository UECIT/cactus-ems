package uk.nhs.ctp.service.report.dos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status" })
public class Capacity {

	@JsonProperty("status")
	private Status status;

	@JsonProperty("status")
	public Status getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(Status status) {
		this.status = status;
	}

	/*
	 * Inner classes
	 */

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "rag", "human", "hex" })
	/**
	 * Status
	 * 
	 * @author wilcockl
	 *
	 */
	public class Status {

		@JsonProperty("rag")
		private String rag;
		@JsonProperty("human")
		private String human;
		@JsonProperty("hex")
		private String hex;

		@JsonProperty("rag")
		public String getRag() {
			return rag;
		}

		@JsonProperty("rag")
		public void setRag(String rag) {
			this.rag = rag;
		}

		@JsonProperty("human")
		public String getHuman() {
			return human;
		}

		@JsonProperty("human")
		public void setHuman(String human) {
			this.human = human;
		}

		@JsonProperty("hex")
		public String getHex() {
			return hex;
		}

		@JsonProperty("hex")
		public void setHex(String hex) {
			this.hex = hex;
		}

	}

}
