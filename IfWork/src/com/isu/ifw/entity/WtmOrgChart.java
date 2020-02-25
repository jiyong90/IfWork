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
@Table(name="WTM_ORG_CHART")
public class WtmOrgChart {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ORG_CHART_ID")
	private Long orgChartId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="ORG_CHART_NM")
	private String orgChartNm;
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
	
	public Long getOrgChartId() {
		return orgChartId;
	}

	public void setOrgChartId(Long orgChartId) {
		this.orgChartId = orgChartId;
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

	public String getOrgChartNm() {
		return orgChartNm;
	}

	public void setOrgChartNm(String orgChartNm) {
		this.orgChartNm = orgChartNm;
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
