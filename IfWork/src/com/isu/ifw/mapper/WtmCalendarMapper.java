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
	 * 관리자용_근무제도 확인
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStdMgrInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 관리자요_근태 달력 하루 조회(선근제 근무계획없음용)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getEmpWorkCalendarDayInfoSele(Map<String, Object> paramMap) throws Exception;
	
	
	/**
	 * 달력에서 출퇴근 시간만 업데이트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateEntryDate(Map<String, Object> paramMap) throws Exception;

	/**
	 * 타각프로시저 호출
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateTimeCard(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 타각프로시저 호출(외출복귀 기능변경)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateTimeCard3(Map<String, Object> paramMap) throws Exception;
	

	/**
	 * 관리자가 강제로 타각정보 업데이트
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updateEntryDateByAdm(Map<String, Object> paramMap);

	public int updateEntryDateByAdmTest(Map<String, Object> paramMap);
	
	/**
	 * 캘린더 타각정보 업데이트 (모바일)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updateEntryDateCalendar(Map<String, Object> paramMap);
	/**
	 * 캘린더 타각정보 업데이트 (모바일)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int cancelEntryDateCalendar(Map<String, Object> paramMap);
}
