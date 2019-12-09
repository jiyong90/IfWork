package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmFlexibleApplyMgrMapper {
	
	public List<Map<String, Object>> getApplyList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyRepeatList(Map<String, Object> paramMap);
	
	public Map<String, Object> getEymd(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getworkTypeList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyConfirmList(Map<String, Object> paramMap);
	
	public int insertApplyEmp(Map<String, Object> paramMap);
	
	public int updateApplyEmp(Map<String, Object> paramMap);
	
	public Map<String, Object> setApplyEmpId(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyGrpList(Map<String, Object> paramMap);
	
	public int insertGrp(List<Map<String, Object>> list);
	
	public int updateGrp(List<Map<String, Object>> list);
	
	public int deleteGrp(List<Map<String, Object>> list);
	
	public List<Map<String, Object>> getApplyEmpList(Map<String, Object> paramMap);
	
	public int insertEmp(List<Map<String, Object>> list);
	
	public int updateEmp(List<Map<String, Object>> list);
	
	public int deleteEmp(List<Map<String, Object>> list);
	
	public int deleteApplyEmpTemp(Map<String, Object> paramMap);
	
	public int insertApplyEmpTemp(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyEmpPopList(Map<String, Object> paramMap);
}
