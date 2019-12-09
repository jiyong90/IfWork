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
@Table(name="WTM_COMP_MGR")
public class WtmCompMgr {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COMP_MGR_ID")
	private Long compMgrId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="BUSINESS_PLACE_CD")
	private String businessPlaceCd;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="COMP_TIME_TYPE")
	private String compTimeType;
	@Column(name="TIME_TYPE")
	private String timeType;
	@Column(name="TIME_LIMIT")
	private Integer timeLimit;
	@Column(name="LIMIT_TYPE")
	private String limitType;
	@Column(name="USE_TYPE")
	private String useType;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;


	public Long getCompMgrId() {
		return compMgrId;
	}


	public void setCompMgrId(Long compMgrId) {
		this.compMgrId = compMgrId;
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


	public String getBusinessPlaceCd() {
		return businessPlaceCd;
	}


	public void setBusinessPlaceCd(String businessPlaceCd) {
		this.businessPlaceCd = businessPlaceCd;
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


	public String getCompTimeType() {
		return compTimeType;
	}


	public void setCompTimeType(String compTimeType) {
		this.compTimeType = compTimeType;
	}


	public String getTimeType() {
		return timeType;
	}


	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}


	public Integer getTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}


	public String getLimitType() {
		return limitType;
	}


	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}


	public String getUseType() {
		return useType;
	}


	public void setUseType(String useType) {
		this.useType = useType;
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
