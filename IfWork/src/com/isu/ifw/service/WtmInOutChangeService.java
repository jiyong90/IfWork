package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmInOutChangeService {

	public int setInOutChangeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	public List<Map<String, Object>> getInpoutChangeHis(Map<String, Object> paramMap);
}
