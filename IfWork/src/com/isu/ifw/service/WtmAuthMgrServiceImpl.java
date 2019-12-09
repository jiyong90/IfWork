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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.CommAuth;
import com.isu.ifw.entity.CommAuthRule;
import com.isu.ifw.mapper.WtmAuthMgrMapper;
import com.isu.ifw.repository.WtmCommAuthRepository;
import com.isu.ifw.repository.WtmCommAuthRuleRepository;
import com.isu.ifw.repository.WtmUserAuthRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.option.service.TenantConfigManagerService;

@Service
public class WtmAuthMgrServiceImpl implements WtmAuthMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmCommAuthRepository commAuthRepo;
	
	@Autowired
	WtmCommAuthRuleRepository commAuthRuleRepo;
	
	@Autowired
	WtmAuthMgrMapper authMgrMapper;
	
	@Autowired
	WtmUserAuthRepository userAuthRepo;
	
	@Autowired
	private TenantConfigManagerService tcms;
	
	
	@Override
	public List<Map<String, Object>> getAuthList(Long tenantId) {
		List<Map<String, Object>> authList = new ArrayList();	
		List<CommAuth> auths = commAuthRepo.findByTenantId(tenantId);
		
		if(auths!=null && auths.size()>0) {
			for(CommAuth auth : auths) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("authId", auth.getAuthId());
				m.put("authNm", auth.getAuthNm());
				m.put("note", auth.getNote());
				m.put("tenantId", auth.getTenantId());
				m.put("ruleText", "");
				CommAuthRule ruleText = commAuthRuleRepo.findByAuthId(auth.getAuthId());
				if(ruleText!=null && ruleText.getRuleText()!=null) {
					m.put("ruleText", ruleText.getRuleText());
				}
			
				
				authList.add(m);
			}
		}
		
		return authList;
	}
	
	@Override
	public int saveAuthList(Long tenantId, String enterCd, Map<String, Object> convertMap, String userId) {
		int cnt = 0;
		try {
			
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> mergeList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				
				cnt = mergeList.size();
				if(mergeList != null && cnt > 0) {
					for(Map<String, Object> m : mergeList) {
						
						CommAuth auth = new CommAuth();
						if(m.get("authId")!=null && !"".equals(m.get("authId"))) {
							Long authId = Long.valueOf(m.get("authId").toString());
							CommAuth commAuth = commAuthRepo.findById(authId).get();
							
							if(commAuth != null)
								auth = commAuth;
						} 
						
						auth.setTenantId(tenantId);
						auth.setAuthNm(m.get("authNm").toString());
						auth.setNote(m.get("note").toString());
						
						CommAuth result = commAuthRepo.save(auth);
						
						if(m.get("ruleText")!=null && !"".equals(m.get("ruleText"))) {
							ObjectMapper mapper = new ObjectMapper();
							List<String> ruleText = mapper.readValue(m.get("ruleText").toString(), new ArrayList<String>().getClass());
							CommAuthRule rule = commAuthRuleRepo.findByAuthId(result.getAuthId());
							if(rule==null){
								rule = new CommAuthRule();
								rule.setAuthId(result.getAuthId());
							}
							rule.setCompanyCd(enterCd);
							
							rule.setRuleText(mapper.writeValueAsString(ruleText));
							commAuthRuleRepo.save(rule);
						}
						
					}
				}
				
				MDC.put("merge cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> authIds = new ArrayList<Long>();
				List<CommAuthRule> authRules = new ArrayList<CommAuthRule>();
				if(deleteList != null && deleteList.size() > 0) {
					for(Map<String, Object> d : deleteList) {
						Long authId = Long.parseLong(d.get("authId").toString());
						
						CommAuthRule rule = commAuthRuleRepo.findByAuthId(authId);
						if(rule!=null)
							authRules.add(rule);
						
						authIds.add(authId);
					}
					
					if(authRules!=null && authRules.size()>0)
						commAuthRuleRepo.deleteAll(authRules);
					commAuthRepo.deleteByAuthIdsIn(authIds);
					
					cnt += authIds.size();
				}
				
				MDC.put("delete cnt", "" + authIds.size());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("saveAuthList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getAuthUserList(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("ymd", WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
		
		return authMgrMapper.getAuthUserList(paramMap);	
	}
	
	@Override
	public int saveAuthUserList(Long tenantId, String enterCd, Map<String, Object> convertMap, String userId) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> mergeList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				
				cnt = mergeList.size();
				List<Long> userAuthIds = new ArrayList<Long>();
				if(mergeList != null && cnt > 0) {
					for(Map<String, Object> m : mergeList) {
						if(m.get("userAuthId")!=null && !"".equals(m.get("userAuthId"))) {
							Long userAuthId = Long.valueOf(m.get("userAuthId").toString());
							userAuthIds.add(userAuthId);
						} 
					}
					
					//권한 삭제 후 다시 넣기
					if(userAuthIds!=null && userAuthIds.size()>0)
						userAuthRepo.deleteByUserAuthIdsIn(userAuthIds);
					
					String encKey = tcms.getConfigValue(tenantId, "SECURITY.AES.KEY", true, "");
					
					convertMap.put("tenantId", tenantId);
					convertMap.put("enterCd", enterCd);
					convertMap.put("encKey", encKey);
					authMgrMapper.saveAuthUser(convertMap);
				}
				
				MDC.put("merge cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> userAuthIds = new ArrayList<Long>();
				if(deleteList != null && deleteList.size() > 0) {
					for(Map<String, Object> d : deleteList) {
						Long userAuthId = Long.parseLong(d.get("userAuthId").toString());
						
						userAuthIds.add(userAuthId);
					}
					
					userAuthRepo.deleteByUserAuthIdsIn(userAuthIds);
					
					cnt += userAuthIds.size();
				}
				
				MDC.put("delete cnt", "" + userAuthIds.size());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("saveAuthUserList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
}