package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmAuthMgrMapper {
	/**
	 * 권한 대상자 조회
	 * @param paramMap
	 * tenantId
	 * enterCd
	 * authId
	 * ymd
	 * @return
	 */
	public List<Map<String, Object>> getAuthUserList(Map<String, Object> paramMap);
	
	/**
	 * 권한 대상자 저장
	 * @param paramMap
	 * @return
	 */
	public int saveAuthUser(Map<String, Object> paramMap);
	
	/**
	 * 사용자의 권한 조회
	 * @param paramMap
	 * tenantId
	 * enterCd
	 * sabun
	 * @return
	 */
	public List<Map<String, Object>> findAuthByTenantIdAndEnterCdAndSabun(Map<String, Object> paramMap);
}
