package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmCompApplListService {
	
	
	/**
	 * 보상휴가 신청내역서 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
	
	/**
	 * 보상휴가 사용가능 시간 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getPossibleUseTime(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
	/**
	 * 보상휴가 사용가능 시간 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getWorkDay(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
}
