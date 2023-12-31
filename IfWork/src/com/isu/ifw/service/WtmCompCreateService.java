package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 
 * @author 
 *
 */
public interface WtmCompCreateService {
	
	public List<Map<String, Object>> getCompCreateList(Map<String, Object> paramMap);

	public List<Map<String, Object>> getCompCreateDetList(Map<String, Object> paramMap);
	
	/** 마감확정 후 보상휴가 생성 내역 조회  */
	public List<Map<String, Object>> getCompCreateHRList(Map<String, Object> paramMap);
}
