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
@Table(name="WTM_OT_APPL")
public class WtmOtAppl {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_APPL_ID")
	private Long otApplId; 
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="OLD_OT_APPL_ID")
	private Long oldOtApplId;
	
	@Column(name="YMD")
	private String ymd;
	@Column(name="HOLIDAY_YN")
	private String holidayYn;
	
	@Column(name="OT_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date otSdate;
	@Column(name="OT_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date otEdate;
	@Column(name="OT_MINUTE")
	private String otMinute;
	
	@Column(name="RET_OT_MINUTE")
	private String retOtMinute;
	
	@Column(name="REASON_CD")
	private String reasonCd;
	@Column(name="REASON")
	private String reason;
	@Column(name="SUB_YN")
	private String subYn;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId; 
	
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
	public Long getOldOtApplId() {
		return oldOtApplId;
	}
	public void setOldOtApplId(Long oldOtApplId) {
		this.oldOtApplId = oldOtApplId;
	}
	public String getYmd() {
		return ymd;
	}
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	public String getHolidayYn() {
		return holidayYn;
	}
	public void setHolidayYn(String holidayYn) {
		if(holidayYn == null || holidayYn.equals(""))
			this.holidayYn = "N";
		else
			this.holidayYn = holidayYn;
	}
	public Date getOtSdate() {
		return otSdate;
	}
	public void setOtSdate(Date otSdate) {
		this.otSdate = otSdate;
	}
	public Date getOtEdate() {
		return otEdate;
	}
	public void setOtEdate(Date otEdate) {
		this.otEdate = otEdate;
	}
	public String getOtMinute() {
		return otMinute;
	}
	public void setOtMinute(String otMinute) {
		this.otMinute = otMinute;
	}
	public String getRetOtMinute() {
		return retOtMinute;
	}
	public void setRetOtMinute(String retOtMinute) {
		this.retOtMinute = retOtMinute;
	}
	public String getReasonCd() {
		return reasonCd;
	}
	public void setReasonCd(String reasonCd) {
		this.reasonCd = reasonCd;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSubYn() {
		return subYn;
	}
	public void setSubYn(String subYn) {
		this.subYn = subYn;
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
