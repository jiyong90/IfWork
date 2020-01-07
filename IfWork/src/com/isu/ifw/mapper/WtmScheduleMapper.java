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
	
	
	
}
