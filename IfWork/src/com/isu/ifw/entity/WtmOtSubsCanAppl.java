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
@Table(name="WTM_OT_SUBS_CAN_APPL")
public class WtmOtSubsCanAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_SUBS_CAN_APPL_ID")
	private Long otSubsCanApplId; 
	@Column(name="SUBS_APPL_ID")
	private Long subsApplId;
	@Column(name="APPL_ID")
	private Long applId;
	
	@Column(name="REASON")
	private String reason;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId; 
	
	public Long getOtSubsCanApplId() {
		return otSubsCanApplId;
	}

	public void setOtSubsCanApplId(Long otSubsCanApplId) {
		this.otSubsCanApplId = otSubsCanApplId;
	}

	public Long getSubsApplId() {
		return subsApplId;
	}

	public void setSubsApplId(Long subsApplId) {
		this.subsApplId = subsApplId;
	}

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
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
