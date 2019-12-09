package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmCodeService {
	
	public List<Map<String, Object>> getCodeList(Long tenantId, String enterCd, String grpCodeCd);
	
	public List<Map<String, Object>> getCodeListWeb(Long tenantId, String enterCd, String grpCodeCd, String ymd);
	
	public int setCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
	
	public List<Map<String, Object>> getCodeGrpList(Long tenantId, String enterCd);
	
	public int setCodeGrpList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap);
}
