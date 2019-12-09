package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmEmpHisMapper {
	
	public List<Map<String, Object>> getEmpHisList(Map<String, Object> paramMap);
	public Map<String, Object> getEmpHis(Map<String, Object> paramMap);
}
