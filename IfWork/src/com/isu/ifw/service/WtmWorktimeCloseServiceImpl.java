package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmWorktimeCloseMapper;

@Service("worktimeCloseService")
public class WtmWorktimeCloseServiceImpl implements WtmWorktimeCloseService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmWorktimeCloseMapper wtmWorktimeCloseMapper;
	
	@Override
	public List<Map<String, Object>> getDayList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			searchList =  wtmWorktimeCloseMapper.getDayList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getDayList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	@Override
	public List<Map<String, Object>> getMonList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			searchList =  wtmWorktimeCloseMapper.getMonList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getMonList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	
}