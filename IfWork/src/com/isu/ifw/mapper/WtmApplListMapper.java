package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmApplLineVO;

public interface WtmApplListMapper {
	public List<Map<String, Object>> getOtList(Map<String, Object> paramMap);

	public List<Map<String, Object>> getEntryList(Map<String, Object> paramMap);

	public List<Map<String, Object>> getTaaList(Map<String, Object> paramMap);

	public List<Map<String, Object>> getOtSubsList(Map<String, Object> paramMap);
}
