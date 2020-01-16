package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_HOLIDAY")
public class WtmIntfHoliday {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="INTF_ID")
	private Long intfId;
	@Column(name="YYYYMMDDHHMISS")
	private String yyyymmddhhmiss;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="LOCATION_CD")
	private String locationCd;
	@Column(name="YMD")
	private String ymd;
	@Column(name="HOLIDAY_NM")
	private String holidayNm;
	@Column(name="FESTIVE_YN")
	private String festiveYn;
	@Column(name="PAY_YN")
	private String payYn;
	@Column(name="NOTE")
	private String note;
	public Long getIntfId() {
		return intfId;
	}
	public void setIntfId(Long intfId) {
		this.intfId = intfId;
	}
	public String getYyyymmddhhmiss() {
		return yyyymmddhhmiss;
	}
	public void setYyyymmddhhmiss(String yyyymmddhhmiss) {
		this.yyyymmddhhmiss = yyyymmddhhmiss;
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
	public String getLocationCd() {
		return locationCd;
	}
	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}
	public String getYmd() {
		return ymd;
	}
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	public String getHolidayNm() {
		return holidayNm;
	}
	public void setHolidayNm(String holidayNm) {
		this.holidayNm = holidayNm;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	 
	
	
 
}
