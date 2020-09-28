package com.isu.ifw.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class WtmHolidayMgrPK implements Serializable  {
	
	@Column(name="TENANT_ID")
	private Long tenantId;

	@Column(name="ENTER_CD")
	private String enterCd;

	@Column(name="HOLIDAY_YMD")
	private String holidayYmd;

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

	public String getHolidayYmd() {
		return holidayYmd;
	}

	public void setHolidayYmd(String holidayYmd) {
		this.holidayYmd = holidayYmd;
	}

	
}
