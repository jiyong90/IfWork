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
@Table(name="WTM_WORKTEAM_EMP")
public class WtmWorkteamEmp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="WORKTEAM_EMP_ID")
	private Long workteamEmpId;
	@Column(name="WORKTEAM_MGR_ID")
	private Long workteamMgrId;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
    public Long getWorkteamEmpId() {
		return workteamEmpId;
	}


	public void setWorkteamEmpId(Long workteamEmpId) {
		this.workteamEmpId = workteamEmpId;
	}

	public Long getWorkteamMgrId() {
		return workteamMgrId;
	}


	public void setWorkteamMgrId(Long workteamMgrId) {
		this.workteamMgrId = workteamMgrId;
	}


	public String getSabun() {
		return sabun;
	}


	public void setSabun(String sabun) {
		this.sabun = sabun;
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
