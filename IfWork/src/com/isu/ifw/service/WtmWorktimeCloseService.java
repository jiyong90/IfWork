package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmWorktimeCloseService {
	
	public List<Map<String, Object>> getDayList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMonList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	/**
	 * 근무마감 리스트(관리자용)	 
	 */
	public List<Map<String, Object>> getCloseList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	/**
	 * 근무마감 대상자 리스트(관리자용)	 
	 */
	public List<Map<String, Object>> getCloseEmpList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	/**
	 * 근무마감 리스트 저장(관리자용)	 
	 */
	public int setWorktimeCloseList(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap);
	
	/**
	 * 월근무마감 확정 저장(관리자용)	 
	 */
	public int setWorktimeCloseConfirm(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 근무마감 리스트 조회 코드
	 */
	public List<Map<String, Object>> getWorktimeCloseCode(Long tenantId, String enterCd);
}
