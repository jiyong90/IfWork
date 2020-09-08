package com.isu.ifw.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="WTM_IF_APPL")
public class WtmIfAppl {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="APPL_ID")
	private Long applId;
	@Column(name="APPL_CD")
	private String applCd;
	@Column(name="APPL_SABUN")
	private String applSabun;
	@Column(name="APPL_IN_SABUN")
	private String applInSabun;
	@Column(name="APPL_STATUS_CD")
	private String applStatusCd;
	@Column(name="APPL_YMD")
	private String applYmd;
	@Column(name="APPL_TITLE")
	private String applTitle;
	@Column(name="APPL_BODY")
	private String applBody;
	@Column(name="IF_APPL_NO")
	private String ifApplNo;

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
}
