package com.isu.ifw.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="WTM_TAA_CAN_APPL")
public class WtmTaaCanAppl {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="TAA_CAN_APPL_ID")
	private Long   taaCanApplId;
	@Column(name="TENANT_ID")
	private Long   tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date   updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;


	public Long getTaaCanApplId() {
		return taaCanApplId;
	}

	public void setTaaCanApplId(Long taaCanApplId) {
		this.taaCanApplId = taaCanApplId;
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
