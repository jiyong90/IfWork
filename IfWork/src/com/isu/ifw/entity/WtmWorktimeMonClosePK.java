package com.isu.ifw.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class WtmWorktimeMonClosePK implements Serializable  {

	@Column(name="WORKTIME_CLOSE_ID")
	private Long worktimeCloseId;

	@Column(name="SABUN")
	private String sabun;

	@Column(name="WORK_TYPE_CD")
	private String workTypeCd;

	public Long getWorktimeCloseId() {
		return worktimeCloseId;
	}

	public void setWorktimeCloseId(Long worktimeCloseId) {
		this.worktimeCloseId = worktimeCloseId;
	}

	public String getSabun() {
		return sabun;
	}

	public void setSabun(String sabun) {
		this.sabun = sabun;
	}

	public String getWorkTypeCd() {
		return workTypeCd;
	}
	
	public void setWorkTypeCd(String workTypeCd) {
		this.workTypeCd = workTypeCd;
	}
	
}
