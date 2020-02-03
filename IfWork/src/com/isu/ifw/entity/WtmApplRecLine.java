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
@Table(name="WTM_APPL_REC_LINE")
public class WtmApplRecLine {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="APPL_REC_LINE_ID")
	private Long applRecLineId;
	@Column(name="APPL_CODE_ID")
	private Long applCodeId;
	@Column(name="SEQ")
	private int seq;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	public Long getApplRecLineId() {
		return applRecLineId;
	}
	public void setApplRecLineId(Long applRecLineId) {
		this.applRecLineId = applRecLineId;
	}
	public Long getApplCodeId() {
		return applCodeId;
	}
	public void setApplCodeId(Long applCodeId) {
		this.applCodeId = applCodeId;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
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
