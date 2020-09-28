package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="WTM_HOLIDAY_MGR")
public class WtmHolidayMgr {

	@EmbeddedId
	private WtmHolidayMgrPK id;

	@Column(name="HOLIDAY_NM")
	private String holidayNm;

	@Column(name="SUN_YN")
	private String sunYn;

	@Column(name="FESTIVE_YN")
	private String festiveYn;
	
	@Column(name="PAY_YN")
	private String payYn;
	
	@Column(name="BISINESS_PLACE_CD")
	private String bisinessPlaceCd;
	
	@Column(name="NOTE")
	private String note;
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	@PrePersist
    protected void onCreate() {
		this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
    	this.updateDate = new Date();
    }

	public WtmHolidayMgrPK getId() {
		return id;
	}

	public void setId(WtmHolidayMgrPK id) {
		this.id = id;
	}

	
	public String getHolidayNm() {
		return holidayNm;
	}

	public void setHolidayNm(String holidayNm) {
		this.holidayNm = holidayNm;
	}

	public String getSunYn() {
		return sunYn;
	}

	public void setSunYn(String sunYn) {
		this.sunYn = sunYn;
	}

	public String getFestiveYn() {
		return festiveYn;
	}

	public void setFestiveYn(String festiveYn) {
		this.festiveYn = festiveYn;
	}

	public String getPayYn() {
		return payYn;
	}

	public void setPayYn(String payYn) {
		this.payYn = payYn;
	}

	public String getBisinessPlaceCd() {
		return bisinessPlaceCd;
	}

	public void setBisinessPlaceCd(String bisinessPlaceCd) {
		this.bisinessPlaceCd = bisinessPlaceCd;
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
