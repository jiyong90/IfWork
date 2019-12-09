package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmCalendarMapper {
	
	/**
	 * 회사 달력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCalendar(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 근태 달력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getWorkCalendar(Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> getWorkTimeCalendar(Map<String, Object> paramMap) throws Exception;
	
	
	/**
	 * 관리자요_근태 달력 전체 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getEmpWorkCalendar(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 부서장용_근태 달력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrgEmpWorkCalendar(Map<String, Object> paramMap) throws Exception;

	/**
	 * 관리자요_근태 달력 하루 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getEmpWorkCalendarDayInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 달력에서 출퇴근 시간만 업데이트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateEntryDate(Map<String, Object> paramMap) throws Exception;

	/**
	 * 관리자가 강제로 타각정보 업데이트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updateEntryDateByAdm(Map<String, Object> paramMap);

	public int updateEntryDateByAdmTest(Map<String, Object> paramMap);
}
