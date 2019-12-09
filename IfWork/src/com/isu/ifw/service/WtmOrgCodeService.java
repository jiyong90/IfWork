package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmOrgCodeService {
	
	public List<Map<String, Object>> getOrgCodeList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	public List<Map<String, Object>> getOrgComboList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	
	
}
