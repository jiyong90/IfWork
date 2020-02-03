package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmApplRecLine;

/**
 * 
 * @author 
 *
 */
public interface WtmApplCodeService {
	
	public List<Map<String, Object>> getApplCodeList(Long tenantId, String enterCd);

	public int setApplCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	/**
	 * 신청서별 수신처 조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * applCodeId
	 * @return
	 */
	public List<Map<String, Object>> getRecLine(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	/**
	 * 신청서 수신처 저장
	 * @param tenantId
	 * @param enterCd
	 * @param userId
	 * @param convertMap
	 * @return
	 */
	public int saveRecLine(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
