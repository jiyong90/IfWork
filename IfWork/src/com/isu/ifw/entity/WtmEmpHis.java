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
@Table(name="WTM_EMP_HIS")
public class WtmEmpHis {
	
	public Long getEmpHisId() {
		return empHisId;
	}


	public void setEmpHisId(Long empHisId) {
		this.empHisId = empHisId;
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


	public String getStatusCd() {
		return statusCd;
	}


	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}


	public String getOrgCd() {
		return orgCd;
	}


	public void setOrgCd(String orgCd) {
		this.orgCd = orgCd;
	}


	public String getBusinessPlaceCd() {
		return businessPlaceCd;
	}


	public void setBusinessPlaceCd(String businessPlaceCd) {
		this.businessPlaceCd = businessPlaceCd;
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


	public String getOrgPath() {
		return orgPath;
	}


	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}


	public String getLeaderYn() {
		return leaderYn;
	}


	public void setLeaderYn(String leaderYn) {
		this.leaderYn = leaderYn;
	}


/*	public String getEmpImg() {
		return empImg;
	}


	public void setEmpImg(String empImg) {
		this.empImg = empImg;
	}
*/

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


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="EMP_HIS_ID")
	private Long empHisId;
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
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="STATUS_CD")
	private String statusCd;
	@Column(name="ORG_CD")
	private String orgCd;
	@Column(name="BUSINESS_PLACE_CD")
	private String businessPlaceCd;
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
	@Column(name="ORG_PATH")
	private String orgPath;
	@Column(name="LEADER_YN")
	private String leaderYn;
	//@Column(name="EMP_IMG")
	//private String empImg;
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
}
