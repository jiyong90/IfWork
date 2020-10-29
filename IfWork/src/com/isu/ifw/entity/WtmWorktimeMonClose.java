package com.isu.ifw.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * WTM_ANNUAL_CREATE
 *
 * @author
 */
@Entity
@Table(name = "WTM_WORKTIME_MON_CLOSE")
public class WtmWorktimeMonClose {


	@EmbeddedId
	private WtmWorktimeMonClosePK id;
	  

	@Column(name = "SYMD")
	private String symd;
	@Column(name = "EYMD")
	private String eymd;

	@Column(name = "BASE_MINUTE")
	private Integer baseMinute;
	
	@Column(name = "BASE_OT_MINUTE")
	private Integer baseOtMinute;
	
	@Column(name = "FIX_OT_MINUTE")
	private Integer fixOtMinute;
	
	@Column(name = "WORK_MINUTE")
	private Integer workMinute;

	@Column(name = "OT_MINUTE")
	private Integer otMinute;
	
	@Column(name = "OTN_MINUTE")
	private Integer otnMinute;
	
	@Column(name = "HOL_MINUTE")
	private Integer holMinute;
	
	@Column(name = "HOL_OT_MINUTE")
	private Integer holOtMinute;
	
	@Column(name = "A_WORK_MINUTE")
	private Integer aWorkMinute;
	
	@Column(name = "A_OT_MINUTE")
	private Integer aOtMinute;
	
	@Column(name = "A_OTN_MINUTE")
	private Integer aOtnMinute;
	
	@Column(name = "A_HOL_MINUTE")
	private Integer aHolMinute;
	
	@Column(name = "A_HOL_OT_MINUTE")
	private Integer aHolOtMinute;
	
	@Column(name = "A_NONPAY_MINUTE")
	private Integer aNonpayMinute;
	
	@Column(name = "A_PAY_MINUTE")
	private Integer aPayMinute;
	
	@Column(name = "LATE_MINUTE")
	private Integer lateMinute;
	
	@Column(name = "LEAVE_MINUTE")
	private Integer leaveMinute;
	
	@Column(name = "ABSENCE_MINUTE")
	private Integer absenceMinute; 
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ID")
	private String updateId;

	@PrePersist
	protected void onCreate() {
		this.updateDate = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = new Date();
	}

	public WtmWorktimeMonClosePK getId() {
		return id;
	}

	public void setId(WtmWorktimeMonClosePK id) {
		this.id = id;
	}

	public String getSymd() {
		return symd;
	}

	public void setSymd(String symd) {
		this.symd = symd;
	}

	public String getEymd() {
		return eymd;
	}

	public void setEymd(String eymd) {
		this.eymd = eymd;
	}

	public Integer getBaseMinute() {
		return baseMinute;
	}

	public void setBaseMinute(Integer baseMinute) {
		this.baseMinute = baseMinute;
	}

	public Integer getBaseOtMinute() {
		return baseOtMinute;
	}

	public void setBaseOtMinute(Integer baseOtMinute) {
		this.baseOtMinute = baseOtMinute;
	}

	public Integer getFixOtMinute() {
		return fixOtMinute;
	}

	public void setFixOtMinute(Integer fixOtMinute) {
		this.fixOtMinute = fixOtMinute;
	}

	public Integer getWorkMinute() {
		return workMinute;
	}

	public void setWorkMinute(Integer workMinute) {
		this.workMinute = workMinute;
	}

	public Integer getOtMinute() {
		return otMinute;
	}

	public void setOtMinute(Integer otMinute) {
		this.otMinute = otMinute;
	}

	public Integer getOtnMinute() {
		return otnMinute;
	}

	public void setOtnMinute(Integer otnMinute) {
		this.otnMinute = otnMinute;
	}

	public Integer getHolMinute() {
		return holMinute;
	}

	public void setHolMinute(Integer holMinute) {
		this.holMinute = holMinute;
	}

	public Integer getHolOtMinute() {
		return holOtMinute;
	}

	public void setHolOtMinute(Integer holOtMinute) {
		this.holOtMinute = holOtMinute;
	}

	public Integer getaWorkMinute() {
		return aWorkMinute;
	}

	public void setaWorkMinute(Integer aWorkMinute) {
		this.aWorkMinute = aWorkMinute;
	}

	public Integer getaOtMinute() {
		return aOtMinute;
	}

	public void setaOtMinute(Integer aOtMinute) {
		this.aOtMinute = aOtMinute;
	}

	public Integer getaOtnMinute() {
		return aOtnMinute;
	}

	public void setaOtnMinute(Integer aOtnMinute) {
		this.aOtnMinute = aOtnMinute;
	}

	public Integer getaHolMinute() {
		return aHolMinute;
	}

	public void setaHolMinute(Integer aHolMinute) {
		this.aHolMinute = aHolMinute;
	}

	public Integer getaHolOtMinute() {
		return aHolOtMinute;
	}

	public void setaHolOtMinute(Integer aHolOtMinute) {
		this.aHolOtMinute = aHolOtMinute;
	}

	public Integer getaNonpayMinute() {
		return aNonpayMinute;
	}

	public void setaNonpayMinute(Integer aNonpayMinute) {
		this.aNonpayMinute = aNonpayMinute;
	}

	public Integer getaPayMinute() {
		return aPayMinute;
	}

	public void setaPayMinute(Integer aPayMinute) {
		this.aPayMinute = aPayMinute;
	}

	public Integer getLateMinute() {
		return lateMinute;
	}

	public void setLateMinute(Integer lateMinute) {
		this.lateMinute = lateMinute;
	}

	public Integer getLeaveMinute() {
		return leaveMinute;
	}

	public void setLeaveMinute(Integer leaveMinute) {
		this.leaveMinute = leaveMinute;
	}

	public Integer getAbsenceMinute() {
		return absenceMinute;
	}

	public void setAbsenceMinute(Integer absenceMinute) {
		this.absenceMinute = absenceMinute;
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