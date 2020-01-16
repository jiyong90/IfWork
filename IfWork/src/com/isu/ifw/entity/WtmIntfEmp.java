package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_EMP")
public class WtmIntfEmp {

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
	
	@Column(name="EMP_NM")
	private String empNm;
	
	@Column(name="EMP_ENG_NM")
	private String empEngNm;
	@Column(name="ORG_CD")
	private String orgCd;
	@Column(name="LOCATION_CD")
	private String locationCd;
	@Column(name="DUTY_CD")
	private String dutyCd;
	@Column(name="POS_CD")
	private String posCd;
	@Column(name="CLASS_CD")
	private String classCd;
	@Column(name="JOB_GROUP_CD")
	private String jobGroupCd;
	@Column(name="JOB_CD")
	private String jobCd;
	@Column(name="PAY_TYPE_CD")
	private String payTypeCd;
	@Column(name="LEADER_YN")
	private String leaderYn;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="STATUS_CD")
	private String statusCd;
	
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
	public String getEmpNm() {
		return empNm;
	}
	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}
	public String getEmpEngNm() {
		return empEngNm;
	}
	public void setEmpEngNm(String empEngNm) {
		this.empEngNm = empEngNm;
	}
	public String getOrgCd() {
		return orgCd;
	}
	public void setOrgCd(String orgCd) {
		this.orgCd = orgCd;
	}
	public String getLocationCd() {
		return locationCd;
	}
	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}
	public String getDutyCd() {
		return dutyCd;
	}
	public void setDutyCd(String dutyCd) {
		this.dutyCd = dutyCd;
	}
	public String getPosCd() {
		return posCd;
	}
	public void setPosCd(String posCd) {
		this.posCd = posCd;
	}
	public String getClassCd() {
		return classCd;
	}
	public void setClassCd(String classCd) {
		this.classCd = classCd;
	}
	public String getJobGroupCd() {
		return jobGroupCd;
	}
	public void setJobGroupCd(String jobGroupCd) {
		this.jobGroupCd = jobGroupCd;
	}
	public String getJobCd() {
		return jobCd;
	}
	public void setJobCd(String jobCd) {
		this.jobCd = jobCd;
	}
	public String getPayTypeCd() {
		return payTypeCd;
	}
	public void setPayTypeCd(String payTypeCd) {
		this.payTypeCd = payTypeCd;
	}
	public String getLeaderYn() {
		return leaderYn;
	}
	public void setLeaderYn(String leaderYn) {
		this.leaderYn = leaderYn;
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
	public String getStatusCd() {
		return statusCd;
	}
	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
	
	
	
	 
	 
 
}
