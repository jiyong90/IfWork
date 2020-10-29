package com.isu.ifw.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * WTM_WORKTIME_CLOSE
 *
 * @author
 */
@Entity
@Table(name = "WTM_WORKTIME_CLOSE")
public class WtmWorktimeClose {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WORKTIME_CLOSE_ID")
	private Long worktimeCloseId;

	@Column(name = "TENANT_ID")
	private Long tenantId;

	@Column(name = "ENTER_CD")
	private String enterCd; 

	@Column(name = "SYMD")
	private String symd;

	@Column(name = "EYMD")
	private String eymd;

	@Column(name = "CLOSE_NM")
	private String closeNm;
	
	@Column(name = "CLOSE_YN")
	private String closeYn;

	@Column(name = "NOTE")
	private String note;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ID")
	private String updateId;
   
	@PrePersist
	protected void onCreate() {
		this.updateDate = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = new Date();
	}

	public Long getWorktimeCloseId() {
		return worktimeCloseId;
	}

	public void setWorktimeCloseId(Long worktimeCloseId) {
		this.worktimeCloseId = worktimeCloseId;
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

	public String getCloseNm() {
		return closeNm;
	}

	public void setCloseNm(String closeNm) {
		this.closeNm = closeNm;
	}

	public String getCloseYn() {
		return closeYn;
	}

	public void setCloseYn(String closeYn) {
		this.closeYn = closeYn;
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
	
}