package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmCompApplMapper {
	
	/**
	 * 보상휴가 신청 내역서 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getApplList(Map<String, Object> paramMap);
	
	/**
	 * 보상휴가 신청 저장
	 * @param paramMap
	 * @return
	 */
	public void saveApplRequest(Map<String, Object> paramMap);
	
	
	/**
	 * 보상휴가 신청 가능 시간 조회
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getPossibleUseTime(Map<String, Object> paramMap);
	
	/**
	 * 근무일수 확인
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getWorkDayCalendar(Map<String, Object> paramMap);
	
	/**
	 * 보상휴가 신청 가능 시간 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getCheckAppl(Map<String, Object> paramMap);
	
	
	public List<Map<String, Object>> compApplfindByApplId(Map<String, Object> paramMap);

	public List<Map<String, Object>> getCompApplList(Map<String, Object> paramMap);
}
