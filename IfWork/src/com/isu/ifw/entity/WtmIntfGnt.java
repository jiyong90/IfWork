package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_GNT")
public class WtmIntfGnt {

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
	@Column(name="GNT_CD")
	private String gntCd;
	@Column(name="GNT_NM")
	private String gntNm;
	@Column(name="GNT_GUBUN_CD")
	private String gntGubunCd;
	@Column(name="HOL_INCL_YN")
	private String holInclYn;
	@Column(name="REQ_USE_TYPE")
	private String reqUseType;
	@Column(name="WORK_YN")
	private String workYn;
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
	public String getGntCd() {
		return gntCd;
	}
	public void setGntCd(String gntCd) {
		this.gntCd = gntCd;
	}
	public String getGntNm() {
		return gntNm;
	}
	public void setGntNm(String gntNm) {
		this.gntNm = gntNm;
	}
	public String getGntGubunCd() {
		return gntGubunCd;
	}
	public void setGntGubunCd(String gntGubunCd) {
		this.gntGubunCd = gntGubunCd;
	}
	public String getHolInclYn() {
		return holInclYn;
	}
	public void setHolInclYn(String holInclYn) {
		this.holInclYn = holInclYn;
	}
	public String getReqUseType() {
		return reqUseType;
	}
	public void setReqUseType(String reqUseType) {
		this.reqUseType = reqUseType;
	}
	public String getWorkYn() {
		return workYn;
	}
	public void setWorkYn(String workYn) {
		this.workYn = workYn;
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
