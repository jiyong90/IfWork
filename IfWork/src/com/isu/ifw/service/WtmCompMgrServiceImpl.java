package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmCompMgr;
import com.isu.ifw.repository.WtmCompMgrRepository;

@Service("compMgrService")
public class WtmCompMgrServiceImpl implements WtmCompMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmCompMgrRepository compMgrRepository;

	@Override
	public List<Map<String, Object>> getCompMgrList(Long tenantId, String enterCd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		try {
			List<WtmCompMgr> list = compMgrRepository.findByTenantIdAndEnterCdOrderBySymdDesc(tenantId, enterCd);
			
			for(WtmCompMgr l : list) {
				Map<String, Object> code = new HashMap();
				code.put("compMgrId", l.getCompMgrId());
				code.put("tenantId", l.getTenantId());
				code.put("enterCd", l.getEnterCd());
				code.put("businessPlaceCd", l.getBusinessPlaceCd());
				code.put("symd", l.getSymd());
				code.put("eymd", l.getEymd());
				code.put("compTimeType", l.getCompTimeType());
				code.put("timeType", l.getTimeType());
				code.put("timeLimit", l.getTimeLimit());
				code.put("limitType", l.getLimitType());
				code.put("useType", l.getUseType());
				code.put("note", l.getNote());
				codeList.add(code);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getTaaCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return codeList;
	}
	
	@Override
	public int setCompMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmCompMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCompMgr code = new WtmCompMgr();
						code.setCompMgrId(l.get("compMgrId").toString().equals("") ? null : Long.parseLong(l.get("compMgrId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setBusinessPlaceCd(l.get("businessPlaceCd").toString());
						code.setSymd(l.get("symd").toString());
						code.setEymd(l.get("eymd").toString());
						code.setCompTimeType(l.get("compTimeType").toString());
						code.setTimeType(l.get("timeType").toString());
						code.setTimeLimit(l.get("timeLimit").toString().equals("") ? null : Integer.parseInt(l.get("timeLimit").toString()));
						code.setLimitType(l.get("limitType").toString());
						code.setUseType(l.get("useType").toString());
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						codes.add(code);
					}
					codes = compMgrRepository.saveAll(codes);
					cnt += codes.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmCompMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCompMgr code = new WtmCompMgr();
						code.setCompMgrId(Long.parseLong(l.get("compMgrId").toString()));
						codes.add(code);
					}
					compMgrRepository.deleteAll(codes);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setTaaCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
}