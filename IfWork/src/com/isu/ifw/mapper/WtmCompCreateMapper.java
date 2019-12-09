package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmCompCreateMapper {
	public List<Map<String, Object>> getCompCreateList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getCompCreateDetList(Map<String, Object> paramMap);
}
