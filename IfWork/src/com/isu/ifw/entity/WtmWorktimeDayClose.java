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
@Table(name = "WTM_WORKTIME_DAY_CLOSE")
public class WtmWorktimeDayClose {


	@EmbeddedId
	private WtmWorktimeDayClosePK id;
	  

	@Column(name = "WORK_TYPE_CD")
	private String workTypeCd;

	@Column(name = "TIME_CD_MGR_ID")
	private Long timeCdMgrId;

	@Column(name = "WORK_MINUTE")
	private Integer workMinute;

	@Column(name = "OT_MINUTE")
	private Integer otMinute;
	
	@Column(name = "OTN_MINUTE")
	private Integer otnMinute;
	
	@Column(name = "NONPAY_MINUTE")
	private Integer nonpayMinute;
	
	@Column(name = "PAY_MINUTE")
	private Integer payMinute;
	
	@Column(name = "LATE_MINUTE")
	private Integer lateMinute;
	
	@Column(name = "LEAVE_MINUTE")
	private Integer leaveMinute;
	
	@Column(name = "ABSENCE_MINUTE")
	private Integer absenceMinute; 

	@Column(name = "HOLIDAY_YN")
	private String holidayYn;
	
	@Column(name = "SUB_YN")
	private String subYn;

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

	public WtmWorktimeDayClosePK getId() {
		return id;
	}

	public void setId(WtmWorktimeDayClosePK id) {
		this.id = id;
	}

	public String getWorkTypeCd() {
		return workTypeCd;
	}

	public void setWorkTypeCd(String workTypeCd) {
		this.workTypeCd = workTypeCd;
	}

	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}

	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
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

	public Integer getNonpayMinute() {
		return nonpayMinute;
	}

	public void setNonpayMinute(Integer nonpayMinute) {
		this.nonpayMinute = nonpayMinute;
	}

	public Integer getPayMinute() {
		return payMinute;
	}

	public void setPayMinute(Integer payMinute) {
		this.payMinute = payMinute;
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

	public String getHolidayYn() {
		return holidayYn;
	}

	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}

	public String getSubYn() {
		return subYn;
	}

	public void setSubYn(String subYn) {
		this.subYn = subYn;
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