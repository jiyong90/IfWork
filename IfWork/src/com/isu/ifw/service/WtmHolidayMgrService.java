package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmHolidayMgrService {
	
	public List<Map<String, Object>> getHolidayList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	public int setHolidayList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}