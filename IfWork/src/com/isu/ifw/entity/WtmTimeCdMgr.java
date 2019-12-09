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
@Table(name="WTM_TIME_CD_MGR")
public class WtmTimeCdMgr {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TIME_CD_MGR_ID")
	private Long timeCdMgrId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="TIME_CD")
	private String timeCd;
	@Column(name="TIME_NM")
	private String timeNm;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="WORK_SHM")
	private String workShm;
	@Column(name="WORK_EHM")
	private String workEhm;
	@Column(name="HOL_YN")
	private String holYn;
	@Column(name="BREAK_TYPE_CD")
	private String breakTypeCd;
	@Column(name="HOL_TIME_CD_MGR_ID")
	private Long holTimeCdMgrId;
	@Column(name="LATE_CHK_YN")
	private String lateChkYn;
	@Column(name="LEAVE_CHK_YN")
	private String leaveChkYn;
	@Column(name="ABSENCE_CHK_YN")
	private String absenceChkYn;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	
	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}


	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
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


	public String getTimeCd() {
		return timeCd;
	}


	public void setTimeCd(String timeCd) {
		this.timeCd = timeCd;
	}


	public String getTimeNm() {
		return timeNm;
	}


	public void setTimeNm(String timeNm) {
		this.timeNm = timeNm;
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


	public String getWorkShm() {
		return workShm;
	}


	public void setWorkShm(String workShm) {
		this.workShm = workShm;
	}


	public String getWorkEhm() {
		return workEhm;
	}


	public void setWorkEhm(String workEhm) {
		this.workEhm = workEhm;
	}


	public String getHolYn() {
		return holYn;
	}


	public void setHolYn(String holYn) {
		this.holYn = holYn;
	}

	
	public String getBreakTypeCd() {
		return breakTypeCd;
	}


	public void setBreakTypeCd(String breakTypeCd) {
		this.breakTypeCd = breakTypeCd;
	}


	public Long getHolTimeCdMgrId() {
		return holTimeCdMgrId;
	}


	public void setHolTimeCdMgrId(Long holTimeCdMgrId) {
		this.holTimeCdMgrId = holTimeCdMgrId;
	}


	public String getLateChkYn() {
		return lateChkYn;
	}


	public void setLateChkYn(String lateChkYn) {
		this.lateChkYn = lateChkYn;
	}


	public String getLeaveChkYn() {
		return leaveChkYn;
	}


	public void setLeaveChkYn(String leaveChkYn) {
		this.leaveChkYn = leaveChkYn;
	}


	public String getAbsenceChkYn() {
		return absenceChkYn;
	}


	public void setAbsenceChkYn(String absenceChkYn) {
		this.absenceChkYn = absenceChkYn;
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
