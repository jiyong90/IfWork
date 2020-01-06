package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.entity.WtmFlexibleApplyMgr;
import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmFlexibleApplyMgrService {
	
	public List<Map<String, Object>> getApplyList(Long tenantId, String enterCd, String sYmd);

	public int setApplyList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	public ReturnParam setApply(Map<String, Object> paramMap);
	
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
