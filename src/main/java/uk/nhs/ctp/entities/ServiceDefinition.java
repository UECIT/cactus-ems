package uk.nhs.ctp.entities;

import javax.persistence.*;

@Entity
@Table(name = "service_definition")
public class ServiceDefinition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cdss_supplier_id", nullable = true)
	private String cdssSupplierId;

	@Column(name = "service_definition_id")
	private String serviceDefinitionId;

	@Column(name = "description")
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCdssSupplierId() {
		return cdssSupplierId;
	}

	public void setCdssSupplierId(String cdssSupplierId) {
		this.cdssSupplierId = cdssSupplierId;
	}
}
