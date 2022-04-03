package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmTaaListMapper;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.service.WtmInterfaceService;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("taaListService")
public class WtmTaaListServiceImpl implements WtmTaaListService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmTaaListMapper taaListMapper;
	
	@Autowired
	@Qualifier("wtmInterfaceService")
	private WtmInterfaceService wtmInterfaceService;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Override
	public List<Map<String, Object>> getTaaApplDetList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> applList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString().replaceAll("[-.]", "");
				paramMap.put("sYmd", sYmd);
			}
			String eYmd = paramMap.get("eYmd").toString();
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				eYmd = paramMap.get("eYmd").toString().replaceAll("[-.]", "");
				paramMap.put("eYmd", eYmd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
			}
			
			applList = taaListMapper.getTaaApplDetList(paramMap);
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
	public ReturnParam saveWtmTaaSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("정상적으로 저장되었습니다.");
		try {
			 if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				logger.debug("convertMap  "+convertMap.toString());
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> map : iList) {
						logger.debug("map : "+map.toString());
						map.put("tenantId", tenantId);
						map.put("enterCd", enterCd);
						map.put("userId", userId);
						map.put("updtSabun", map.get("sabun").toString());
						map.put("sabun", sabun);
						map.put("taaSdate", map.get("taaSdate").toString());
						map.put("taaEdate", map.get("taaEdate").toString());
						try {
						//WTM_APPL 테이블 결재 삭제
						taaListMapper.delWtmAppl(map);
						//WTM_TAA_APPL 테이블 결재 삭제
						taaListMapper.delWtmTaaAppl(map);
						
						//WTM_TAA_APPL_DET 테이블 결재 삭제
						taaListMapper.delWtmTaaApplDet(map);
						
						//WTM_WORK_CALENDAR 테이블 타각 시간 및 타입 상태값 변경
						taaListMapper.saveWtmWorkCaldar(map);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			 }
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
			
			rp.setFail(e.getMessage());
			return rp;
		} finally {
			MDC.clear();
			logger.debug("getEntryList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
		}
		
		return rp;
	}
	
	
	@Override
	public List<Map<String, Object>> getTaaApplUdtList(Long tenantId, String enterCd, String sabun, 
			Map<String, Object> convertMap) {
		List<Map<String, Object>> applUpdList = null;
		
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				
				Long tid = null;
				String entCd = "";
				String applSabun = "";
				String ifApplNo = "";
				String status = "";
				String paramSymd  = "";
				String paramEymd  = "";
				ObjectMapper mapper = new ObjectMapper();
				
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> map : iList) {
						map.put("tenantId", tenantId);
						map.put("enterCd", enterCd);
						map.put("sabun", sabun);
						
						applUpdList = taaListMapper.getTaaApplUdtList(map);
						
						for(Map<String, Object> w : applUpdList) {
							
							tid = (Long) w.get("TENANT_ID");
							entCd = (String) w.get("ENTER_CD");
							applSabun = (String) w.get("APPL_IN_SABUN");
							ifApplNo = (String) w.get("IF_APPL_NO");
							status = (String) w.get("APPL_STATUS_CD");
							paramSymd = (String) w.get("SYMD");
							paramEymd = (String) w.get("EYMD");
							
							List<WtmWorkCalendar> works = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, sabun, paramSymd, paramEymd);
							
							logger.debug("works ydh : " + mapper.writeValueAsString(works));
							// 근태정보 재생성
							for(WtmWorkCalendar calendar : works) {
								try {
									wtmInterfaceService.resetTaaResultNoFinish(tenantId, enterCd, sabun, calendar.getYmd());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						}
						
					}
					
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getOtList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return applUpdList;
	}
	
}