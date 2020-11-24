package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualMgr;
import com.isu.ifw.entity.WtmTaaCode;

import java.util.List;
import java.util.Map;

public interface WtmTaaService {

	public List<Map<String, Object>> getWtmTaaCodelist(Long tenantId, String enterCd) throws Exception ;

	public Map<String, Object> convertWtmTaaCodetoMap(WtmTaaCode taaCode) throws Exception ;

	public WtmAnnualMgr getTaaInfo(Long tenantId, String enterCd, Long applId) throws Exception;
}
