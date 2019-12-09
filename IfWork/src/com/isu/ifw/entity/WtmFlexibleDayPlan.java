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
@Table(name="WTM_FLEXIBLE_DAY_PLAN")
public class WtmFlexibleDayPlan {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FLEXIBLE_DAY_PLAN_ID")
	private Long flexibleDayPlanId;
	
	@Column(name="FLEXIBLE_APPL_ID")
	private Long flexibleApplId;
	@Column(name="YMD")
	private String ymd;
	@Column(name="TIME_CD")
	private String timeCd;
	@Column(name="SHM")
	private String shm;
	@Column(name="EHM")
	private String ehm;
	@Column(name="WORK_MINUTE")
	private String workMinute;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	 
	
	public Long getFlexibleDayPlanId() {
		return flexibleDayPlanId;
	}

	public void setFlexibleDayPlanId(Long flexibleDayPlanId) {
		this.flexibleDayPlanId = flexibleDayPlanId;
	}

	public Long getFlexibleApplId() {
		return flexibleApplId;
	}

	public void setFlexibleApplId(Long flexibleApplId) {
		this.flexibleApplId = flexibleApplId;
	}

	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public String getTimeCd() {
		return timeCd;
	}

	public void setTimeCd(String timeCd) {
		this.timeCd = timeCd;
	}

	public String getShm() {
		return shm;
	}

	public void setShm(String shm) {
		this.shm = shm;
	}

	public String getEhm() {
		return ehm;
	}

	public void setEhm(String ehm) {
		this.ehm = ehm;
	}

	public String getWorkMinute() {
		return workMinute;
	}

	public void setWorkMinute(String workMinute) {
		this.workMinute = workMinute;
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
