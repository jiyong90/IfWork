package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmFlexibleApplyMgr;
import com.isu.ifw.mapper.WtmApplListMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.util.WtmUtil;

@Service("applListService")
public class WtmApplListServiceImpl implements WtmApplListService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmApplListMapper applListMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmApplService wtmApplService;
	
	@Autowired
	WtmOtApplMapper wtmOtAppMapper;
	
	@Override
	public List<Map<String, Object>> getOtList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> applList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
			}
			String eYmd = paramMap.get("eYmd").toString();
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				eYmd = paramMap.get("eYmd").toString().replaceAll("-", "");
				paramMap.put("eYmd", eYmd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			applList = applListMapper.getOtList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getOtList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return applList;
	}
	
	@Override
	public List<Map<String, Object>> getEntryList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> applList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
			}
			String eYmd = paramMap.get("eYmd").toString();
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				eYmd = paramMap.get("eYmd").toString().replaceAll("-", "");
				paramMap.put("eYmd", eYmd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			applList = applListMapper.getEntryList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getEntryList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return applList;
	}
	
	
	@Override
	public List<Map<String, Object>> getTaaList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> applList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);

			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
			}
			String eYmd = paramMap.get("eYmd").toString();
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				eYmd = paramMap.get("eYmd").toString().replaceAll("-", "");
				paramMap.put("eYmd", eYmd);
			}

			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}

			applList = applListMapper.getTaaList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getEntryList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}

		return applList;
	}

}