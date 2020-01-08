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
@Table(name="WTM_TIME_CHG_HIS")
public class WtmTimeChgHis {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TIME_CHG_HIS_ID")
	private Long timeChgHisId; 
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="YMD")
	private String ymd;
	@Column(name="TIME_CD_MGR_ID")
	private Long timeCdMgrId;
	@Column(name="TIME_TYPE_CD")
	private String timeTypeCd;
	
	@Column(name="PLAN_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planSdate;
	@Column(name="PLAN_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date planEdate;
	
	@Column(name="PLAN_MINUTE")
	private Integer planMinute; 

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	public Long getTimeChgHisId() {
		return timeChgHisId;
	}

	public void setTimeChgHisId(Long timeChgHisId) {
		this.timeChgHisId = timeChgHisId;
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

	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}

	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
	}

	public String getTimeTypeCd() {
		return timeTypeCd;
	}

	public void setTimeTypeCd(String timeTypeCd) {
		this.timeTypeCd = timeTypeCd;
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

	public Integer getPlanMinute() {
		return planMinute;
	}

	public void setPlanMinute(Integer planMinute) {
		this.planMinute = planMinute;
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
