package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmTaaCodeService {
	
	public List<Map<String, Object>> getTaaCodeList(Long tenantId, String enterCd, Map<String, Object> paramMap);

	public int setTaaCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
