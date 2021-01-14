package com.isu.ifw.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 근태 이상자
 *
 * @author 
 *
 */
@Service
public interface WtmWorkPlanTimeMonService {
	
	/** 
	 * 계획근무시간 월별조회
	 * @param tenantId
	 * @param enterCd
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getWorkPlanTimeMonList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);

}