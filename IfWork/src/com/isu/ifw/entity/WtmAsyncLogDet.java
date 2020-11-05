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
@Table(name="WTM_ASYNC_LOG_DET")
public class WtmAsyncLogDet {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ASYNC_LOG_DET_ID")
	private Long asyncLogDetId;
	@Column(name="ASYNC_LOG_ID")
	private Long asyncLogId;
	@Column(name="ASYNC_YMDHIS")
	private String asyncYmdhis;
	@Column(name="ASYNC_KEY")
	private String asyncKey;
	@Column(name="ASYNC_DESC")
	private String asyncDesc; 
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	 
	public Long getAsyncLogDetId() {
		return asyncLogDetId;
	}

	public void setAsyncLogDetId(Long asyncLogDetId) {
		this.asyncLogDetId = asyncLogDetId;
	}

	public Long getAsyncLogId() {
		return asyncLogId;
	}

	public void setAsyncLogId(Long asyncLogId) {
		this.asyncLogId = asyncLogId;
	}

	public String getAsyncYmdhis() {
		return asyncYmdhis;
	}

	public void setAsyncYmdhis(String asyncYmdhis) {
		this.asyncYmdhis = asyncYmdhis;
	}

	public String getAsyncKey() {
		return asyncKey;
	}

	public void setAsyncKey(String asyncKey) {
		this.asyncKey = asyncKey;
	}

	public String getAsyncDesc() {
		return asyncDesc;
	}

	public void setAsyncDesc(String asyncDesc) {
		this.asyncDesc = asyncDesc;
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
