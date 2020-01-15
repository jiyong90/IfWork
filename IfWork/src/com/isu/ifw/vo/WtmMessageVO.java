package com.isu.ifw.vo;

import java.util.List;
import java.util.Map;

public class WtmMessageVO {

	private Long tenantId;
	private String enterCd;
	private String stdType; 
	private List<Map<String, Object>> targets;
	private String title;
	private String content;
	private String updateId;
	private boolean mobileYn;
	private boolean smsYn;
	private boolean mailYn;
	
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
	public String getStdType() {
		return stdType;
	}
	public void setStdType(String stdType) {
		this.stdType = stdType;
	}
	public List<Map<String, Object>> getTargets() {
		return targets;
	}
	public void setTargets(List<Map<String, Object>> targets) {
		this.targets = targets;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUpdateId() {
		return updateId;
	}
	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}
	public boolean isMobileYn() {
		return mobileYn;
	}
	public void setMobileYn(boolean mobileYn) {
		this.mobileYn = mobileYn;
	}
	public boolean isSmsYn() {
		return smsYn;
	}
	public void setSmsYn(boolean smsYn) {
		this.smsYn = smsYn;
	}
	public boolean isMailYn() {
		return mailYn;
	}
	public void setMailYn(boolean mailYn) {
		this.mailYn = mailYn;
	}
	
}
