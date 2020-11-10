package com.isu.ifw.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="WTM_FLEXIBLE_APPLY_EMP_TEMP")
public class WtmFlexibleApplyEmpTemp {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FLEXIBLE_APPLY_TEMP_ID")
	private Long   flexibleApplyTempId;
	@Column(name="FLEXIBLE_APPLY_ID")
	private Long   flexibleApplyId;
	@Column(name="TENANT_ID")
	private Long   tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date   updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	@Column(name="APPLY_YN")
	private String applyYn;

	public Long getFlexibleApplyTempId() {
		return flexibleApplyTempId;
	}

	public void setFlexibleApplyTempId(Long flexibleApplyTempId) {
		this.flexibleApplyTempId = flexibleApplyTempId;
	}

	public Long getFlexibleApplyId() {
		return flexibleApplyId;
	}

	public void setFlexibleApplyId(Long flexibleApplyId) {
		this.flexibleApplyId = flexibleApplyId;
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

	public String getApplyYn() {
		return applyYn;
	}

	public void setApplyYn(String applyYn) {
		this.applyYn = applyYn;
	}


	@PrePersist
	protected void onCreate() {
		this.updateDate = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDate = new Date();
	}
}
