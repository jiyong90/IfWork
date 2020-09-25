package com.isu.ifw.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * WTM_ANNUAL_MGR
 *
 * @author
 */
@Entity
@Table(name = "WTM_ANNUAL_MGR")
public class WtmAnnualMgr implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ANN_MGR_ID")
	private Long   annMgrId;
    @Column(name="TENANT_ID")
    private Long tenantId;
    @Column(name="ENTER_CD")
    private String   enterCd;
    @Column(name="TAA_TYPE_CD")
    private String taaTypeCd;
    @Column(name="MINUS_YN")
	private String minusYn;
    @Column(name="NOTE")
	private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATE_DATE", columnDefinition="DATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date   updateDate;
    @Column(name="UPDATE_ID")
	private String updateId;

    @PrePersist
    protected void onCreate() {
        this.updateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = new Date();
    }

    public Long getAnnMgrId() {
        return annMgrId;
    }

    public void setAnnMgrId(Long annMgrId) {
        this.annMgrId = annMgrId;
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

    public String getMinusYn() {
        return minusYn;
    }

    public void setMinusYn(String minusYn) {
        this.minusYn = minusYn;
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
