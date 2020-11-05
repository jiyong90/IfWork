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
@Table(name="WTM_ASYNC_LOG")
public class WtmAsyncLog {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ASYNC_LOG_ID")
	private Long asyncLogId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="ASYNC_NM")
	private String asyncNm;
	@Column(name="ASYNC_STATUS")
	private String asyncStatus; 
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	
	public Long getAsyncLogId() {
		return asyncLogId;
	}


	public void setAsyncLogId(Long asyncLogId) {
		this.asyncLogId = asyncLogId;
	}


	public String getAsyncNm() {
		return asyncNm;
	}


	public void setAsyncNm(String asyncNm) {
		this.asyncNm = asyncNm;
	}


	public String getAsyncStatus() {
		return asyncStatus;
	}


	public void setAsyncStatus(String asyncStatus) {
		this.asyncStatus = asyncStatus;
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
