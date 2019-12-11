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
@Table(name="WTM_PUSH_SEND_HIS")
public class WtmPushSendHis {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PUSH_SEND_HIS_ID")
	private Long pushSendHisId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="UNIT_TYPE_NM")
	private String unitTypeNm;
	@Column(name="STD_TYPE")
	private String stdType;
	@Column(name="SEND_TYPE")
	private String sendType;
	@Column(name="SEND_SABUN")
	private String sendSabun;
	@Column(name="RECEIVE_SABUN")
	private Integer receiveSabun;
	@Column(name="RECEIVE_MAIL")
	private String receiveMail;
	@Column(name="SEND_MSG")
	private String sendMsg;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
		
	public Long getPushSendHisId() {
		return pushSendHisId;
	}

	public void setPushSendHisId(Long pushSendHisId) {
		this.pushSendHisId = pushSendHisId;
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

	public String getUnitTypeNm() {
		return unitTypeNm;
	}

	public void setUnitTypeNm(String unitTypeNm) {
		this.unitTypeNm = unitTypeNm;
	}

	public String getStdType() {
		return stdType;
	}

	public void setStdType(String stdType) {
		this.stdType = stdType;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getSendSabun() {
		return sendSabun;
	}

	public void setSendSabun(String sendSabun) {
		this.sendSabun = sendSabun;
	}

	public Integer getReceiveSabun() {
		return receiveSabun;
	}

	public void setReceiveSabun(Integer receiveSabun) {
		this.receiveSabun = receiveSabun;
	}

	public String getReceiveMail() {
		return receiveMail;
	}

	public void setReceiveMail(String receiveMail) {
		this.receiveMail = receiveMail;
	}

	public String getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
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
