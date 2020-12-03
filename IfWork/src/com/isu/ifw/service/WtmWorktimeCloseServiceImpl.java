package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.mapper.WtmWorktimeCloseMapper;

@Service("worktimeCloseService")
public class WtmWorktimeCloseServiceImpl implements WtmWorktimeCloseService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
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
			logger.debug("getDayList Service End");
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
			logger.debug("getMonList Service End");
		}
		
		return searchList;
	}	
	
	//2020-03-11
	@Override
	public List<Map<String, Object>> getCloseList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			searchList =  wtmWorktimeCloseMapper.getCloseList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getMonList Service End");
		}
		
		return searchList;
	}
		
	@Override
	public int setWorktimeCloseList(Long tenantId, String enterCd,String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			ObjectMapper mapper = new ObjectMapper();
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> insertList = (List<Map<String, Object>>) convertMap.get("insertRows");
				if(insertList != null && insertList.size() > 0) {
					cnt += wtmWorktimeCloseMapper.insertCloseList(convertMap);
				}
				logger.debug("setWorktimeCloseList insertCnt : " + cnt);
			}
			
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> updateList = (List<Map<String, Object>>) convertMap.get("updateRows");
				
				if(updateList != null && updateList.size() > 0) {
					cnt += wtmWorktimeCloseMapper.updateCloseList(convertMap);
				}
				
				logger.debug("setWorktimeCloseList updateCnt : " + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {				
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> closeIds = new ArrayList<Long>();
				if(deleteList != null && deleteList.size() > 0) {
					cnt += wtmWorktimeCloseMapper.deleteCloseList(convertMap);									
				}			

				logger.debug("setWorktimeCloseList deleteCnt : " + cnt);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setWorktimeCloseList end");
		}
		return cnt;
	}
	
	@Override
	public int setWorktimeCloseConfirm(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap) throws Exception {
		int cnt = 0;	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("userId", userId);
			// System.out.println("Impl setWorktimeCloseConfirm paramMap" + paramMap.toString());
			
			cnt = wtmWorktimeCloseMapper.setWorkTimeCloseConfirm(paramMap);
			
			if(cnt>0) {
				
				wtmWorktimeCloseMapper.setWorkTimeClose(paramMap);
				// 월마감 보상휴가기준 및 사용처 확인
				// 월마감용 보상휴가생성이면
				// System.out.println("Impl setWorktimeCloseConfirm cnt" + cnt);
				wtmWorktimeCloseMapper.setCompMon(paramMap);
			}			
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setWorkTimeCloseConfirm Service End");
		}
		//System.out.println("setWorktimeCloseConfirm UPDATE >>> "+cnt);
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getCloseEmpList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			searchList =  wtmWorktimeCloseMapper.getCloseEmpList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getMonList Service End");
		}
		
		return searchList;
	}
	
	@Override
	public List<Map<String, Object>> getWorktimeCloseCode(Long tenantId, String enterCd) {
		List<Map<String, Object>> searchList = new ArrayList();	
		Map<String, Object> paramMap = new HashMap();
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			searchList =  wtmWorktimeCloseMapper.getWorktimeCloseCode(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getMonList Service End");
		}
		
		return searchList;
	}
	
	@Override
	public Map<String, Object> getCloseYn(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		Map<String, Object> searchMap = new HashMap();
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			searchMap =  wtmWorktimeCloseMapper.getCloseYn(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getMonList Service End");
		}
		
		return searchMap;
	}
	
	
	
}