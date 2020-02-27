package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorktimeCloseMapper {
		
	/* 근무마감
	 **/
	public void setWorkTimeCloseIf(Map<String, Object> paramMap);
	
	/* 근무마감 확정
	 **/
	// public int setWorkTimeCloseConfirm(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getDayList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMonList(Map<String, Object> paramMap);
	
	
}
