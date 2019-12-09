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
@Table(name="WTM_WORK_DAY_RESULT")
public class WtmWorkDayResult {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="WORK_DAY_RESULT_ID")
	private Long workDayResultId; 
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="YMD")
	private String ymd;
	
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="TIME_TYPE_CD")
	private String timeTypeCd;
	@Column(name="TAA_CD")
	private String taaCd; 
	
	@Column(name="PLAN_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planSdate;
	@Column(name="PLAN_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planEdate;
	 
	@Column(name="APPR_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date apprSdate;
	@Column(name="APPR_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date apprEdate;
	
	@Column(name="PLAN_MINUTE")
	private Integer planMinute; 
	@Column(name="APPR_MINUTE")
	private Integer apprMinute;
	
	@Column(name="WORK_YN")
	private String workYn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	public Long getWorkDayResultId() {
		return workDayResultId;
	}
	public void setWorkDayResultId(Long workDayResultId) {
		this.workDayResultId = workDayResultId;
	} 
	
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public String getEnterCd() {
		return enterCd;
	}
	public void setEnterCd(String enterCd) {
		this.enterCd = enterCd;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
	}
	public String getYmd() {
		return ymd;
	}
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	public Long getApplId() {
		return applId;
	}
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	public String getTimeTypeCd() {
		return timeTypeCd;
	}
	public void setTimeTypeCd(String timeTypeCd) {
		this.timeTypeCd = timeTypeCd;
	}
	public String getTaaCd() {
		return taaCd;
	}
	public void setTaaCd(String taaCd) {
		this.taaCd = taaCd;
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
	public Date getApprSdate() {
		return apprSdate;
	}
	public void setApprSdate(Date apprSdate) {
		this.apprSdate = apprSdate;
	}
	public Date getApprEdate() {
		return apprEdate;
	}
	public void setApprEdate(Date apprEdate) {
		this.apprEdate = apprEdate;
	}
	public Integer getPlanMinute() {
		return planMinute;
	}
	public void setPlanMinute(Integer planMinute) {
		this.planMinute = planMinute;
	}
	public Integer getApprMinute() {
		return apprMinute;
	}
	public void setApprMinute(Integer apprMinute) {
		this.apprMinute = apprMinute;
	}
	public String getWorkYn() {
		return workYn;
	}
	public void setWorkYn(String workYn) {
		this.workYn = workYn;
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
