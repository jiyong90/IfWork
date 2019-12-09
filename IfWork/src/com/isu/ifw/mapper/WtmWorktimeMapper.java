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
}
