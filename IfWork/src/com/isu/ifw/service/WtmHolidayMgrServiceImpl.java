package com.isu.ifw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.mapper.WtmHolidayMapper;

@Service("holidayMgrService")
public class WtmHolidayMgrServiceImpl implements WtmHolidayMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	private WtmHolidayMapper holidayMapper;

	@Override
	public List<Map<String, Object>> getHolidayList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> holidayList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			paramMap.put("symd", paramMap.get("symd").toString().replaceAll("[-.]", ""));
			paramMap.put("eymd", paramMap.get("eymd").toString().replaceAll("[-.]", ""));
			
			holidayList = holidayMapper.getHolidayList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getHolidayList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return holidayList;
	}
	
	@Override
	@Transactional
	public int setHolidayList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				
				if(iList != null && iList.size() > 0) {
					paramMap.put("deleteList", iList);
					holidayMapper.deleteHolidays(paramMap);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
				if(iList != null && iList.size() > 0) {
					paramMap.put("insertList", iList);
					holidayMapper.insertHolidays(paramMap);
				}
				
				MDC.put("insert cnt", "" + iList.size());
				cnt += iList.size();
			}
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				if(iList != null && iList.size() > 0) {
					paramMap.put("updateList", iList);
					holidayMapper.updateHolidays(paramMap);
				}
				
				MDC.put("update cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
}