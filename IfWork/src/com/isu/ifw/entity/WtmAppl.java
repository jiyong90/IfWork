package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="WTM_APPL")
public class WtmAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPL_CD")
	private String applCd;
	@Column(name="APPL_SABUN")
	private String applSabun;
	@Column(name="APPL_YMD")
	private String applYmd;
	@Column(name="APPL_IN_SABUN")
	private String applInSabun;
	@Column(name="APPL_STATUS_CD")
	private String applStatusCd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	public Long getApplId() {
		return applId;
	}
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public String getEnterCd() {
		return enterCd;
	}
	public void setEnterCd(String enterCd) {
		this.enterCd = enterCd;
	}
	public String getApplCd() {
		return applCd;
	}
	public void setApplCd(String applCd) {
		this.applCd = applCd;
	}
	public String getApplSabun() {
		return applSabun;
	}
	public void setApplSabun(String applSabun) {
		this.applSabun = applSabun;
	}
	public String getApplYmd() {
		return applYmd;
	}
	public void setApplYmd(String applYmd) {
		this.applYmd = applYmd;
	}
	public String getApplInSabun() {
		return applInSabun;
	}
	public void setApplInSabun(String applInSabun) {
		this.applInSabun = applInSabun;
	}
	public String getApplStatusCd() {
		return applStatusCd;
	}
	public void setApplStatusCd(String applStatusCd) {
		this.applStatusCd = applStatusCd;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateId() {
		return updateId;
	}
	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}
	
	@PrePersist
    protected void onCreate() {
		this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
    	this.updateDate = new Date();
    }

	
	
	
}
