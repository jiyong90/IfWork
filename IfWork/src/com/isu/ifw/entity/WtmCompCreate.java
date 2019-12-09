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
@Table(name="WTM_COMP_CREATE")
public class WtmCompCreate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COMP_CREATE_ID")
	private Long compCreateId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="YMD")
	private String ymd;
	@Column(name="OT_MINUTE")
	private Integer otMinute;
	@Column(name="ADD_MINUTE")
	private Integer addMinute;
	@Column(name="TOT_MINUTE")
	private Integer totMinute;
	@Column(name="REST_MINUTE")
	private Integer restMinute;
	@Column(name="ALLOW_MINUTE")
	private Integer allowMinute;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	public Long getCompCreateId() {
		return compCreateId;
	}


	public void setCompCreateId(Long compCreateId) {
		this.compCreateId = compCreateId;
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


	public Integer getOtMinute() {
		return otMinute;
	}


	public void setOtMinute(Integer otMinute) {
		this.otMinute = otMinute;
	}


	public Integer getAddMinute() {
		return addMinute;
	}


	public void setAddMinute(Integer addMinute) {
		this.addMinute = addMinute;
	}


	public Integer getTotMinute() {
		return totMinute;
	}


	public void setTotMinute(Integer totMinute) {
		this.totMinute = totMinute;
	}


	public Integer getRestMinute() {
		return restMinute;
	}


	public void setRestMinute(Integer restMinute) {
		this.restMinute = restMinute;
	}


	public Integer getAllowMinute() {
		return allowMinute;
	}


	public void setAllowMinute(Integer allowMinute) {
		this.allowMinute = allowMinute;
	}


	public String getSymd() {
		return symd;
	}


	public void setSymd(String symd) {
		this.symd = symd;
	}


	public String getEymd() {
		return eymd;
	}


	public void setEymd(String eymd) {
		this.eymd = eymd;
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
