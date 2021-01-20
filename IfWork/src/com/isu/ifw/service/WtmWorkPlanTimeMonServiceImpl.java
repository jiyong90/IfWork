package com.isu.ifw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorkPlanTimeMonMapper;
import com.isu.ifw.mapper.WtmWorkteamEmpMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WtmWorkPlanTimeMonServiceImpl implements WtmWorkPlanTimeMonService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");

	@Autowired
	WtmWorkPlanTimeMonMapper workPlanTimeMonMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;


	@Override
	public List<Map<String, Object>> getWorkPlanTimeMonList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> workPlanTimeMonList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("yyyyMM", paramMap.get("yyyyMM").toString().replaceAll("[-.]", "" ));


			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			sYmd = sYmd.replaceAll("[-.]", "");
			paramMap.put("sYmd", sYmd);


			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}


			workPlanTimeMonList = workPlanTimeMonMapper.getWorkPlanTimeMonList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorkPlanTimeMonList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return workPlanTimeMonList;
	}

}