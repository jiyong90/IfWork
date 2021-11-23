package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmHolidayMapper {
	
	/**
	 * 공휴일 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getHolidayList(Map<String, Object> paramMap);
	
	/**
	 * 공휴일 삭제
	 * @param paramMap
	 */
	public void deleteHolidays(Map<String, Object> paramMap);
	
	/**
	 * 공휴일 저장
	 * @param paramMap
	 */
	public void insertHolidays(Map<String, Object> paramMap);
	
	/**
	 * 공휴일 수정
	 * @param paramMap
	 */
	public void updateHolidays(Map<String, Object> paramMap);
	
	
	//근무계획 변경 대상자의 근무 계획 조회
	public List<Map<String, Object>> getWorkPlan(Map<String, Object> paramMap);
	
	/**
	 * 공휴일 calendar 삭제
	 * @param paramMap
	 */
	public void deleteWorkCalendar(Map<String, Object> paramMap);
	
	/**
	 * 공휴일 저장
	 * @param paramMap
	 */
	public void insertWorkResult(Map<String, Object> paramMap);
	
	/**
	 * 시간코드 조회
	 * @param paramMap
	 * @return 
	 */
	public Map<String, Object> getTimeCdMgrId(Map<String, Object> paramMap);
	
}
