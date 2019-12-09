package com.isu.ifw.service;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author 
 *
 */
public interface WtmEmpMgrService {
	//사원 이력 조회
	public List<Map<String, Object>> getEmpHisList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	//사원 조회
	public Map<String, Object> getEmpHis(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	//사원변경이력조회
	public List<Map<String, Object>> getEmpIfMsgList(Long tenantId, String enterCd, Map<String, Object> paramMap);
}
