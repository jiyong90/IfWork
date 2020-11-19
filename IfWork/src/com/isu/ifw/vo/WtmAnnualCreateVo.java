package com.isu.ifw.vo;

import java.util.Date;

/**
 * 연차내역관리 VO
 * WTM_ANNUAL_CREATE
 * com.isu.ifw.vo.WtmAnnualCreateVo
 */
public class WtmAnnualCreateVo {

	private Long annCreateId;
	private Long tenantId;
	private String enterCd;
	private String taaTypeCd;
	private String sabun;
	private String yy;
	private String symd;
	private String eymd;
	private Double createCnt;
	private String note;
	private Date updateDate;
	private String updateId;
	private String empNm;
	private String orgNm;
	private Double usedCnt;


	public Long getAnnCreateId() {
		return annCreateId;
	}

	public void setAnnCreateId(Long annCreateId) {
		this.annCreateId = annCreateId;
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

	public String getTaaTypeCd() {
		return taaTypeCd;
	}

	public void setTaaTypeCd(String taaTypeCd) {
		this.taaTypeCd = taaTypeCd;
	}

	public String getSabun() {
		return sabun;
	}

	public void setSabun(String sabun) {
		this.sabun = sabun;
	}

	public String getYy() {
		return yy;
	}

	public void setYy(String yy) {
		this.yy = yy;
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

	public Double getCreateCnt() {
		return createCnt;
	}

	public void setCreateCnt(Double createCnt) {
		this.createCnt = createCnt;
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

	public String getEmpNm() {
		return empNm;
	}

	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public Double getUsedCnt() {
		return usedCnt;
	}

	public void setUsedCnt(Double usedCnt) {
		this.usedCnt = usedCnt;
	}
}
