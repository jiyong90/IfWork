package com.isu.ifw.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="WTM_FLEXIBLE_EMP")
@NamedNativeQuery(name="WtmFlexibleEmp.getTotalWorkMinuteAndRealWorkMinute",
query="SELECT E.FLEXIBLE_EMP_ID AS flexibleEmpId, E.SYMD AS symd, E.EYMD AS eymd, E.TENANT_ID AS tenantId, E.ENTER_CD AS enterCd, E.SABUN AS sabun, E.WORK_MINUTE AS workMinute \n" + 
"			    	 , SUM(CASE WHEN R.TIME_TYPE_CD IN ('BASE' ,'REGA') THEN F_WTM_NVL(R.APPR_MINUTE,R.PLAN_MINUTE)\n" +
"		                        WHEN M.TAA_TIME_YN = 'Y' AND R.TIME_TYPE_CD = 'TAA' THEN F_WTM_NVL(R.APPR_MINUTE,R.PLAN_MINUTE)\n" +
"		                        ELSE '0' END ) AS workHour\n" +
"		             , SUM(CASE WHEN R.TIME_TYPE_CD = 'EXCEPT' AND  T.TAA_INFO_CD = 'BREAK' THEN F_WTM_NVL(R.APPR_MINUTE,'0') ELSE '0' END ) AS breakHour\n" +
"		      FROM WTM_FLEXIBLE_EMP E\n" +
"			  JOIN WTM_FLEXIBLE_STD_MGR M\n" +
"				ON E.FLEXIBLE_STD_MGR_ID = M.FLEXIBLE_STD_MGR_ID\n " +
"			  LEFT OUTER JOIN WTM_WORK_DAY_RESULT R\n" + 
"			    ON R.TENANT_ID = E.TENANT_ID\n" + 
"			   AND R.ENTER_CD = E.ENTER_CD\n" + 
"			   AND R.SABUN = E.SABUN\n" + 
"			   AND R.YMD BETWEEN E.SYMD AND E.EYMD\n" + 
"		      LEFT OUTER JOIN WTM_TAA_CODE T ON T.TENANT_ID = R.TENANT_ID AND T.ENTER_CD = R.ENTER_CD AND T.TAA_CD = R.TAA_CD  \n" + 
"             WHERE E.TENANT_ID = :tenantId\n" + 
"               AND E.ENTER_CD = :enterCd\n" + 
"               AND E.SABUN = :sabun\n" + 
"	 		   AND :symd BETWEEN E.SYMD AND E.EYMD\n" + 
"		     GROUP BY E.FLEXIBLE_EMP_ID, E.SYMD, E.EYMD, E.TENANT_ID, E.ENTER_CD, E.SABUN, E.WORK_MINUTE\n" +
"			", resultSetMapping="WtmFlexibleEmp.getTotalWorkMinuteAndRealWorkMinute")
@SqlResultSetMappings({
	@SqlResultSetMapping(
		name="WtmFlexibleEmp.getTotalWorkMinuteAndRealWorkMinute",
		classes = {
				@ConstructorResult(
						targetClass = WtmFlexibleEmpCalc.class,
						columns = {
								@ColumnResult(name="tenantId", type=Long.class),
								@ColumnResult(name="enterCd", type=String.class),
								@ColumnResult(name="sabun", type=String.class),
								@ColumnResult(name="symd", type=String.class),
								@ColumnResult(name="flexibleEmpId", type=Long.class),
								@ColumnResult(name="workMinute", type=Integer.class),
								@ColumnResult(name="workHour", type=Integer.class),
								@ColumnResult(name="breakHour", type=Integer.class)
						}
						)
		}
		)
	}
)
public class WtmFlexibleEmp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="FLEXIBLE_EMP_ID")
	private Long flexibleEmpId;
	@Column(name="TENANT_ID")
	private Long tenantId;
	@Column(name="ENTER_CD")
	private String enterCd;
	@Column(name="FLEXIBLE_STD_MGR_ID")
	private Long flexibleStdMgrId;
	@Column(name="SABUN")
	private String sabun;
	@Column(name="SYMD")
	private String symd;
	@Column(name="EYMD")
	private String eymd;
	@Column(name="WORK_TYPE_CD")
	private String workTypeCd;
	@Column(name="FLEXIBLE_NM")
	private String flexibleNm;
	
	@Column(name="WORK_MINUTE")
	private Integer workMinute;
	@Column(name="OT_MINUTE")
	private Integer otMinute;
	
	@Column(name="NOTE")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE", columnDefinition="DATETIME") 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	@Column(name="UPDATE_ID")
	private String updateId;

	@Transient
    private Integer workHour;
	@Transient
    private Integer breakhour;

	public Integer getWorkHour() {
		return workHour;
	}

	public void setWorkHour(int workHour) {
		this.workHour = workHour;
	}

	public Integer getBreakhour() {
		return breakhour;
	}

	public void setBreakhour(int breakhour) {
		this.breakhour = breakhour;
	}
	
	public Long getFlexibleEmpId() {
		return flexibleEmpId;
	}
	public void setFlexibleEmpId(Long flexibleEmpId) {
		this.flexibleEmpId = flexibleEmpId;
	}
	public Long getFlexibleStdMgrId() {
		return flexibleStdMgrId;
	}
	public void setFlexibleStdMgrId(Long flexibleStdMgrId) {
		this.flexibleStdMgrId = flexibleStdMgrId;
	}
	public String getSymd() {
		return symd;
	}
	public void setSymd(String symd) {
		this.symd = symd;
	}
	
	public String getFlexibleNm() {
		return flexibleNm;
	}
	public void setFlexibleNm(String flexibleNm) {
		this.flexibleNm = flexibleNm;
	}
	public String getEymd() {
		return eymd;
	}
	public void setEymd(String eymd) {
		this.eymd = eymd;
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
	public String getWorkTypeCd() {
		return workTypeCd;
	}
	public void setWorkTypeCd(String workTypeCd) {
		this.workTypeCd = workTypeCd;
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
	@Override
	public String toString() {
		return "WtmFlexibleEmp [flexibleEmpId=" + flexibleEmpId + ", tenantId=" + tenantId + ", enterCd=" + enterCd
				+ ", flexibleStdMgrId=" + flexibleStdMgrId + ", sabun=" + sabun + ", symd=" + symd + ", eymd=" + eymd
				+ ", workTypeCd=" + workTypeCd + ", flexibleNm=" + flexibleNm + ", workMinute=" + workMinute
				+ ", otMinute=" + otMinute + ", note=" + note + ", updateDate=" + updateDate + ", updateId=" + updateId
				+ "]";
	}
	
	
	
}
