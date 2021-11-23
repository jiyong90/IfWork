package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmHolidayMgrService {
	
	public List<Map<String, Object>> getHolidayList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	public int setHolidayList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	/**
	 * 근무 계획(시간코드) 변경
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @param userId
	 */
	public void changeWorktime(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
	
	/**
	 * 근무 계획(시간코드) 변경 대상자의 현재 근무 계획 조회
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param paramMap
	 * ymd
	 * targetSabunList
	 * @return
	 */
	public List<Map<String, Object>> getWorkPlan(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
}
