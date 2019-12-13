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
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap);
	//특정 기간 부서원 리스트
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap);
}
