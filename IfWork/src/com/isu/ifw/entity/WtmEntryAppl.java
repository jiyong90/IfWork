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
@Table(name="WTM_ENTRY_APPL")
public class WtmEntryAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ENTRY_APPL_ID")
	private Long entryApplId;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="YMD")
	private String ymd;
	@Column(name="PLAN_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planSdate;
	@Column(name="PLAN_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planEdate;
	@Column(name="ENTRY_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date entrySdate;
	@Column(name="ENTRY_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date entryEdate;
	@Column(name="CHG_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date chgSdate;
	@Column(name="CHG_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date chgEdate;
	@Column(name="REASON")
	private String reason;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	public Long getEntryApplId() {
		return entryApplId;
	}

	public void setEntryApplId(Long entryApplId) {
		this.entryApplId = entryApplId;
	}

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
	}

	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public Date getPlanSdate() {
		return planSdate;
	}

	public void setPlanSdate(Date planSdate) {
		this.planSdate = planSdate;
	}

	public Date getPlanEdate() {
		return planEdate;
	}

	public void setPlanEdate(Date planEdate) {
		this.planEdate = planEdate;
	}

	public Date getEntrySdate() {
		return entrySdate;
	}

	public void setEntrySdate(Date entrySdate) {
		this.entrySdate = entrySdate;
	}

	public Date getEntryEdate() {
		return entryEdate;
	}

	public void setEntryEdate(Date entryEdate) {
		this.entryEdate = entryEdate;
	}

	public Date getChgSdate() {
		return chgSdate;
	}

	public void setChgSdate(Date chgSdate) {
		this.chgSdate = chgSdate;
	}

	public Date getChgEdate() {
		return chgEdate;
	}

	public void setChgEdate(Date chgEdate) {
		this.chgEdate = chgEdate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
