package com.isu.ifw.common.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the beio_management_infomation database table.
 * 
 */
@Entity(name="WtmManagementInfomation")
@Table(name="comm_management_infomation")
@NamedQuery(name="CommManagementInfomation.findAll", query="SELECT c FROM WtmManagementInfomation c")
public class CommManagementInfomation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="management_infomation_id")
	private Long managementInfomationId;

	@Lob
	@Column(name="clob_data")
	private String clobData;

	@Column(name="info_data")
	private String infoData;

	@Column(name="info_key")
	private String infoKey;

	@Column(name="info_name")
	private String infoName;

	@Column(name="info_type")
	private String infoType;

	@Column(name="tenant_id")
	private Long tenantId;

	public CommManagementInfomation() {
	}

	public Long getManagementInfomationId() {
		return this.managementInfomationId;
	}

	public void setManagementInfomationId(Long managementInfomationId) {
		this.managementInfomationId = managementInfomationId;
	}

	public String getClobData() {
		return this.clobData;
	}

	public void setClobData(String clobData) {
		this.clobData = clobData;
	}

	public String getInfoData() {
		return this.infoData;
	}

	public void setInfoData(String infoData) {
		this.infoData = infoData;
	}

	public String getInfoKey() {
		return this.infoKey;
	}

	public void setInfoKey(String infoKey) {
		this.infoKey = infoKey;
	}

	public String getInfoName() {
		return this.infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public String getInfoType() {
		return this.infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public Long getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}