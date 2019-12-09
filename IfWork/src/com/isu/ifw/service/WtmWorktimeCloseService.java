package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.option.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmWorktimeCloseService {
	
	public List<Map<String, Object>> getDayList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMonList(Long tenantId, String enterCd, Map<String, Object> paramMap);
}
