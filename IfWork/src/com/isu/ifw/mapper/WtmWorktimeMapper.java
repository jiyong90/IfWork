package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorktimeMapper {
	
	//근무 이상자 조회 : 근무시간 초과자
	public List<Map<String, Object>> getWorktimeCheckList(Map<String, Object> paramMap);
	
	//근무 상세
	public List<Map<String, Object>> getWorktimeDetail(Map<String, Object> paramMap);
	
	//근무 이상자 조회 : 출퇴근 미타각자
	public List<Map<String, Object>> getEntryCheckList(Map<String, Object> paramMap);
	
	//근무 이상자 조회 : 출퇴근 차이자
	public List<Map<String, Object>> getEntryDiffList(Map<String, Object> paramMap);
	
	//모바일 부서원 근태현황  : 달별 기간 목록 조회
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap);

	//모바일 부서원 근태현황  : 기간별 부서원 근태 현황 조회
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap);

	//근무계획 변경 대상자의 근무 계획 조회
	public List<Map<String, Object>> getWorkPlan(Map<String, Object> paramMap);

	//근무계획 변경 대상자 조회
	public List<Map<String, Object>> getWorkTimeChangeTarget(Map<String, Object> paramMap);

	//근무시간조회
	public List<Map<String, Object>> getWorkTimeList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getCloseDayList(Map<String, Object> paramMap);

	/**
	 * 근무시간 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getWorktimeCheckAllList(Map<String, Object> paramMap);
}
