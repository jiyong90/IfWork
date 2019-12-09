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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmBaseWorkMgr;
import com.isu.ifw.entity.WtmRule;
import com.isu.ifw.mapper.WtmRuleMapper;
import com.isu.ifw.repository.WtmRuleRepository;

@Service("ruleService")
public class WtmRuleServiceImpl implements WtmRuleService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmRuleRepository ruleRepository;
	
	@Resource
	WtmRuleMapper ruleMapper;

	@Override
	public List<Map<String, Object>> getRuleList(Long tenantId, String enterCd) {
		List<Map<String, Object>> ruleList = new ArrayList();	
		try {
			List<WtmRule> list = ruleRepository.findByTenantIdAndEnterCd(tenantId, enterCd);
			
			for(WtmRule m : list) {
				Map<String, Object> rule = new HashMap();
				rule.put("ruleId", m.getRuleId());
				rule.put("tenantId", m.getTenantId());
				rule.put("enterCd", m.getEnterCd());
				rule.put("ruleNm", m.getRuleNm());
				rule.put("ruleValue", m.getRuleValue());
				rule.put("note", m.getNote());
				ruleList.add(rule);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getRuleList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return ruleList;
	}
	
	@Override
	public int saveRule(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			ObjectMapper mapper = new ObjectMapper();
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> insertList = (List<Map<String, Object>>) convertMap.get("insertRows");
				if(insertList != null && insertList.size() > 0) {
					cnt += ruleMapper.insertRule(convertMap);
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> updateList = (List<Map<String, Object>>) convertMap.get("updateRows");
				
				if(updateList != null && updateList.size() > 0) {
					cnt += ruleMapper.updateRule(convertMap);
				}
				
				MDC.put("update cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> ruleIds = new ArrayList<Long>();
				if(deleteList != null && deleteList.size() > 0) {
					for(Map<String, Object> d : deleteList) {
						Long ruleId = Long.parseLong(d.get("ruleId").toString());
						ruleIds.add(ruleId);
					}
					ruleRepository.deleteByRuleIdsIn(ruleIds);
					
					cnt += ruleIds.size();
				}
				
				MDC.put("delete cnt", "" + ruleIds.size());
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("saveRule Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
}