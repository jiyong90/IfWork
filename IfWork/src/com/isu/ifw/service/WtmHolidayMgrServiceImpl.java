package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmTimeChgHis;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmHolidayMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmTimeChgHisRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.service.WtmTimeCdMgrService;

@Service("holidayMgrService")
public class WtmHolidayMgrServiceImpl implements WtmHolidayMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	private WtmHolidayMapper holidayMapper;

	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmTimeChgHisRepository timeChgHisRepo;
	
	@Autowired
	WtmWorktimeMapper worktimeMapper;

	@Autowired
	WtmTimeCdMgrService timeCdMgrService;
	
	@Autowired 
	private WtmTimeCdMgrRepository timeCdMgrRepo;
	@Autowired 
	private WtmFlexibleEmpRepository flexEmpRepo;
	
	@Autowired
	private WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired 
	private WtmWorkDayResultRepository workDayResultRepo;
	
	@Autowired 
	private WtmFlexibleEmpResetService flexibleEmpResetSerevice;
	
	@Autowired 
	private WtmInterfaceService interfaceService;
	
	@Override
	public List<Map<String, Object>> getHolidayList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> holidayList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			paramMap.put("symd", paramMap.get("symd").toString().replaceAll("[-.]", ""));
			paramMap.put("eymd", paramMap.get("eymd").toString().replaceAll("[-.]", ""));
			
			holidayList = holidayMapper.getHolidayList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getHolidayList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return holidayList;
	}
	
	@Override
	@Transactional
	public int setHolidayList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			Map<String, Object> timeCdMgrId ;
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				// 삭제시 공통휴일로 변경한거는 기본근무로 다시 insert 해야됨; getWorkPlan 쿼리 타서 공통휴일 등록된 애들 불러올 것 
				// 불러온 애들 time_cd_mgr_id, time_type_cd, plan_sdate, plan_edate 기본근무로 변경할 것
				// 인정된 업무 있는지도 확인할 것 
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> t : iList) {
						paramMap.put("holidayYmd", t.get("holidayYmd").toString());
					}
					timeCdMgrId = holidayMapper.getTimeCdMgrId(paramMap); //평일 뽑음 
					
					paramMap.put("deleteList", iList);
					paramMap.put("timeCdMgrId",timeCdMgrId);
					
					logger.debug("paramMap YDH : "+paramMap.toString());
					holidayMapper.deleteHolidays(paramMap); // 휴일관리 테이블에 추가한 휴일 삭제 (기존)
					
					if(timeCdMgrId != null) { // CALENDAR 테이블에 휴일 생성 됐을때는 RESULT는 공통휴일이라 해당일자에 데이터 삭제된 상태임
						holidayMapper.deleteWorkCalendar(paramMap); //CALENDAR 테이블에 휴일여부 N 및 기본근무로 변경 (추가)
						// RESULT 테이블에 기존 근무 있던 사람들은 근무 넣어줘야 함
						// REULST 테이블에 기존 근무 없던 사람들은 근무 타입에 맞게 생성 해야됨 
						
						//holidayMapper.insertWorkResult(paramMap);
						
					}
					
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
				if(iList != null && iList.size() > 0) {
					paramMap.put("insertList", iList);
					holidayMapper.insertHolidays(paramMap);
				}
				
				MDC.put("insert cnt", "" + iList.size());
				cnt += iList.size();
			}
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				if(iList != null && iList.size() > 0) {
					paramMap.put("updateList", iList);
					holidayMapper.updateHolidays(paramMap);
				}
				
				MDC.put("update cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	
	@Override
	public void changeWorktime(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId) {
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		String holYn = "Y";
		List<WtmTimeChgHis> histories = new ArrayList<WtmTimeChgHis>();
		// 업데이트 대상
		List<Map<String, Object>> chgTargetList = getWorkPlan(tenantId, enterCd, paramMap);
		// timeCdMgrId 의 공통휴일 code값만 뽑을 것 
		List<Map<String, Object>> timeCdMgtList = null;
		timeCdMgtList = timeCdMgrService.getTimeCodeList(tenantId, enterCd, holYn);
		Long timeCdMgrId ;
		
		if(timeCdMgtList!=null && timeCdMgtList.size()>0) {
			for(Map<String, Object> tcm : timeCdMgtList) {
				String timeNm = tcm.get("timeNm").toString();
				if(timeNm.contains("공통휴일") || ( tenantId.equals("2") && timeNm.equals("휴일") )) {
					timeCdMgrId = Long.valueOf(tcm.get("timeCdMgrId").toString());
					paramMap.put("timeCdMgrIds",timeCdMgrId);
				}
				
			}
		}
		logger.debug("timeCdMgtList ydh IN : "+ timeCdMgtList);
		
		logger.debug("chgTargetList ydh IN : "+ chgTargetList);
		// 업데이트 대상
		if(chgTargetList!=null && chgTargetList.size()>0) {
			timeCdMgrId = Long.valueOf(paramMap.get("timeCdMgrIds").toString());
			logger.debug("BASE ydh timeCdMgrId  : "+timeCdMgrId);
			
			for(Map<String, Object> t : chgTargetList) {
				String ymd = t.get("ymd").toString();
				String sabun = t.get("sabun").toString();
				
				if("BASE".equals(t.get("timeTypeCd"))) {
					
					WtmTimeChgHis history = new WtmTimeChgHis();
					paramMap.put("sabun", sabun);

					history.setTenantId(tenantId);
					history.setEnterCd(enterCd);
					history.setSabun(sabun);
					history.setYmd(ymd);
					history.setTimeTypeCd(t.get("timeTypeCd").toString());
					history.setTimeCdMgrId(Long.valueOf(t.get("timeCdMgrId").toString()));

					if (t.get("planSdate") != null && !"".equals(t.get("planSdate"))) {
						history.setPlanSdate(WtmUtil.toDate(t.get("planSdate").toString(), "yyyyMMddHHmmss"));
					}
					if (t.get("planEdate") != null && !"".equals(t.get("planEdate"))) {
						history.setPlanEdate(WtmUtil.toDate(t.get("planEdate").toString(), "yyyyMMddHHmmss"));
					}
					if (t.get("planMinute") != null && !"".equals(t.get("planMinute"))) {
						String planMinute = t.get("planMinute").toString();
						String[] hm = planMinute.split(":");

						int hour = 0;
						int min = 0;
						if (hm[0] != null && !"".equals(hm[0])) {
							hour = Integer.valueOf(hm[0]);
							hour *= 60;
						}
						if (hm[1] != null && !"".equals(hm[1])) {
							min = Integer.valueOf(hm[1]);
						}

						history.setPlanMinute(hour + min);
					}

					history.setUpdateId(userId);

					histories.add(history);
				}
					//calendar timeCdMgrId 변경
					logger.debug("BASE ydh ELSE : "+timeCdMgrId);
					WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
				    WtmTimeCdMgr timeCdMgrMap = timeCdMgrRepo.findByTenantIdAndEnterCdAndTimeCdMgrId(tenantId, enterCd, Long.parseLong(timeCdMgrId.toString()));
					calendar.setTimeCdMgrId(timeCdMgrId);
					calendar.setHolidayYn(timeCdMgrMap.getHolYn());
					calendar = workCalendarRepo.save(calendar);
					
					WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(timeCdMgrId).get();
					WtmFlexibleEmp emp = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd());
					WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findById(emp.getFlexibleStdMgrId()).get();
					try {
						List<String> timeTypeCds = new ArrayList<String>();
						timeTypeCds.add(WtmApplService.TIME_TYPE_TAA);
						timeTypeCds.add(WtmApplService.TIME_TYPE_BASE);
						timeTypeCds.add(WtmApplService.TIME_TYPE_REGA);
						List<WtmWorkDayResult> taaResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCds, ymd, ymd);
						workDayResultRepo.deleteAll(taaResults);
						logger.debug("timeTypeCds ydh : "+timeTypeCds);
						logger.debug("taaResults ydh  : "+taaResults);
						logger.debug("timeCdMgr ydh  : "+timeCdMgr);
						logger.debug("flexStdMgr ydh  : "+flexStdMgr);
						logger.debug("calendar ydh  : "+calendar);
						logger.debug("ymd ydh  : "+ymd);
						logger.debug("sabun ydh  : "+sabun);
						
						flexibleEmpResetSerevice.P_WTM_WORK_DAY_RESULT_RESET(calendar, flexStdMgr, timeCdMgr, userId);
						interfaceService.resetTaaResult(tenantId, enterCd, sabun, ymd);
					} catch (Exception e) {
						e.printStackTrace();
					};
					
			}
			timeChgHisRepo.saveAll(histories);
		}
		
	}
	
	@Override
	public List<Map<String, Object>> getWorkPlan(Long tenantId, String enterCd, Map<String, Object> paramMap){
		List<Map<String, Object>> workplan = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			logger.debug("paramMap : " +paramMap);
			
			if(paramMap.containsKey("updateRows") && ((List)paramMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) paramMap.get("updateRows");
				List<String> updateList = new ArrayList<String>();
				String ymd ="";
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> ul : iList) {
						ymd = ul.get("holidayYmd").toString();
						updateList.add(ymd);
					}
					paramMap.put("updateList", updateList);
				}
				workplan = holidayMapper.getWorkPlan(paramMap);
			}
			
			logger.debug("workplan ydh : " +workplan);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorkTimeChangeTarget End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
		}
		
		return workplan;
	}
	
}