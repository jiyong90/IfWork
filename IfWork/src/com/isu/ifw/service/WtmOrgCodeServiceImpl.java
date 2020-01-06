package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.entity.CommAuth;
import com.isu.ifw.common.entity.CommAuthRule;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmOrgCode;
import com.isu.ifw.mapper.WtmOrgCodeMapper;
import com.isu.ifw.repository.WtmOrgCodeRepository;
import com.isu.ifw.util.WtmUtil;

@Service("WtmOrgCodeService")
public class WtmOrgCodeServiceImpl implements WtmOrgCodeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmOrgCodeMapper wtmOrgCodeMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmOrgCodeRepository orgCodeRepo;

	@Override
	public List<Map<String, Object>> getOrgCodeList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(!paramMap.containsKey("sYmd")) {
				paramMap.put("sYmd", "");
			} 
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			searchList =  wtmOrgCodeMapper.getOrgCodeList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	@Override
	public List<Map<String, Object>> getOrgComboList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			searchList =  wtmOrgCodeMapper.getOrgComboList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getOrgComboList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	
	@Transactional
	@Override
	public int saveOrgCode(Long tenantId, String enterCd, Map<String, Object> convertMap, String userId) {
		int cnt = 0;
		try {
			
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> mergeList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				
				cnt = mergeList.size();
				if(mergeList != null && cnt > 0) {
					List<WtmOrgCode> orgCodes = new ArrayList<WtmOrgCode>();
					for(Map<String, Object> m : mergeList) {
						WtmOrgCode orgCode = new WtmOrgCode();
						String orgCd = m.get("orgCd").toString();
						String symd = m.get("symd").toString();
						String eymd = m.get("eymd").toString();
						
						List<WtmOrgCode> orgList = orgCodeRepo.findByTenantIdAndEnterCdAndOrgCdAndBetweenSymdAndEymd(tenantId, enterCd, orgCd, symd, eymd);
						if(orgList!=null && orgList.size()>0) {
							for(WtmOrgCode o : orgList) {
								//신청기간내에 시작 종료가 포함되어있을 경우
								if(Integer.parseInt(symd) <= Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) >= Integer.parseInt(o.getEymd())) {
									orgCodeRepo.delete(o);
								//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
								}else if(Integer.parseInt(symd) > Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) < Integer.parseInt(o.getEymd())) {
									String ed = o.getEymd();
									
									o.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(symd, ""), -1),null));
									orgCodeRepo.save(o);
									
									WtmOrgCode newOrgCode = new WtmOrgCode();
									newOrgCode.setTenantId(o.getTenantId());
									newOrgCode.setEnterCd(o.getEnterCd());
									newOrgCode.setOrgCd(o.getOrgCd());
									newOrgCode.setOrgNm(o.getOrgNm());
									newOrgCode.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eymd, ""), 1),null));
									newOrgCode.setEymd(ed);
									newOrgCode.setOrgType(o.getOrgType());
									newOrgCode.setNote(o.getNote());
									newOrgCode.setUpdateId(userId);
									
									orgCodeRepo.save(newOrgCode);

								//시작일만 포함되어있을 경우 
								}else if(Integer.parseInt(symd) >= Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) < Integer.parseInt(o.getEymd())) {
									//시작일을 신청종료일 다음날로 업데이트 해주자
									o.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eymd, ""), 1),null));
									orgCodeRepo.save(o);
								//종료일만 포함되어있을 경우
								}else if(Integer.parseInt(symd) > Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) <= Integer.parseInt(o.getEymd())) {
									//종료일을 신청시작일 전날로 업데이트 해주자
									o.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(symd, ""), -1),null));
									orgCodeRepo.save(o);
									
								}
							}
						}
						
						orgCode.setTenantId(tenantId);
						orgCode.setEnterCd(enterCd);
						orgCode.setOrgCd(orgCd);
						orgCode.setOrgNm(m.get("orgNm").toString());
						orgCode.setSymd(symd);
						orgCode.setEymd(eymd);
						if(m.get("orgType")!=null)
							orgCode.setOrgType(m.get("orgType").toString());
						if(m.get("note")!=null)
							orgCode.setNote(m.get("note").toString());
						orgCode.setUpdateId(userId);
						orgCodes.add(orgCode);
					}
					
					orgCodeRepo.saveAll(orgCodes);
				}
				
				MDC.put("merge cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> orgCodeIds = new ArrayList<Long>();
				if(deleteList != null && deleteList.size() > 0) {
					for(Map<String, Object> d : deleteList) {
						Long orgCodeId = Long.valueOf(d.get("orgCodeId").toString());
						orgCodeIds.add(orgCodeId);
					}
					
					orgCodeRepo.deleteByOrgCodeIdsIn(orgCodeIds);
					
					cnt += orgCodeIds.size();
				}
				
				MDC.put("delete cnt", "" + orgCodeIds.size());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("saveOrgCode Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
}