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
	
	public List<Map<String, Object>> getInpoutChangeHis(Long tenantId, String enterCd, String sabun,Map<String, Object> paramMap);
	
	public Map<String, Object> setInOutChange(Map<String, Object> paramMap);
	
	/**
	 * 출/퇴근 로우데이터 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getEntryInoutList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);

}
