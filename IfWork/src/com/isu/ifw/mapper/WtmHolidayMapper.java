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
}
