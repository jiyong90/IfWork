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
@Table(name="WTM_PUSH_MGR")
public class WtmPushMgr {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PUSH_MGR_ID")
	private Long pushMgrId;
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
	@Column(name="PUSH_OBJ")
	private String pushObj;
	@Column(name="STD_MINUTE")
	private Integer stdMinute;
	@Column(name="STD_TYPE")
	private String stdType;
	@Column(name="PUSH_MSG")
	private String pushMsg;
	@Column(name="MOBILE_YN")
	private String mobileYn;
	@Column(name="EMAIL_YN")
	private String emailYn;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	
	public Long getPushMgrId() {
		return pushMgrId;
	}
	public void setPushMgrId(Long pushMgrId) {
		this.pushMgrId = pushMgrId;
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
	public String getPushObj() {
		return pushObj;
	}
	public void setPushObj(String pushObj) {
		this.pushObj = pushObj;
	}
	public Integer getStdMinute() {
		return stdMinute;
	}
	public void setStdMinute(Integer stdMinute) {
		this.stdMinute = stdMinute;
	}
	public String getStdType() {
		return stdType;
	}
	public void setStdType(String stdType) {
		this.stdType = stdType;
	}
	public String getPushMsg() {
		return pushMsg;
	}
	public void setPushMsg(String pushMsg) {
		this.pushMsg = pushMsg;
	}
	
	public String getMobileYn() {
		return mobileYn;
	}
	public void setMobileYn(String mobileYn) {
		this.mobileYn = mobileYn;
	}
	public String getEmailYn() {
		return emailYn;
	}
	public void setEmailYn(String emailYn) {
		this.emailYn = emailYn;
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
