package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmValidatorMapper {
	 
	public Map<String, Object> checkDuplicateTaa(Map<String, Object> paramMap);
	public Map<String, Object> checkDuplicateTaaByTaaTypeH(Map<String, Object> paramMap);

	public int getWorkCnt(Map<String, Object> paramMap);
}
