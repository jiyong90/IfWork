package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmRuleMapper {
	/**
	 * 규칙 입력
	 * @param list
	 * @return
	 */
	public int insertRule(Map<String, Object> paramMap);
	
	/**
	 * 규칙 수정
	 * @param list
	 * @return
	 */
	public int updateRule(Map<String, Object> paramMap);
}
