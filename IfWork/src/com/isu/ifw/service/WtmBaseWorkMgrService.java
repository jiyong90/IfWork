package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.entity.WtmBaseWorkMgr;

/**
 * 
 * @author 
 *
 */
public interface WtmBaseWorkMgrService {
	
	public List<Map<String, Object>> getBaseWorkList(Long tenantId, String enterCd, Map<String, Object> paramMap);

	public int setBaseWorkList(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap);
	
	public List<WtmBaseWorkMgr> findByFlexibleStdMgrId(Long flexibleStdMgrId);
}
