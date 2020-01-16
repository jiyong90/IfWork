package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_ORG_CONC")
public class WtmIntfOrgConc {

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
	
	@Column(name="ORG_CD")
	private String orgCd;

	@Column(name="SABUN")
	private String sabun;
	
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd; 
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
	public String getOrgCd() {
		return orgCd;
	}
	public void setOrgCd(String orgCd) {
		this.orgCd = orgCd;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	 
	
	 
 
}
