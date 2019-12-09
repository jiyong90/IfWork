package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.option.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmInoutService {
	
	public Map<String, Object> getMenuContext(Long tenantId, String enterCd, String sabun);
	public ReturnParam updateTimecard(Long tenantId, String enterCd, String sabun, String ymd, String inoutType, String entryType) throws Exception;
//	public int checkGoback(Long tenantId, String enterCd, String sabun) throws Exception;
	public Map<String, Object> updateTimeStamp(Map<String, Object> paramMap);
	public List<Map<String, Object>> getMyInoutList(Long tenantId, String enterCd, String sabun, String month) throws Exception;
	public List<Map<String, Object>> getMyInoutHistory(Long tenantId, String enterCd, String sabun, String ymd) throws Exception;
	public Map<String, Object> getMyInoutDetail(Long tenantId, String enterCd, String sabun, String inoutTypeCd, String inoutDate) throws Exception;
	public ReturnParam cancel(Map<String, Object> paramMap) throws Exception;
}
