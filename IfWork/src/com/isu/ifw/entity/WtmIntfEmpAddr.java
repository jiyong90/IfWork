package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_EMP_ADDR")
public class WtmIntfEmpAddr {

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
	
	@Column(name="SABUN")
	private String sabun;
	@Column(name="EMAIL")
	private String email;
	@Column(name="PHONE")
	private String phone;
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
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	 
	
	
	
	 
 
}
