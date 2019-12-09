package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorkteamEmpMapper {
	
	public List<Map<String, Object>> getWorkteamEmpList(Map<String, Object> paramMap);
	public List<Map<String, Object>> dupCheckByYmd(Map<String, Object> paramMap);
}
