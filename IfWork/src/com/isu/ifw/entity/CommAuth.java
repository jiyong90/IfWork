package com.isu.ifw.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="comm_auth")
public class CommAuth {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="auth_id")
	private Long authId;

	@Column(name="auth_nm")
	private String authNm;

	private String note;

	@Column(name="tenant_id")
	private Long tenantId;

	public CommAuth() {
	}

	public Long getAuthId() {
		return this.authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	public String getAuthNm() {
		return this.authNm;
	}

	public void setAuthNm(String authNm) {
		this.authNm = authNm;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}


}