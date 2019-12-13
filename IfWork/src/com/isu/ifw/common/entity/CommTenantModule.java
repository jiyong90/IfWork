package com.isu.ifw.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * The persistent class for the comm_tenant_module database table.
 * 
 */
@Entity(name="WtmTenantModule")
@Table(name="comm_tenant_module")
@NamedQuery(name="CommTenantModule.findAll", query="SELECT c FROM WtmTenantModule c")
public class CommTenantModule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="tenant_module_id")
	private Long tenantModuleId;

	@Lob
	@Column(name="module_value")
	private String moduleValue;

	private String note;

	@Column(name="tenant_id")
	private Long tenantId;
	
	@Column(name="tenant_key")
	private String tenantKey;
	
	@Column(name="api_allowed_ips")
	private String apiAllowedIps;
	
	@Column(name="api_key")
	private String apiKey;
	
	private String secret;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_date", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;

	//bi-directional many-to-one association to CommModule
	@ManyToOne
	@JoinColumn(name="module_id")
	private CommModule commModule;

	public CommTenantModule() {
	}

	@PreUpdate
    protected void onUpdate() {
    	this.updateDate = new Date();
    }
	
	public Long getTenantModuleId() {
		return this.tenantModuleId;
	}

	public void setTenantModuleId(Long tenantModuleId) {
		this.tenantModuleId = tenantModuleId;
	}

	public String getModuleValue() {
		return this.moduleValue;
	}

	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public CommModule getCommModule() {
		return this.commModule;
	}

	public void setCommModule(CommModule commModule) {
		this.commModule = commModule;
	}

	public String getTenantKey() {
		return tenantKey;
	}

	public void setTenantKey(String tenantKey) {
		this.tenantKey = tenantKey;
	}

	public String getApiAllowedIps() {
		return apiAllowedIps;
	}

	public void setApiAllowedIps(String apiAllowedIps) {
		this.apiAllowedIps = apiAllowedIps;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	
}