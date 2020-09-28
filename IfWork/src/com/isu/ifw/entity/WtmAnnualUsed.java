package com.isu.ifw.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.util.Date;

/**
 * WTM_ANNUAL_USED
 * @author 
 */
@Entity
@Table(name="WTM_ANNUAL_USED")
public class WtmAnnualUsed implements Serializable {
    /**
     * 연차사용내역ID
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ANN_USED_ID")
    private Long annUsedId;

    /**
     * 테넌트아이디
     */
    @Column(name = "TENANT_ID")
    private Long tenantId;

    /**
     * 회사구분
     */
    @Column(name = "ENTER_CD")
    private String enterCd;

    /**
     * 근태타입코드
     */
    @Column(name = "TAA_TYPE_CD")
    private String taaTypeCd;

    /**
     * 사번
     */
    @Column(name = "SABUN")
    private String sabun;

    /**
     * 년도
     */
    @Column(name = "YY")
    private String yy;

    /**
     * 시작일
     */
    @Column(name = "SYMD")
    private String symd;

    /**
     * 종료일
     */
    @Column(name = "EYMD")
    private String eymd;

    /**
     * 발생일수
     */
    @Max(999)
    @Column(name = "CREATE_CNT")
    private Integer createCnt;

    /**
     * 비고
     */
    @Column(name = "NOTE")
    private String note;

    /**
     * 최종수정시간
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATE_DATE", columnDefinition="DATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /**
     * 최종수정자
     */
    @Column(name = "UPDATE_ID")
    private String updateId;

    @PrePersist
    protected void onCreate() {
        this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = new Date();
    }

    private static final long serialVersionUID = 1L;

    public Long getAnnUsedId() {
        return annUsedId;
    }

    public void setAnnUsedId(Long annUsedId) {
        this.annUsedId = annUsedId;
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

    public String getTaaTypeCd() {
        return taaTypeCd;
    }

    public void setTaaTypeCd(String taaTypeCd) {
        this.taaTypeCd = taaTypeCd;
    }

    public String getSabun() {
        return sabun;
    }

    public void setSabun(String sabun) {
        this.sabun = sabun;
    }

    public String getYy() {
        return yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
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

    public Integer getCreateCnt() {
        return createCnt;
    }

    public void setCreateCnt(Integer createCnt) {
        this.createCnt = createCnt;
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
}