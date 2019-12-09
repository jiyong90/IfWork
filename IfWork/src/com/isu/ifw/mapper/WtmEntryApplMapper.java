package com.isu.ifw.mapper;

import java.util.Map;


public interface WtmEntryApplMapper {
	/**
	 * 같은 날 이미 신청 중인 근태사유서가 있는지 체크
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> checkDuplicateEntryAppl(Map<String, Object> paramMap);
	
}
