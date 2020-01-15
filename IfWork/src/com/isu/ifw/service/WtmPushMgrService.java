package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmPushMgrService {
	
	public List<Map<String, Object>> getPushMgrList(Long tenantId, String enterCd);
	public List<Map<String, Object>> allPushMgrList(String ymd);
	public int setPushMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	/**
	 * 메시지 저장
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @param userId
	 * @return
	 */
	public void saveMsg(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
}
