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
@Table(name="WTM_APPL_LINE")
public class WtmApplLine {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="APPL_DET_ID")
	private Long applDetId;
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="APPR_SEQ")
	private int apprSeq;
	@Column(name="APPR_SABUN")
	private String apprSabun;
	@Column(name="APPR_DATE")
	private String apprDate;
	@Column(name="APPR_STATUS_CD")
	private String apprStatusCd;
	@Column(name="APPR_TYPE_CD")
	private String apprTypeCd;
	@Column(name="APPR_OPINION")
	private String apprOpinion;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	public Long getApplDetId() {
		return applDetId;
	}
	public void setApplDetId(Long applDetId) {
		this.applDetId = applDetId;
	}
	public Long getApplId() {
		return applId;
	}
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	public int getApprSeq() {
		return apprSeq;
	}
	public void setApprSeq(int apprSeq) {
		this.apprSeq = apprSeq;
	}
	public String getApprSabun() {
		return apprSabun;
	}
	public void setApprSabun(String apprSabun) {
		this.apprSabun = apprSabun;
	}
	public String getApprDate() {
		return apprDate;
	}
	public void setApprDate(String apprDate) {
		this.apprDate = apprDate;
	}
	public String getApprStatusCd() {
		return apprStatusCd;
	}
	public void setApprStatusCd(String apprStatusCd) {
		this.apprStatusCd = apprStatusCd;
	}
	public String getApprTypeCd() {
		return apprTypeCd;
	}
	public void setApprTypeCd(String apprTypeCd) {
		this.apprTypeCd = apprTypeCd;
	}
	public String getApprOpinion() {
		return apprOpinion;
	}
	public void setApprOpinion(String apprOpinion) {
		this.apprOpinion = apprOpinion;
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
