package com.isu.ifw.vo;

import java.util.List;

import com.isu.ifw.entity.WtmOtSubsAppl;

public class WtmOtApplVO {

	private Long otApplId;
	private Long applId;
	private String ymd;
	private String holidayYn;
	private String otSdate;
	private String otEdate;
	private int otMinute;
	private int retOtMinute;
	private String reasonCd;
	private String reason;
	private String subYn;
	private Long oldOtApplId;
	private String cancelYn;
	private String updateDate;
	private String updateId;
	private List<WtmOtSubsAppl> otSubsAppls;
	
	public Long getOtApplId() {
		return otApplId;
	}
	public void setOtApplId(Long otApplId) {
		this.otApplId = otApplId;
	}
	public Long getApplId() {
		return applId;
	}
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	public String getYmd() {
		return ymd;
	}
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	public String getHolidayYn() {
		return holidayYn;
	}
	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}
	public String getOtSdate() {
		return otSdate;
	}
	public void setOtSdate(String otSdate) {
		this.otSdate = otSdate;
	}
	public String getOtEdate() {
		return otEdate;
	}
	public void setOtEdate(String otEdate) {
		this.otEdate = otEdate;
	}
	public int getOtMinute() {
		return otMinute;
	}
	public void setOtMinute(int otMinute) {
		this.otMinute = otMinute;
	}
	public int getRetOtMinute() {
		return retOtMinute;
	}
	public void setRetOtMinute(int retOtMinute) {
		this.retOtMinute = retOtMinute;
	}
	public String getReasonCd() {
		return reasonCd;
	}
	public void setReasonCd(String reasonCd) {
		this.reasonCd = reasonCd;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSubYn() {
		return subYn;
	}
	public void setSubYn(String subYn) {
		this.subYn = subYn;
	}
	public Long getOldOtApplId() {
		return oldOtApplId;
	}
	public void setOldOtApplId(Long oldOtApplId) {
		this.oldOtApplId = oldOtApplId;
	}
	public String getCancelYn() {
		return cancelYn;
	}
	public void setCancelYn(String cancelYn) {
		this.cancelYn = cancelYn;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateId() {
		return updateId;
	}
	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}
	public List<WtmOtSubsAppl> getOtSubsAppls() {
		return otSubsAppls;
	}
	public void setOtSubsAppls(List<WtmOtSubsAppl> otSubsAppls) {
		this.otSubsAppls = otSubsAppls;
	}
	
}
