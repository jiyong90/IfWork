package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmOrgChartMapper {
	
	/**
	 * 하위 조직 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getLowLevelOrg(Map<String, Object> paramMap) throws Exception;
	
}
