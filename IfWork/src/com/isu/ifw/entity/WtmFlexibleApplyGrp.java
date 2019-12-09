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
@Table(name="WTM_FLEXIBLE_APPLY_GROUP")
public class WtmFlexibleApplyGrp {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FLEXIBLE_APPLY_GROUP_ID")
	private Long flexibleApplyGroupId;
	@Column(name="FLEXIBLE_APPLY_ID")
	private Long flexibleApplyId;
	@Column(name="ORG_CD")
	private String orgCd;
	@Column(name="JOB_CD")
	private String jobCd;
	@Column(name="DUTY_CD")
	private String dutyCd;
	@Column(name="POS_CD")
	private String posCd;
	@Column(name="CLASS_CD")
	private String classCd;
	@Column(name="WORKTEAM_CD")
	private String workteamCd;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	
	public Long getFlexibleApplyGroupId() {
		return flexibleApplyGroupId;
	}
	public void setFlexibleApplyGroupId(Long flexibleApplyGroupId) {
		this.flexibleApplyGroupId = flexibleApplyGroupId;
	}
	public Long gettFlexibleApplyId() {
		return flexibleApplyId;
	}
	public void setFlexibleApplyId(Long flexibleApplyId) {
		this.flexibleApplyId = flexibleApplyId;
	}
	public String getOrgCd() {
		return orgCd;
	}
	public void setOrgCd(String orgCd) {
		this.orgCd = orgCd;
	}
	public String getJobCd() {
		return jobCd;
	}
	public void setJobCd(String jobCd) {
		this.jobCd = jobCd;
	}
	public String getDutyCd() {
		return dutyCd;
	}
	public void setDutyCd(String dutyCd) {
		this.dutyCd = dutyCd;
	}
	public String getPosCd() {
		return posCd;
	}
	public void setPosCd(String posCd) {
		this.posCd = posCd;
	}
	public String getClassCd() {
		return classCd;
	}
	public void setClassCd(String classCd) {
		this.classCd = classCd;
	}
	public String getWorkteamCd() {
		return workteamCd;
	}
	public void setWorkteamCd(String workteamCd) {
		this.workteamCd = workteamCd;
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
