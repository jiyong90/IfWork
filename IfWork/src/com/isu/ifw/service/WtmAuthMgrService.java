package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.isu.ifw.entity.CommAuth;

/**
 * 권한 관리
 *
 * @author 
 *
 */
@Service
public interface WtmAuthMgrService{
	
	/**
	 * 권한 조회
	 * @param tenantId
	 * @return
	 */
	public List<Map<String, Object>> getAuthList(Long tenantId);
	
	/**
	 * 권한 저장
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @param userId
	 * @return
	 */
	public int saveAuthList(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
	
	/**
	 * 권한 대상자 조회
	 * @param tenantId
	 * @return
	 */
	public List<Map<String, Object>> getAuthUserList(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
	
	/**
	 * 권한 대상자 저장
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @param userId
	 * @return
	 */
	public int saveAuthUserList(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
}