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
@Table(name="WTM_OT_SUBS_APPL")
public class WtmOtSubsAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_SUBS_APPL_ID")
	private Long otSubsApplId; 
	@Column(name="OT_APPL_ID")
	private Long otApplId;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="OLD_SUBS_APPL_ID")
	private Long oldSubsApplId;
	
	@Column(name="SUB_YMD")
	private String subYmd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBS_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date subsSdate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBS_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date subsEdate;
	
	@Column(name="SUBS_MINUTE")
	private String subsMinute;
	@Column(name="CANCEL_YN")
	private String cancelYn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId; 
	
	public Long getOtSubsApplId() {
		return otSubsApplId;
	}

	public void setOtSubsApplId(Long otSubsApplId) {
		this.otSubsApplId = otSubsApplId;
	}

	public Long getOtApplId() {
		return otApplId;
	}

	public void setOtApplId(Long otApplId) {
		this.otApplId = otApplId;
	}

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
	}

	public Long getOldSubsApplId() {
		return oldSubsApplId;
	}

	public void setOldSubsApplId(Long oldSubsApplId) {
		this.oldSubsApplId = oldSubsApplId;
	}

	public String getSubYmd() {
		return subYmd;
	}

	public void setSubYmd(String subYmd) {
		this.subYmd = subYmd;
	}
	
	public Date getSubsSdate() {
		return subsSdate;
	}

	public void setSubsSdate(Date subsSdate) {
		this.subsSdate = subsSdate;
	}

	public Date getSubsEdate() {
		return subsEdate;
	}

	public void setSubsEdate(Date subsEdate) {
		this.subsEdate = subsEdate;
	}

	public String getSubsMinute() {
		return subsMinute;
	}

	public void setSubsMinute(String subsMinute) {
		this.subsMinute = subsMinute;
	}

	public String getCancelYn() {
		return cancelYn;
	}

	public void setCancelYn(String cancelYn) {
		this.cancelYn = cancelYn;
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
