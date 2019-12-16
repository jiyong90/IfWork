package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmInoutService {
	
	public Map<String, Object> getMenuContext(Long tenantId, String enterCd, String sabun);
	public Map<String, Object> getMenuContext2(Long tenantId, String enterCd, String sabun);
	public ReturnParam updateTimecard(Map<String, Object> paramMap) throws Exception;
//	public int checkGoback(Long tenantId, String enterCd, String sabun) throws Exception;
	public Map<String, Object> updateTimeStamp(Map<String, Object> paramMap);
	public List<Map<String, Object>> getMyInoutList(Map<String, Object> paramMap) throws Exception;
	public List<Map<String, Object>> getMyInoutHistory(Map<String, Object> paramMap) throws Exception;
	public Map<String, Object> getMyInoutDetail(Map<String, Object> paramMap) throws Exception;
	public ReturnParam cancel(Map<String, Object> paramMap) throws Exception;
}
