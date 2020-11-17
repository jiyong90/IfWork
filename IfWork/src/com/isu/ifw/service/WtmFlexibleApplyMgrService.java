package com.isu.ifw.service;

import com.isu.ifw.entity.WtmFlexibleApply;
import com.isu.ifw.vo.ReturnParam;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmFlexibleApplyMgrService {
	
	public List<Map<String, Object>> getApplyList(Long tenantId, String enterCd, String sYmd);

	public int setApplyList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) throws Exception;
	
	//확정, 비동기
	public void setApplyAsync(List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList);

	//확정, 비동기
	public void setApplyAsync2(WtmFlexibleApply flexibleApplyId, List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList, String id, String workTypeCd) throws ParseException;

	//확정, 동기
	public int setApply(List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList);
	public void setApply(WtmFlexibleApply flexibleApply, List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList);

	//	public int setApplyForOne(Map<String, Object> searchSabun, List<Map<String, Object>> ymdList);
	public List<Map<String, Object>> getApplyYmdList(Map<String, Object> paramMap) throws Exception;

	
	public List<Map<String, Object>> getworkTypeList(Long flexibleStdMgrId);
	
	public Map<String, Object> getEymd(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyGrpList(Map<String, Object> paramMap);

	public int setApplyGrpList(String userId, Long flexibleApplyId, Map<String, Object> convertMap);
	
	public List<Map<String, Object>> getApplyEmpList(Map<String, Object> paramMap);

	public int setApplyEmpList(String userId,Long flexibleApplyId, Map<String, Object> convertMap);
	
	public List<Map<String, Object>> getApplyEmpPopList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getElasDetail(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);

	public ReturnParam createElasPlan(Long tenantId, String enterCd, Long flexibleApplyId, String userId);
}
