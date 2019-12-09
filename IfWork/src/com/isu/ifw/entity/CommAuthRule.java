package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * The persistent class for the comm_auth_rule database table.
 * 
 */
@Entity
@Table(name="comm_auth_rule")
public class CommAuthRule{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rule_id")
	private Long ruleId;

	@Column(name="company_cd")
	private String companyCd;
 
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	private Date endDate;

	@Column(name="rule_name")
	private String ruleName;

	@Lob
	@Column(name="rule_text")
	private String ruleText;

	@Column(name="rule_type")
	private String ruleType;
 
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="sta_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	private Date staDate;
	
	@Column(name="auth_id")
	private Long authId;

	public CommAuthRule() {
	}

	public Long getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getCompanyCd() {
		return this.companyCd;
	}

	public void setCompanyCd(String companyCd) {
		this.companyCd = companyCd;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleText() {
		return this.ruleText;
	}

	public void setRuleText(String ruleText) {
		this.ruleText = ruleText;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Date getStaDate() {
		return this.staDate;
	}

	public void setStaDate(Date staDate) {
		this.staDate = staDate;
	}

	public Long getAuthId() {
		return authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}
	
}