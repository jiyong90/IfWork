package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmApplCodeService {
	
	public List<Map<String, Object>> getApplCodeList(Long tenantId, String enterCd);

	public int setApplCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
