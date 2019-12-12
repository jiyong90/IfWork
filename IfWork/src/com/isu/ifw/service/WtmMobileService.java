package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmMobileService {
	
	public List<Map<String, Object>> getTermList(Long tenantId, String enterCd, String sabun, String Ymd);
}
