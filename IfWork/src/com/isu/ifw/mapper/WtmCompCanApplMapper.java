package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmCompCanApplMapper {
	
	
	public List<Map<String, Object>> compCanApplfindByApplId(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getCompCanApplList(Map<String, Object> paramMap);

}
