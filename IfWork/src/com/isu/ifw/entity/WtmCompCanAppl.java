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
@Table(name="WTM_COMP_CAN_APPL")
public class WtmCompCanAppl {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COMP_CAN_APPL_ID")
	private Long compCanApplId;
	@Column(name="COMP_APPL_ID")
	private Long compApplId;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="REASON")
	private String reason; 
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	public Long getCompCanApplId() {
		return compCanApplId;
	}
	public void setCompCanApplId(Long compCanApplId) {
		this.compCanApplId = compCanApplId;
	}
	public Long getCompApplId() {
		return compApplId;
	}
	public void setCompApplId(Long compApplId) {
		this.compApplId = compApplId;
	}
	public Long getApplId() {
		return applId;
	}
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
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
