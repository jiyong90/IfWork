package com.isu.ifw.service;

import java.util.Date;

public interface WtmCalcService {

	/**
	 * 고정 OT 일괄 소진에 대한 부분 & no plan 케이스
	 * 기존 P_WTM_WORK_DAY_RESULT_CREATE_N
	 */
	public void P_WTM_WORK_DAY_RESULT_CREATE_N(Long tenantId, String enterCd,  String sabun, String ymd, String userId);
	
	public Date WorkTimeCalcApprDate(Date rDt, Date dt, int unitMinute, String calcType);
		
}
