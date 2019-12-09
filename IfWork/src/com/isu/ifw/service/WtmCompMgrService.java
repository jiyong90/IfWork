package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmCompMgrService {
	
	public List<Map<String, Object>> getCompMgrList(Long tenantId, String enterCd);

	public int setCompMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
