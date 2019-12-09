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
@Table(name="WTM_TIME_BREAK_TIME")
public class WtmTimeBreakTime {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TIME_BREAK_TIME_ID")
	private Long timeBreakTimeId;
	@Column(name="TIME_CD_MGR_ID")
	private Long timeCdMgrId;
	@Column(name="WORK_MINUTE")
	private String workMinute;
	@Column(name="BREAK_MINUTE")
	private String breakMinute;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	

	public Long getTimeBreakTimeId() {
		return timeBreakTimeId;
	}
	public void setTimeBreakTimeId(Long timeBreakTimeId) {
		this.timeBreakTimeId = timeBreakTimeId;
	}
	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}
	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
	}
	public String getWorkMinute() {
		return workMinute;
	}
	public void setWorkMinute(String workMinute) {
		this.workMinute = workMinute;
	}
	public String getBreakMinute() {
		return breakMinute;
	}
	public void setBreakMinute(String breakMinute) {
		this.breakMinute = breakMinute;
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
