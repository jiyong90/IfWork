package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmTaaListService {
	
	/**
	 * 근태신청내역(관리자) 리스트 조회
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getTaaApplDetList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
	/**
	 * 근태신청내역(관리자) 결재상태 변경
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param userId
	 * @param convertMap
	 * @return
	 */
	public ReturnParam saveWtmTaaSts(Long tenantId, String enterCd, String sabun, String userId, Map<String, Object> convertMap);

	/**
	 * 수정된 근태신청내역(관리자) 리스트 조회
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getTaaApplUdtList(Long tenantId, String enterCd, String sabun, Map<String, Object> convertMap);
	
}
