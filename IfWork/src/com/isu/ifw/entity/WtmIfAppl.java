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
@Table(name="WTM_IF_APPL")
public class WtmIfAppl {

	@Id
//	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="APPL_CD")
	private String applCd;
	@Column(name="APPL_SABUN")
	private String applSabun;
	@Column(name="APPL_IN_SABUN")
	private String applInSabun;
	@Column(name="APPL_STATUS_CD")
	private String applStatusCd;
	@Column(name="APPL_YMD")
	private String applYmd;
	@Column(name="APPL_TITLE")
	private String applTitle;
	@Column(name="APPL_BODY")
	private String applBody;
	@Column(name="IF_APPL_NO")
	private String ifApplNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date   updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	@PrePersist
	protected void onCreate() {
		this.updateDate = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = new Date();
	}

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
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

	public String getApplYmd() {
		return applYmd;
	}

	public void setApplYmd(String applYmd) {
		this.applYmd = applYmd;
	}

	public String getApplTitle() {
		return applTitle;
	}

	public void setApplTitle(String applTitle) {
		this.applTitle = applTitle;
	}

	public String getApplBody() {
		return applBody;
	}

	public void setApplBody(String applBody) {
		this.applBody = applBody;
	}

	public String getIfApplNo() {
		return ifApplNo;
	}

	public void setIfApplNo(String ifApplNo) {
		this.ifApplNo = ifApplNo;
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
	
	
}
