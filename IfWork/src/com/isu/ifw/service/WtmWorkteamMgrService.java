package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmWorkteamMgrService {
	
	public List<Map<String, Object>> getWorkteamMgrList(Long tenantId, String enterCd, Map<String, Object> paramMap);

	public int setWorkteamMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap);
	
	/**
	 * 근무조 코드 조회
	 * @param tenantId - 테넌트 아이디
	 * @param enterCd - 회사코드
	 * @return 
	 */
	public List<Map<String, Object>> getWorkteamCdList(Long tenantId, String enterCd);
}
