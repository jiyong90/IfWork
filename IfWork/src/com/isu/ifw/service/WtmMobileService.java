package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmMobileService {

	//부서원 work term 기간 리스트
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap) throws Exception ;
	//특정 기간 부서원 리스트
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap) throws Exception ;
	//특정 기간 타각 이력
	public List<Map<String, Object>> getTeamDetail(Map<String, Object> paramMap) throws Exception ;


	//모바일에서 보여줄 코드 목록 
	public Map<String,Object> getCodeList(Long tenantId, String enterCd, String key) throws Exception ;
	
	//adapter에서 data get
	public Map<String, Object> getDataMap(String url, String queryId, Map<String, Object> user) throws Exception ;
	//adapter에서 data get(신)
	public Map<String, Object> getDataMap(String url, Map<String, Object> user) throws Exception ;
	
	//신청서 목록 조회
	public List<Map<String, Object>> getApplList(Long tenantId, String enterCd, String sabun, String typeCd, int startPage, int pageCount) throws Exception ;

	//신청서 상세
	public Map<String, Object> getApplDetail(Long tenantId, String enterCd, String sabun, String applKey) throws Exception ;

	//계획 시간 조회
	public List<Map<String, Object>> getPlan(Map<String, Object> paramMap) throws Exception ;

	//부서원 계획 시간 조회
	public List<Map<String, Object>> getPlanList(Map<String, Object> paramMap) throws Exception ;
}
