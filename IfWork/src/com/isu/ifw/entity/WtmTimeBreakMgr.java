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
@Table(name="WTM_TIME_BREAK_MGR")
public class WtmTimeBreakMgr {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TIME_BREAK_MGR_ID")
	private Long timeBreakMgrId;
	@Column(name="TIME_CD_MGR_ID")
	private Long timeCdMgrId;
	@Column(name="BREAK_TIME_CD")
	private String breakTimeCd;
	@Column(name="SHM")
	private String shm;
	@Column(name="EHM")
	private String ehm;
	@Column(name="SEQ")
	private Integer seq;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	public Long getTimeBreakMgrId() {
		return timeBreakMgrId;
	}
	public void setTimeBreakMgrId(Long timeBreakMgrId) {
		this.timeBreakMgrId = timeBreakMgrId;
	}
	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}
	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
	}
	public String getBreakTimeCd() {
		return breakTimeCd;
	}
	public void setBreakTimeCd(String breakTimeCd) {
		this.breakTimeCd = breakTimeCd;
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
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
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
