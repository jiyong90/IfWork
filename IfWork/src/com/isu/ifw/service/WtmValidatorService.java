package com.isu.ifw.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.isu.option.vo.ReturnParam;
 
public interface WtmValidatorService {

	/**
	 * 근무제 유효성 검사기
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param symd
	 * @param eymd
	 * @param applId null 또는 빈값이어도 됨.
	 * @return
	 */
	public ReturnParam checkDuplicateFlexibleWork(Long tenantId, String enterCd, String sabun, String symd, String eymd, Long applId);
	/**
	 * 일별 유효성 검사기
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param sdate
	 * @param edate
	 * @param applId null 또는 빈값이어도 됨.
	 * @return
	 */
	public ReturnParam checkDuplicateWorktime(Long tenantId, String enterCd, String sabun, Date sdate, Date edate, Long applId);
	
	public ReturnParam validTaa(Long tenantId, String enterCd, String sabun,
			String timeTypeCd, String taaCd,
			String symd, String shm, String eymd, String ehm, Long applId, String locale);
	
	public ReturnParam checkDuplicateEntryAppl(Long tenantId, String enterCd, String sabun, String ymd, Long applId);
	
}
