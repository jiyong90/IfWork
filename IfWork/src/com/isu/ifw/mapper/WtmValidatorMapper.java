package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmValidatorMapper {
	 
	public Map<String, Object> checkDuplicateTaa(Map<String, Object> paramMap);
	public Map<String, Object> checkDuplicateTaaByTaaTypeH(Map<String, Object> paramMap);

	public int getWorkCnt(Map<String, Object> paramMap);
	
	public Map<String, Object> checkDuplicateTaaAppl(Map<String, Object> paramMap);
	public Map<String, Object> checkApplMinute(Map<String, Object> paramMap);
	public List<Map<String, Object>> checkTotalWorkMinuteForSele(Map<String, Object> paramMap);
}
