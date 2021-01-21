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

import com.isu.ifw.entity.WtmPushMgr;
import com.isu.ifw.entity.WtmPushSendHis;
import com.isu.ifw.repository.WtmPushMgrRepository;
import com.isu.ifw.vo.WtmMessageVO;

@Service("pushMgrService")
public class WtmPushMgrServiceImpl implements WtmPushMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmPushMgrRepository pushMgrRepository;

	@Override
	public List<Map<String, Object>> getPushMgrList(Long tenantId, String enterCd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		try {
			//List<WtmPushMgr> list = pushMgrRepository.findByTenantIdAndEnterCd(tenantId, enterCd);
			List<WtmPushMgr> list = pushMgrRepository.findByTenantIdAndEnterCdAndPushObjNot(tenantId, enterCd, "COMM");
			
			for(WtmPushMgr l : list) {
				Map<String, Object> code = new HashMap();
				code.put("pushMgrId", l.getPushMgrId());
				code.put("tenantId", l.getTenantId());
				code.put("enterCd", l.getEnterCd());
				code.put("businessPlaceCd", l.getBusinessPlaceCd());
				code.put("symd", l.getSymd());
				code.put("eymd", l.getEymd());
				code.put("pushObj", l.getPushObj());
				code.put("stdMinute", l.getStdMinute());
				code.put("stdMinute2", l.getStdMinute2());
				code.put("stdType", l.getStdType());
				code.put("title", l.getTitle());
				code.put("pushMsg", l.getPushMsg());
				code.put("pushDetail", 0);
				code.put("mobileYn", l.getMobileYn());
				code.put("smsYn", l.getSmsYn());
				code.put("emailYn", l.getEmailYn());
				code.put("note", l.getNote());
				codeList.add(code);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getPushMgrList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return codeList;
	}
	
	@Override
	public List<Map<String, Object>> allPushMgrList(String ymd) {
		List<Map<String, Object>> pushList = new ArrayList();	
		try {
			List<WtmPushMgr> list = pushMgrRepository.findBySymdAndEymd(ymd);
			
			for(WtmPushMgr l : list) {
				Map<String, Object> push = new HashMap();
				push.put("pushMgrId", l.getPushMgrId());
				push.put("tenantId", l.getTenantId());
				push.put("enterCd", l.getEnterCd());
				push.put("businessPlaceCd", l.getBusinessPlaceCd());
				push.put("symd", l.getSymd());
				push.put("eymd", l.getEymd());
				push.put("pushObj", l.getPushObj());
				push.put("stdMinute", l.getStdMinute());
				push.put("stdMinute2", l.getStdMinute2());
				push.put("stdType", l.getStdType());
				push.put("title", l.getTitle());
				push.put("pushMsg", l.getPushMsg());
				push.put("pushDetail", 0);
				push.put("mobileYn", l.getMobileYn());
				push.put("smsYn", l.getSmsYn());
				push.put("emailYn", l.getEmailYn());
				push.put("note", l.getNote());
				pushList.add(push);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return pushList;
	}
	
	@Override
	public int setPushMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmPushMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmPushMgr code = new WtmPushMgr();
						code.setPushMgrId(l.get("pushMgrId").toString().equals("") ? null : Long.parseLong(l.get("pushMgrId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setBusinessPlaceCd(l.get("businessPlaceCd").toString());
						code.setSymd(l.get("symd").toString());
						code.setEymd(l.get("eymd").toString());
						code.setPushObj(l.get("pushObj").toString());
						code.setStdMinute(Integer.parseInt(l.get("stdMinute").toString()));
						if(l.containsKey("stdMinute2") && l.get("stdMinute2") != null) {
							code.setStdMinute2(Integer.parseInt(l.get("stdMinute2").toString()));
						}else {
							code.setStdMinute2(null);
						}
						code.setStdType(l.get("stdType").toString());
						code.setPushMsg(l.get("pushMsg") == null || l.get("pushMsg").equals("") ? l.get("title").toString() :l.get("pushMsg").toString());
						code.setTitle(l.get("title").toString());;
						code.setMobileYn(l.get("mobileYn").toString());
						code.setSmsYn(l.get("smsYn").toString());;
						code.setEmailYn(l.get("emailYn").toString());
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						codes.add(code);
					}
					codes = pushMgrRepository.saveAll(codes);
					cnt += codes.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmPushMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmPushMgr code = new WtmPushMgr();
						code.setPushMgrId(Long.parseLong(l.get("pushMgrId").toString()));
						codes.add(code);
					}
					pushMgrRepository.deleteAll(codes);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setPushMgrList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public void saveMsg(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId) {
		Long pushMgrId = Long.valueOf(paramMap.get("pushMgrId").toString());
		WtmPushMgr pushMgr = pushMgrRepository.findById(pushMgrId).get();
		
		pushMgr.setPushMsg(paramMap.get("pushMsg").toString());
		
		pushMgrRepository.save(pushMgr);
	}
	
}