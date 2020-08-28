package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;


public class WtmFlexibleEmpCalc {
	
	private Long flexibleEmpId;
	private Long tenantId;
	private String enterCd;
	private Long flexibleStdMgrId;
	private String sabun;
	private String symd;
	private String eymd;
	private String workTypeCd;
	private String flexibleNm;
	private Integer workMinute;
	private Integer otMinute;
	private String note;
	private Date updateDate;
	private String updateId;
    private Integer workHour;
    private Integer breakhour;

    public WtmFlexibleEmpCalc(Long TenantId, String enterCd, String sabun, String symd, Long flexibleEmpId, Integer workMinute, Integer workHour, Integer breakhour) {
    	this.tenantId = tenantId;
    	this.enterCd = enterCd;
    	this.sabun = sabun;
    	this.symd = symd;
    	this.eymd = symd;
    	this.flexibleEmpId = flexibleEmpId;
    	this.workMinute = workMinute;
    	this.workHour = workHour;
    	this.breakhour = breakhour;
    }
    
	public Integer getWorkHour() {
		return workHour;
	}

	public void setWorkHour(int workHour) {
		this.workHour = workHour;
	}

	public Integer getBreakhour() {
		return breakhour;
	}

	public void setBreakhour(int breakhour) {
		this.breakhour = breakhour;
	}
	
	public Long getFlexibleEmpId() {
		return flexibleEmpId;
	}
	public void setFlexibleEmpId(Long flexibleEmpId) {
		this.flexibleEmpId = flexibleEmpId;
	}
	public Long getFlexibleStdMgrId() {
		return flexibleStdMgrId;
	}
	public void setFlexibleStdMgrId(Long flexibleStdMgrId) {
		this.flexibleStdMgrId = flexibleStdMgrId;
	}
	public String getSymd() {
		return symd;
	}
	public void setSymd(String symd) {
		this.symd = symd;
	}
	
	public String getFlexibleNm() {
		return flexibleNm;
	}
	public void setFlexibleNm(String flexibleNm) {
		this.flexibleNm = flexibleNm;
	}
	public String getEymd() {
		return eymd;
	}
	public void setEymd(String eymd) {
		this.eymd = eymd;
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
	
	public Integer getWorkMinute() {
		return workMinute;
	}
	public void setWorkMinute(Integer workMinute) {
		this.workMinute = workMinute;
	}
	
	public Integer getOtMinute() {
		return otMinute;
	}
	public void setOtMinute(Integer otMinute) {
		this.otMinute = otMinute;
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
	@Override
	public String toString() {
		return "WtmFlexibleEmpCalc [flexibleEmpId=" + flexibleEmpId + ", tenantId=" + tenantId + ", enterCd=" + enterCd
				+ ", flexibleStdMgrId=" + flexibleStdMgrId + ", sabun=" + sabun + ", symd=" + symd + ", eymd=" + eymd
				+ ", workTypeCd=" + workTypeCd + ", flexibleNm=" + flexibleNm + ", workMinute=" + workMinute
				+ ", otMinute=" + otMinute + ", workHour=" + workHour + ", breakhour=" + breakhour + ", note=" + note + ", updateDate=" + updateDate + ", updateId=" + updateId
				+ "]";
	}
	
	
	
}
