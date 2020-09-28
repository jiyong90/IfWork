package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_DAY_MGR")
public class WtmDayMgr {
	
	@Id
	@Column(name="SUN_YMD")
	private String sunYmd;

	@Column(name="MOON_YMD")
	private String moonYmd;

	@Column(name="DAY_NM")
	private String dayNm;

	@Column(name="HOLIDAY_YN")
	private String holidayYn;
	
	@Column(name="NOTE")
	private String note;

	public String getSunYmd() {
		return sunYmd;
	}

	public void setSunYmd(String sunYmd) {
		this.sunYmd = sunYmd;
	}

	public String getMoonYmd() {
		return moonYmd;
	}

	public void setMoonYmd(String moonYmd) {
		this.moonYmd = moonYmd;
	}

	public String getDayNm() {
		return dayNm;
	}

	public void setDayNm(String dayNm) {
		this.dayNm = dayNm;
	}

	public String getHolidayYn() {
		return holidayYn;
	}

	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	} 
	
	
}
