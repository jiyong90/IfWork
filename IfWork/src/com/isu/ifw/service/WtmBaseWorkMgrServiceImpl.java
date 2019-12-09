package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmBaseWorkMgr;
import com.isu.ifw.repository.WtmBaseWorkMgrRepository;
import com.isu.ifw.util.WtmUtil;

@Service("baseWorkMgrService")
public class WtmBaseWorkMgrServiceImpl implements WtmBaseWorkMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmBaseWorkMgrRepository baseWorkRepository;
	
	@Override
	public List<Map<String, Object>> getBaseWorkList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> baseList = new ArrayList();			
		
		String sYmd = null;
		if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd").toString())) {
			sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
		} else {
			sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		}
		
		List<WtmBaseWorkMgr> list = baseWorkRepository.findByTenantIdAndEnterCdAndSymd(tenantId, enterCd, sYmd);
		
		for(WtmBaseWorkMgr l : list) {
			Map<String, Object> time = new HashMap();
			time.put("baseWorkMgrId", l.getBaseWorkMgrId());
			time.put("businessPlaceCd", l.getBusinessPlaceCd());
			time.put("flexibleStdMgrId", l.getFlexibleStdMgrId());
			time.put("symd", l.getSymd());
			time.put("eymd", l.getEymd());
			time.put("note", l.getNote());
			baseList.add(time);
		}
		return baseList;
	}
	
	@Override
	public int setBaseWorkList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmBaseWorkMgr> works = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmBaseWorkMgr work = new WtmBaseWorkMgr();
						work.setEnterCd(enterCd);
						work.setTenantId(tenantId);
						work.setUpdateId(userId);
						work.setBaseWorkMgrId(l.get("baseWorkMgrId").toString().equals("") ? null : Long.parseLong(l.get("baseWorkMgrId").toString()));
						work.setBusinessPlaceCd(l.get("businessPlaceCd").toString());
						work.setEymd(l.get("eymd").toString());
						work.setNote(l.get("note").toString());
						work.setSymd(l.get("symd").toString());
						work.setFlexibleStdMgrId(Long.parseLong(l.get("flexibleStdMgrId").toString()));
						works.add(work);
					}
					works = baseWorkRepository.saveAll(works);
					cnt += works.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmBaseWorkMgr> works = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmBaseWorkMgr work = new WtmBaseWorkMgr();
						work.setBaseWorkMgrId(Long.parseLong(l.get("baseWorkMgrId").toString()));
						works.add(work);
					}
					baseWorkRepository.deleteAll(works);
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