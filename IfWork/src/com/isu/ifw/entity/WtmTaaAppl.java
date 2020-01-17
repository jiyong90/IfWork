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
@Table(name="WTM_TAA_APPL")
public class WtmTaaAppl {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TAA_APPL_ID")
	private Long taaApplId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="IF_APPL_NO")
	private String ifApplNo;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	public Long getTaaApplId() {
		return taaApplId;
	}


	public void setTaaApplId(Long taaApplId) {
		this.taaApplId = taaApplId;
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


	public Long getApplId() {
		return applId;
	}


	public void setApplId(Long applId) {
		this.applId = applId;
	}


	public String getSabun() {
		return sabun;
	}


	public void setSabun(String sabun) {
		this.sabun = sabun;
	}


	public String getIfApplNo() {
		return ifApplNo;
	}


	public void setIfApplNo(String ifApplNo) {
		this.ifApplNo = ifApplNo;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
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
