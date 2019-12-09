package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorktimeCloseMapper {
	
	public List<Map<String, Object>> getDayList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMonList(Map<String, Object> paramMap);
}
