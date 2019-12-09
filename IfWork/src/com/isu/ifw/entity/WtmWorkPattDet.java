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
@Table(name="WTM_WORK_PATT_DET")
public class WtmWorkPattDet {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="WORK_PATT_DET_ID")
	private Long workPattDetId;
	@Column(name="FLEXIBLE_STD_MGR_ID")
	private Long flexibleStdMgrId;
	@Column(name="SEQ")
	private Integer seq;
	@Column(name="TIME_CD_MGR_ID")
	private Long timeCdMgrId;
	@Column(name="HOLIDAY_YN")
	private String holidayYn;
	@Column(name="PLAN_SHM")
	private String planShm;
	@Column(name="PLAN_EHM")
	private String planEhm;
	@Column(name="PLAN_MINUTE")
	private Integer planMinute; 
	@Column(name="OTB_MINUTE")
	private Integer otbMinute;
	@Column(name="OTA_MINUTE")
	private Integer otaMinute;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	
	public Long getWorkPattDetId() {
		return workPattDetId;
	}
	public void setWorkPattDetId(Long workPattDetId) {
		this.workPattDetId = workPattDetId;
	}
	public Long getFlexibleStdMgrId() {
		return flexibleStdMgrId;
	}
	public void setFlexibleStdMgrId(Long flexibleStdMgrId) {
		this.flexibleStdMgrId = flexibleStdMgrId;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}
	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
	}
	public String getHolidayYn() {
		return holidayYn;
	}
	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}
	public String getPlanShm() {
		return planShm;
	}
	public void setPlanShm(String planShm) {
		this.planShm = planShm;
	}
	public String getPlanEhm() {
		return planEhm;
	}
	public void setPlanEhm(String planEhm) {
		this.planEhm = planEhm;
	}
	public Integer getPlanMinute() {
		return planMinute;
	}
	public void setPlanMinute(Integer planMinute) {
		this.planMinute = planMinute;
	}
	public Integer getOtbMinute() {
		return otbMinute;
	}
	public void setOtbMinute(Integer otbMinute) {
		this.otbMinute = otbMinute;
	}
	public Integer getOtaMinute() {
		return otaMinute;
	}
	public void setOtaMinute(Integer otaMinute) {
		this.otaMinute = otaMinute;
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
