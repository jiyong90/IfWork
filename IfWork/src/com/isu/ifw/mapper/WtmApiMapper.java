package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmApiMapper {
	public List<Map<String, Object>> getValidEvent(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getEmpList(Map<String, Object> paramMap);
}