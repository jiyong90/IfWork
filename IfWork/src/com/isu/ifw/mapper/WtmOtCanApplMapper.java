package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmOtCanApplMapper {
	 
	
	/**
	 * 연장근무 신청서 조회
	 * @param applId
	 * @return
	 */
	public Map<String, Object> otCanApplfindByApplId(Long applId);
	
	/**
	 * 연장근무 신청서 조회
	 * @param applId
	 * @return
	 */
	public Map<String, Object> otApplAndOtCanApplfindByApplId(Long applId);
	 
}
