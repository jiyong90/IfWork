package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WTM_INTF_CODE")
public class WtmIntfCode {

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
	@Column(name="GRP_CODE_CD")
	private String grpCodeCd;
	@Column(name="CODE_CD")
	private String codeCd;
	@Column(name="CODE_NM")
	private String codeNm;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="SEQ")
	private Integer seq;
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


	public String getGrpCodeCd() {
		return grpCodeCd;
	}


	public void setGrpCodeCd(String grpCodeCd) {
		this.grpCodeCd = grpCodeCd;
	}


	public String getCodeCd() {
		return codeCd;
	}


	public void setCodeCd(String codeCd) {
		this.codeCd = codeCd;
	}


	public String getCodeNm() {
		return codeNm;
	}


	public void setCodeNm(String codeNm) {
		this.codeNm = codeNm;
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


	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}
 
}
