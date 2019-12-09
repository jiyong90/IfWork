package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmOrgCodeMapper {
	
	public List<Map<String, Object>> getOrgCodeList(Map<String, Object> paramMap);
	public List<Map<String, Object>> getOrgComboList(Map<String, Object> paramMap);
}
