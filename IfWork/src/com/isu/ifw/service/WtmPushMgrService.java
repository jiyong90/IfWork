package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmPushMgrService {
	
	public List<Map<String, Object>> getPushMgrList(Long tenantId, String enterCd);

	public int setPushMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
