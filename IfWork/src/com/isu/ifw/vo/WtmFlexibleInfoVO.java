package com.isu.ifw.vo;

import java.util.Date;

public class WtmFlexibleInfoVO {
	private Long tenantId;
	private String enterCd;
	private String ymd;
	private String sabun;
	private Long flexibleEmpId;
	private Integer workMinute;
	private Integer sumWorkMinute;
	private Integer otMinute;
	private Long timeCdMgrId;
	private Date entrySdate;
	private Date entryEdate;
	private String workShm;
	private String workEhm;
	private Integer unitMinute;
	private String breakTypeCd;
	private String defaultWorkUseYn;
	private String fixotUseType;
	private String unplannedYn;
	private Integer fixotUseLimit;
	private Integer sumFixOtMinute;
	private String symd;
	private String eymd;
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
	public String getYmd() {
		return ymd;
	}
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	public String getSabun() {
		return sabun;
	}
	public void setSabun(String sabun) {
		this.sabun = sabun;
	}
	public Long getFlexibleEmpId() {
		return flexibleEmpId;
	}
	public void setFlexibleEmpId(Long flexibleEmpId) {
		this.flexibleEmpId = flexibleEmpId;
	}
	public Integer getWorkMinute() {
		if(workMinute == null) return 0;
		return workMinute;
	}
	public void setWorkMinute(Integer workMinute) {
		this.workMinute = workMinute;
	}
	public Integer getSumWorkMinute() {
		if(sumWorkMinute == null) return 0;
		return sumWorkMinute;
	}
	public void setSumWorkMinute(Integer sumWorkMinute) {
		this.sumWorkMinute = sumWorkMinute;
	}
	public Integer getOtMinute() {
		if(otMinute == null) return 0;
		return otMinute;
	}
	public void setOtMinute(Integer otMinute) {
		this.otMinute = otMinute;
	}
	public Long getTimeCdMgrId() {
		return timeCdMgrId;
	}
	public void setTimeCdMgrId(Long timeCdMgrId) {
		this.timeCdMgrId = timeCdMgrId;
	}
	public Date getEntrySdate() {
		return entrySdate;
	}
	public void setEntrySdate(Date entrySdate) {
		this.entrySdate = entrySdate;
	}
	public Date getEntryEdate() {
		return entryEdate;
	}
	public void setEntryEdate(Date entryEdate) {
		this.entryEdate = entryEdate;
	}
	public String getWorkShm() {
		return workShm;
	}
	public void setWorkShm(String workShm) {
		this.workShm = workShm;
	}
	public String getWorkEhm() {
		return workEhm;
	}
	public void setWorkEhm(String workEhm) {
		this.workEhm = workEhm;
	}
	public Integer getUnitMinute() {
		if(unitMinute == null) return 0;
		return unitMinute;
	}
	public void setUnitMinute(Integer unitMinute) {
		this.unitMinute = unitMinute;
	}
	public String getBreakTypeCd() {
		return breakTypeCd;
	}
	public void setBreakTypeCd(String breakTypeCd) {
		this.breakTypeCd = breakTypeCd;
	}
	public String getDefaultWorkUseYn() {
		return defaultWorkUseYn;
	}
	public void setDefaultWorkUseYn(String defaultWorkUseYn) {
		this.defaultWorkUseYn = defaultWorkUseYn;
	}
	public String getFixotUseType() {
		return fixotUseType;
	}
	public void setFixotUseType(String fixotUseType) {
		this.fixotUseType = fixotUseType;
	}
	public String getUnplannedYn() {
		return unplannedYn;
	}
	public void setUnplannedYn(String unplannedYn) {
		this.unplannedYn = unplannedYn;
	}
	public Integer getFixotUseLimit() {
		if(fixotUseLimit == null) return 0;
		return fixotUseLimit;
	}
	public void setFixotUseLimit(Integer fixotUseLimit) {
		this.fixotUseLimit = fixotUseLimit;
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
	public Integer getSumFixOtMinute() {
		if(sumFixOtMinute == null) return 0;
		return sumFixOtMinute;
	}
	public void setSumFixOtMinute(Integer sumFixOtMinute) {
		this.sumFixOtMinute = sumFixOtMinute;
	}
	@Override
	public String toString() {
		return "WtmFlexibleInfoVO [tenantId=" + tenantId + ", enterCd=" + enterCd + ", ymd=" + ymd + ", sabun=" + sabun
				+ ", flexibleEmpId=" + flexibleEmpId + ", workMinute=" + workMinute + ", sumWorkMinute=" + sumWorkMinute
				+ ", otMinute=" + otMinute + ", timeCdMgrId=" + timeCdMgrId + ", entrySdate=" + entrySdate
				+ ", entryEdate=" + entryEdate + ", workShm=" + workShm + ", workEhm=" + workEhm + ", unitMinute="
				+ unitMinute + ", breakTypeCd=" + breakTypeCd + ", defaultWorkUseYn=" + defaultWorkUseYn
				+ ", fixotUseType=" + fixotUseType + ", unplannedYn=" + unplannedYn + ", fixotUseLimit=" + fixotUseLimit
				+ ", sumFixOtMinute=" + sumFixOtMinute + ", symd=" + symd + ", eymd=" + eymd + "]";
	} 
	
	
	
	
	
	
}
