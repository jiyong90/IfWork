package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmGpsMgrService {
	
	public List<Map<String, Object>> getGpsList(Long tenantId, String enterCd, Map<String, Object> paramMap) throws Exception;
	
}
