package com.isu.ifw.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmFlexibleStdVO;

public interface WtmScheduleMapper {
		
	/**
	 * 일 퇴근마감
	 **/
	public List<Map<String, Object>> getWtmCloseDay(Map<String, Object> paramMap);
	
	/**
	 * 연장근무시간 조회
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getOtList(Map<String, Object> paramMap);

	/**
	 * 출퇴근 미타각자 조회
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getInoutCheckList(Map<String, Object> paramMap);
}
