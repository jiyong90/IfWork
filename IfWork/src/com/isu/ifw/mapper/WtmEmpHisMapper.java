package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface WtmEmpHisMapper {
	
	public List<Map<String, Object>> getEmpHisList(Map<String, Object> paramMap);
	public Map<String, Object> getEmpHis(Map<String, Object> paramMap);
	
	public void insertCommUser(@Param("tenantId") Long tenantId);
	public void updateCommUserPw(Map<String, Object> paramMap);
	public void deleteCommUser(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getCreateBaseTarget(Map<String, Object> paramMap);
}
