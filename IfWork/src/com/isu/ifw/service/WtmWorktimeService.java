package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 근태 이상자
 *
 * @author 
 *
 */
@Service
public interface WtmWorktimeService{
	
	/** 
	 * 근무시간 초과자 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getWorktimeCheckList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
	/**
	 * 근무시간 상세 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getWorktimeDetail(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	/**
	 * 출/퇴근 미타각자 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getEntryCheckList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
	/**
	 * 출/퇴근 차이자 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getEntryDiffList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
}