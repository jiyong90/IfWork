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
@Table(name="WTM_WORK_CALENDAR")
public class WtmWorkCalendar {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="WORK_CALENDAR_ID")
	private Long workCalendarId;
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
	
	@Column(name="ENTRY_SDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date entrySdate;
	@Column(name="ENTRY_EDATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date entryEdate;
	
	@Column(name="ENTRY_STYPE_CD")
	private String entryStypeCd;
	@Column(name="ENTRY_ETYPE_CD")
	private String entryEtypeCd;
	
	@Column(name="WORK_CLOSE_YN")
	private String workCloseYn;
	
	@Column(name="HOLIDAY_YN")
	private String holidayYn;
	
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
 
	
	
	public Long getWorkCalendarId() {
		return workCalendarId;
	}

	public void setWorkCalendarId(Long workCalendarId) {
		this.workCalendarId = workCalendarId;
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

	public Date getEntrySdate() {
		return entrySdate;
	}

	public void setEntrySdate(Date entrySdate) {
		this.entrySdate = entrySdate;
	}

	public Date getEntryEdate() {
		return entryEdate;
	}

	public void setEntryEdate(Date entryEdate) {
		this.entryEdate = entryEdate;
	}

	public String getEntryStypeCd() {
		return entryStypeCd;
	}

	public void setEntryStypeCd(String entryStypeCd) {
		this.entryStypeCd = entryStypeCd;
	}

	public String getEntryEtypeCd() {
		return entryEtypeCd;
	}

	public void setEntryEtypeCd(String entryEtypeCd) {
		this.entryEtypeCd = entryEtypeCd;
	}

	public String getWorkCloseYn() {
		return workCloseYn;
	}

	public void setWorkCloseYn(String workCloseYn) {
		this.workCloseYn = workCloseYn;
	}

	public String getHolidayYn() {
		return holidayYn;
	}

	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
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
