package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmInOutChangeMapper {
	public List<Map<String, Object>> getInOutChangeList(Map<String, Object> paramMap);
	
	public int setInOutChangeList(Map<String, Object> data);
	
	public int setInOutChange(Map<String, Object> data);
	
	public List<Map<String, Object>> getEntryInoutList(Map<String, Object> paramMap);
	
	/**
	 * 출퇴근 삭제시 Result 업데이트
	 * @param paramMap
	 * @return
	 */
	public int updateResultApprDate(Map<String, Object> paramMap);
	
	
}
