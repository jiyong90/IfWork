package com.isu.ifw.service;

import java.text.SimpleDateFormat;
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
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmTimeChgHis;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorkteamEmpMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmBaseWorkMgrRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmTimeChgHisRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.util.WtmUtil;

@Service
public class WtmWorkTimeServiceImpl implements WtmWorktimeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");

	@Autowired
	WtmWorktimeMapper worktimeMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmFlexibleEmpMapper flexibleEmpMapper;
	
	@Autowired
	WtmTimeChgHisRepository timeChgHisRepo;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmWorkPattDetRepository workPattDetRepo;
	
	@Autowired
	WtmWorkteamEmpMapper workteamEmpMapper;
	
	@Autowired
	WtmBaseWorkMgrRepository baseWorkMgrRepo;
	
	@Autowired private WtmFlexibleEmpResetService flexibleEmpResetSerevice;
	@Autowired private WtmWorkDayResultRepository workDayResultRepo;
	@Autowired private WtmFlexibleEmpRepository flexEmpRepo;
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;
	@Autowired private WtmFlexibleStdMgrRepository flexStdMgrRepo;
	@Autowired private WtmInterfaceService interfaceService;
	
	private SimpleDateFormat dateFormat;

	@Override
	public List<Map<String, Object>> getWorktimeCheckList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> WorktimeCheckList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("sYmd")!=null && !"".equals("sYmd")) {
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
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
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
			}
			
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				String eYmd = paramMap.get("eYmd").toString().replaceAll("-", "");
				paramMap.put("eYmd", eYmd);
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
				sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
				paramMap.put("sYmd", sYmd);
			}
			
			if(paramMap.get("eYmd")!=null && !"".equals("eYmd")) {
				String eYmd = paramMap.get("eYmd").toString().replaceAll("-", "");
				paramMap.put("eYmd", eYmd);
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
	
	@Override
	public List<Map<String, Object>> getWorkTimeChangeTarget(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap){
		List<Map<String, Object>> chgTargetList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(!paramMap.containsKey("ymd")) {
				paramMap.put("ymd", "");
			} else {
				ymd = paramMap.get("ymd").toString().replaceAll("-", "");
				paramMap.put("ymd", ymd);
			}
			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, ymd));
			}
			
			chgTargetList = worktimeMapper.getWorkTimeChangeTarget(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorkTimeChangeTarget End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return chgTargetList;
	}
	
	@Override
	public List<Map<String, Object>> getWorkPlan(Long tenantId, String enterCd, Map<String, Object> paramMap){
		List<Map<String, Object>> workplan = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			if(paramMap.containsKey("sabuns") && paramMap.get("sabuns")!=null && !"".equals(paramMap.get("sabuns"))) {
				ObjectMapper mapper = new ObjectMapper();
				List<String> empList = mapper.readValue(paramMap.get("sabuns").toString(), new ArrayList<String>().getClass());
				paramMap.put("empList", empList);
			}
			
			workplan = worktimeMapper.getWorkPlan(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorkTimeChangeTarget End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return workplan;
	}
	
	@Override
	public void changeWorktime(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId) {
		
		List<String> empList = null;
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
				
		//result 조회
		if(paramMap.containsKey("sabuns") && paramMap.get("sabuns")!=null && !"".equals(paramMap.get("sabuns"))) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				empList = mapper.readValue(paramMap.get("sabuns").toString(), new ArrayList<String>().getClass());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			paramMap.put("empList", empList);
		}
		
		List<WtmTimeChgHis> histories = new ArrayList<WtmTimeChgHis>();
		List<Map<String, Object>> chgTargetList = getWorkPlan(tenantId, enterCd, paramMap);
		if(chgTargetList!=null && chgTargetList.size()>0) {
			String ymd = paramMap.get("ymd").toString();
			Long timeCdMgrId = Long.valueOf(paramMap.get("timeCdMgrId").toString());
			
			//base 와 fixot를 다시 생성하기 위한 파라미터
			paramMap.put("sYmd", ymd);
			paramMap.put("eYmd", ymd);
			
			for(Map<String, Object> t : chgTargetList) {

				if("BASE".equals(t.get("timeTypeCd"))) {
					WtmTimeChgHis history = new WtmTimeChgHis();
					String sabun = t.get("sabun").toString();
					paramMap.put("sabun", sabun);

					history.setTenantId(tenantId);
					history.setEnterCd(enterCd);
					history.setSabun(t.get("sabun").toString());
					history.setYmd(ymd);
					history.setTimeTypeCd(t.get("timeTypeCd").toString());
					history.setTimeCdMgrId(Long.valueOf(t.get("timeCdMgrId").toString()));

					if(t.get("planSdate")!=null && !"".equals(t.get("planSdate"))) {
						history.setPlanSdate(WtmUtil.toDate(t.get("planSdate").toString(), "yyyyMMddHHmmss"));
					}
					if(t.get("planEdate")!=null && !"".equals(t.get("planEdate"))) {
						history.setPlanEdate(WtmUtil.toDate(t.get("planEdate").toString(), "yyyyMMddHHmmss"));
					}
					if(t.get("planMinute")!=null && !"".equals(t.get("planMinute"))) {
						String planMinute = t.get("planMinute").toString();
						String[] hm = planMinute.split(":");

						int hour = 0;
						int min = 0;
						if(hm[0]!=null && !"".equals(hm[0])) {
							hour = Integer.valueOf(hm[0]);
							hour *= 60;
						}
						if(hm[1]!=null && !"".equals(hm[1])) {
							min = Integer.valueOf(hm[1]);
						}

						history.setPlanMinute(hour+min);
					}

					history.setUpdateId(userId);

					histories.add(history);

					//calendar timeCdMgrId 변경
					WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
					calendar.setTimeCdMgrId(timeCdMgrId);
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
						//workDayResultRepo.flush();
						
						flexibleEmpResetSerevice.P_WTM_WORK_DAY_RESULT_RESET(calendar, flexStdMgr, timeCdMgr, userId);
						interfaceService.resetTaaResult(tenantId, enterCd, sabun, ymd);
					} catch (Exception e) {
						e.printStackTrace();
					};
					
					/*
					Map<String, Object> flexibleEmp = flexibleEmpMapper.getFlexibleEmp(paramMap);

					// 공휴일 제외 여부
					String holExceptYn = "N";
					if(flexibleEmp.get("holExceptYn")!=null && !"".equals(flexibleEmp.get("holExceptYn")))
						holExceptYn = flexibleEmp.get("holExceptYn").toString();

					// 근무제 패턴으로 정해놓은 일 수
					int maxPattDet = 0;
					WtmWorkPattDet workPattDet = workPattDetRepo.findTopByFlexibleStdMgrIdOrderBySeqDesc(Long.valueOf(flexibleEmp.get("flexibleStdMgrId").toString()));
					if(workPattDet!=null && workPattDet.getSeq()!=null)
						maxPattDet = workPattDet.getSeq();

					//calendar 기반으로 result reset
					Long pKey = null;
					String pType = null;


					//근무조인지
					Map<String, Object> workteam = workteamEmpMapper.getWorkteamEmp(paramMap);
					if(workteam!=null && workteam.containsKey("workteamMgrId") && workteam.get("workteamMgrId")!=null) {
						pType = "WORKTEAM";
						pKey = Long.valueOf(workteam.get("workteamMgrId").toString());
					} else {
						pType = "BASE";
						WtmBaseWorkMgr baseWorkMgr = baseWorkMgrRepo.findByTenantIdAndEnterCdAndFlexibleStdMgrIdAndYmd(tenantId, enterCd, Long.valueOf(flexibleEmp.get("flexibleStdMgrId").toString()), ymd);
						if(baseWorkMgr!=null && baseWorkMgr.getBaseWorkMgrId()!=null)
							pKey = baseWorkMgr.getBaseWorkMgrId();
					}

					paramMap.put("pKey", pKey); //workteam이면 workTeamMgrId, base이면 baseMgrId
					paramMap.put("pType", pType); //workteam or base
					paramMap.put("holExceptYn", holExceptYn);
					paramMap.put("maxPattSeq", maxPattDet);
					paramMap.put("flexibleEmpId", Long.valueOf(flexibleEmp.get("flexibleEmpId").toString()));
					flexibleEmpMapper.resetWorkDayResult(paramMap);


					
					//그린캐미칼(TENANT_ID = 102) 근무시간 변경시 변경전 OT 근무가 있을경우 변경 후에도 OT 근무를 생성해 준다.
					if(tenantId == 102 || tenantId == 4) {
						if(t.get("planMinute")!=null && !"".equals(t.get("planMinute"))) {

						}
					}

					//calc 인정근무시간
					if(workCalendar.getEntrySdate()!=null || workCalendar.getEntryEdate()!=null) {
						//appr null로
						paramMap.put("sabun", sabun);
						paramMap.put("stdYmd", ymd);
						flexibleEmpMapper.updateResultAppr(paramMap);
						empService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
					}
					*/
				}

			}
			timeChgHisRepo.saveAll(histories);
		}
		
		
	}

	@Override
	public List<Map<String, Object>> getWorkTimeList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> workTimeList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);

			String searchType = "";
			if((String)paramMap.get("searchType") != null && "person".equals((String)paramMap.get("searchType"))) {
				paramMap.put("searchKeyword", sabun);
			}


			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			String now = sdf.format(date);

			String ymd = "";
			if(paramMap.get("ymd") != null && !"".equals((String)paramMap.get("ymd"))) {
				ymd = ((String) paramMap.get("ymd")).replace("-", "");
				paramMap.put("ymd", ymd);
				paramMap.put("sYmd", ymd+"01");
				paramMap.put("eYmd", ymd+"31");
			}

			
			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, now));
			}

			//workTimeList = worktimeMapper.getWorkTimeList(paramMap);
			workTimeList = flexibleEmpMapper.getWorktermByYmd(paramMap); 
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("workTimeList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}

		return workTimeList;
	}

	@Override
	public List<Map<String, Object>> getCloseDayList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> closeDayList = null;
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);

			String searchType = "";
			if((String)paramMap.get("searchType") != null && "person".equals((String)paramMap.get("searchType"))) {
				paramMap.put("searchKeyword", sabun);
			}

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			String now = sdf.format(date);
			String ymd = "";
			if(paramMap.get("ymd") != null && !"".equals((String)paramMap.get("ymd"))) {
				ymd = ((String) paramMap.get("ymd")).replace("-", "");
				paramMap.put("ymd", ((String) paramMap.get("ymd")).replace("-", ""));
			}

			List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
			if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
				//하위 조직 조회
				paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, now));
			}

			closeDayList = worktimeMapper.getCloseDayList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("workTimeList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}

		return closeDayList;
	}
}