package com.isu.ifw.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * WTM_ANNUAL_CREATE
 *
 * @author
 */
@Entity
@Table(name = "WTM_ANNUAL_CREATE")
public class WtmAnnualCreate implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ANN_CREATE_ID")
	private Long annCreateId;

	@Column(name = "TENANT_ID")
	private Long tenantId;

	@Column(name = "ENTER_CD")
	private String enterCd;

	@Column(name = "TAA_TYPE_CD")
	private String taaTypeCd;

	@Column(name = "SABUN")
	private String sabun;

	@Column(name = "YY")
	private String yy;

	@Column(name = "SYMD")
	private String symd;

	@Column(name = "EYMD")
	private String eymd;

	@Column(name = "CREATE_CNT")
	private Integer createCnt;

	@Column(name = "NOTE")
	private String note;

	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	@Column(name = "UPDATE_ID")
	private String updateId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "TENANT_ID",
	                          referencedColumnName = "TENANT_ID",
	                          insertable = false,
	                          updatable = false), @JoinColumn(name = "ENTER_CD",
	                                                          referencedColumnName = "ENTER_CD",
	                                                          insertable = false,
	                                                          updatable = false), @JoinColumn(name = "SABUN",
	                                                                                          referencedColumnName = "SABUN",
	                                                                                          insertable = false,
	                                                                                          updatable = false)})
	private WtmEmpHis wtmEmpHis;

	@Transient
	private String empNm;

	@Transient
	private String orgNm;

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getEmpNm() {
		return empNm;
	}

	public void setEmpNm(String empNm) {
		this.empNm = empNm;
	}

	public WtmEmpHis getWtmEmpHis() {
		return wtmEmpHis;
	}

	public void setWtmEmpHis(WtmEmpHis wtmEmpHis) {
		this.wtmEmpHis = wtmEmpHis;
		this.empNm = wtmEmpHis.getEmpNm();
	}

	@PrePersist
	protected void onCreate() {
		this.updateDate = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = new Date();
	}

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

	public Integer getCreateCnt() {
		return createCnt;
	}

	public void setCreateCnt(Integer createCnt) {
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
}