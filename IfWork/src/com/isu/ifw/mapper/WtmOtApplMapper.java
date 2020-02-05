package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmOtApplVO;

public interface WtmOtApplMapper {
	
	/**
	 * 연장근무 신청 시간 업데이트
	 * @param paramMap
	 * @return
	 */
	public void calcOtMinute(Map<String, Object> paramMap);
	
	/**
	 * 연장근무 신청서 조회
	 * @param applId
	 * @return
	 */
	public List<Map<String, Object>> otApplfindByApplId(Map<String, Object> paramMap);
	
	/**
	 * 휴일연장근무 신청서 조회
	 * @param applId
	 * @return
	 */
	public List<Map<String, Object>> otSubsApplfindByOtApplId(Long otApplId);
	 	
	/**
	 * 이전에 신청한 휴일신청서 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getPrevOtSubsApplList(Map<String, Object> paramMap);
	
	/**
	 * 특정 구간의 연장근무 합을 가지고 오자
	 * @param paramMap
	 * @return totOtMinute
	 */
	public Map<String, Object> getTotOtMinuteBySymdAndEymd(Map<String, Object> paramMap);
	
	/**
	 * 연장 근무 신청서(휴일대체 포함) 모바일용
	 * @param paramMap
	 * @return totOtMinute
	 */
	public Map<String, Object> otApplDetailByApplId(Map<String, Object> paramMap);
	
	/**
	 * 잔여 연장근로 
	 * @param paramMap
	 * tenantId
	 * enterCd
	 * List<String> sabuns
	 * ymd
	 * @return
	 */
	public List<Map<String, Object>> getRestOtMinute(Map<String, Object> paramMap);
	
	/**
	 * 대체휴가를 생성해야 할 연장근무신청서
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> subsCreateTarget(Map<String, Object> paramMap);
	
}
