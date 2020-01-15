package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmMobileApplService {
	//연장/휴일 근무 신청서 validate
	public ReturnParam validateOtAppl(String eventSource, Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;
	
	//근태사유서 validate
	public ReturnParam validateEntryChgAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;

	//초기데이터
	public Map<String, Object> init(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;
	//연장/휴일 근무 신청서 request
	public ReturnParam requestOtAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;

	//근태사유서 request
	public ReturnParam requestEntryChgAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;
}
