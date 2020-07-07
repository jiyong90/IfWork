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
@Table(name="WTM_COMP_APPL")
public class WtmCompAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COMP_APPL_ID")
	private Long compApplId;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="TAA_CD")
	private String taaCd;
	@Column(name="COMP_SYMD")
	private String compSymd;
	@Column(name="COMP_EYMD")
	private String compEymd;
	@Column(name="COMP_MINUTE")
	private String compMinute;
	@Column(name="REASON")
	private String reason;
	@Column(name="CANCEL_YN")
	private Integer cancelYn;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
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
	public String getTaaCd() {
		return taaCd;
	}
	public void setTaaCd(String taaCd) {
		this.taaCd = taaCd;
	}
	public String getCompSymd() {
		return compSymd;
	}
	public void setCompSymd(String compSymd) {
		this.compSymd = compSymd;
	}
	public String getCompEymd() {
		return compEymd;
	}
	public void setCompEymd(String compEymd) {
		this.compEymd = compEymd;
	}
	public String getCompMinute() {
		return compMinute;
	}
	public void setCompMinute(String compMinute) {
		this.compMinute = compMinute;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getCancelYn() {
		return cancelYn;
	}
	public void setCancelYn(Integer cancelYn) {
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
	
	 
}
