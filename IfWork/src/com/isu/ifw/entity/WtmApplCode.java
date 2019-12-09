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
@Table(name="WTM_APPL_CODE")
public class WtmApplCode {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="APPL_CODE_ID")
	private Long applCodeId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="APPL_CD")
	private String applCd;
	@Column(name="APPL_NM")
	private String applNm;
	@Column(name="APPL_LEVEL_CD")
	private String applLevelCd;
	@Column(name="REC_LEVEL_CD")
	private String recLevelCd;
	@Column(name="TIME_UNIT")
	private String timeUnit;
	@Column(name="USE_MINUTES")
	private String useMinutes;
	@Column(name="IN_SHM")
	private String inShm;
	@Column(name="IN_EHM")
	private String inEhm;
	@Column(name="SUBS_YN")
	private String subsYn;
	@Column(name="SUBS_RULE_ID")
	private Long subsRuleId;
	@Column(name="SUBS_SDAY")
	private Integer subsSday;
	@Column(name="SUBS_EDAY")
	private Integer subsEday;
	@Column(name="TARGET_RULE_ID")
	private Long targetRuleId;
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;
	@Column(name="HOL_IN_SHM")
	private String holInShm;
	@Column(name="HOL_IN_EHM")
	private String holInEhm;
	@Column(name="HOL_APPL_TYPE_CD")
	private String holApplTypeCd;
	@Column(name="HOL_MAX_MINUTE")
	private Integer holMaxMinute;
	
	public Long getApplCodeId() {
		return applCodeId;
	}
	public void setApplCodeId(Long applCodeId) {
		this.applCodeId = applCodeId;
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
	public String getApplCd() {
		return applCd;
	}
	public void setApplCd(String applCd) {
		this.applCd = applCd;
	}
	public String getApplNm() {
		return applNm;
	}
	public void setApplNm(String applNm) {
		this.applNm = applNm;
	}
	public String getApplLevelCd() {
		return applLevelCd;
	}
	public void setApplLevelCd(String applLevelCd) {
		this.applLevelCd = applLevelCd;
	}
	public String getRecLevelCd() {
		return recLevelCd;
	}
	public void setRecLevelCd(String recLevelCd) {
		this.recLevelCd = recLevelCd;
	}
	public String getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	public String getUseMinutes() {
		return useMinutes;
	}
	public void setUseMinutes(String useMinutes) {
		this.useMinutes = useMinutes;
	}
	public String getInShm() {
		return inShm;
	}
	public void setInShm(String inShm) {
		this.inShm = inShm;
	}
	public String getInEhm() {
		return inEhm;
	}
	public void setInEhm(String inEhm) {
		this.inEhm = inEhm;
	}
	public String getSubsYn() {
		return subsYn;
	}
	public void setSubsYn(String subsYn) {
		this.subsYn = subsYn;
	}
	public Long getSubsRuleId() {
		return subsRuleId;
	}
	public void setSubsRuleId(Long subsRuleId) {
		this.subsRuleId = subsRuleId;
	}
	public Integer getSubsSday() {
		return subsSday;
	}
	public void setSubsSday(Integer subsSday) {
		this.subsSday = subsSday;
	}
	public Integer getSubsEday() {
		return subsEday;
	}
	public void setSubsEday(Integer subsEday) {
		this.subsEday = subsEday;
	}
	
	public Long getTargetRuleId() {
		return targetRuleId;
	}
	public void setTargetRuleId(Long targetRuleId) {
		this.targetRuleId = targetRuleId;
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
	public String getHolInShm() {
		return holInShm;
	}
	public void setHolInShm(String holInShm) {
		this.holInShm = holInShm;
	}
	public String getHolInEhm() {
		return holInEhm;
	}
	public void setHolInEhm(String holInEhm) {
		this.holInEhm = holInEhm;
	}
	public String getHolApplTypeCd() {
		return holApplTypeCd;
	}
	public void setHolApplTypeCd(String holApplTypeCd) {
		this.holApplTypeCd = holApplTypeCd;
	}
	public Integer getHolMaxMinute() {
		return holMaxMinute;
	}
	public void setHolMaxMinute(Integer holMaxMinute) {
		this.holMaxMinute = holMaxMinute;
	}
    
}
