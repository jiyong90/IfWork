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
}
