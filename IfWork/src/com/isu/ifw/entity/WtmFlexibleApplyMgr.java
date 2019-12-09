package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="WTM_FLEXIBLE_APPLY")
public class WtmFlexibleApplyMgr {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FLEXIBLE_APPLY_ID")
	private Long flexibleApplyId;
	@Column(name="FLEXIBLE_STD_MGR_ID")
	private Long flexibleStdMgrId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPLY_NM")
	private String applyNm;
	@Column(name="USE_SYMD")
	private String useSymd;
	@Column(name="USE_EYMD")
	private String useEymd;
	@Column(name="REPEAT_TYPE_CD")
	private String repeatTypeCd;
	@Column(name="REPEAT_CNT")
	private Integer repeatCnt;
	@Column(name="WORK_MINUTE")
	private Integer workMinute;
	@Column(name="OT_MINUTE")
	private Integer otMinute;
	@Column(name="APPLY_YN")
	private String applyYn;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	
	
	public Long getFlexibleApplyId() {
		return flexibleApplyId;
	}
	public void setFlexibleApplyId(Long flexibleApplyId) {
		this.flexibleApplyId = flexibleApplyId;
	}
	public Long getFlexibleStdMgrId() {
		return flexibleStdMgrId;
	}
	public void setFlexibleStdMgrId(Long flexibleStdMgrId) {
		this.flexibleStdMgrId = flexibleStdMgrId;
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
	public String getApplyNm() {
		return applyNm;
	}
	public void setApplyNm(String applyNm) {
		this.applyNm = applyNm;
	}
	public String getUseSymd() {
		return useSymd;
	}
	public void setUseSymd(String useSymd) {
		this.useSymd = useSymd;
	}
	public String getUseEymd() {
		return useEymd;
	}
	public void setUseEymd(String useEymd) {
		this.useEymd = useEymd;
	}
	
	public String getRepeatTypeCd() {
		return repeatTypeCd;
	}
	public void setRepeatTypeCd(String repeatTypeCd) {
		this.repeatTypeCd = repeatTypeCd;
	}
	public Integer getRepeatCnt() {
		return repeatCnt;
	}
	public void setRepeatCnt(Integer repeatCnt) {
		this.repeatCnt = repeatCnt;
	}
	public Integer getWorkMinute() {
		return workMinute;
	}
	public void setWorkMinute(Integer workMinute) {
		this.workMinute = workMinute;
	}
	public Integer getOtMinute() {
		return otMinute;
	}
	public void setOtMinute(Integer otMinute) {
		this.otMinute = otMinute;
	}
	public String getApplyYn() {
		return applyYn;
	}
	public void setApplyYn(String applyYn) {
		this.applyYn = applyYn;
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
	
	@PrePersist
    protected void onCreate() {
		this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
    	this.updateDate = new Date();
    }
	
}
