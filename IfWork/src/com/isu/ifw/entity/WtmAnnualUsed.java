package com.isu.ifw.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
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
    private Long annUsedId;

    /**
     * 테넌트아이디
     */
    @NotEmpty
    private Long tenantId;

    /**
     * 회사구분
     */
    @NotEmpty
    private String enterCd;

    /**
     * 근태타입코드
     */
    @NotEmpty
    private String taaTypeCd;

    /**
     * 사번
     */
    @NotEmpty
    private String sabun;

    /**
     * 년도
     */
    @NotEmpty
    private String yy;

    /**
     * 시작일
     */
    @NotEmpty
    private String symd;

    /**
     * 종료일
     */
    @NotEmpty
    private String eymd;

    /**
     * 발생일수
     */
    @NotEmpty
    private Integer createCnt;

    /**
     * 비고
     */
    private String note;

    /**
     * 최종수정시간
     */
    @NotEmpty
    private Date updateDate;

    /**
     * 최종수정자
     */
    @NotEmpty
    private String updateId;

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