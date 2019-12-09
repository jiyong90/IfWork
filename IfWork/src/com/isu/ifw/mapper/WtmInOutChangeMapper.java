package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmInOutChangeMapper {
	public List<Map<String, Object>> getInOutChangeList(Map<String, Object> paramMap);
	
	public int setInOutChangeList(Map<String, Object> data);
}
