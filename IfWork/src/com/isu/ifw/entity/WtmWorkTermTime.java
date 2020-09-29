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
@Table(name="WTM_WORK_TERM_TIME")
public class WtmWorkTermTime {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="WORK_TERM_TIME_ID")
	private Long workTermTimeId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	
	@Column(name="WORK_TYPE_CD")
	private String workTypeCd;
	
	@Column(name="FLEXIBLE_SDATE")
	private String flexibleSdate;
	@Column(name="FLEXIBLE_EDATE")
	private String flexibleEdate;
	
	@Column(name="WEEK_SDATE")
	private String weekSdate;
	@Column(name="WEEK_EDATE")
	private String weekEdate;	
	
	@Column(name="AVL_MINUTE")
	private int avlMinute;
	@Column(name="PLAN_WORK_MINUTE")
	private int planWorkMinute;
	
	@Column(name="PLAN_OT_MINUTE")
	private int planOtMinute;

	@Column(name="APPR_WORK_MINUTE")
	private int apprWorkMinute;
	
	@Column(name="APPR_OT_MINUTE")
	private int apprOtMinute;
	
	@Column(name="NOW_WORK_MINUTE")
	private int nowWorkMinute;
	
	@Column(name="NOW_OT_MINUTE")
	private int nowOtMinute;
	
	@Column(name="NOTE")
	private String note;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	public Long getWorkTermTimeId() {
		return workTermTimeId;
	}
	public void setWorkTermTimeId(Long workTermTimeId) {
		this.workTermTimeId = workTermTimeId;
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
	public String getWorkTypeCd() {
		return workTypeCd;
	}
	public void setWorkTypeCd(String workTypeCd) {
		this.workTypeCd = workTypeCd;
	}
	public String getFlexibleSdate() {
		return flexibleSdate;
	}
	public void setFlexibleSdate(String flexibleSdate) {
		this.flexibleSdate = flexibleSdate;
	}
	public String getFlexibleEdate() {
		return flexibleEdate;
	}
	public void setFlexibleEdate(String flexibleEdate) {
		this.flexibleEdate = flexibleEdate;
	}
	public String getWeekSdate() {
		return weekSdate;
	}
	public void setWeekSdate(String weekSdate) {
		this.weekSdate = weekSdate;
	}
	public String getWeekEdate() {
		return weekEdate;
	}
	public void setWeekEdate(String weekEdate) {
		this.weekEdate = weekEdate;
	} 
	
	public int getAvlMinute() {
		return avlMinute;
	}
	public void setAvlMinute(int avlMinute) {
		this.avlMinute = avlMinute;
	}
	public int getPlanWorkMinute() {
		return planWorkMinute;
	}
	public void setPlanWorkMinute(int planWorkMinute) {
		this.planWorkMinute = planWorkMinute;
	}
	public int getPlanOtMinute() {
		return planOtMinute;
	}
	public void setPlanOtMinute(int planOtMinute) {
		this.planOtMinute = planOtMinute;
	}
	public int getApprWorkMinute() {
		return apprWorkMinute;
	}
	public void setApprWorkMinute(int apprWorkMinute) {
		this.apprWorkMinute = apprWorkMinute;
	}
	public int getApprOtMinute() {
		return apprOtMinute;
	}
	public void setApprOtMinute(int apprOtMinute) {
		this.apprOtMinute = apprOtMinute;
	}
	public int getNowWorkMinute() {
		return nowWorkMinute;
	}
	public void setNowWorkMinute(int nowWorkMinute) {
		this.nowWorkMinute = nowWorkMinute;
	}
	public int getNowOtMinute() {
		return nowOtMinute;
	}
	public void setNowOtMinute(int nowOtMinute) {
		this.nowOtMinute = nowOtMinute;
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

	@Override
	public String toString() {
		return "tenantId="+this.tenantId+","
				+"enterCd="+this.enterCd+","
				+"sabun="+this.sabun+","
				+"workTypeCd="+this.workTypeCd+","
				+"flexibleSdate="+this.flexibleSdate+","
				+"flexibleEdate="+this.flexibleEdate+","
				+"weekSdate="+this.weekSdate+","
				+"weekEdate="+this.weekEdate;
				
	}
}
