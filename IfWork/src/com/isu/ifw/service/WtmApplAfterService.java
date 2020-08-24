package com.isu.ifw.service;

import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmApplAfterService {
	
	/**
	 * 결재 상태값 변경 후처리
	 * @param tenantId
	 * @param enterCd
	 * @param applId
	 * @param paramMap
	 * @param sabun
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ReturnParam applyStsAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws Exception;

	public ReturnParam applyCanAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap,String sabun, String userId) throws Exception;
	
	public ReturnParam applyOtCanAdminAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap,String sabun, String userId) throws Exception;
}
