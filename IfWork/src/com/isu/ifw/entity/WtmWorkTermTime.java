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
	private Integer avlMinute;
	@Column(name="PLAN_WORK_MINUTE")
	private Integer planWorkMinute;
	
	@Column(name="PLAN_OT_MINUTE")
	private Integer planOtMinute;

	@Column(name="APPR_WORK_MINUTE")
	private Integer apprWorkMinute;
	
	@Column(name="APPR_OT_MINUTE")
	private Integer apprOtMinute;
	
	@Column(name="NOW_WORK_MINUTE")
	private Integer nowWorkMinute;
	
	@Column(name="NOW_OT_MINUTE")
	private Integer nowOtMinute;
	
	@Column(name="O_MINUTE")
	private Integer oMinute;
	@Column(name="N_MINUTE")
	private Integer nMinute;
	@Column(name="E_O_MINUTE")
	private Integer eOMinute;
	@Column(name="E_N_MINUTE")
	private Integer eNMinute;
	
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
	
	public Integer getAvlMinute() {
		return avlMinute;
	}
	public void setAvlMinute(Integer avlMinute) {
		this.avlMinute = avlMinute;
	}
	public Integer getPlanWorkMinute() {
		return planWorkMinute;
	}
	public void setPlanWorkMinute(Integer planWorkMinute) {
		this.planWorkMinute = planWorkMinute;
	}
	public Integer getPlanOtMinute() {
		return planOtMinute;
	}
	public void setPlanOtMinute(Integer planOtMinute) {
		this.planOtMinute = planOtMinute;
	}
	public Integer getApprWorkMinute() {
		return apprWorkMinute;
	}
	public void setApprWorkMinute(Integer apprWorkMinute) {
		this.apprWorkMinute = apprWorkMinute;
	}
	public Integer getApprOtMinute() {
		return apprOtMinute;
	}
	public void setApprOtMinute(Integer apprOtMinute) {
		this.apprOtMinute = apprOtMinute;
	}
	public Integer getNowWorkMinute() {
		return nowWorkMinute;
	}
	public void setNowWorkMinute(Integer nowWorkMinute) {
		this.nowWorkMinute = nowWorkMinute;
	}
	public Integer getNowOtMinute() {
		return nowOtMinute;
	}
	public void setNowOtMinute(Integer nowOtMinute) {
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

	public Integer getoMinute() {
		return oMinute;
	}
	public void setoMinute(Integer oMinute) {
		this.oMinute = oMinute;
	}
	public Integer getnMinute() {
		return nMinute;
	}
	public void setnMinute(Integer nMinute) {
		this.nMinute = nMinute;
	}
	public Integer geteOMinute() {
		return eOMinute;
	}
	public void seteOMinute(Integer eOMinute) {
		this.eOMinute = eOMinute;
	}
	public Integer geteNMinute() {
		return eNMinute;
	}
	public void seteNMinute(Integer eNMinute) {
		this.eNMinute = eNMinute;
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
