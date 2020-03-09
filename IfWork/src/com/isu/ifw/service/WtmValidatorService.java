package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;
 
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
	public ReturnParam checkDuplicateWorktime(Long tenantId, String enterCd, String sabun, String sdate, String edate, Long applId);
	
	public ReturnParam validTaa(Long tenantId, String enterCd, String sabun,
			String timeTypeCd, String taaCd,
			String symd, String shm, String eymd, String ehm, Long applId, String locale);
	
	public ReturnParam checkDuplicateEntryAppl(Long tenantId, String enterCd, String sabun, String ymd, Long applId);
	
	/**
	 * 근태 신청(인터페이스) 시 validation
	 * @param tenantId
	 * @param enterCd
	 * @param applNo
	 * @param works [{"sabun":"11014", "works" : [{"sdate":"20200226","edate":"20200226","shm":"","ehm":""}]}]
	 * @param applSabun
	 * @return
	 */
	public ReturnParam worktimeValid(Long tenantId, String enterCd, String applNo, List<Map<String, Object>> works, String applSabun);
	
}
