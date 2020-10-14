package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmApplListService {
	
	public List<Map<String, Object>> getOtList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getEntryList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);

	public List<Map<String, Object>> getTaaList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);

}
