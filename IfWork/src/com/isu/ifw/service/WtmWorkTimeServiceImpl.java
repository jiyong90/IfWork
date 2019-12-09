package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.util.WtmUtil;

@Service
public class WtmWorkTimeServiceImpl implements WtmWorktimeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");

	@Autowired
	WtmWorktimeMapper worktimeMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Override
	public List<Map<String, Object>> getWorktimeCheckList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> WorktimeCheckList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString();
				Date s = WtmUtil.toDate(sYmd, "yyyy-MM-dd");
				paramMap.put("sYmd", WtmUtil.parseDateStr(s, "yyyyMMdd"));
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			
			WorktimeCheckList = worktimeMapper.getWorktimeCheckList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorktimeCheckList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return WorktimeCheckList;
	}
	
	@Override
	public List<Map<String, Object>> getWorktimeDetail(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> WorktimeDetailList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			WorktimeDetailList = worktimeMapper.getWorktimeDetail(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorktimeDetail End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return WorktimeDetailList;
	}

	@Override
	public List<Map<String, Object>> getEntryCheckList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> entryCheckList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString();
				Date s = WtmUtil.toDate(sYmd, "yyyy-MM-dd");
				paramMap.put("sYmd", WtmUtil.parseDateStr(s, "yyyyMMdd"));
			}
			
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				String eYmd = paramMap.get("eYmd").toString();
				Date e = WtmUtil.toDate(eYmd, "yyyy-MM-dd");
				paramMap.put("eYmd", WtmUtil.parseDateStr(e, "yyyyMMdd"));
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			entryCheckList = worktimeMapper.getEntryCheckList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getEntryCheckList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return entryCheckList;
	}

	@Override
	public List<Map<String, Object>> getEntryDiffList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> entryDiffList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString();
				Date s = WtmUtil.toDate(sYmd, "yyyy-MM-dd");
				paramMap.put("sYmd", WtmUtil.parseDateStr(s, "yyyyMMdd"));
			}
			
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				String eYmd = paramMap.get("eYmd").toString();
				Date e = WtmUtil.toDate(eYmd, "yyyy-MM-dd");
				paramMap.put("eYmd", WtmUtil.parseDateStr(e, "yyyyMMdd"));
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			entryDiffList = worktimeMapper.getEntryDiffList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("entryDiffList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return entryDiffList;
	}
	
}