package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmInOutChangeMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.util.WtmUtil;

@Service("inOutChangeService")
public class WtmInOutChangeServiceImpl implements WtmInOutChangeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmInOutChangeMapper inOutChangeMapper;
	
	@Autowired
	WtmCalendarMapper calendarMapper;

	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmInoutHisMapper inoutHisMapper;
		
	@Override
	public int setInOutChangeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
			List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
			List<Map<String, Object>> saveList = new ArrayList();
				
			try {
				if(iList != null && iList.size() > 0) {
					saveInout(tenantId, enterCd, userId, convertMap);
				}
			} catch(Exception e) {
				e.printStackTrace();
				return 0;
			}
			
			for(Map<String, Object> data : iList) {
				String chgSdate = null;
				if(data.containsKey("chgSdate") && data.get("chgSdate")!=null && !"".equals(data.get("chgSdate")))
					chgSdate = data.get("chgSdate").toString();
				String chgEdate = null;
				if(data.containsKey("chgEdate") && data.get("chgEdate")!=null && !"".equals(data.get("chgEdate")))
					chgEdate = data.get("chgEdate").toString();
				
				//출퇴근 타각이 둘 다 들어왔을 때만 인정시간 넣어줌
				if(chgSdate!=null && chgEdate!=null)
					empService.calcApprDayInfo(tenantId, enterCd, data.get("ymd").toString(), data.get("ymd").toString(), data.get("sabun").toString());
			}
			
		}
			
		logger.debug("setInOutChangeList Service End ");
		
		return 1;
	}
	
	@Transactional
	public void saveInout(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		
		convertMap.put("tenantId", tenantId);
		convertMap.put("enterCd", enterCd);
		convertMap.put("userId", userId);
		convertMap.put("typeCd", "ADM");
		
		cnt = inOutChangeMapper.setInOutChangeList(convertMap);
		System.out.println("inOutChangeMapper.setInOutChangeList " + cnt);
		cnt = calendarMapper.updateEntryDateByAdm(convertMap);
		System.out.println("calendarMapper.updateEntryDateByAdm " + cnt);
//		for(Map<String, Object> data : iList) {
//			data.put("tenantId", tenantId);
//			data.put("enterCd", enterCd);
//			data.put("userId", userId);
//			data.put("typeCd", "ADM");
//			int n = inOutChangeMapper.setInOutChangeList(data);
//			System.out.println("inOutChangeMapper.setInOutChangeList " + n);
//			n = calendarMapper.updateEntryDateByAdm(data);
//			System.out.println("calendarMapper.updateEntryDateByAdm " + n);
//
//			cnt++;
//		}
	}
	
	
	@Override
	@Transactional
	public Map<String, Object> setInOutChange( Map<String, Object> paramMap) {
		Map<String, Object> retMap = new HashMap();
		int cnt = 0;
		
		cnt = inOutChangeMapper.setInOutChange(paramMap);
		System.out.println("inOutChangeMapper.setInOutChange " + cnt);
		cnt = calendarMapper.updateEntryDateByAdmRow(paramMap);
		System.out.println("calendarMapper.updateEntryDateByAdmRow " + cnt);
		try {
			retMap = calendarMapper.getStdMgrInfo(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	@Override
	public List<Map<String, Object>> getInpoutChangeHis(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> list = new ArrayList();
		try {
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(!paramMap.containsKey("sYmd")) {
				paramMap.put("ymd", "");
			} else {
				ymd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("ymd", ymd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, ymd));
			}
			
			list = inOutChangeMapper.getInOutChangeList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getInpoutChangeHis Service End");
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getEntryInoutList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> entryInoutList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("ymd")!=null && !"".equals("ymd")) {
				ymd = paramMap.get("ymd").toString().replaceAll("-", "");
				paramMap.put("ymd", ymd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, ymd));
			}
			
			entryInoutList = inOutChangeMapper.getEntryInoutList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			logger.debug("entryDiffList End");

		}
		
		return entryInoutList;
	}
	
}