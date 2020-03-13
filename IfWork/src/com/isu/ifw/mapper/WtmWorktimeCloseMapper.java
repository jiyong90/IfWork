package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorktimeCloseMapper {
		
	/* 근무마감  **/
	public void setWorkTimeCloseIf(Map<String, Object> paramMap);
	
	/* 근무마감 확정  **/
	public int setWorkTimeCloseConfirm(Map<String, Object> paramMap);			
	
	/* 근무마감 조회  **/
	public List<Map<String, Object>> getCloseList(Map<String, Object> paramMap);
	
	/* 근무마감 대상자 조회  **/
	public List<Map<String, Object>> getCloseEmpList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getDayList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getMonList(Map<String, Object> paramMap);
	
	/* 근무마감리스트 입력  **/
	public int insertCloseList(Map<String, Object> paramMap);
	
	/* 근무마감리스트 수정  **/
	public int updateCloseList(Map<String, Object> paramMap);
	
	/* 근무마감리스트 삭제  **/
	public int deleteCloseList(Map<String, Object> paramMap);	
	
	public List<Map<String, Object>> getWorktimeCloseCode(Map<String, Object> paramMap);   
	
	public void setCompMon(Map<String, Object> paramMap);
	
}
