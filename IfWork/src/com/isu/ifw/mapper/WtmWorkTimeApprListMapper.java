package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorkTimeApprListMapper {
	
	/**
	 * 인정근무조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getWorkTimeApprList(Map<String, Object> paramMap) throws Exception;
	
}
