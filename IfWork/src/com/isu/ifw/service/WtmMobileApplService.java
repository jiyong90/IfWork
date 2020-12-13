package com.isu.ifw.service;

import com.isu.ifw.vo.ReturnParam;

import java.util.Map;

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

	//연차신청 validate
	public ReturnParam validateAnnualAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;

	//출장신청 validate
	public ReturnParam validateRegaAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;

	//연차신청 request
	public ReturnParam requestAnnualAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;

	//연차신청 request
	public ReturnParam requestRegaAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception ;
}
