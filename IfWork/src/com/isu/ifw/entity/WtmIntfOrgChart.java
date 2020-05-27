package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_ORG_CHART")
public class WtmIntfOrgChart {

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
	
	@Column(name="ORG_NM")
	private String orgNm;
	
	@Column(name="ORG_LEVEL")
	private String orgLevel;

	@Column(name="PRIOR_ORG_CD")
	private String priorOrgCd;

	@Column(name="SEQ")
	private String seq;

	
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

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

	public String getPriorOrgCd() {
		return priorOrgCd;
	}

	public void setPriorOrgCd(String priorOrgCd) {
		this.priorOrgCd = priorOrgCd;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
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

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	
	@Column(name="ORG_TYPE")
	private String orgType;
	
	@Column(name="NOTE")
	private String note;

}
