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
@Table(name="WTM_IF_TAA_HIS")
public class WtmIfTaaHis {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="IF_TAA_HIS_ID")
	private Long id;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPL_NO")
	private String applNo;
	@Column(name="IF_YMDHIS")
	private String ifYmdhis;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="WORK_TIME_CODE")
	private String workTimeCode;
	@Column(name="START_YMD")
	private String startYmd;
	@Column(name="END_YMD")
	private String endYmd;
	@Column(name="START_HM")
	private String startHm;
	@Column(name="END_HM")
	private String endHm;
	@Column(name="STATUS")
	private String status;
	@Column(name="IF_STATUS")
	private String ifStatus;
	@Column(name="IF_MSG")
	private String ifMsg;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getApplNo() {
		return applNo;
	}
	public void setApplNo(String applNo) {
		this.applNo = applNo;
	}
	public String getIfYmdhis() {
		return ifYmdhis;
	}
	public void setIfYmdhis(String ifYmdhis) {
		this.ifYmdhis = ifYmdhis;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
	}
	public String getWorkTimeCode() {
		return workTimeCode;
	}
	public void setWorkTimeCode(String workTimeCode) {
		this.workTimeCode = workTimeCode;
	}
	public String getStartYmd() {
		return startYmd;
	}
	public void setStartYmd(String startYmd) {
		this.startYmd = startYmd;
	}
	public String getEndYmd() {
		return endYmd;
	}
	public void setEndYmd(String endYmd) {
		this.endYmd = endYmd;
	}
	public String getStartHm() {
		return startHm;
	}
	public void setStartHm(String startHm) {
		this.startHm = startHm;
	}
	public String getEndHm() {
		return endHm;
	}
	public void setEndHm(String endHm) {
		this.endHm = endHm;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIfStatus() {
		return ifStatus;
	}
	public void setIfStatus(String ifStatus) {
		this.ifStatus = ifStatus;
	}
	public String getIfMsg() {
		return ifMsg;
	}
	public void setIfMsg(String ifMsg) {
		this.ifMsg = ifMsg;
	}
}
