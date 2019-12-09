package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmRuleService {
	
	public List<Map<String, Object>> getRuleList(Long tenantId, String enterCd);
	
	public int saveRule(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
