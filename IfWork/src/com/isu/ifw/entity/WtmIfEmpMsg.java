package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="WTM_IF_EMP_MSG")
public class WtmIfEmpMsg {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="IF_EMP_MSG_ID")
	private Long ifEmpMsgId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="CHG_YMD")
	private String chgYmd;
	@Column(name="CHG_TYPE_CD")
	private String chgTypeCd;
	@Column(name="OLD_VALUE")
	private String oldValue;
	@Column(name="NEW_VALUE")
	private String newValue;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;


	public Long getIfEmpMsgId() {
		return ifEmpMsgId;
	}


	public void setIfEmpMsgId(Long ifEmpMsgId) {
		this.ifEmpMsgId = ifEmpMsgId;
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


	public String getSabun() {
		return sabun;
	}


	public void setSabun(String sabun) {
		this.sabun = sabun;
	}


	public String getChgYmd() {
		return chgYmd;
	}


	public void setChgYmd(String chgYmd) {
		this.chgYmd = chgYmd;
	}
	
	public String getChgTypeCd() {
		return chgTypeCd;
	}


	public void setChgTypeCd(String chgTypeCd) {
		this.chgTypeCd = chgTypeCd;
	}

	public String getOldValue() {
		return oldValue;
	}


	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}


	public String getNewValue() {
		return newValue;
	}


	public void setNewValue(String newValue) {
		this.newValue = newValue;
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
    protected void onUpdate() {
		this.updateDate = new Date();
    }
}
