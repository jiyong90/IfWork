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
@Table(name="WTM_TAA_APPL_DET")
public class WtmTaaApplDet {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="TAA_APPL_DET_ID")
	private Long taaApplDetId;
	@Column(name="TAA_APPL_ID")
	private Long taaApplId;
	@Column(name="TAA_CD")
	private String taaCd;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="SHM")
	private String shm;
	@Column(name="EHM")
	private String ehm;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	@Column(name="TAA_MINUTE")
	private String taaMinute;

	public String getTaaMinute() {
		return taaMinute;
	}


	public void setTaaMinute(String taaMinute) {
		this.taaMinute = taaMinute;
	}


	public Long getTaaApplDetId() {
		return taaApplDetId;
	}


	public void setTaaApplDetId(Long taaApplDetId) {
		this.taaApplDetId = taaApplDetId;
	}


	public Long getTaaApplId() {
		return taaApplId;
	}


	public void setTaaApplId(Long taaApplId) {
		this.taaApplId = taaApplId;
	}


	public String getTaaCd() {
		return taaCd;
	}


	public void setTaaCd(String taaCd) {
		this.taaCd = taaCd;
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


	public String getShm() {
		return shm;
	}


	public void setShm(String shm) {
		this.shm = shm;
	}


	public String getEhm() {
		return ehm;
	}


	public void setEhm(String ehm) {
		this.ehm = ehm;
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
