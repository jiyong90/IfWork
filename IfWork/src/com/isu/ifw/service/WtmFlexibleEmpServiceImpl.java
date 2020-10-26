package com.isu.ifw.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.service.TenantConfigManagerService;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmFlexibleAppl;
import com.isu.ifw.entity.WtmFlexibleApplDet;
import com.isu.ifw.entity.WtmFlexibleApplyDet;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmOrgConc;
import com.isu.ifw.entity.WtmOtAppl;
import com.isu.ifw.entity.WtmOtSubsAppl;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkDayResultO;
import com.isu.ifw.entity.WtmWorkPattDet;
import com.isu.ifw.mapper.WtmAuthMgrMapper;
import com.isu.ifw.mapper.WtmEmpHisMapper;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplyMgrMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmOrgChartMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmScheduleMapper;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleApplDetRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmFlexibleApplyDetRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmOrgConcRepository;
import com.isu.ifw.repository.WtmOtApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultORepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.repository.WtmWorkteamMgrRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmDayPlanVO;
import com.isu.ifw.vo.WtmDayWorkVO;

@Service("flexibleEmpService")
public class WtmFlexibleEmpServiceImpl implements WtmFlexibleEmpService {

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	@Qualifier("WtmTenantConfigManagerService")
	private TenantConfigManagerService tcms;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmFlexibleStdMapper flexStdMapper;
	
	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmWorkDayResultRepository workDayResultRepo;

	@Autowired private WtmWorkDayResultORepository workDayResultORepo;
	
	@Autowired
	WtmOtApplRepository otApplRepo;
	
	@Autowired
	WtmFlexibleApplRepository flexApplRepo;
	
	@Autowired
	WtmFlexibleApplDetRepository flexApplDetRepo;
	
	@Autowired
	WtmTaaCodeRepository taaCodeRepo;
	
	@Autowired
	WtmFlexibleApplMapper flexApplMapper;
	
	@Autowired
	WtmTimeCdMgrRepository wtmTimeCdMgrRepo;
	
	@Autowired
	WtmApplRepository applRepo;
	
	@Autowired
	@Qualifier("wtmOtApplService")
	WtmApplService applService;
	
	@Autowired
	WtmAuthMgrMapper authMgrMapper;
	
	@Autowired
	WtmOrgChartMapper wtmOrgChartMapper;
	
	@Autowired
	WtmEmpHisRepository empHisRepo;
	
	
	@Autowired
	WtmOrgConcRepository orgConcRepo;
	
	@Autowired
	WtmFlexibleApplyDetRepository flexibleApplyDetRepo;
	
	@Autowired
	WtmOtApplMapper otApplMapper;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
	
	@Autowired
	WtmAsyncService asyncService;
	
	@Autowired
	WtmFlexibleApplyMgrMapper wtmFlexibleApplyMgrMapper;

	@Autowired
	private WtmFlexibleEmpService empService;
	
	@Autowired
	WtmEmpHisMapper empHisMapper;

	@Autowired
	WtmCalcService calcService;

	//@Autowired private PlatformTransactionManager transactionManager;

	@Autowired
	WtmScheduleMapper wtmScheduleMapper;
	
	@Autowired private WtmTaaCodeRepository wtmTaaCodeRepository;

	
	@Autowired private WtmFlexibleEmpResetService flexibleEmpResetSerevice;
	
	@Autowired private WtmWorkPattDetRepository workPattDetRepo;
	
	@Override
	public List<Map<String, Object>> getFlexibleEmpList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub 
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		List<Map<String, Object>> flexibleList = flexEmpMapper.getFlexibleEmpList(paramMap);
		if(flexibleList!=null && flexibleList.size()>0) {
			for(Map<String, Object> flex : flexibleList) {
				if(flex.containsKey("flexibleEmpId") && flex.get("flexibleEmpId")!=null && !"".equals(flex.get("flexibleEmpId"))) {
					paramMap.put("flexibleEmpId", Long.valueOf(flex.get("flexibleEmpId").toString()));
					List<Map<String, Object>> plans = flexEmpMapper.getWorktimePlanByYmdBetween(paramMap);
					flex.put("flexibleEmp", getDayWorks(plans, userId));
				}
			}
		}
		
		return flexibleList;
	}
	
	public Map<String, Object> getFlexibleEmp(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		return flexEmpMapper.getFlexibleEmp(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getFlexibleEmpListForPlan(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		//paramMap.put("ymd", WtmUtil.parseDateStr(new Date(), null));
		paramMap.put("ymd", paramMap.get("ymd").toString());
		
		List<Map<String, Object>> flexibleEmpList = flexEmpMapper.getFlexibleEmpListForPlan(paramMap);
		if(flexibleEmpList!=null && flexibleEmpList.size()>0) {
			for(Map<String, Object> flexibleEmp : flexibleEmpList) {
				if(flexibleEmp.get("flexibleEmpId")!=null && !"".equals(flexibleEmp.get("flexibleEmpId"))) {
					//List<Map<String, Object>> plans = flexEmpMapper.getWorktimePlan(Long.valueOf(flexibleEmp.get("flexibleEmpId").toString()));
					//List<WtmDayWorkVO> dayWorks = getDayWorks(plans, userId);
					//flexibleEmp.put("dayWorks", dayWorks);
					
					List<Map<String, Object>> plans = flexEmpMapper.getWorktimePlanByYmdBetween(paramMap);
					List<WtmDayWorkVO> dayWorks = getDayWorks(plans, userId);
					flexibleEmp.put("dayWorks", dayWorks);
				}
			}
		}
		
		return flexibleEmpList;
	}
	
	@Override
	public Map<String, Object> getDayWorkHm(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		
		Map<String, Object> result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> sabuns = null;
		if(paramMap.containsKey("sabuns") && paramMap.get("sabuns")!=null && !"".equals(paramMap.get("sabuns"))) {
			try {
				sabuns = mapper.readValue(paramMap.get("sabuns").toString(), new ArrayList<String>().getClass());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			sabuns = new ArrayList<String>();
			sabuns.add(sabun);
		}
		
		try {
			System.out.println("sabuns : " + mapper.writeValueAsString(sabuns));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(sabuns.size()>0) {
			Long timeCdMgrId = null;
			int i =0;
			for(String s : sabuns) {
				WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, s, paramMap.get("ymd").toString());
				
				if(calendar!=null && calendar.getTimeCdMgrId()!=null) {
					if(i==0) {
						timeCdMgrId = Long.valueOf(calendar.getTimeCdMgrId());
						paramMap.put("tenantId", tenantId);
						paramMap.put("enterCd", enterCd);
						paramMap.put("sabun", s);
					}
					
					System.out.println("calendar.getTimeCdMgrId() : " + calendar.getTimeCdMgrId());
					
					if(timeCdMgrId.compareTo(Long.valueOf(calendar.getTimeCdMgrId())) != 0) {
						timeCdMgrId = null;
						result = new HashMap<String, Object>();
						result.put("message", "대상자의 근무시간표가 다릅니다.");
					}
				}
				
				i++;
			}
			
			
			System.out.println("timeCdMgrId : " + timeCdMgrId);
			
			
			if(timeCdMgrId!=null) {
				result = calcMinuteExceptBreaktime(timeCdMgrId, paramMap, userId);
				
				String breakTypeCd = null;
				WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
				if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
					breakTypeCd = timeCdMgr.getBreakTypeCd();
				
				if("TIME".equals(breakTypeCd)) {
					int calcMinute = 0;
					int breakMinute = 0;
					
					if(result.get("calcMinute")!=null && !"".equals(result.get("calcMinute")))
						calcMinute = Integer.parseInt(result.get("calcMinute")+"");
					if(result.get("breakMinute")!=null && !"".equals(result.get("breakMinute")))
						breakMinute = Integer.parseInt(result.get("breakMinute")+"");
					
					if(calcMinute!=0)
						result.put("calcMinute", (calcMinute - breakMinute));
				}
			}
			
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> getWorkDayResult(Long tenantId, String enterCd, String sabun, String ymd, String userId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		//출퇴근 타각 정보
		WtmWorkCalendar workCalendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
		/*Map<String, Object> entry = new HashMap<String, Object>();
		if(workCalendar!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(workCalendar.getEntrySdate()!=null && !"".equals(workCalendar.getEntrySdate()))
				entry.put("entrySdate", sdf.format(workCalendar.getEntrySdate()));
			if(workCalendar.getEntryStypeCd()!=null && !"".equals(workCalendar.getEntryStypeCd()))
				entry.put("entryStypeCd", workCalendar.getEntryStypeCd());
			if(workCalendar.getEntryEdate()!=null && !"".equals(workCalendar.getEntryEdate()))
				entry.put("entryEdate", sdf.format(workCalendar.getEntryEdate()));
			if(workCalendar.getEntryStypeCd()!=null && !"".equals(workCalendar.getEntryStypeCd()))
				entry.put("entryStypeCd", workCalendar.getEntryStypeCd());
			if(workCalendar.getEntryEtypeCd()!=null && !"".equals(workCalendar.getEntryEtypeCd()))
				entry.put("entryEtypeCd", workCalendar.getEntryEtypeCd());
			
		}
		result.put("entry", entry);*/
		
		String holidayYn = "";
		if(workCalendar!=null && workCalendar.getHolidayYn()!=null)
			holidayYn = workCalendar.getHolidayYn();
		
		result.put("holidayYn", holidayYn);
		result.put("entry", workCalendar);
		
		//근태, 근무 정보
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		
		List<Map<String, Object>> workDayResult = flexEmpMapper.getWorkDayResult(paramMap);
		
		/*Map<String, Object> dayResults = new HashMap<String, Object>();
		if(result!=null && result.size()>0) {
			for(Map<String, Object> r : workDayResult) {
				List<Map<String, Object>> dayResult = null;
				String date = r.get("ymd").toString();
				if(dayResults.containsKey(date)) {
					dayResult = (List<Map<String, Object>>)dayResults.get(date);
				} else {
					dayResult = new ArrayList<Map<String, Object>>();
				}
				
				dayResult.add(r);
				dayResults.put(ymd, dayResult);
			}
		}*/
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			result.put("dayResults", mapper.writeValueAsString(workDayResult));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> getFlexibleRangeInfo(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		return flexEmpMapper.getFlexibleRangeInfo(paramMap);
	}
	
	@Override
	public Map<String, Object> getFlexibleDayInfo(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		String ymd = paramMap.get("ymd").toString();
		
		Map<String, Object> dayInfo = flexEmpMapper.getFlexibleDayInfo(paramMap);
		
		//근무제의 근태가산여부, 근태일 근무여부 조회
		Map<String, Object> flexEmp = flexEmpMapper.getFlexibleEmp(paramMap);
		String taaTimeYn = null;
		String taaWorkYn = null;
		if(flexEmp!=null) {
			if(flexEmp.get("taaTimeYn")!=null)
				taaTimeYn = flexEmp.get("taaTimeYn").toString();
			if(flexEmp.get("taaWorkYn")!=null)
				taaWorkYn = flexEmp.get("taaWorkYn").toString();
		}
		
		//휴게시간 기준
		Long timeCdMgrId = null;
		String breakTypeCd = null;
		if(dayInfo!=null) {
			if(dayInfo.get("timeCdMgrId")!=null && !"".equals(dayInfo.get("timeCdMgrId")))
				timeCdMgrId = Long.valueOf(dayInfo.get("timeCdMgrId").toString());
			if(dayInfo.get("breakTypeCd")!=null)
				breakTypeCd = dayInfo.get("breakTypeCd").toString();
		}
		
		//심야근무 시간
		Date nightSdate = WtmUtil.toDate( ymd + "220000" , "yyyyMMddHHmmss");
		Date nightEdate = WtmUtil.addDate(WtmUtil.toDate( ymd + "060000" , "yyyyMMddHHmmss"), 1);
		
		if(timeCdMgrId!=null) {
			paramMap.put("timeCdMgrId", timeCdMgrId);
			List<Map<String, Object>> dayResult = flexEmpMapper.getDayResultByYmd(paramMap);
			if(dayResult!=null && dayResult.size()>0) {
				Float workMin = 0f;
				Map<String, Object> taaMap = new HashMap<String, Object>();
				Float noPayBreakMin = 0f;
				Float paidBreakMin = 0f;
				Float otMin = 0f;
				Float otNightMin = 0f;
				Float regaMin = 0f;
				
				for(Map<String, Object> r : dayResult) {
					String timeTypeCd = r.get("timeTypeCd").toString();
					String taaCd = null;
					String taaNm = null;
					
					if(r.get("taaCd")!=null)
						taaCd = r.get("taaCd").toString();
					
					if(r.get("taaNm")!=null)
						taaNm = r.get("taaNm").toString().replaceAll("\\p{Z}", "");
					
					System.out.println("timeTypeCd : " + timeTypeCd);
					System.out.println("breakTypeCd : " + breakTypeCd);
					//System.out.println("taaNm : " + taaNm);
					
					String sDate = null;
					String eDate = null;
					Float min = 0f;
					
					if(r.get("apprSdate")!=null && !"".equals(r.get("apprSdate"))) {
						sDate = r.get("apprSdate").toString();
					} else {
						if(r.get("planSdate")!=null && !"".equals(r.get("planSdate"))) {
							sDate = r.get("planSdate").toString();
						}
					}
					
					if(r.get("apprEdate")!=null && !"".equals(r.get("apprEdate"))) {
						eDate = r.get("apprEdate").toString();
					} else {
						if(r.get("planEdate")!=null && !"".equals(r.get("planEdate"))) {
							eDate = r.get("planEdate").toString();
						}
					}
					
					if(r.get("apprSdate")!=null && !"".equals(r.get("apprSdate"))
							&& r.get("apprEdate")!=null && !"".equals(r.get("apprEdate")) 
							&& r.get("apprMinute")!=null && !"".equals(r.get("apprMinute"))) {
						min = Float.valueOf(r.get("apprMinute").toString());
					} else {
						if(r.get("planMinute")!=null && !"".equals(r.get("planMinute"))) {
							min = Float.valueOf(r.get("planMinute").toString());
						}
					}
					
					//if(sDate==null || eDate==null)
					//	continue;
					
					//휴게시간
					//MGR일 때는 그대로
					//breakTypeCd가 TIME이나 TIMEFIX 인 경우엔 유급 휴게는 0, Except 만 합치면 됨
					Float break01 = 0f;
					Float break02 = 0f;
					
					if(sDate!=null && eDate!=null) {
						Date sd = WtmUtil.toDate(sDate, "yyyyMMddHHmm");
						Date ed = WtmUtil.toDate(eDate, "yyyyMMddHHmm");
						
						SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
						paramMap.put("shm", sdf.format(sd));
						paramMap.put("ehm", sdf.format(ed));
						
						if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
							Map<String, Object> breakMap = calcMinuteExceptBreaktime(timeCdMgrId, paramMap, sabun);
							if(breakMap!=null && breakMap.get("breakMinute")!=null) {
								break01 =  Float.valueOf(breakMap.get("breakMinuteNoPay").toString());
								break02 = Float.valueOf(breakMap.get("breakMinutePaid").toString());
							}
							
							noPayBreakMin += break01;
							paidBreakMin += break02;
							
							System.out.println("break01: " + break01);
							System.out.println("break02: " + break02);
						} 
					}
					
					if((breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME) || breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX))
							&& timeTypeCd.equals(WtmApplService.TIME_TYPE_EXCEPT)) {
						noPayBreakMin += min;
						
						if(taaCd.equals("BREAK"))
							workMin -= min;
						else if(taaCd.equals("BREAK_OT"))
							otMin -= min;
						else if(taaCd.equals("BREAK_NIGHT"))
							otNightMin -= min;
					}
					
					System.out.println("noPayBreakMin: " + noPayBreakMin);
					System.out.println("paidBreakMin: " + paidBreakMin);
					
					if(timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE)) {
						workMin += min;
					} else if(timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) || timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_OT)  || timeTypeCd.equals(WtmApplService.TIME_TYPE_FIXOT)) {
						otMin += min;
					} else if(timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT) || timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)) {
						otNightMin += min;
					} else if(timeTypeCd.equals(WtmApplService.TIME_TYPE_TAA) || timeTypeCd.equals(WtmApplService.TIME_TYPE_SUBS) || timeTypeCd.equals(WtmApplService.TIME_TYPE_LLA)) {
						//근태 현황
						//근태시간 포함여부와 근태일근무여부가 모두 Y이면, 휴게시간 포함한 근무시간
						Float taa = min;
						
						//System.out.println("taa : " + taa);
						
						if(timeTypeCd.equals(WtmApplService.TIME_TYPE_SUBS))
							taaNm = "대체휴가";
						
						if(timeTypeCd.equals(WtmApplService.TIME_TYPE_LLA)) { //결근,지각,조퇴
							taaMap.put(taaNm, "");
						} else {
							if(taaMap.get(taaNm)!=null && !"".equals(taaMap.get(taaNm))) {
								taaMap.put(taaNm, Float.parseFloat(taaMap.get(taaNm).toString()) + taa);
							} else {
								taaMap.put(taaNm, taa);
							}
						}
						
						
					} else if(timeTypeCd.equals(WtmApplService.TIME_TYPE_REGA)) {
						regaMin += min;
					}
				}
				
				dayInfo.put("workHour", minToHHmmStr(workMin+""));
				dayInfo.put("workMinute", workMin);
				dayInfo.put("otHour", minToHHmmStr((otMin+otNightMin)+""));
				dayInfo.put("otMinute", (otMin+otNightMin));
				dayInfo.put("otBasicHour", minToHHmmStr(otMin+""));
				dayInfo.put("otBasicMinute", otMin);
				dayInfo.put("otNightHour", minToHHmmStr(otNightMin+""));
				dayInfo.put("otNightMinute", otNightMin);
				dayInfo.put("regaHour", minToHHmmStr(regaMin+""));
				dayInfo.put("regaMinute", regaMin);
				
				Map<String, Object> taa = new HashMap<String, Object>();
				if(taaMap!=null) {
					for(String k : taaMap.keySet()) {
						taa.put(k, minToHHmmStr(taaMap.get(k).toString()));
					}
				}
				
				dayInfo.put("taa", taa);
				dayInfo.put("breakHour", minToHHmmStr(noPayBreakMin+""));
				dayInfo.put("paidHour", minToHHmmStr(paidBreakMin+""));
				
				dayInfo.put("breakMinute", noPayBreakMin);
				dayInfo.put("paidMinute", paidBreakMin);
				
			}
		}

		String totalCnt = "0" , usedCnt = "0", noUsedCnt = "0";
		if(tenantId == 95) {
			Map<String, Object> annudalInfo = flexEmpMapper.getAnnualUsed(paramMap);
			
			
			if(annudalInfo != null){
				totalCnt = annudalInfo.get("totalCnt") != null ? annudalInfo.get("totalCnt").toString() : "0";
				usedCnt = annudalInfo.get("usedCnt") != null ? annudalInfo.get("usedCnt").toString() : "0";
				noUsedCnt = annudalInfo.get("noUsedCnt") != null ? annudalInfo.get("noUsedCnt").toString() : "0";
			}
			
		}
		//  연차현황 조회

		dayInfo.put("annualTotalCnt", totalCnt);
		dayInfo.put("annualUsedCnt", usedCnt);
		dayInfo.put("annualNoUsedCnt", noUsedCnt);

		return dayInfo;
	}
	
	protected String minToHHmmStr(String min) {
		if(min==null || "".equals(min) || Double.parseDouble(min)==0)
			return "";
	
		Double H = 0d;
		Double i = 0d;
		
		H = Double.parseDouble(min)/60;
		H = Math.ceil(H*100)/100.0;
		i = (H - H.intValue()) * 60;
		
		return ((H.intValue()>0)?String.format("%02d", H.intValue()):"00")+":"+((i.intValue()>0)?String.format("%02d", i.intValue()):"00");
	}
	
	@Override
	public Map<String, Object> getFlexibleWorkTimeInfo(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		paramMap.put("taaTimeYn", "");
		paramMap.put("taaWorkYn", "");
		
		Map<String, Object> flexEmp = flexEmpMapper.getFlexibleEmp(paramMap);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("unplannedYn", "N");
		
		if(flexEmp!=null) {
			if(flexEmp.get("taaTimeYn")!=null) //근태시간포함여부
				paramMap.put("taaTimeYn", flexEmp.get("taaTimeYn").toString());
			if(flexEmp.get("taaWorkYn")!=null) //근태일 근무여부
				paramMap.put("taaWorkYn", flexEmp.get("taaWorkYn").toString());
			if(flexEmp.get("unplannedYn")!=null) //근무계획 없이 타각 여부 수정
				result.put("unplannedYn", flexEmp.get("unplannedYn").toString());
		}
		
		Map<String, Object> worktimeInfo = flexEmpMapper.getFlexibleWorkTimeInfo(paramMap);
		if(worktimeInfo!=null)
			result.putAll(worktimeInfo);
		
		return result; 
	}
	
	@Override
	public Map<String, Object> getPrevFlexible(Long tenantId, String enterCd, String empNo) {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("empNo", empNo);
		//paramMap.put("today", WtmUtil.parseDateStr(new Date(), null));
		
		return flexEmpMapper.getPrevFlexible(paramMap);
	}
	
	public void imsi(Long tenantId, String enterCd, String sabun, Long flexibleApplId, Map<String, Object> dateMap, String userId) throws Exception{
		
		flexEmpMapper.createFlexibleApplDet(flexibleApplId, userId);

		if(dateMap != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			for(String k : dateMap.keySet()) {
				WtmFlexibleApplDet result =  flexApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, k);
				try {
					Map<String, String> dayResult = (Map<String, String>) dateMap.get(k);
					if(dayResult.get("shm") != null && !dayResult.get("shm").equals("")) {
						String shm = dayResult.get("shm");
						String ehm = dayResult.get("ehm");
						Date s = sdf.parse(k+shm);
						Date e = sdf.parse(k+ehm);
						
						if(s.compareTo(e) > 0) {
							// 날짜 더하기
					        Calendar cal = Calendar.getInstance();
					        cal.setTime(e);
					        cal.add(Calendar.DATE, 1);
					        e = cal.getTime();
						}
						result.setPlanSdate(s);
						result.setPlanEdate(e);
						Map<String, Object> paramMap = new HashMap<>();
						paramMap.put("shm", shm);
						paramMap.put("ehm", ehm);
						paramMap.put("tenantId", tenantId);
						paramMap.put("enterCd", enterCd);
						paramMap.put("sabun", sabun);
						paramMap.put("ymd", result.getYmd());
						
						Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, userId);
						result.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					}else {
						result.setPlanSdate(null);
						result.setPlanEdate(null);
						result.setPlanMinute(0);
					}
					flexApplDetRepo.save(result);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//특정한 주의 근로시간은 52시간을, 특정일의 근로시간은 12시간을 초과할 수 없음(연장 . 휴일근로시간 제외)
		}
		
	}
	
	@Override
	public ReturnParam mergeWorkDayResult(Long tenantId, String enterCd, String ymd, String sabun, Long applId, String timeTypeCd, String taaCd, Date planSdate, Date planEdate, String defaultWorkUseYn , String fixotUseType, Integer fixotUseLimit,  String userId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		 
		Map<String, Object> pMap = new HashMap<>();
		pMap.put("tenantId", tenantId);
		pMap.put("enterCd", enterCd);
		pMap.put("sabun", sabun);
		pMap.put("ymd", ymd);
		
		//기본근무에 대한 입력 또는 변경 건에 경우
		if(timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE)) {
			//해당일의 근무 계획 정보를 가지고 온다. 
			
			//일별 고정 OT의 경우 기설정된 OT정보를 찾아 지워주자.
			//지우기 전에 기본근무 시간 종료시간을 가지고 오자.
			/*if(defaultWorkUseYn!=null && defaultWorkUseYn.equals("Y") && fixotUseType!=null && fixotUseType.equalsIgnoreCase("DAY")) {
				pMap.put("timeTypeCd", WtmApplService.TIME_TYPE_BASE);
				//기본근무 종료시간을 구하자.
				Date maxEdate = flexEmpMapper.getMaxPlanEdate(pMap);
				
				if(maxEdate!=null) {
					pMap.put("yyyyMMddHHmmss", format.format(maxEdate));
					pMap.put("intervalMinute", fixotUseLimit);
					
					//고정 OT의 종료시간을 가지고 오자.
					Date flxotEdate = flexEmpMapper.getIntervalDateTime(pMap);
					//데이터는 같아야 한다. 설정의 변경으로 인해 데이터가 망가지는건 설정화면에서 변경하지 못하도록 제어한다. 비슷하다는걸로 판단하면 안됨.
					WtmWorkDayResult otDayResult = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndPlanSdateAndPlanEdate(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_FIXOT, maxEdate, flxotEdate);
					if(otDayResult!=null)
						workDayResultRepo.delete(otDayResult);
				}
				
			}*/
			
			//기설정된 고정 OT지우기.
			//설정 값이 변경되어 FIXOT가 안지워짐
			workDayResultRepo.deleteByTenantIdAndEnterCdAndYmdAndTimeTypeCdAndSabun(tenantId, enterCd, ymd, WtmApplService.TIME_TYPE_FIXOT, sabun);
			
			//기본근무 정보는 삭제 하고 다시 만들자 그게 속편하다
			workDayResultRepo.deleteByTenantIdAndEnterCdAndYmdAndTimeTypeCdAndSabun(tenantId, enterCd, ymd, timeTypeCd, sabun);
			
			if(planSdate!=null && planEdate!=null) {
			
				List<WtmWorkDayResult> dayResults = workDayResultRepo.findByTenantIdAndEnterCdAndYmdAndSabunAndPlanSdateLessThanEqualAndPlanEdateGreaterThanEqualOrderByPlanSdateAsc(tenantId, enterCd, ymd, sabun, planSdate, planEdate);
	
				Date insSdate = planSdate;
				Date insEdate = planEdate;
				boolean isInsert = false;
				
	
				if(dayResults != null && dayResults.size() > 0) {
					
					
					for(WtmWorkDayResult r : dayResults) {
						//기본근무 사이에 올 수 있는 근무는 시간단위 연차와 반차, 대체휴일, 간주근무, 음? OT빼고 다? ㅡㅡ 연차나 출장 교육의 경우 사전 벨리데이션에서 걸러진다고 보자 여기선 테트리스
						//dayResults 에는 OT가 있어선 안된다.. 넘어오는 기본데이터는 OT중복되어서 작성할 수 없기때문인다 오케?
					
						//데이터가 시종이 똑같을 경우
						if(r.getPlanSdate().compareTo(insSdate) == 0 && r.getPlanEdate().compareTo(insEdate) == 0) {
							//는 없어야한다. ㅋ 유효성 검사기 고장
							
						//시작만 같은 데이터.  
						}else if(r.getPlanSdate().compareTo(insSdate) == 0 && r.getPlanEdate().compareTo(insEdate) < 0) {
							insSdate = r.getPlanEdate(); //시작일시를 바꿔준다.
							isInsert = true; //다음 데이터가 없을 경우 for문밖에서 인써얼트를 해줄라고 
							
						//계획 시작 시간 보다 등록된 일정이 이후 일때
						}else if(r.getPlanSdate().compareTo(insSdate) > 0 && r.getPlanEdate().compareTo(insEdate) <= 0) {
							//바로 넣는다 
							isInsert = false; 
							
							WtmWorkDayResult newDayResult = new WtmWorkDayResult();
							newDayResult.setTenantId(tenantId);
							newDayResult.setEnterCd(enterCd);
							newDayResult.setYmd(ymd);
							newDayResult.setSabun(sabun);
							newDayResult.setApplId(applId);
							newDayResult.setTimeTypeCd(timeTypeCd);
							newDayResult.setTaaCd(taaCd);
							newDayResult.setPlanSdate(insSdate);
							newDayResult.setPlanEdate(r.getPlanSdate());  //insEdata 의 종료일 값은 변경하지 않는다.  끝까지 돌아야하기때무넹
							String shm = sdf.format(insSdate);
							String ehm = sdf.format(r.getPlanSdate()); 
							pMap.put("shm", shm);
							pMap.put("ehm", ehm);
							Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
							newDayResult.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+"")); 
							newDayResult.setUpdateId(userId);
							workDayResultRepo.save(newDayResult);
							
							//계획 종료시간에 못 미칠경우 데이터를 생성해주거나 다음데이터도 봐야한다. 종료시간까지.
							if(r.getPlanEdate().compareTo(insEdate) < 0) {
								isInsert = true;
								insSdate = r.getPlanEdate();
							}
							
						}  
					}
						
				}else {
					//1건의 경우 진입시 삭제했기 때문에 갱신의 개념이다.. 
					//텅비었다 공략해라
					isInsert = true;  
				} 
	
				if(isInsert) { 
					WtmWorkDayResult newDayResult = new WtmWorkDayResult();
					newDayResult.setTenantId(tenantId);
					newDayResult.setEnterCd(enterCd);
					newDayResult.setYmd(ymd);
					newDayResult.setSabun(sabun);
					newDayResult.setApplId(applId);
					newDayResult.setTimeTypeCd(timeTypeCd);
					newDayResult.setTaaCd(taaCd);
					newDayResult.setPlanSdate(insSdate);
					newDayResult.setPlanEdate(insEdate);  
					String shm = sdf.format(insSdate);
					String ehm = sdf.format(insEdate); 
					pMap.put("shm", shm);
					pMap.put("ehm", ehm);
					Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
					newDayResult.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+"")); 
					newDayResult.setUpdateId(userId);
					workDayResultRepo.save(newDayResult);
				}
				
				//고정 OT여부 확인  / 기본 일 근무시간(분) 체크 / 일별소진 옵션만 / 고정 OT시간
				if(defaultWorkUseYn!=null && defaultWorkUseYn.equals("Y") && fixotUseType!=null && fixotUseType.equalsIgnoreCase("DAY")) {
					//일별, 일괄 소진 여부 : 일괄 소진은 여기서 할수 없다. 일마감 시 일괄소진 여부에 따라 OT데이터를 생성해주자.
	
					pMap.put("yyyyMMddHHmmss", format.format(insEdate));
					pMap.put("intervalMinute", fixotUseLimit);
					
					//고정 OT의 종료시간을 가지고 오자.
					Date flxotEdate = flexEmpMapper.getIntervalDateTime(pMap);
					//데이터는 같아야 한다. 설정의 변경으로 인해 데이터가 망가지는건 설정화면에서 변경하지 못하도록 제어한다. 비슷하다는걸로 판단하면 안됨.
					WtmWorkDayResult otDayResult = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndPlanSdateAndPlanEdate(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_FIXOT, insEdate, flxotEdate);
					if(otDayResult == null) {
						//고정 OT 생성
						WtmWorkDayResult newDayResult = new WtmWorkDayResult();
						newDayResult.setTenantId(tenantId);
						newDayResult.setEnterCd(enterCd);
						newDayResult.setYmd(ymd);
						newDayResult.setSabun(sabun);
						newDayResult.setApplId(null);
						newDayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
						newDayResult.setTaaCd(null);
						newDayResult.setPlanSdate(insEdate);
						newDayResult.setPlanEdate(flxotEdate);  
						String shm = sdf.format(insEdate);
						String ehm = sdf.format(flxotEdate); 
						pMap.put("shm", shm);
						pMap.put("ehm", ehm);
						Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
						newDayResult.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+"")); 
						newDayResult.setUpdateId(userId);
						workDayResultRepo.save(newDayResult);
						
					}else {
						//데이터가 있으면 안되는디.. 
						System.out.println("고정 OT 데이터 생성 실패... 있다 이미... 왜!!");
					} 
				}
				
				/**
				 * Time타입 휴게시간 일 경우만
				 * type이 plan이면 계획데이터를 생성한다. 
				 * 인정 데이터 생성을 위함
				 */
				pMap.put("sYmd", WtmUtil.parseDateStr(planSdate, "yyyyMMdd"));
				pMap.put("eYmd", WtmUtil.parseDateStr(planEdate, "yyyyMMdd"));
				pMap.put("taaInfoCd", "BREAK");
				pMap.put("type", "PLAN");
				pMap.put("userId", userId);
				flexEmpMapper.createWorkDayResultOfTimeType(pMap);
				
				//만약 대체휴가와 같은 time block이 있어 base가 여러 개인 경우에 base를 다시 만들어줌
				List<WtmWorkDayResult> dupTimeList = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdNotAndPlanSdateGreaterThanEqualAndPlanEdateLessThanEqualOrderByPlanSdateAsc(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_BASE, planSdate, planEdate);
				if(dupTimeList!=null && dupTimeList.size()>0) {
					for(WtmWorkDayResult r : dupTimeList) {
						addWtmDayResultInBaseTimeType(tenantId, enterCd, ymd, sabun, r.getTimeTypeCd(), r.getTaaCd(), r.getPlanSdate(), r.getPlanEdate(), applId, userId, false);
					}
				}
				
			}
		}
		return rp;
	}
	
	@Transactional
	@Override
	public ReturnParam save(Long flexibleEmpId, Map<String, Object> dateMap, String userId) throws Exception{
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmFlexibleEmp emp =  flexEmpRepo.findById(flexibleEmpId).get();
		WtmFlexibleStdMgr stdMgr = flexStdMgrRepo.findById(emp.getFlexibleStdMgrId()).get();
		flexEmpMapper.createWorkCalendarOfSeleC(flexibleEmpId, userId);

		rp.put("sabun", emp.getSabun());
		
		if(dateMap != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			
			
			for(String k : dateMap.keySet()) {
//				
//				Map<String, String> dayResult = (Map<String, String>) dateMap.get(k);
//				if(dayResult.get("shm") != null && !dayResult.get("shm").equals("")) {
//					String shm = dayResult.get("shm");
//					String ehm = dayResult.get("ehm");
//					Date s = sdf.parse(k+shm);
//					Date e = sdf.parse(k+ehm);
//					
//					this.addWtmDayResultInBaseTimeType(emp.getTenantId(), emp.getEnterCd(), k, emp.getSabun(), WtmApplService.TIME_TYPE_BASE, null, s, e, null, userId);
//				}
//				
					
				Map<String, String> drMap = (Map<String, String>) dateMap.get(k);
				if(drMap.get("shm") != null && !drMap.get("shm").equals("")) {
					String shm = drMap.get("shm");
					String ehm = drMap.get("ehm");
					Date s = sdf.parse(k+shm);
					Date e = sdf.parse(k+ehm);
					
					if(s.compareTo(e) > 0) {
						// 날짜 더하기
				        Calendar cal = Calendar.getInstance();
				        cal.setTime(e);
				        cal.add(Calendar.DATE, 1);
				        e = cal.getTime();
					}
					
					this.mergeWorkDayResult(emp.getTenantId(), emp.getEnterCd(), k, emp.getSabun(), null, WtmApplService.TIME_TYPE_BASE, null, s, e, stdMgr.getDefaultWorkUseYn() , stdMgr.getFixotUseType(), stdMgr.getFixotUseLimit(), userId);
				} else {
					this.mergeWorkDayResult(emp.getTenantId(), emp.getEnterCd(), k, emp.getSabun(), null, WtmApplService.TIME_TYPE_BASE, null, null, null, stdMgr.getDefaultWorkUseYn() , stdMgr.getFixotUseType(), stdMgr.getFixotUseLimit(), userId);
				}
				
				/*
				WtmWorkDayResult result =  workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), k);
				if(result == null) {
					result = new WtmWorkDayResult();
					
					//부모키 가져오기
					WtmWorkCalendar c = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd( emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), k);
					result.setTenantId(c.getTenantId());
					result.setEnterCd(c.getEnterCd());
					result.setYmd(c.getYmd());
					result.setSabun(c.getSabun());
				}
				try {
					Map<String, String> dayResult = (Map<String, String>) dateMap.get(k);
					if(dayResult.get("shm") != null && !dayResult.get("shm").equals("")) {
						String shm = dayResult.get("shm");
						String ehm = dayResult.get("ehm");
						Date s = sdf.parse(k+shm);
						Date e = sdf.parse(k+ehm);
						
						if(s.compareTo(e) > 0) {
							// 날짜 더하기
					        Calendar cal = Calendar.getInstance();
					        cal.setTime(e);
					        cal.add(Calendar.DATE, 1);
					        e = cal.getTime();
						}
						result.setPlanSdate(s);
						result.setPlanEdate(e);
						Map<String, Object> paramMap = new HashMap<>();
						paramMap.put("shm", shm);
						paramMap.put("ehm", ehm);
						paramMap.put("tenantId", emp.getTenantId());
						paramMap.put("enterCd", emp.getEnterCd());
						paramMap.put("sabun", emp.getSabun());
						paramMap.put("ymd", result.getYmd());
						
						Map<String, Object> planMinuteMap = flexEmpMapper.calcMinuteExceptBreaktime(paramMap);
						result.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					}else {
						result.setPlanSdate(null);
						result.setPlanEdate(null);
						result.setPlanMinute(0);
					}
					result.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
					result.setUpdateId(userId);
					workDayResultRepo.save(result);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("flexibleEmpId", flexibleEmpId);
			paramMap.put("userId", userId);
			
			ObjectMapper mapper  = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(paramMap));
			//holidayYn 갱신
			//flexEmpMapper.updateHolidayYnOFWorkCalendar(paramMap);
			//planMinute갱신
			//flexEmpMapper.updatePlanMinute(flexibleEmpId);
			
			Map<String, Object> result = flexEmpMapper.checkBaseWorktime(flexibleEmpId);
			if(result!=null && result.get("isValid")!=null && "0".equals(result.get("isValid").toString())) {
				throw new RuntimeException(result.get("totalWorktime").toString() + "분의 소정근로시간을 넘을 수 없습니다.");
			}
		}
		
		//저장된 데이터를 기반으로 유효성 검사 진행
		//select CEIL( F_WTM_TO_DAYS(E.SYMD, E.EYMD) * 40 / 7) from WTM_FLEXIBLE_EMP E;
		
		return rp;
	}
	
	@Transactional
	@Override
	public ReturnParam saveElasPlan(Long flexibleApplId, Map<String, Object> paramMap, String userId) throws Exception{
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		if(paramMap.get("dayResult")!=null && !"".equals(paramMap.get("dayResult"))){
			//dayResult는 이런 형식 {ymd : {shm: 0900, ehm: 1800, otbMinute: 1, otaMinute:2 } }
			Map<String, Object> dayResult = (Map<String, Object>)paramMap.get("dayResult");
			
			if(dayResult!=null && dayResult.size()>0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				List<WtmFlexibleApplDet> applDets = new ArrayList<WtmFlexibleApplDet>();
				
				for(String k : dayResult.keySet()) {
					Map<String, Object> vMap = (Map<String, Object>)dayResult.get(k);
					WtmFlexibleApplDet applDet = flexApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, k);
					
					if(applDet!=null) {
						String shm = null;
						Date planSdate = null;
						if(vMap.get("shm") != null && !vMap.get("shm").equals("") && !vMap.get("shm").equals("0000")) {
							shm = vMap.get("shm").toString();
							planSdate = sdf.parse(k+shm);
							
							applDet.setPlanSdate(planSdate);
						} else {
							applDet.setPlanSdate(null);
						}
						String ehm = null;
						Date planEdate = null;
						if(vMap.get("ehm") != null && !vMap.get("ehm").equals("") && !vMap.get("ehm").equals("0000")) {
							ehm = vMap.get("ehm").toString();
							planEdate = sdf.parse(k+ehm);
							
							applDet.setPlanEdate(planEdate);
						} else {
							applDet.setPlanEdate(null);
						}
						
						paramMap.put("ymd", k);
						
						if(shm!=null && ehm!=null) {
							paramMap.put("shm", shm);
							paramMap.put("ehm", ehm);
							
							Map<String, Object> planMinuteMap = calcMinuteExceptBreaktimeForElas(false, flexibleApplId, paramMap, userId);
							applDet.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute").toString()));
						} else {
							applDet.setPlanMinute(null);
						}
						
						if(shm!=null && ehm!=null && vMap.get("otbMinute") != null && !vMap.get("otbMinute").equals("")) {
							Map<String, Object> otbMinuteMap = calcOtMinuteExceptBreaktimeForElas(false, flexibleApplId, k, k+shm, k+ehm, "OTB", Integer.parseInt(vMap.get("otbMinute").toString()), userId);
							
							if(otbMinuteMap!=null) {
								Date otbSdate = WtmUtil.toDate(otbMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
								Date otbEdate = WtmUtil.toDate(otbMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
								
								applDet.setOtbSdate(otbSdate);
								applDet.setOtbEdate(otbEdate);
								applDet.setOtbMinute(Integer.parseInt(otbMinuteMap.get("calcMinute").toString()));
							}
							
						} else {
							applDet.setOtbSdate(null);
							applDet.setOtbEdate(null);
							applDet.setOtbMinute(null);
						}
						
						if(shm!=null && ehm!=null && vMap.get("otaMinute") != null && !vMap.get("otaMinute").equals("")) {
							Map<String, Object> otaMinuteMap = calcOtMinuteExceptBreaktimeForElas(false, flexibleApplId, k, k+shm, k+ehm, "OTA", Integer.parseInt(vMap.get("otaMinute").toString()), userId);
							Date otaSdate = WtmUtil.toDate(otaMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
							Date otaEdate = WtmUtil.toDate(otaMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
							
							applDet.setOtaSdate(otaSdate);
							applDet.setOtaEdate(otaEdate);
							applDet.setOtaMinute(Integer.parseInt(otaMinuteMap.get("calcMinute").toString()));
						} else {
							applDet.setOtaSdate(null);
							applDet.setOtaEdate(null);
							applDet.setOtaMinute(null);
						}
							
						applDet.setUpdateDate(new Date());
						
						applDets.add(applDet);
					}
				}
				
				if(applDets.size()>0) 
					flexApplDetRepo.saveAll(applDets);
			}
				
		}
		
		if(paramMap.get("reason")!=null && !"".equals(paramMap.get("reason"))){
			WtmFlexibleAppl flexAppl =  flexApplRepo.findById(flexibleApplId).get();
			if(flexAppl!=null) {
				flexAppl.setReason(paramMap.get("reason").toString());
				flexApplRepo.save(flexAppl);
			}
		}
		
		return rp;
	}

	@Override
	//public List<WtmDayWorkVO> getDayWorks(Long flexibleEmpId, Long userId) {
	public List<WtmDayWorkVO> getDayWorks(List<Map<String, Object>> plans, String userId) {
		//List<Map<String, Object>> plans = flexEmpMapper.getWorktimePlan(flexibleEmpId);
		
		Map<String, Object> imsiMap = new HashMap<>();
		
		if(plans != null && plans.size() > 0) {
			WtmDayWorkVO work = new WtmDayWorkVO();
			for(Map<String, Object> plan : plans) {
				String ymd = plan.get("ymd").toString();
				String holidayYn = "";
				if(plan.containsKey("holidayYn") && plan.get("holidayYn") != null) {
					holidayYn = plan.get("holidayYn").toString();
				}
				String timeNm = "";
				if(plan.containsKey("timeNm") && plan.get("timeNm") != null) {
					timeNm = plan.get("timeNm").toString();
				}
				
				String shm = "";
				if(plan.containsKey("shm") && plan.get("shm") != null) {
					shm = plan.get("shm").toString();
				}
				String ehm = "";
				if(plan.containsKey("ehm") && plan.get("ehm") != null) {
					ehm = plan.get("ehm").toString();
				}
				String m = "";
				Double H = 0d;
				Double i = 0d;
				if(plan.containsKey("minute") && plan.get("minute") != null) {
					m = plan.get("minute").toString();
					H = Double.parseDouble(m)/60;
					H = Math.ceil(H*100)/100.0;
					i = (H - H.intValue()) * 60;
				}

				String taaCd =  "";
				if(plan.containsKey("taaCd") && plan.get("taaCd") != null) {
					 taaCd = plan.get("taaCd").toString();
				}
				String taaNm =  "";
				if(plan.containsKey("taaNm") && plan.get("taaNm") != null) {
					taaNm = plan.get("taaNm").toString();
				}
				String timeTypeCd =  "";
				if(plan.containsKey("timeTypeCd") && plan.get("timeTypeCd") != null) {
					timeTypeCd = plan.get("timeTypeCd").toString();
				}
				
				List<WtmDayPlanVO> planVOs = new ArrayList<>();
				Map<String, Object> dtMap = new HashMap<>();
				if(imsiMap.containsKey(ymd)) {
					dtMap = (Map<String, Object>) imsiMap.get(ymd);
					planVOs = (List<WtmDayPlanVO>) dtMap.get("plan");

				}
				WtmDayPlanVO planVO = new WtmDayPlanVO();
				planVO.setKey(ymd);
				if(taaNm != null && !taaNm.equals("")) {
					planVO.setLabel(taaNm);
				}else{
					String label = shm + "~" + ehm + "("+H.intValue()+"시간"+((i.intValue()>0)?i.intValue()+"분":"")+")";
					planVO.setLabel(label);
				}
				
				Map<String, Object> valueMap = new HashMap<>();
				valueMap.put("shm", shm);
				valueMap.put("ehm", ehm);
				valueMap.put("m", m);
				valueMap.put("taaNm", taaNm);
				valueMap.put("taaCd", taaCd);
				valueMap.put("timeTypeCd", timeTypeCd);
				planVO.setValueMap(valueMap);
				planVOs.add(planVO);

				dtMap.put("holidayYn", holidayYn);
				dtMap.put("timeNm", timeNm);
				dtMap.put("plan", planVOs);
				
				imsiMap.put(ymd, dtMap);
			}
		}
		List<WtmDayWorkVO> works = new ArrayList<WtmDayWorkVO>();
		for(String k : imsiMap.keySet()) {
			WtmDayWorkVO workVO = new WtmDayWorkVO();
			workVO.setDay(k);
			Map<String, Object> dtMap = (Map<String, Object>) imsiMap.get(k);
			workVO.setHolidayYn(dtMap.get("holidayYn").toString());
			workVO.setTimeNm(dtMap.get("timeNm").toString());
			workVO.setPlans((List<WtmDayPlanVO>)dtMap.get("plan"));
			works.add(workVO);
		}
		return works;
	}

	@Override
	public void updEntrySdate(Long tenantId, String enterCd, String sabun, String ymd, String entryTypeCd, Date sdate, String userId) {
		WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
		calendar.setEntryStypeCd(entryTypeCd);
		calendar.setEntrySdate(sdate); 
		calendar.setUpdateId(userId);
		workCalendarRepo.save(calendar);
	}

	@Override
	public void updEntryEdate(Long tenantId, String enterCd, String sabun, String ymd, String entryTypeCd, Date edate, String userId) {
		
		WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
		calendar.setEntryEtypeCd(entryTypeCd);
		calendar.setEntryEdate(edate); 
		calendar.setUpdateId(userId);
		workCalendarRepo.save(calendar);
		//트랜젝션을 묶지 않는다.
		//타각은 타각대로 저장이 저장적으로 이루어져야 한다.
		
	}
	
	public void debug(String format, Object... arguments) {
		
	}

	@Transactional
	@Override
	public void calcApprDayInfo(Long tenantId, String enterCd, String sYmd, String eYmd, String sabun) {
		//TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		
		List<WtmWorkCalendar> works = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, sabun, sYmd, eYmd);
		
		
		if(works != null) {
			//Result -> Result_O		
			calcApprDayInfo0(tenantId, enterCd, sabun, "BASE", sYmd, eYmd);
			
			//인정시간 초기화
			calcApprDayInfoApprReset(tenantId, enterCd, sabun, "BASE", sYmd, eYmd);
			
			for(WtmWorkCalendar calendar : works) {
				WtmFlexibleEmp flexEmp = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, calendar.getYmd());
				if(flexEmp == null) {
					continue;
				}
				WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findById(flexEmp.getFlexibleStdMgrId()).get();
				WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(calendar.getTimeCdMgrId()).get();
				
				
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("tenantId", calendar.getTenantId());
				paramMap.put("enterCd", calendar.getEnterCd());
				paramMap.put("sabun", calendar.getSabun());
				paramMap.put("sYmd", calendar.getYmd());
				paramMap.put("eYmd", calendar.getYmd());
				
				ObjectMapper mapper = new ObjectMapper();
				try {
					logger.debug("calcApprDayInfo : " + mapper.writeValueAsString(paramMap));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				calcApprDayInfo1(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd());
				
				calcApprDayInfo2(calendar, flexStdMgr, timeCdMgr);
				
			}
			
			
		}
	}
	@Override
	public void calcApprDayInfo1(Long tenantId, String enterCd, String sabun, String ymd) {
		WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
		List<String> timeTypeCd = new ArrayList<>();
		timeTypeCd.add(WtmApplService.TIME_TYPE_LLA);
		timeTypeCd.add(WtmApplService.TIME_TYPE_EXCEPT);
		if(flexStdMgr.getFixotUseType().equals("ALL")) {
			timeTypeCd.add(WtmApplService.TIME_TYPE_FIXOT); //20200805 jyp 추가
		}
		
		logger.debug("1. 지각 조퇴 무단결근 고정 오티 데이터 삭제 ", "timeTypeCd : " + WtmApplService.TIME_TYPE_LLA + ", sabun : " + sabun);
		//지각 조퇴 무단결근 데이터 삭제
		if(sabun != null && !sabun.equals("")) {
			logger.debug("calcApprDayInfo 1");
			//try { logger.debug("workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc ", "tenantId : " + tenantId + ", enterCd : " +  enterCd +", sabun : " + sabun + ", timeTypeCd : " + mapper.writeValueAsString(timeTypeCd) + ", calendar.getYmd() : " + calendar.getYmd(), "findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc"); } catch (JsonProcessingException e) { e.printStackTrace(); }	
			List<WtmWorkDayResult> result = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, ymd, ymd);
			logger.debug("result.size > deleteAll", result.size());
			if(result != null && result.size() > 0) {
				workDayResultRepo.deleteAll(result);
			}

			//workDayResultRepo.flush();
			//result.clear();
			
		}
		/*else {
			//try { logger.debug("workDayResultRepo.findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween ", "tenantId : " + tenantId + ", enterCd : " +  enterCd +", sabun : " + sabun + ", timeTypeCd : " + mapper.writeValueAsString(timeTypeCd) + ", calendar.getYmd() : " + calendar.getYmd(), "findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween"); } catch (JsonProcessingException e) { e.printStackTrace(); }
			List<WtmWorkDayResult> result = workDayResultRepo.findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween(tenantId, enterCd, timeTypeCd, ymd, ymd);
			logger.debug("result.size > deleteAll", result.size());
			if(result != null && result.size() > 0) {
				workDayResultRepo.deleteAll(result);
				workDayResultRepo.flush();
			}

			workDayResultRepo.flush();
			result.clear();
			
			
		}  
		*/
	}
	
	@Transactional
	public void calcApprDayInfo0(Long tenantId, String enterCd, String sabun, String timeTypeCd, String sYmd, String eYmd) {
		logger.debug("calcApprDayInfo0");
		List<WtmWorkDayResultO> delResults = workDayResultORepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, timeTypeCd, sYmd, eYmd);
		List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, timeTypeCd, sYmd, eYmd);
		if(delResults != null && delResults.size() > 0) {
			workDayResultORepo.deleteAll(delResults);
		}

		logger.debug("calcApprDayInfo0 results : " + results.size());
		for(WtmWorkDayResult r : results) {
			
			WtmWorkDayResultO nr = new WtmWorkDayResultO();
			nr.setTenantId(r.getTenantId());
			nr.setEnterCd(r.getEnterCd());
			nr.setYmd(r.getYmd());
			nr.setSabun(r.getSabun());
			nr.setApplId(r.getApplId());
			nr.setTimeTypeCd(r.getTimeTypeCd());
			nr.setTaaCd(r.getTaaCd());
			nr.setPlanSdate(r.getPlanSdate());
			nr.setPlanEdate(r.getPlanEdate());
			nr.setPlanMinute(r.getPlanMinute());
			nr.setApprSdate(r.getApprSdate());
			nr.setApprEdate(r.getApprEdate());
			nr.setApprMinute(r.getApprMinute());
			logger.debug("nr : " + nr.toString());
			workDayResultORepo.save(nr);
		}
		 
	}
	
	@Transactional
	public void calcApprDayInfoApprReset(Long tenantId, String enterCd, String sabun, String timeTypeCd, String sYmd, String eYmd) {
		logger.debug("calcApprDayInfoApprReset");
		
		List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, timeTypeCd, sYmd, eYmd);

		logger.debug("calcApprDayInfoApprReset results : " + results.size());
		for(WtmWorkDayResult r : results) {
			
			WtmWorkDayResult nr = new WtmWorkDayResult();
			nr.setWorkDayResultId(r.getWorkDayResultId());
			nr.setTenantId(r.getTenantId());
			nr.setEnterCd(r.getEnterCd());
			nr.setYmd(r.getYmd());
			nr.setSabun(r.getSabun());
			nr.setApplId(r.getApplId());
			nr.setTimeTypeCd(r.getTimeTypeCd());
			nr.setTaaCd(r.getTaaCd());
			nr.setPlanSdate(r.getPlanSdate());
			nr.setPlanEdate(r.getPlanEdate());
			nr.setPlanMinute(r.getPlanMinute());
			nr.setApprSdate(null);
			nr.setApprEdate(null);
			nr.setApprMinute(null);
			logger.debug("nr : " + nr.toString());
			workDayResultRepo.save(nr);
		}
		 
	}
	
	@Transactional
	public void calcaApprDayReset(WtmWorkCalendar calendar, WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr) {
		//타각 시간 기준으로 인정근무를 생성할 경우
		//계획된 정보의 출퇴근 시간을 변경하고 
		//간주근무 정보가 있을 경우 타임블럭을 재배치한다.
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		//List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd(), WtmApplService.TIME_TYPE_BASE);
		
		
		//계획시간이 있으며, 타각시간 기준으로 잔여 시간을 연장근무 시간으로 만들어 준다.
		//빗썸에서 추가한 내용 임. 
		if(!"".equals(flexStdMgr.getCreateOtIfOutOfPlanYn()) && "Y".equals(flexStdMgr.getCreateOtIfOutOfPlanYn())) {
			logger.debug("flexStdMgr.getCreateOtIfOutOfPlanYn() : " + flexStdMgr.getCreateOtIfOutOfPlanYn());
			//사이 데이터는 무시한다. 앞위 데이터를 만들어 주고 이후 로직을 태워보자 .
			//Result전체 체크
			List<WtmWorkDayResult> ress = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdOrderByPlanSdateAsc(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd());
			if(ress != null && ress.size() > 0) {
				//심야근무 시간
				Date nightSdate = WtmUtil.toDate( calendar.getYmd() + "220000" , "yyyyMMddHHmmss");
				Date nightEdate = WtmUtil.addDate(WtmUtil.toDate( calendar.getYmd() + "060000" , "yyyyMMddHHmmss"), 1);
				//조출에 대한 부분을 위함이다. 
				Date prenightEdate = WtmUtil.toDate( calendar.getYmd() + "060000" , "yyyyMMddHHmmss");
				
				boolean hasBASE = false;
				Date minPlanSdate = null, maxPlanEdate = null;
				//반드시 계획은 있어야한다. 단 기본 근무 계획은 반드시 있어야 한다. - BASE
				for(WtmWorkDayResult r : ress) {
					if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_LLA)) {
						continue;
					}
					if(minPlanSdate == null || minPlanSdate.compareTo(r.getPlanSdate()) > 0) {
						minPlanSdate = r.getPlanSdate();
					}
					if(maxPlanEdate == null || maxPlanEdate.compareTo(r.getPlanEdate()) < 0 ) {
						maxPlanEdate = r.getPlanEdate();
					}
					
					if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
						hasBASE = true;
					}
				}
				if(hasBASE) {
					logger.debug("");
					//타각시간이 빨라야한다. 
					if(calendar.getEntrySdate().compareTo(minPlanSdate) < 0) {
						logger.debug("타각시간이 계획시작시간보다 빠르다.");
						logger.debug("calendar.getEntrySdate() : " + calendar.getEntrySdate());
						logger.debug("minPlanSdate : " + minPlanSdate);
						
						if(calendar.getEntrySdate().compareTo(prenightEdate) < 0) {
							logger.debug("타각시간이 NIGHT 시간내에 있다.");
							Date planSdate = calendar.getEntrySdate();
							Date planEdate = prenightEdate;
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_EARLY_NIGHT, planSdate, planEdate, null, null, null, null);
							planSdate = prenightEdate;
							planEdate = minPlanSdate;
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_EARLY_OT, planSdate, planEdate, null, null, null, null);
						}else {
							Date planSdate = calendar.getEntrySdate();
							Date planEdate = minPlanSdate;
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_EARLY_OT, planSdate, planEdate, null, null, null, null);
						}
					}
					
					if(calendar.getEntryEdate().compareTo(maxPlanEdate) > 0){
						logger.debug("타각시간이 계획종료시간보다 빠르다.");
						logger.debug("calendar.getEntryEdate() : " + calendar.getEntryEdate());
						logger.debug("maxPlanEdate : " + maxPlanEdate);
						if(calendar.getEntryEdate().compareTo(nightSdate) > 0){
							logger.debug("타각시간이 NIGHT 시간내에 있다.");
							Date planSdate = maxPlanEdate;
							Date planEdate = nightSdate;
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_OT, planSdate, planEdate, null, null, null, null);
							planSdate = nightSdate;
							planEdate = calendar.getEntryEdate();
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_NIGHT, planSdate, planEdate, null, null, null, null);
						}else {
							Date planSdate = maxPlanEdate;
							Date planEdate = calendar.getEntryEdate();
							this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), WtmApplService.TIME_TYPE_OT, planSdate, planEdate, null, null, null, null);
						}
					}
				}
				
			}
		}else {
			List<String> timeTypeCds = new ArrayList<String>();
			timeTypeCds.add(WtmApplService.TIME_TYPE_BASE); 
			timeTypeCds.add(WtmApplService.TIME_TYPE_OT);
			timeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
			timeTypeCds.add(WtmApplService.TIME_TYPE_NIGHT);
			timeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_NIGHT);
			timeTypeCds.add(WtmApplService.TIME_TYPE_SUBS);
			/*
			timeTypeCds.add(WtmApplService.TIME_TYPE_REGA);  // 아래서 하고 있음 
			timeTypeCds.add(WtmApplService.TIME_TYPE_TAA);	// 아래서 하고 있음
			timeTypeCds.add(WtmApplService.TIME_TYPE_GOBACK); // 아래서 하고 있음
			*/ 
			
			/**
			 * 사이사이 비어있는 구간을 재생성 해줘야한다.타각시간 기준으로 계획 데이터가 변경 시 
			 * 계획이 없고 타각이 있을 경우엔 생성해줘야하는건가. 계획이 무슨 의미가 있는것인가.. 
			 */
			List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdInAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd(), timeTypeCds);
			boolean isLast = false; 
			boolean isUpdate = false;
			WtmWorkDayResult preResult = null;
			WtmWorkDayResult tmpResult = null;
			if(results != null && results.size() > 0) {
				int cnt = 1;
				logger.debug("calcaApprDayReset :: results.size = " + results.size());
				logger.debug("calcaApprDayReset :: flexStdMgr.getApplyEntrySdateYn() = " + flexStdMgr.getApplyEntrySdateYn());
				logger.debug("calcaApprDayReset :: flexStdMgr.getApplyEntryEdateYn() = " + flexStdMgr.getApplyEntryEdateYn());
				for(WtmWorkDayResult result : results) {
					logger.debug("calcaApprDayReset :: cnt = " + cnt);
					if(cnt == results.size() && results.size() > 1 ) {
						isLast = true;
					}
					
					Date sDate = null;
					Date eDate = null;
					
					//타각시간에서 벗어난 데이터는 패스 한다 .
					if(calendar.getEntrySdate().compareTo(result.getPlanEdate()) < 0
							&& calendar.getEntryEdate().compareTo(result.getPlanSdate()) > 0) {
							
						logger.debug("타각시간에 포함된다. = " + results.size());
						if(results.size() > 1) {
							/*
							if(cnt != 1 && cnt != results.size()) {
								//중간 데이터는 건너 뛴다. 
								cnt++;
								continue;
							}
							//첫번째 데이터
							if(cnt == 1) {
								if(flexStdMgr.getApplyEntrySdateYn().equalsIgnoreCase("Y")) {
									sDate = calendar.getEntrySdate();
								} else {
									sDate = result.getPlanSdate();
								} 
							}else {
								sDate = result.getPlanSdate();
							}
							
							//마지막 데이터
							if(cnt == results.size() && results.size() > 1 ) {
								if(flexStdMgr.getApplyEntryEdateYn().equalsIgnoreCase("Y")) {
									eDate = calendar.getEntryEdate();
								} else {
									eDate = result.getPlanEdate();
								}
							}else {
								eDate = result.getPlanEdate();
							}
							*/
							if(cnt == 1) {
								if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
									if(cnt == 1 && flexStdMgr.getApplyEntrySdateYn().equalsIgnoreCase("Y")) {
										if(flexStdMgr.getApplyEntryEdateYn().equalsIgnoreCase("Y") && result.getPlanEdate().compareTo(calendar.getEntryEdate()) < 0) {	//종료시간을 타각시간으로 갱신시에만
											//다음 데이터를 체크 한다. 
											preResult = result;
											preResult.setPlanSdate(calendar.getEntrySdate());
										}else {
											Date nSdate =  calendar.getEntrySdate();
											Date nEdate = result.getPlanEdate();
											this.saveWorkDayResult(flexStdMgr, timeCdMgr, result, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											tmpResult = result;
										}
									}
								}else {
									//베이스가 아니면 출퇴근 적용 여부가 모두 Y이어야 한다. 
									if(cnt == 1 && flexStdMgr.getApplyEntrySdateYn().equalsIgnoreCase("Y") && flexStdMgr.getApplyEntryEdateYn().equalsIgnoreCase("Y")) {
										//첫번째 데이터일 경우 
										if(result.getPlanSdate().compareTo( calendar.getEntrySdate() ) > 0) {
											Date nSdate =  calendar.getEntrySdate();
											Date nEdate = null;
											boolean isCheck = false;
											if(result.getPlanSdate().compareTo( calendar.getEntryEdate()) < 0) {
												nEdate = result.getPlanSdate();
												isCheck = true;
											}else {
												nEdate = calendar.getEntryEdate();
											}
											preResult = this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											
											if(!isCheck) {
												preResult = null;
												tmpResult = result;
											}
										}
									}
									
								}
							}else {
								if(preResult != null) {
									if(result.getPlanSdate().compareTo(calendar.getEntryEdate()) < 0) {
										preResult.setPlanEdate(result.getPlanSdate());
										Date nSdate = preResult.getPlanSdate();
										Date nEdate = result.getPlanSdate();
										this.saveWorkDayResult(flexStdMgr, timeCdMgr, preResult, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
										
										//현 데이터가 베이스면
										if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
											if(isLast) {
												//마지막일 경우 
												nSdate = result.getPlanSdate();
												nEdate = calendar.getEntryEdate();
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, result, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
												
											}else {
												//지금 데이터 보다 타각 시간이 뒤일 경우 체크
												preResult = result;
												tmpResult = null;
											}
										}else {
											//베이스가 아니면 
											//마지막 데이터이면 
											if(isLast) {
												if(result.getPlanEdate().compareTo(calendar.getEntryEdate()) < 0) {
													nSdate = result.getPlanEdate();
													nEdate = calendar.getEntryEdate();
													//새로 생성
													this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
												}
											}else {
												preResult = null;
												tmpResult = result;
											}
											//nSdate = result.getPlanSdate();
											//nEdate = calendar.getEntryEdate();
											//this.saveWorkDayResult(flexStdMgr, timeCdMgr, result, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											//break;
										}
									}else {
										logger.debug("현재의 계획 시작일이 타각 시간보다 늦다. " + preResult);
										logger.debug("result : " + result);
										preResult.setPlanEdate(calendar.getEntryEdate());
										this.saveWorkDayResult(flexStdMgr, timeCdMgr, preResult, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, null, null, null, null, null, null);
										logger.debug("그만 돌아도 된다.");
									}
								}else if(tmpResult != null) {
									if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
										if(calendar.getEntryEdate().compareTo(result.getPlanSdate())<0) {
											workDayResultRepo.delete(result);
										}else {
											if(isLast) {
												result.setPlanSdate(tmpResult.getPlanEdate());
												result.setPlanEdate(calendar.getEntryEdate());
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, result, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, null, null, null, null, null, null);
											}else {
												result.setPlanSdate(tmpResult.getPlanEdate());
												preResult = result;
												tmpResult = null;
											}
										}
									}else {
										//사이 데이터를 만들자
										if(tmpResult.getPlanEdate().compareTo(result.getPlanSdate()) <0 ) {
											if(calendar.getEntryEdate().compareTo(result.getPlanSdate()) < 0) {
												Date nSdate = tmpResult.getPlanEdate();
												Date nEdate = calendar.getEntryEdate();
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
												tmpResult = null;
												preResult = null;
												//끝이다
											}
										}else if(result.getPlanEdate().compareTo(calendar.getEntryEdate()) < 0){
											if(isLast) {
												Date nSdate = result.getPlanEdate();
												Date nEdate = calendar.getEntryEdate();
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
												//끝이다
											}else {
												tmpResult = result;
												preResult = null;
											}
										}
									}
								}
							}
						}else { 
							logger.debug("1개다");
							if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
								
								//1개면 좋다. 
								if(flexStdMgr.getApplyEntrySdateYn().equalsIgnoreCase("Y")) {
									sDate = calendar.getEntrySdate();
								}else {
									sDate = result.getPlanSdate();
								}
								
								if(flexStdMgr.getApplyEntryEdateYn().equalsIgnoreCase("Y")) {
									eDate = calendar.getEntryEdate();
								}else {
									eDate = result.getPlanEdate();
								}
								isUpdate = true;
							}else {
								//없을 경우 타각시간 구간을 새로 생성한다. 
								if(flexStdMgr.getApplyEntrySdateYn().equalsIgnoreCase("Y")) {
									sDate = calendar.getEntrySdate();
								}
								if(flexStdMgr.getApplyEntryEdateYn().equalsIgnoreCase("Y")) {
									eDate = calendar.getEntryEdate();
								}
								if(sDate != null && eDate != null && sDate.compareTo(eDate) < 0) {
									if(result.getPlanSdate().compareTo(sDate) <= 0 && result.getPlanEdate().compareTo(eDate) >= 0) {
										//만들지 않는다.
									}else {
										if(result.getPlanSdate().compareTo(sDate) > 0 && result.getPlanEdate().compareTo(eDate) < 0) {
											logger.debug("앞뒤로 만들어 준다.");
											Date nSdate = sDate;
											Date nEdate = result.getPlanSdate();
											this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											nSdate = result.getPlanEdate();
											nEdate = eDate;
											this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											
										}else if(result.getPlanSdate().compareTo(eDate) >= 0 || sDate.compareTo(result.getPlanEdate()) >= 0) {
											//아예 벗어난 구간은 새로 만든다.
											this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, sDate, eDate, null, null, null, null);
										}else {
											logger.debug("걸쳤다");
											if(sDate.compareTo(result.getPlanSdate()) < 0 && eDate.compareTo(result.getPlanSdate()) > 0) {
												Date nSdate = sDate;
												Date nEdate = result.getPlanSdate();
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
												
											}
											
											if(sDate.compareTo(result.getPlanEdate()) < 0 && eDate.compareTo(result.getPlanEdate()) > 0) {
												Date nSdate = result.getPlanEdate();
												Date nEdate = eDate;
												this.saveWorkDayResult(flexStdMgr, timeCdMgr, null, result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun(), WtmApplService.TIME_TYPE_BASE, nSdate, nEdate, null, null, null, null);
											}
										}
				
									}
								}
								isUpdate = false;
							}
							
						}
						
						if(isUpdate && !result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT) && !result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) && !result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT) && !result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT) && !result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS)) {
							isUpdate = false;
							try {
								if(flexStdMgr.getWorkShm() != null && !"".equals(flexStdMgr.getWorkShm()) && flexStdMgr.getWorkEhm() != null && !"".equals(flexStdMgr.getWorkEhm())) {
									// 20200803 선택근무제가 아니면 근무시각 옵션이 비활성화됨. 그리고 선근제도 근무제한이 없을수있음.
									Date limitSdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkShm());
									Date limitEdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkEhm());
								
									if(limitSdate.compareTo(limitEdate) > 0) {
										logger.debug("제한시간 셋팅이 종료시간 보다 시작시간이 늦을 경우 종료시간을 1일 더해서 다음날로 만든다. sHm : " + flexStdMgr.getWorkShm() + " eHm : " + flexStdMgr.getWorkEhm());
										Calendar cal = Calendar.getInstance();
										cal.setTime(limitEdate);
										cal.add(Calendar.DATE, 1);
										limitEdate = cal.getTime();
									}
					
									if(sDate.compareTo(limitSdate) < 0) {
										logger.debug("시작일 근무 제한 시간 적용. sDate : " + sDate + " limitSdate : " + limitSdate);
										sDate = limitSdate;
									}
			//						
			//						if(sDate.compareTo(limitEdate) >= 0) {
			//							logger.debug("근무 제한 시간보다 이후 시간입니다.");
			//							return 0;
			//						}
									
									if(eDate.compareTo(limitEdate) > 0) {
										logger.debug("종료일 근무 제한 시간 적용. eDate : " + eDate + " limitEdate : " + limitEdate);
										eDate = limitEdate;
									}
								}
		
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result.setPlanSdate(sDate);
							result.setPlanEdate(eDate);
							
							SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
							int apprMinute = calcService.WtmCalcMinute(sdf.format(sDate), sdf.format(eDate), null, null, flexStdMgr.getUnitMinute());
							int breakMinute = 0;
							if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
								breakMinute = calcService.getBreakMinuteIfBreakTimeMGR(sDate, eDate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
								logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : apprMinute " + apprMinute);
								logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : breakMinute " + breakMinute);
								apprMinute = apprMinute - breakMinute;
								breakMinute = 0;
							}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
								breakMinute = calcService.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), apprMinute);
								logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : apprMinute " + apprMinute);
								logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : breakMinute " + breakMinute);
							//}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
							}
							
							result.setPlanMinute(apprMinute);
							result.setUpdateDate(new Date());
							result.setUpdateId("calcaApprDayReset");
							logger.debug("calcaApprDayReset = " + result);
							workDayResultRepo.save(result);
						}
						
					}
					cnt++;
				}
			}
		}
		

		/**
		 * 간주근무 정보가 있을 경우 타임블럭을 재생성 하자. 
		 * ngv 의 경우 타각정보 기준으로 인정근무를 생성하고 출장데이터가 하루에 여러건이 될 수 있다
		 */
		List<WtmWorkDayResult> r = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), WtmApplService.TIME_TYPE_REGA, calendar.getYmd(), calendar.getYmd());
		for(WtmWorkDayResult res : r) {
			this.addWtmDayResultInBaseTimeType(res.getTenantId()
												 , res.getEnterCd()
												 , res.getYmd()
												 , res.getSabun()
												 , res.getTimeTypeCd()
												 ,""
												 , res.getPlanSdate()
												 , res.getPlanEdate()
												 , null
												 , "rega reset timeblock"
												 , false);
			res.setApprSdate(res.getPlanSdate());
			res.setApprEdate(res.getPlanEdate());
			res.setApprMinute(res.getPlanMinute());
			res.setUpdateDate(new Date());
			res.setUpdateId("rega appr");
			workDayResultRepo.save(res);
			
		}
		List<WtmWorkDayResult> taa = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), WtmApplService.TIME_TYPE_TAA, calendar.getYmd(), calendar.getYmd());
		for(WtmWorkDayResult res : taa) {
			if(res.getApprSdate() == null && res.getPlanSdate() != null) {
				res.setApprSdate(res.getPlanSdate());
				res.setApprEdate(res.getPlanEdate());
				res.setApprMinute(res.getPlanMinute());
				res.setUpdateDate(new Date());
				res.setUpdateId("taa appr");
				workDayResultRepo.save(res);
			}
		}
		
		/**
		 * 외출 복귀 데이터 재계산
		 */
		List<WtmWorkDayResult> gobackResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), WtmApplService.TIME_TYPE_GOBACK, calendar.getYmd(), calendar.getYmd());
		for(WtmWorkDayResult gobackResult : gobackResults) {

			Date calcSdate = calcService.WorkTimeCalcApprDate(gobackResult.getPlanSdate(), gobackResult.getPlanSdate(), flexStdMgr.getUnitMinute(), "S");
			Date calcEdate = calcService.WorkTimeCalcApprDate(gobackResult.getPlanEdate(), gobackResult.getPlanEdate(), flexStdMgr.getUnitMinute(), "E");
			//gobackResult.setApprSdate(gobackResult.getPlanSdate());
			//gobackResult.setApprEdate(gobackResult.getPlanEdate());
			this.addWtmDayResultInBaseTimeType(gobackResult.getTenantId()
												 , gobackResult.getEnterCd()
												 , gobackResult.getYmd()
												 , gobackResult.getSabun()
												 , gobackResult.getTimeTypeCd()
												 ,""
												 , calcSdate
												 , calcEdate
												 , null
												 , "goback reset timeblock"
												 , false);
			
			gobackResult.setApprSdate(calcSdate);
			gobackResult.setApprEdate(calcEdate);
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			int apprMinute = calcService.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute());
			gobackResult.setApprMinute(apprMinute);
			gobackResult.setUpdateDate(new Date());
			gobackResult.setUpdateId("goback appr");
			workDayResultRepo.save(gobackResult);
		}
		
	}
	
	@Transactional
	protected WtmWorkDayResult saveWorkDayResult(WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr, WtmWorkDayResult result, Long tenantId, String enterCd, String ymd, String sabun, String timeTypeCd, Date planSdate, Date planEdate, Date apprSdate, Date apprEdate, Long applId, String taaCd) {

		logger.debug("save ymd : " + ymd);
		logger.debug("save timeTypeCd : " + timeTypeCd);
		logger.debug("save planSdate : " + planSdate);
		logger.debug("save planEdate : " + planEdate);
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		if(result == null) {
			result = new WtmWorkDayResult();
			result.setTenantId(tenantId);
			result.setEnterCd(enterCd);
			result.setSabun(sabun);
			result.setYmd(ymd);
			result.setTimeTypeCd(timeTypeCd);
			
		} 
		result.setApplId(applId);
		result.setTaaCd(taaCd);
		if(planSdate != null) {
			result.setPlanSdate(planSdate);
		}
		if(planEdate != null) {
			result.setPlanEdate(planEdate);
		}
		Date calcSdate = result.getPlanSdate();
		Date calcEdate = result.getPlanEdate();

		if(calcSdate != null && calcEdate != null) {
			try {
				if(timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) && flexStdMgr.getWorkShm() != null && !"".equals(flexStdMgr.getWorkShm()) && flexStdMgr.getWorkEhm() != null && !"".equals(flexStdMgr.getWorkEhm())) {
					// 20200803 선택근무제가 아니면 근무시각 옵션이 비활성화됨. 그리고 선근제도 근무제한이 없을수있음.
					Date limitSdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkShm());
					Date limitEdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkEhm());
				
					if(limitSdate.compareTo(limitEdate) > 0) {
						logger.debug("제한시간 셋팅이 종료시간 보다 시작시간이 늦을 경우 종료시간을 1일 더해서 다음날로 만든다. sHm : " + flexStdMgr.getWorkShm() + " eHm : " + flexStdMgr.getWorkEhm());
						Calendar cal = Calendar.getInstance();
						cal.setTime(limitEdate);
						cal.add(Calendar.DATE, 1);
						limitEdate = cal.getTime();
					}
	
					if(calcSdate.compareTo(limitSdate) < 0) {
						logger.debug("시작일 근무 제한 시간 적용. sDate : " + calcSdate + " limitSdate : " + limitSdate);
						calcSdate = limitSdate;
					}
	//						
	//						if(sDate.compareTo(limitEdate) >= 0) {
	//							logger.debug("근무 제한 시간보다 이후 시간입니다.");
	//							return 0;
	//						}
					
					if(calcEdate.compareTo(limitEdate) > 0) {
						logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
						calcEdate = limitEdate;
					}
					
				}
	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(calcSdate.compareTo(calcEdate) >= 0) {
				return null;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			int apprMinute = calcService.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute());
			int breakMinute = 0;
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
				breakMinute = calcService.getBreakMinuteIfBreakTimeMGR(calcSdate, calcEdate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : apprMinute " + apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : breakMinute " + breakMinute);
				apprMinute = apprMinute - breakMinute;
				breakMinute = 0;
			}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
				breakMinute = calcService.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : apprMinute " + apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : breakMinute " + breakMinute);
			//}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			}
		
			result.setPlanMinute(apprMinute);
		}
		

		if(apprSdate != null) {
			result.setApprSdate(apprSdate);
		}
		if(apprEdate != null) {
			result.setApprEdate(apprEdate);
		} 
		calcSdate = result.getApprSdate();
		calcEdate = result.getApprEdate();
		if(calcSdate != null && calcEdate != null) {
			try {
				if(timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) && flexStdMgr.getWorkShm() != null && !"".equals(flexStdMgr.getWorkShm()) && flexStdMgr.getWorkEhm() != null && !"".equals(flexStdMgr.getWorkEhm())) {
					// 20200803 선택근무제가 아니면 근무시각 옵션이 비활성화됨. 그리고 선근제도 근무제한이 없을수있음.
					Date limitSdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkShm());
					Date limitEdate = ymdhm.parse(result.getYmd()+flexStdMgr.getWorkEhm());
				
					if(limitSdate.compareTo(limitEdate) > 0) {
						logger.debug("제한시간 셋팅이 종료시간 보다 시작시간이 늦을 경우 종료시간을 1일 더해서 다음날로 만든다. sHm : " + flexStdMgr.getWorkShm() + " eHm : " + flexStdMgr.getWorkEhm());
						Calendar cal = Calendar.getInstance();
						cal.setTime(limitEdate);
						cal.add(Calendar.DATE, 1);
						limitEdate = cal.getTime();
					}
	
					if(calcSdate.compareTo(limitSdate) < 0) {
						logger.debug("시작일 근무 제한 시간 적용. sDate : " + calcSdate + " limitSdate : " + limitSdate);
						calcSdate = limitSdate;
					}
	//						
	//						if(sDate.compareTo(limitEdate) >= 0) {
	//							logger.debug("근무 제한 시간보다 이후 시간입니다.");
	//							return 0;
	//						}
					
					if(calcEdate.compareTo(limitEdate) > 0) {
						logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
						calcEdate = limitEdate;
					}
				}
	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if(calcSdate.compareTo(calcEdate) >= 0) {
				return null;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			int apprMinute = calcService.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute());
			int breakMinute = 0;
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
				breakMinute = calcService.getBreakMinuteIfBreakTimeMGR(calcSdate, calcEdate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : apprMinute " + apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : breakMinute " + breakMinute);
				apprMinute = apprMinute - breakMinute;
				breakMinute = 0;
			}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
				breakMinute = calcService.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : apprMinute " + apprMinute);
				logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : breakMinute " + breakMinute);
			//}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			}
		
			result.setApprMinute(apprMinute);
		}
		result.setUpdateDate(new Date());
		result.setUpdateId("new calcaApprDayReset"); 
		return workDayResultRepo.save(result);
	}
	/**
	 * 타각시간 기준으로 인정시간 계산
	 */
	@Override
	public void calcApprDayInfo2(WtmWorkCalendar calendar,WtmFlexibleStdMgr flexStdMgr,WtmTimeCdMgr timeCdMgr) {
		
		
		Long tenantId = calendar.getTenantId();
		String enterCd = calendar.getEnterCd();
		String sabun = calendar.getSabun();
		
		
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", calendar.getYmd());
		paramMap.put("eYmd", calendar.getYmd());
		paramMap.put("ymd", calendar.getYmd());
		
		
		List<String> timeTypeCd = new ArrayList<>();
		timeTypeCd.add(WtmApplService.TIME_TYPE_LLA);
		/*
		List<WtmWorkDayResult> rrr = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, calendar.getYmd(), calendar.getYmd());
		for(WtmWorkDayResult rr : rrr) {
			System.out.println("================================== " + rr.getTimeTypeCd());
		}
		*/
		ObjectMapper mapper = new ObjectMapper();
				 
		
				
		WtmTaaCode absenceTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_ABSENCE);
		//코어타임 사용 시 코어타임 필수여부에 따라 근무시간이 코어타임에 미치지 못하면 결근으로 본다.
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", absenceTaaCode.getTaaCd());
		paramMap.put("userId", "SYSTEM");
		//여기서 결근이 들어갈 경우 아래 출퇴근 타각이 모두 있을 때 조퇴처리가 될수 있다. 결근데이터가 있을 경우를 제외해줘야한다.
		try { logger.debug("2.출/퇴근 타각정보가 없을 경우 결근 데이터를 생성한다. " + mapper.writeValueAsString(paramMap) + " createDayResultByTimeTypeAndCheckRequireCoreTimeYn"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
		flexEmpMapper.createDayResultByTimeTypeAndCheckRequireCoreTimeYn(paramMap);
		
		logger.debug("calcApprDayInfo 4");
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_REGA);
		// 간주근무의 경우 출/퇴근 타각데이터를 계획 데이터로 생성해 준다.
		//List<WtmWorkDayResult> regaResult = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_REGA, calendar.getYmd(), calendar.getYmd());
		boolean isRega = false;	//간주근무 여부
		boolean isTaaWork = true; //근태일에 근무 가능 여부 일 경우 마감을 돌린당
		String taaCd = null;
		
		Date minPlanSdate_REGA = null;
		Date maxPlanEdate_REGA = null;
		

		Date minPlanSdate_BASE = null;
		Date maxPlanEdate_BASE = null;
		
		
		Date dayPlanSdate = null;
		Date dayPlanEdate = null;
		//외출복귀데이터를 따로 담아두자. 인정근무 계산할때 외복귀시간은 제외한다. 
		List<WtmWorkDayResult> gobackResults = new ArrayList<WtmWorkDayResult>();
		List<WtmWorkDayResult> dayResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, calendar.getYmd());
		for(WtmWorkDayResult r : dayResults) {
			if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_GOBACK)) {
				gobackResults.add(r);
			}
			//통상적으로 근태가 있는 날은 마감을 멈춘다. 
			//단 예외의 경우도 있다 아래에서 체크한다.
			if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) && r.getApprSdate() != null && r.getApprEdate() != null) {
				isTaaWork = false;
				taaCd = r.getTaaCd();
			}
			if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)) {
				isRega = true;
				//break;
			}
			//간주근무일때 출퇴근 타각을 생성하기 위함
			if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)) {
				if(minPlanSdate_REGA == null || minPlanSdate_REGA.compareTo(r.getPlanSdate()) > 0) {
					minPlanSdate_REGA = r.getPlanSdate();
					logger.debug("3. 간주근무의 경우 출근 타각데이터를 계획 데이터로 생성해 준다. minPlanSdate : " +  minPlanSdate_REGA); 
				}
				if(maxPlanEdate_REGA == null || maxPlanEdate_REGA.compareTo(r.getPlanEdate()) < 0 ) {
					maxPlanEdate_REGA = r.getPlanEdate();
					logger.debug("3. 간주근무의 경우 출근 타각데이터를 계획 데이터로 생성해 준다. maxPlanEdate : " +  maxPlanEdate_REGA); 
				}
			}
			
			if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
				if(minPlanSdate_BASE == null || minPlanSdate_BASE.compareTo(r.getPlanSdate()) > 0) {
					minPlanSdate_BASE = r.getPlanSdate();
				}
				if(maxPlanEdate_BASE == null || maxPlanEdate_BASE.compareTo(r.getPlanEdate()) < 0 ) {
					maxPlanEdate_BASE = r.getPlanEdate();
				}
			}
			
			//출근 자동 생성
			if(!flexStdMgr.getDayOpenType().equals("N") && calendar.getEntrySdate() == null
				&& (
						(flexStdMgr.getDayCloseType().equals("BASE") && r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE))
						|| (flexStdMgr.getDayCloseType().equals("OT") && (r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT)  || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT)  ) 
						    )
					)
				
			){
				if(r.getPlanSdate() != null) {
					if(dayPlanSdate == null || dayPlanSdate.compareTo(r.getPlanSdate()) > 0) {
						dayPlanSdate = r.getPlanSdate();
						logger.debug("4.(출근)타각 자동 업데이트. dayPlanSdate : " +  dayPlanSdate); 
					}
				}else {
					logger.debug("4.(출근)타각 자동 업데이트.  r.getPlanSdate() is null : " + r.getSabun() + " : " + r.getEnterCd() + " : " + r.getTimeTypeCd());
				}
			}
			
			//퇴근 자동 생성
			if(!flexStdMgr.getDayCloseType().equals("N")) {
				
				if(tenantId == 102) { //그린캐미컬 타각정보 강제로 계획시간으로 엎어버리기
					if(dayPlanSdate != null) {
						if( (flexStdMgr.getDayCloseType().equals("BASE") && r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE))
								|| (flexStdMgr.getDayCloseType().equals("OT") && (r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT)  || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) ))){
							
							if(dayPlanEdate == null || dayPlanEdate.compareTo(r.getPlanEdate()) < 0)  {
								if(calendar.getEntryEdate() != null && r.getPlanEdate().compareTo(calendar.getEntryEdate()) < 0) {
									dayPlanEdate = calendar.getEntryEdate();
								} else {
									dayPlanEdate = r.getPlanEdate();
								}
							}
						}
					}
				} else {
					if(calendar.getEntryEdate() == null
							&& ( (flexStdMgr.getDayCloseType().equals("BASE") && r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE))
							|| (flexStdMgr.getDayCloseType().equals("OT") && (r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)|| r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT)  || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT)							|| r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) )))){
						if(r.getPlanEdate() != null) {
							if(dayPlanEdate == null || dayPlanEdate.compareTo(r.getPlanEdate()) < 0) {
								dayPlanEdate = r.getPlanEdate();
								logger.debug("4.(퇴근)타각 자동 업데이트. dayPlanEdate : " +  dayPlanEdate);
							}
						}else {
							logger.debug("4.(퇴근)타각 자동 업데이트.  r.getPlanEdate() is null : " + r.getSabun() + " : " + r.getEnterCd() + " : " + r.getTimeTypeCd());
						}
					}
				}

			}
			
		} 
		
		if(!isTaaWork) {
			//완/부 선근제 이며
			WtmTaaCode taaCode = wtmTaaCodeRepository.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);
			if(flexStdMgr.getWorkTypeCd().startsWith("SELE_")) {
				if(taaCode.getRequestTypeCd().equals("D") && flexStdMgr.getTaaWorkYn().equals("Y")){
					isTaaWork = true;
				}else if(taaCode.getRequestTypeCd().equals("P") || taaCode.getRequestTypeCd().equals("A") || taaCode.getRequestTypeCd().equals("H")){
					isTaaWork = true;
				}
			}else {
				if(!"D".equals(taaCode.getRequestTypeCd())) {
					isTaaWork = true;
				}
			}
		}
		if(!isTaaWork) {
			return;
		}
		/**
		 * 간주근무의 경우 출퇴근 타각정보가 있으면 출퇴근 타각정보를 갱신하지 않는다. 
		 * 간주근무는 계획된 시각 모두 인정한다. 출퇴근 시간은 출퇴근 시간으로 인정근무를 생성해야할 경우에만 사용한다. NGV 케이스
		 */
		if(tenantId != 95) {
			if(isRega && calendar.getEntrySdate() == null && calendar.getEntryEdate() == null) {
				try { logger.debug("3. 간주근무의 경우 출/퇴근 타각데이터를 계획 데이터로 생성해 준다. " + mapper.writeValueAsString(paramMap) + " updateTimeTypePlanToEntryTimeByTenantIdAndEnterCdAndYmdBetweenAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				//flexEmpMapper.updateTimeTypePlanToEntryTimeByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
				calendar.setEntrySdate(minPlanSdate_REGA);
				calendar.setEntryStypeCd(WtmApplService.TIME_TYPE_REGA);
				calendar.setEntryEdate(maxPlanEdate_REGA);
				calendar.setEntryEtypeCd(WtmApplService.TIME_TYPE_REGA);
				workCalendarRepo.save(calendar);
			}else {
				try { logger.debug("3. 간주근무 없음." + mapper.writeValueAsString(paramMap) + " updateTimeTypePlanToEntryTimeByTenantIdAndEnterCdAndYmdBetweenAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
			}
		}
		
		// 출근시간 자동 여부 -- 출근이 자유인 경운 지각이 없다고 본다?? 일단 ㅋ
		// 출근시간 자동에 대해 일괄 업데이트 한다.
		// 어디까지인가? 조출 / 기본근무
		// 출근 타각데이터가 있는건 갱신하지 않는다.
		
		if(dayPlanSdate != null) {
			try { logger.debug("4.(출근)타각 자동 업데이트 : 출근 자동 여부에 따라 계획 시간을 출근 타각 정보로 업데이트 한다. " + mapper.writeValueAsString(paramMap) + " updateEntrySdateByTenantIdAndEnterCdAndYmdBetweenAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
			//flexEmpMapper.updateEntrySdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
			calendar.setEntrySdate(dayPlanSdate);
			calendar.setEntryStypeCd("AUTO");
			calendar = workCalendarRepo.save(calendar);
		}
		
		//출근타각이 있을 경우에만
		if(dayPlanEdate != null && calendar.getEntrySdate() != null) {
			// 퇴근 시간 자동 여부 (계획시간으로 )
			// 어디까지인가? 기본근무 / 연장
			// 퇴근 타각데이터가 있는건 갱신하지 않는다.
			try { logger.debug("5.(퇴근)타각 자동 업데이트 퇴 자동 여부에 따라 계획 시간을 퇴근 타각 정보로 업데이트 한다. " + mapper.writeValueAsString(paramMap) + "updateEntryEdateByTenantIdAndEnterCdAndYmdBetweenAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
			//flexEmpMapper.updateEntryEdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
			calendar.setEntryEdate(dayPlanEdate);
			calendar.setEntryEtypeCd("AUTO");
			calendar = workCalendarRepo.save(calendar);
		}
		// 출근 타각이 없을 경우
		// 출근 또는 출/퇴근 타각이 모두 없을 경우 무단결근
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", absenceTaaCode.getTaaCd());
		paramMap.put("userId", "SYSTEM");
		boolean isAbsence =  false;
		
		boolean isCreateAbsence = true;

		/*
		 * 선근제가 아니며 BASE정보가 없을 경우에는 결근으로 보지 않는다.
		 */
		//if(!flexStdMgr.getWorkTypeCd().startsWith("SELE_")) {
		if(!flexStdMgr.getUnplannedYn().equals("Y")) {
			if(minPlanSdate_BASE == null) {
				isCreateAbsence = false; 
			}
		}
		
		if(isCreateAbsence && calendar.getEntrySdate() == null && calendar.getEntryEdate() == null && timeCdMgr.getAbsenceChkYn().equals("Y")) {
			//try { logger.debug("6. ABSENCE_CHK_YN = Y 일때 출/퇴근 타각 정보가 없을 경우 결근 데이터 생성 " + mapper.writeValueAsString(paramMap) + "createDayResultByTimeTypeAndEntryDateIsNull"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
			logger.debug("6. ABSENCE_CHK_YN = Y 일때 출/퇴근 타각 정보가 없을 경우 결근 데이터 생성 " );
			//flexEmpMapper.createDayResultByTimeTypeAndEntryDateIsNull(paramMap);
			WtmWorkDayResult laaResult = new WtmWorkDayResult();
			laaResult.setTenantId(calendar.getTenantId());
			laaResult.setEnterCd(calendar.getEnterCd());
			laaResult.setYmd(calendar.getYmd());
			laaResult.setSabun(calendar.getSabun());
			laaResult.setApplId(null);
			laaResult.setTimeTypeCd(WtmApplService.TIME_TYPE_LLA);
			laaResult.setTaaCd(absenceTaaCode.getTaaCd());
			laaResult.setUpdateId(WtmApplService.TIME_TYPE_LLA + absenceTaaCode.getTaaCd());
			workDayResultRepo.save(laaResult);
			isAbsence = true;
		}
		
		//결근이면 이후꺼는 안타도 될듯 
		if(!isAbsence) {
			//paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
			//paramMap.put("taaCd", leaveTaaCode.getTaaCd());
			//BASE 가 없으면 조퇴도 만들지 않는다. 
			if(timeCdMgr.getLateChkYn().equals("Y") && calendar.getEntrySdate() != null && calendar.getEntryEdate() == null
					&& minPlanSdate_BASE != null && maxPlanEdate_BASE != null
					) {
				WtmTaaCode leaveTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LEAVE);
				try { logger.debug("7. LATE_CHK_YN = Y  출근 데이터는 있고 퇴근 타각이 없을 경우 조퇴 (시/종 정보 없이 생성) " + mapper.writeValueAsString(paramMap) + "createDayResultByTimeTypeAndEntrtEdateIsNull"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				// 출근 데이터는 있고 퇴근 타각이 없을 경우 조퇴 (시/종 정보 없이 생성)
				//flexEmpMapper.createDayResultByTimeTypeAndEntrtEdateIsNull(paramMap);
				WtmWorkDayResult laaResult = new WtmWorkDayResult();
				laaResult.setTenantId(calendar.getTenantId());
				laaResult.setEnterCd(calendar.getEnterCd());
				laaResult.setYmd(calendar.getYmd());
				laaResult.setSabun(calendar.getSabun());
				laaResult.setApplId(null);
				laaResult.setTimeTypeCd(WtmApplService.TIME_TYPE_LLA);
				laaResult.setTaaCd(leaveTaaCode.getTaaCd());
				laaResult.setUpdateId(WtmApplService.TIME_TYPE_LLA + leaveTaaCode.getTaaCd());
				workDayResultRepo.save(laaResult);
				
			}
			
			// 계획 시작 시간보다 인정시작시간이 늦을 경우 BASE중에 
			// 지각 데이터 생성
			WtmTaaCode lateTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LATE);
			paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
			paramMap.put("taaCd", lateTaaCode.getTaaCd());
			try { 
				logger.debug("11. BASE중에 계획 시작 시간보다 인정시작시간이 늦을 경우 지각 데이터를 생성한다  " + mapper.writeValueAsString(paramMap) + "createDayResultByTimeTypeAndPlanSdateLessThanApprSdate"); 
			
				//여기서 지각데이터 생성이 되다안되다해서 select를 한번찍어보자
				/*
				Map<String, Object> temp = flexEmpMapper.getTemporaryWorkResult(paramMap);
				if(temp != null) {
					logger.debug("11-1. 지각 데이터 조회  " + mapper.writeValueAsString(temp)); 
				}
				*/
			} catch (JsonProcessingException e) {	
				logger.debug("11-4. 지각 데이터 조회 exception " + e.getMessage()); 
				e.printStackTrace();	
			}
			logger.debug("timeCdMgr.getLateChkYn() : " + timeCdMgr.getLateChkYn() + " 가 Y면 지각 데이터 체크 ");
			if(timeCdMgr.getLateChkYn().equalsIgnoreCase("Y")) {
				dayResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, calendar.getYmd());
				if(dayResults != null && dayResults.size() > 0) {
					boolean isCreateLate = true;
					Date minSdate = null;
					Date minEntrySdate = null;
				
					for(WtmWorkDayResult result : dayResults) {
						if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_LLA)
								&& result.getTaaCd().equals(lateTaaCode.getTaaCd())
								) {
							logger.debug("result.getTimeTypeCd() : " + result.getTimeTypeCd() + " / result.getTaaCd() " + result.getTaaCd() + " 인 데이터가 있는 날은 생성하지 않는다.");
							isCreateLate = false;
						}
						//BASE중 젤 작은 계획시간을 찾는다. 
						if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE) ) {
							if(minSdate == null) {
								minSdate = result.getPlanSdate();
							}else {
								if(minSdate.compareTo(result.getPlanSdate()) > 0) {
									minSdate = result.getPlanSdate();
								}
							}
							
						}
						
						//REGA중 젤 작은 계획시간을 찾는다. 
						//오전 출장일 경우 출근시간으로 보자
						if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) ) {
							if(minEntrySdate == null) {
								minEntrySdate = result.getPlanSdate();
							}else {
								if(minEntrySdate.compareTo(result.getPlanSdate()) > 0) {
									minEntrySdate = result.getPlanSdate();
								}
							}
							
						}
					}
					logger.debug("isCreateLate : " + isCreateLate);
					logger.debug("minSdate : " + minSdate);
					logger.debug("minEntrySdate : " + minEntrySdate);
					logger.debug("calendar.getEntrySdate() : " + calendar.getEntrySdate());
					if(minEntrySdate == null || minEntrySdate.compareTo(calendar.getEntrySdate()) > 0 ) {
						minEntrySdate = calendar.getEntrySdate();
					}
					 
					if(isCreateLate && minSdate != null && minEntrySdate != null) {
						if(minSdate.compareTo(minEntrySdate) < 0) {
							logger.debug("출근 타각 시간이 계획시간 보다 늦으면 지각!");
							WtmWorkDayResult lateResult = new WtmWorkDayResult();
							lateResult.setTenantId(tenantId);
							lateResult.setEnterCd(enterCd);
							lateResult.setYmd(calendar.getYmd());
							lateResult.setSabun(sabun);
							lateResult.setTimeTypeCd(WtmApplService.TIME_TYPE_LLA);
							lateResult.setTaaCd(lateTaaCode.getTaaCd());
							Date calcSdate = calcService.WorkTimeCalcApprDate(minSdate, minSdate, flexStdMgr.getUnitMinute(), "S");
							Date calcEdate = calcService.WorkTimeCalcApprDate(minEntrySdate, minEntrySdate, flexStdMgr.getUnitMinute(), "E");
	
							lateResult.setPlanSdate(calcSdate);
							lateResult.setPlanEdate(calcEdate);
							lateResult.setApprSdate(calcSdate);
							lateResult.setApprEdate(calcEdate);
							SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
							
							int apprMinute = calcService.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute());
							lateResult.setPlanMinute(apprMinute);
							lateResult.setApprMinute(apprMinute);
							lateResult.setUpdateDate(new Date());
							lateResult.setUpdateId(sabun);
							logger.debug("출근 타각 시간이 계획시간 보다 늦으면 지각 여기 " + lateResult.toString());
							workDayResultRepo.save(lateResult);
							logger.debug("출근 타각 시간이 계획시간 보다 늦으면 지각 끝 " + lateResult.toString());
						}
					}
				}
				//int cnt = flexEmpMapper.createDayResultByTimeTypeAndPlanSdateLessThanApprSdate(paramMap);
				//logger.debug("11-2. 지각 데이터 조회 cnt  " + cnt);
			}
			/**
			 * 출퇴근 타각정보로 갱신의 경우 계획 타각시간 기준으로 계획을 변경하고간주근무 외출 / 복귀에 대한 데이터를 재생성한다. 
			 */
			calcaApprDayReset(calendar, flexStdMgr, timeCdMgr);
			
	//		paramMap.put("timeTypeCd", timeTypeCd);
			//소정근로시간의 경우 출퇴근 타각기록으로만 판단 >> 결근 데이터가 있는 날은 빼야한다.
			paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
			paramMap.put("taaCd", absenceTaaCode.getTaaCd());
	
			if(flexStdMgr.getWorkTypeCd().equals("SELE_F")) {
				try { logger.debug("9. APPLY_ENTRY_SDATE_YN / APPLY_ENTRY_EDATE_YN 여부에 따라 타각 시간을 계획시간으로 업데이트 한다. 그리고 인정시간을 다시 계산한다. 계획시간이 변경되었기 때문에 ", mapper.writeValueAsString(paramMap), "call P_WTM_WORK_DAY_RESULT_CREATE_F"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				calcService.P_WTM_WORK_DAY_RESULT_CREATE_F(tenantId, enterCd, sabun,  calendar.getYmd(), flexStdMgr, timeCdMgr, "P_WTM_WORK_DAY_RESULT_CREATE_F-"+sabun);
				
				/**
				 * 소정근로 시간 초과로 인해 소정근로시간 인정을 안할 경우에도 오티 나이트는 타각시간 내에 잇는 것은 인정해준다. 
				 */
				timeTypeCd = new ArrayList<>();
				timeTypeCd.add(WtmApplService.TIME_TYPE_OT);
				timeTypeCd.add(WtmApplService.TIME_TYPE_NIGHT);
				timeTypeCd.add(WtmApplService.TIME_TYPE_EARLY_OT);
				timeTypeCd.add(WtmApplService.TIME_TYPE_EARLY_NIGHT);
				
				List<WtmWorkDayResult> apprResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, calendar.getYmd(), calendar.getYmd());
				for(WtmWorkDayResult r : apprResults) {
					Date sDate = calcService.WorkTimeCalcApprDate(calendar.getEntrySdate(), r.getPlanSdate(), flexStdMgr.getUnitMinute(), "S");
					Date eDate = calcService.WorkTimeCalcApprDate(calendar.getEntryEdate(), r.getPlanEdate(), flexStdMgr.getUnitMinute(), "E");
					if(sDate.compareTo(eDate) < 0) {
						
						boolean isAppr = true;
						/**
						 *  구간내 외출 복귀 구간을 제외하자 
						 */
						if(gobackResults != null) {
							for(WtmWorkDayResult gbr : gobackResults) {

								logger.debug("gbr.getApprSdate() : " + gbr.getApprSdate());
								logger.debug("gbr.getApprEdate() : " + gbr.getApprEdate());
								logger.debug("sDate : " + sDate);
								logger.debug("eDate : " + eDate);
								
								if(gbr.getApprSdate().compareTo(eDate) == -1
										&& gbr.getApprEdate().compareTo(sDate) == 1) {
									
									//시종 시간이 외/복귀 시간에 완전 속해 있으면 인정시간을 계산하지 않는다. 
									if(gbr.getApprSdate().compareTo(sDate) < 1 
											&& gbr.getApprEdate().compareTo(eDate) > -1) {
										logger.debug("제외: " + gbr);
										isAppr = false;
									}
									//외출 복귀 내 속해 있으면.
									if(gbr.getApprEdate().compareTo(sDate) == 1) {
										sDate = gbr.getApprEdate();
									}
									if(gbr.getApprSdate().compareTo(eDate) == -1) {
										eDate = gbr.getApprSdate();
									}
								}
							}
									
						}
						logger.debug("isAppr : " + isAppr);

						if(isAppr) {	
							r.setApprSdate(sDate);
							r.setApprEdate(eDate);
							
							Map<String, Object> calcMap = calcService.calcApprMinute(sDate, eDate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
							int apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
							int breakMinute = Integer.parseInt(calcMap.get("breakMinute")+"");
							r.setApprMinute(apprMinute);
							
							r.setUpdateId("SELE_F_OT_NIGHT");
							workDayResultRepo.save(r);
						}
					}
				}
			}
			else {
				try { logger.debug("8. 결근 데이터를 제외하고 타각 시간으로 계획 시간들의 인정시간을 만들어 준다. " + mapper.writeValueAsString(paramMap) + "updateApprDatetimeByYmdAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				//결근을 제외한 day result의 모든 계획데이터를 인정데이터로 만들어 준다. 
				//flexEmpMapper.updateApprDatetimeByYmdAndSabun(paramMap);
				List<WtmWorkDayResult> apprResults = workDayResultRepo.findBytenantIdAndEnterCdAndYmdAndSabunNotInTimeTypeCdAndTaaCd(calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), WtmApplService.TIME_TYPE_LLA, absenceTaaCode.getTaaCd(), calendar.getSabun());
				for(WtmWorkDayResult r : apprResults) {
					Date sDate = calcService.WorkTimeCalcApprDate(calendar.getEntrySdate(), r.getPlanSdate(), flexStdMgr.getUnitMinute(), "S");
					Date eDate = calcService.WorkTimeCalcApprDate(calendar.getEntryEdate(), r.getPlanEdate(), flexStdMgr.getUnitMinute(), "E");
					if(sDate.compareTo(eDate) < 0) {
						boolean isAppr = true;
						/**
						 *  구간내 외출 복귀 구간을 제외하자 
						 */
						if(gobackResults != null) {
							for(WtmWorkDayResult gbr : gobackResults) {

								logger.debug("gbr.getApprSdate() : " + gbr.getApprSdate());
								logger.debug("gbr.getApprEdate() : " + gbr.getApprEdate());
								logger.debug("sDate : " + sDate);
								logger.debug("eDate : " + eDate);
								
								if(gbr.getApprSdate().compareTo(eDate) == -1
										&& gbr.getApprEdate().compareTo(sDate) == 1) {
									
									//시종 시간이 외/복귀 시간에 완전 속해 있으면 인정시간을 계산하지 않는다. 
									if(gbr.getApprSdate().compareTo(sDate) != -1 
											&& gbr.getApprEdate().compareTo(eDate) > -1) {
										isAppr = false;
									}
									//외출 복귀 내 속해 있으면.
									if(gbr.getApprEdate().compareTo(sDate) == 1) {
										sDate = gbr.getApprEdate();
									}
									if(gbr.getApprSdate().compareTo(eDate) == -1) {
										eDate = gbr.getApprSdate();
									}
								}
							}
									
						}
						logger.debug("isAppr : " + isAppr);
						if(isAppr) {	
							
							r.setApprSdate(sDate);
							r.setApprEdate(eDate);
							
							Map<String, Object> calcMap = calcService.calcApprMinute(sDate, eDate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
							int apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
							int breakMinute = Integer.parseInt(calcMap.get("breakMinute")+"");
							r.setApprMinute(apprMinute);
							
							r.setUpdateId("findBytenantIdAndEnterCdAndYmdAndSabunNotInTimeTypeCdAndTaaCd");
							workDayResultRepo.save(r);
							
						}
					}
				}
			}
			
			
			
			//if("BRS".equals(enterCd) || "LSG".equals(enterCd) || "1000".equals(enterCd)) {
				// 20200518 브로제랑 ls글로벌만 완전선근 사용중 ngv
				//시작일 타각 데이터 기준 옵션에 해당하는 내용을 인정시간을 다시 업데이트 하자
				//계획이 있지만 타각데이터로 인정하는 케이스는 완전선근제 옵션에만 있다. 기억하자 까묵지마라.
				//unplanned 가Y이면 어카지? BASE 데이터가 없을텐데.. resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt 쪼 밑에서 하고 있다
			//	try { logger.debug("9. APPLY_ENTRY_SDATE_YN / APPLY_ENTRY_EDATE_YN 여부에 따라 타각 시간을 계획시간으로 업데이트 한다. 그리고 인정시간을 다시 계산한다. 계획시간이 변경되었기 때문에 ", mapper.writeValueAsString(paramMap), "call P_WTM_WORK_DAY_RESULT_CREATE_F"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				//flexEmpMapper.calcFlexApplyEntryDatetimeByFlexibleEmpId(paramMap);
			//	calcService.P_WTM_WORK_DAY_RESULT_CREATE_F(tenantId, enterCd, sabun, calendar.getYmd(), flexStdMgr, timeCdMgr, sabun);
			//}
	
			//결근은 최상단에서 제외
			if(calendar.getEntrySdate() != null && calendar.getEntryEdate() != null && timeCdMgr.getLeaveChkYn().equals("Y")) {
				
				dayResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, calendar.getYmd());
				//Date maxPlanEdate = null;
				if(dayResults != null && dayResults.size() > 0) {
					for(WtmWorkDayResult r : dayResults) {
						//기본근무 사이에 올 수 있는 근무는 시간단위 연차와 반차, 대체휴일, 간주근무, 음? OT빼고 다? ㅡㅡ 연차나 출장 교육의 경우 사전 벨리데이션에서 걸러진다고 보자 여기선 테트리스
						//dayResults 에는 OT가 있어선 안된다.. 넘어오는 기본데이터는 OT중복되어서 작성할 수 없기때문인다 오케?
					
						if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
							if(minPlanSdate_BASE == null || minPlanSdate_BASE.compareTo(r.getPlanSdate()) > 0) {
								minPlanSdate_BASE = r.getPlanSdate();
							}
							if(maxPlanEdate_BASE == null || maxPlanEdate_BASE.compareTo(r.getPlanEdate()) < 0 ) {
								maxPlanEdate_BASE = r.getPlanEdate();
							}
						}
						
					}
				}
				logger.debug("10. ***********************  " + maxPlanEdate_BASE + ",  " + calendar.getEntryEdate());
				if(minPlanSdate_BASE != null && !minPlanSdate_BASE.equals("") && maxPlanEdate_BASE != null  && !maxPlanEdate_BASE.equals("")  && maxPlanEdate_BASE.compareTo(calendar.getEntryEdate()) > 0) {
					
				
					WtmTaaCode leaveTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LEAVE);
					// 이곳은 출/퇴근 타각데이터가 있는 사람에 한한다.. 
					// 계획 종료 시간 보다 인정종료시간이 빠를 경우 BASE중에 
					// 조퇴 데이터 생성
					paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
					paramMap.put("taaCd", leaveTaaCode.getTaaCd());
					try { logger.debug("10. BASE중에 계획 종료 시간 보다 인정종료시간이 빠를 경우 조퇴 데이터를 생성한다  " + mapper.writeValueAsString(paramMap) + "createDayResultByTimeTypeAndApprEdateLessThanPlanEdate"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
					//flexEmpMapper.createDayResultByTimeTypeAndApprEdateLessThanPlanEdate(paramMap);
					WtmWorkDayResult laaResult = new WtmWorkDayResult();
					laaResult.setTenantId(calendar.getTenantId());
					laaResult.setEnterCd(calendar.getEnterCd());
					laaResult.setYmd(calendar.getYmd());
					laaResult.setSabun(calendar.getSabun());
					laaResult.setApplId(null);
					laaResult.setTimeTypeCd(WtmApplService.TIME_TYPE_LLA);
					laaResult.setTaaCd(leaveTaaCode.getTaaCd());
					laaResult.setUpdateId(WtmApplService.TIME_TYPE_LLA + leaveTaaCode.getTaaCd());
					workDayResultRepo.save(laaResult);
					
				}
			}
			
			
			
			//고정OT 일괄소진의 경우 고정 OT데이터를 삭제후 다시 만들어 준다.
			//근무 기간 내에 고정 OT정보를 확인부터 하자.
			//고정OT 일괄소진의 경우 계획데이터만 있을 수 없다 마감시 인정 시간을 바로 산정한다. 
			/* 위에서 일괄로 만든다.. 
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
				try { logger.debug("12. BREAK_TYPE_CD가 MGR인것만 APPR_MINUTE 계산 (지각 데이터는 생성 시 인정 분을 만들기 때문에 제외한다.)  " + mapper.writeValueAsString(paramMap) + "updateApprMinuteByYmdAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				// BREAK_TYPE_CD가 MGR인것만 계산
				flexEmpMapper.updateApprMinuteByYmdAndSabun(paramMap);
			}
			
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
				try { logger.debug("13. BREAK_TYPE_CD가 TIME인것만 APPR_MINUTE 계산 (지각 데이터는 생성 시 인정 분을 만들기 때문에 제외한다.)  " + mapper.writeValueAsString(paramMap) + "updateTimeTypeApprMinuteByYmdAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				// BREAK_TYPE_CD가 TIME인것만 계산
				flexEmpMapper.updateTimeTypeApprMinuteByYmdAndSabun(paramMap);
			}
			
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
				try { logger.debug("14. BREAK_TYPE_CD가 TIMEFIX인것만 APPR_MINUTE 계산 (지각 데이터는 생성 시 인정 분을 만들기 때문에 제외한다.)  " + mapper.writeValueAsString(paramMap) + "updateTimeFixTypeApprMinuteByYmdAndSabun"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				// BREAK_TYPE_CD가 TIMEFIX인것만 계산
				flexEmpMapper.updateTimeFixTypeApprMinuteByYmdAndSabun(paramMap);
			}
			*/
			
			
			/**
			 * 고정 OT 일괄 소진에 대한 부분
			 */
			if(flexStdMgr.getDefaultWorkUseYn().equalsIgnoreCase("Y") && flexStdMgr.getFixotUseType().equalsIgnoreCase("ALL")) {
	
				//List<WtmFlexibleEmp> emps = flexEmpRepo.findAllTypeFixotByTenantIdAndEnterCdAndSabunAndSymdAndEymdAnd(tenantId, enterCd, sabun, calendar.getYmd(), calendar.getYmd());
				logger.debug("15. 고정 OT 일괄 소진에 대한 부분 & no plan 케이스  " ); 
				//if(emps != null && emps.size() >0) {
				//	for( WtmFlexibleEmp emp : emps) {
						//paramMap.put("flexibleEmpId", emp.getFlexibleEmpId());
						//flexEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(paramMap);
						calcService.P_WTM_WORK_DAY_RESULT_CREATE_N(calendar, flexStdMgr, timeCdMgr, tenantId, enterCd, sabun, calendar.getYmd(), 0, "P_WTM_WORK_DAY_RESULT_CREATE_N-" + sabun);
				//	}
				//}
			}
			
			
			/**
			 * Time타입 휴게시간 일 경우만
			 * type이 plan이면 계획데이터를 생성한다. 
			 * 인정 데이터 생성을 위함
			 */
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
				try { logger.debug("16. Time타입 휴게시간 일 경우만 / type이 plan이면 계획데이터를 생성한다.  인정 데이터 생성을 위함  " + mapper.writeValueAsString(paramMap) + "call P_WTM_WORK_DAY_RESULT_TIME_C"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				
				//찢어져서 생긴 데이터들을 합산해서 만들어준다. 
				List<String> timeTypeCds = new ArrayList<String>();
				timeTypeCds.add(WtmApplService.TIME_TYPE_BASE);
				timeTypeCds.add(WtmApplService.TIME_TYPE_FIXOT);
				timeTypeCds.add(WtmApplService.TIME_TYPE_OT);
				timeTypeCds.add(WtmApplService.TIME_TYPE_NIGHT);
				timeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
				timeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_NIGHT);
				List<WtmWorkDayResult> timeTypeResult = workDayResultRepo.findByTimeTypeCdInAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(timeTypeCds, tenantId, enterCd, sabun, calendar.getYmd());
				
				int baseApprMinute = 0;
				int fixotApprMinute = 0;
				int otApprMinute = 0;
				int nightApprMinute = 0;

				int sumApprMinute = 0;
				List<String> timeTypeOrd = new ArrayList<String>();
				for(WtmWorkDayResult r : timeTypeResult) {
					
					if(timeTypeOrd.indexOf(r.getTimeTypeCd()) == -1 ) {
						timeTypeOrd.add(r.getTimeTypeCd());
					}
					
					if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
						baseApprMinute += (r.getApprMinute() != null)?r.getApprMinute():0;
					}else if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT)) {
						fixotApprMinute += (r.getApprMinute() != null)?r.getApprMinute():0;
					}else if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT)|| r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT)) {
						otApprMinute += (r.getApprMinute() != null)?r.getApprMinute():0;
					}else if(r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) || r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)) {
						nightApprMinute += (r.getApprMinute() != null)?r.getApprMinute():0;
					}
					
					sumApprMinute += (r.getApprMinute() != null)?r.getApprMinute():0;
				}
				int breakMinute = 0;
				if(sumApprMinute > 0) {
					breakMinute = calcService.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), sumApprMinute);
				}
				if(breakMinute > 0) {
					//총근무시간의 휴게시간을 구하고 이를.. 어떤 휴게시간으로 적용할지 선택한다. 
					//BASE와 OT가 있는 날일 경우 
					//breakMinute = 60분 이고BASE가 30분만 있다면 또 찢어야 한다. ㅠㅠ
					// 30분씩.. 만들어야한다.
					for(String ttc : timeTypeOrd) {
						if(ttc.equals(WtmApplService.TIME_TYPE_BASE) && baseApprMinute > 0) {
							String taaInfoCd = "BREAK";
							if(baseApprMinute < breakMinute) {
								int createMinute = baseApprMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = breakMinute - baseApprMinute;
							}else {
								int createMinute = breakMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = 0;
								break;
							}
						}else if(ttc.equals(WtmApplService.TIME_TYPE_FIXOT) && fixotApprMinute > 0) {
							
							String taaInfoCd = "BREAK_FIXOT";
							if(fixotApprMinute < breakMinute) {
								int createMinute = fixotApprMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = breakMinute - fixotApprMinute;
							}else {
								int createMinute = breakMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = 0;
								break;
							}
							
						}else if((ttc.equals(WtmApplService.TIME_TYPE_OT) || ttc.equals(WtmApplService.TIME_TYPE_EARLY_OT))  && otApprMinute > 0) {

							String taaInfoCd = "BREAK_OT";
							if(otApprMinute < breakMinute) {
								int createMinute = otApprMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = breakMinute - otApprMinute;
							}else {
								int createMinute = breakMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = 0;
								break;
							}
							
						}else if((ttc.equals(WtmApplService.TIME_TYPE_NIGHT) || ttc.equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)) && nightApprMinute > 0) {

							String taaInfoCd = "BREAK_NIGHT";
							if(nightApprMinute < breakMinute) {
								int createMinute = nightApprMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = breakMinute - nightApprMinute;
							}else {
								int createMinute = breakMinute;
								calcService.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, calendar.getYmd(), taaInfoCd, "APPR", createMinute, "createWorkDayResultForBreakTime");
								breakMinute = 0;
								break;
							}
							
						}
					}
				} 
				
			}
			
			/**
			 * 대체휴일 생성 
			 */
			List<Map<String, Object>> subsCreateTarget = otApplMapper.subsCreateTarget(paramMap);
			logger.debug("17. subsCreateTarget " + subsCreateTarget.size() + "subsCreateTarget"); 
			if(subsCreateTarget!=null && subsCreateTarget.size()>0) {
				logger.debug("calcApprDayInfo 18 ");
				
				List<WtmOtAppl> otAppls = new ArrayList<WtmOtAppl>();
				for(Map<String, Object> t : subsCreateTarget) {
					WtmOtAppl otAppl = otApplRepo.findById(Long.valueOf(t.get("otApplId").toString())).get();
					otAppls.add(otAppl);
				}
	
				try { logger.debug("18. applyOtSubs ","otAppls : " + mapper.writeValueAsString(otAppls) + "applyOtSubs"); } catch (JsonProcessingException e) {	e.printStackTrace();	}
				applyOtSubs(tenantId, enterCd, otAppls, false, "SYSTEM");
			}
		}
		/**
		 * 인정되지 않은 계획 시간의 경우 일괄로 apprMinute에 0으로 갱신한다.
		 */
		logger.debug("인정되지 않은 계획 시간의 경우 일괄로 apprMinute 0으로 갱신한다.");
		flexEmpMapper.updateWtmWorkDayResultByApprMinuteIsNull(paramMap);
		
	}
	
	
	@Override
	public void workClosed(Long tenantId, String enterCd, String sabun, String ymd, String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelWorkClosed(Long tenantId, String enterCd, String sabun, String ymd, String userId) {
		// TODO Auto-generated method stub
		
	}
	
	@Autowired
	WtmWorkteamMgrRepository wtmWorkteamMgrRepo;
	
	@Autowired
	WtmWorkteamEmpRepository wtmWorkteamEmpRepo;
	
	
	@Transactional
	@Override
	public void createWorkteamEmpData(Long tenantId, String enterCd, Long workteamMgrId, String userId) {
		//ID 채번 때문에 프로시저로 못했다..
		/*
		 * 중복된 근무 체크
		 * 기본근무제는 제외한 나머지는 다 중복되면 안된다. 근무조 및 유연근무제 기간과 중복되면 중복되는 데이터를 선 조정후 가능하다.
		 * 
		 */
		
		/*
		 * 근무조의 시작일 종료일이 바꼈을 경우
		 * WTM_WORK_FLEXIBLE_EMP 테이블의 근무일의 시작/종료일이 다를 경우 데이터를 다시 만들어야한다.
		 * 예를 들어 근무조의 종료일이 줄었을 경우 그 갭을 기본근무정보로 업데이트 하자
		 * 근무정보 업데이트는 반드시 지우면 안된다 실적데이터가 같이 있기때문에 BASE PLAN 정보를 갱신한다
		 * WTM_WORK_FLEXIBLE_EMP 의 시작 종료일을 겹치지 않게 재생성 한다.
			 
		*/
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("workteamMgrId", workteamMgrId);
		paramMap.put("updateId", userId);
		paramMap.put("pId", userId);
		paramMap.put("result", "");
		
		//1. FLEXIBLE_EMP에 없는 사람 부터 넣자
		/*
		 * 근무조에 등록된 대상자를 WTM_FLEXIBLE_EMP 테이블에 생성한다.
		 * 단 근무조 기간에 중복된 데이터가 있는 대상자는 제외하고 등록한다.
		 * 근무조, 유연근무제 모두 중복된 데이터로 한다. 근무조는 근무조 관리에서 시작종료를 관리하기 때문에 선 수정 작업 
		 * 기본근무제는 체크하지 않고 중복데이터 삽입 - 이후에 FLEXIBLE_EMP의 시작종료일 정리하는 업데이트문이 필요하다
		 */
		flexEmpMapper.createWorkteamOfWtmFlexibleEmp(paramMap);
		
		//case 단순히 근무조의 종료일만 증가했을 경우 1번의 케이스에서 빠졌을 것이다. 업데이트 해줘야한다. 기준아이디가 같으면 업데이트 하자
		//기간의 중복은 입력 자체에서 막아야한다!!!!!!!!!!!!!!!
		flexEmpMapper.updateWorkteamOfWtmFlexibleEmp(paramMap);
		
		flexEmpMapper.createWtmWorkteamOfWtmWorkDayResult(paramMap);
		 
		// 근무조 대상자 가져왓!
		//List<WtmWorkteamEmp> workteamEmpList = wtmWorkteamEmpRepo.findByWorkteamMgrId(workteamMgrId);
		// 패턴 가져왓!
		// 이 모든걸 프로시져로..
		
		// loop를 돌며 대상자 별로 calendar와 dayResult를 추가 갱신 하자
		
		//1-1. FLEXIBLE_EMP 중복된 근무 기간에서 FLEXIBLE_STD_MGR 에서 BASE_WORK_YN 이 N일 경우 데이터를 삽입하고 시작일 종료일 정리는 일괄적으로 한다.
		
		//2. FLEXIBLE_EMP에 있는데 근무조 기간 내에 정보가 중복되는 사람을 업데이트
		
		//WtmWorkteamMgr workteamMgr = wtmWorkteamMgrRepo.findById(workteamMgrId).get();
		
		
		
		//END FLEXIBLE_EMP의 시작일 종료일 정리
	}

	/**
	 * calendar id로 일근무표 조회(관리자용)
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param ymd
	 * @param timeCdMgrId
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getEmpDayResults(Long tenantId, String enterCd, String sabun, String ymd, Long timeCdMgrId) {
		List<Map<String, Object>> workDayResult = null;
		try {
			Map<String, Object> paramMap = new HashMap();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("ymd", ymd);
			paramMap.put("timeCdMgrId", timeCdMgrId);
			
			workDayResult = flexEmpMapper.getDayResultByYmd(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workDayResult;

	}
	
	/**
	 * 일근무 다건 저장(관리자용)
	 * @param tenantId
	 * @param enterCd
	 * @param workCalendarId
	 * @return
	 */
	@Override
	public int saveEmpDayResults(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) throws Exception {
		int cnt = 0;
		if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
			List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
			List<Map<String, Object>> day = new ArrayList();
			String retMsg = "";
																
			if(iList != null && iList.size() > 0) {
				for(Map<String, Object> l : iList) {
					
					l.put("shm", l.get("planSdate").toString().substring(8,12));
					l.put("ehm", l.get("planEdate").toString().substring(8,12));
					l.put("updateId", userId);
					l.put("tenantId", tenantId);
					l.put("enterCd", enterCd);
					
					Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), l, userId);
					
					l.put("planMinute", (Integer.parseInt(planMinuteMap.get("calcMinute")+"")));
					
					// 근무검증 start
					String timeTypeCd = l.get("timeTypeCd").toString();
					ReturnParam rp = new ReturnParam();
					Map<String, Object> chkMap = new HashMap();
					
					if("BASE".equals(timeTypeCd)) {
						/* 관리자는 일단 체크 하지 않는다. ....  .. . . . .  .
						Map<String, Object> result2 = flexEmpMapper.checkBaseWorktimeMgr(l);
						if(result2!=null && result2.get("isValid")!=null && result2.get("isValid").equals("0")) {
							retMsg = l.get("sabun").toString() + "," + l.get("ymd").toString() + ", "+ result2.get("totalWorktime").toString() + "시간의 소정근로시간을 넘을 수 없습니다.";
						}
						*/
					} else {
						// ot시간검증
						chkMap.put("ymd", l.get("ymd").toString());
						chkMap.put("otSdate", l.get("planSdate").toString());
						chkMap.put("otEdate", l.get("planEdate").toString());
//						rp = applService.validate( tenantId,  enterCd,  l.get("sabun").toString(),  timeTypeCd, chkMap);
//						if(rp.getStatus().equals("FAIL")) {
//							retMsg = l.get("sabun").toString() + "," + l.get("ymd").toString() + ", "+ rp.get("message").toString();
//						}
					}
					// 근무검증 end
					
					
					if(!"".equals(retMsg)) {
						// 오류내용 저장하기
						throw new RuntimeException(retMsg);
					} else {
										
						WtmWorkDayResult result = new WtmWorkDayResult();
						if(l.get("workDayResultId") != "") {
							result = workDayResultRepo.findByWorkDayResultId(Long.parseLong(l.get("workDayResultId").toString()));
						} else {
							result.setEnterCd(enterCd);
							result.setSabun(l.get("sabun").toString());
							result.setTenantId(tenantId);
							result.setTimeTypeCd(l.get("timeTypeCd").toString());
							result.setYmd(l.get("ymd").toString());
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
						
						result.setPlanSdate(sdf.parse(l.get("planSdate").toString()));
						result.setPlanEdate(sdf.parse(l.get("planEdate").toString()));
						result.setPlanMinute(Integer.parseInt(l.get("planMinute").toString()));
						result.setUpdateId(userId);	
						workDayResultRepo.save(result);	
						
						//오늘 이전인 경우만 돌려주기, 오늘은 일마감에서
						SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
						Date today = format1.parse(format1.format(new Date()));
						Date edate = format1.parse(l.get("planEdate").toString().substring(0, 8));
						
						if(today.compareTo(edate) > 0) {
							empService.calcApprDayInfo(tenantId, 
									enterCd, l.get("ymd").toString(),
									l.get("ymd").toString(), l.get("sabun").toString());
							
							// 근무검증
							// 원래 있던 자리
																
							// 문제가 없으면 근무계획시간 합산
							chkMap.put("tenantId", tenantId);
							chkMap.put("enterCd", enterCd);
							chkMap.put("sabun", l.get("sabun").toString());
							chkMap.put("symd", l.get("ymd").toString());
							chkMap.put("eymd", l.get("ymd").toString());
							chkMap.put("pId", userId);
							flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(chkMap);
						}
						cnt++;
						retMsg = ""; // 메시지초기화
					}
				}
			}
		}
		return cnt;
	}	
	
	@Override
	public List<Map<String, Object>> getFlexibleEmpWebList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		if(paramMap.containsKey("sYmd") && paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
			sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
		}
		
		List<String> auths = getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
		}
		
		List<Map<String, Object>> flexibleList = flexEmpMapper.getFlexibleEmpWebList(paramMap);
		
		return flexibleList;
	}

	/**
	 * BASE,OT,FIXOT,NIGHT 근무시간에 대해 add 로 들어오는 타임 구간을 잘라서 만들어 준다.
	 */
	@Transactional
	@Override
	public void addWtmDayResultInBaseTimeType(Long tenantId, String enterCd, String ymd, String sabun, String addTimeTypeCd, String addTaaCd,
			Date addSdate, Date addEdate, Long applId, String userId) {
		addWtmDayResultInBaseTimeType(tenantId, enterCd, ymd, sabun, addTimeTypeCd, addTaaCd, addSdate, addEdate, applId, userId, true);
	}
	@Transactional
	@Override
	public void addWtmDayResultInBaseTimeType(Long tenantId, String enterCd, String ymd, String sabun, String addTimeTypeCd, String addTaaCd,
			Date addSdate, Date addEdate, Long applId, String userId, boolean isAdd) {
		List<String> timeType = new ArrayList<String>();
		timeType.add(WtmApplService.TIME_TYPE_BASE);
		timeType.add(WtmApplService.TIME_TYPE_OT);
		timeType.add(WtmApplService.TIME_TYPE_FIXOT);
		timeType.add(WtmApplService.TIME_TYPE_NIGHT);
		List<WtmWorkDayResult> base = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeType, ymd, ymd);
		
		//List<WtmWorkDayResult> days = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);

		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		 
		Map<String, Object> pMap = new HashMap<>();
		pMap.put("tenantId", tenantId);
		pMap.put("enterCd", enterCd);
		pMap.put("sabun", sabun);
		pMap.put("ymd", ymd);
		logger.debug("********************** r addSdate : " + addSdate);
		logger.debug("********************** r addEdate : " + addEdate);
		for(WtmWorkDayResult r : base) {
			logger.debug("********************** r " + r.toString()); 
			//근무 계획 시작시간과 종료시간의 범위를 절대 벗어날수 없다. 그렇다 한다. ㅋ
			boolean isDelete = false;
			//시종시간이 동일하면 기본근무 계획시간을 지운다.
			if(r.getPlanSdate().compareTo(addSdate) == 0 && r.getPlanEdate().compareTo(addEdate) == 0) {
				logger.debug("********************** r IF 1"); 
				isDelete = true;
			//감싸고 있으면 지워야지..
			}else if(r.getPlanSdate().compareTo(addSdate) >= 0 && r.getPlanEdate().compareTo(addEdate) <= 0) {
					logger.debug("********************** r IF 1-1"); 
					isDelete = true;
			// 타켓 시간 8~11의 시간에서 휴게시간이 13~15시가 왔을 때는 패쓰 || 반대의케이스도 ㅋ
			}else if(r.getPlanEdate().compareTo(addSdate) <= 0 || r.getPlanSdate().compareTo(addEdate) >= 0) {
				logger.debug("********************** r IF 2");
				continue;
			//시작시간은 같지만 계획 종료 시간 보다 대체휴일종료 시간이 작을 경우
			}else if((r.getPlanSdate().compareTo(addSdate) > 0  || r.getPlanSdate().compareTo(addSdate) == 0) && r.getPlanEdate().compareTo(addEdate) > 0) {
				logger.debug("********************** r IF 3");
				r.setPlanSdate(addEdate); // 계획의 시작일은 휴일대체 종료로 변경한다
				 
			//종료시간은 같지만 계획 시작시간 보다 대체휴일시작시간이 클경우 && //시작시간만 중간에 껴있을 경우
			}else if(r.getPlanSdate().compareTo(addSdate) < 0 && (r.getPlanEdate().compareTo(addEdate) == 0 || r.getPlanEdate().compareTo(addEdate) < 0) ) {
				logger.debug("********************** r IF 4");
				r.setPlanEdate(addSdate); // 계획의 종료일을 휴일대체 시작일로 변경한다
			 
			//계회의 시종 시간 중간에!! 대체휴일 시종시간이 있을 경우! 거지같넹.. 앞에데이터는 수정하고 뒤에 데이터는 만들어줘야한다.. 
			}else if(r.getPlanSdate().compareTo(addSdate) < 0 && r.getPlanEdate().compareTo(addEdate) > 0) {
				logger.debug("********************** r IF 5");
				Date oriEdate = r.getPlanEdate();
				r.setPlanEdate(addSdate);
				
				WtmWorkDayResult addR = new WtmWorkDayResult();
				addR.setApplId(r.getApplId());
				addR.setTenantId(r.getTenantId());
				addR.setEnterCd(enterCd);
				addR.setYmd(r.getYmd());
				addR.setSabun(r.getSabun());
				addR.setPlanSdate(addEdate);
				addR.setPlanEdate(oriEdate);

				Map<String, Object> addMap = new HashMap<>();
				addMap.putAll(pMap);
				
				String shm = sdf.format(addEdate);
				String ehm = sdf.format(oriEdate); 
				addMap.put("shm", shm);
				addMap.put("ehm", ehm);
				Map<String, Object> addPlanMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, addMap, userId);
				addR.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
				addR.setTimeTypeCd(r.getTimeTypeCd());
				addR.setUpdateId(userId);

				addR.setApprSdate(null);
				addR.setApprEdate(null);
				addR.setApprMinute(null);
				workDayResultRepo.save(addR);
				
			}
			if(!isDelete) {
				String shm = sdf.format(r.getPlanSdate());
				String ehm = sdf.format(r.getPlanEdate()); 
				pMap.put("shm", shm);
				pMap.put("ehm", ehm);
				Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
				r.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
				
				r.setApprSdate(null);
				r.setApprEdate(null);
				r.setApprMinute(null);
				logger.debug("********************** r workDayResultRepo.save " + r.toString());
				workDayResultRepo.save(r);
			}else {
				workDayResultRepo.delete(r);
			}
		}
		
		//신규 타임 블럭 생성 여부에 따라 추가 한다.
		if(isAdd) {
			WtmWorkDayResult addDayResult = new WtmWorkDayResult();
			addDayResult.setApplId(applId);
			addDayResult.setTenantId(tenantId);
			addDayResult.setEnterCd(enterCd);
			addDayResult.setYmd(ymd);
			addDayResult.setSabun(sabun);
			addDayResult.setPlanSdate(addSdate);
			addDayResult.setPlanEdate(addEdate);
			
			Map<String, Object> addMap = new HashMap<>();
			addMap.putAll(pMap);
			
			String shm = sdf.format(addSdate);
			String ehm = sdf.format(addEdate); 
			addMap.put("shm", shm);
			addMap.put("ehm", ehm);
			Map<String, Object> addPlanMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, addMap, userId);
			addDayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
			addDayResult.setTimeTypeCd(addTimeTypeCd);
			addDayResult.setTaaCd(addTaaCd);
			addDayResult.setUpdateId(userId);
			
			addDayResult.setApprSdate((addTimeTypeCd.equals(WtmApplService.TIME_TYPE_REGA) || addTimeTypeCd.equals(WtmApplService.TIME_TYPE_TAA) )?addSdate:null);
			addDayResult.setApprEdate((addTimeTypeCd.equals(WtmApplService.TIME_TYPE_REGA) || addTimeTypeCd.equals(WtmApplService.TIME_TYPE_TAA) )?addEdate:null);
			addDayResult.setApprMinute((addTimeTypeCd.equals(WtmApplService.TIME_TYPE_REGA) || addTimeTypeCd.equals(WtmApplService.TIME_TYPE_TAA) )?Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""):null);
			logger.debug("********************** r addDayResult save " + addDayResult.toString());
			workDayResultRepo.save(addDayResult); 
		}
		workDayResultRepo.flush();
	}
	
	@Transactional
	@Override
	public void addApprWtmDayResultInBaseTimeType(Long tenantId, String enterCd, String ymd, String sabun, String addTimeTypeCd, String addTaaCd,
			Date addSdate, Date addEdate, Long applId, String userId, boolean isAdd) {
		List<String> timeType = new ArrayList<String>();
		timeType.add(WtmApplService.TIME_TYPE_BASE);
		timeType.add(WtmApplService.TIME_TYPE_OT);
		timeType.add(WtmApplService.TIME_TYPE_FIXOT);
		timeType.add(WtmApplService.TIME_TYPE_NIGHT);
		List<WtmWorkDayResult> base = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeType, ymd, ymd);
		
		//List<WtmWorkDayResult> days = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);

		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		 
		Map<String, Object> pMap = new HashMap<>();
		pMap.put("tenantId", tenantId);
		pMap.put("enterCd", enterCd);
		pMap.put("sabun", sabun);
		pMap.put("ymd", ymd);
		
		for(WtmWorkDayResult r : base) {
			logger.debug("********************** r " + r.toString()); 
			//근무 계획 시작시간과 종료시간의 범위를 절대 벗어날수 없다. 그렇다 한다. ㅋ
			if(r.getApprSdate() != null && r.getApprEdate() != null && !"".equals(r.getApprSdate()) && !"".equals(r.getApprEdate())) {
				boolean isDelete = false;
				//시종시간이 동일하면 기본근무 계획시간을 지운다.
				if(r.getApprSdate().compareTo(addSdate) == 0 && r.getApprEdate().compareTo(addEdate) == 0) {
					logger.debug("********************** r IF 1"); 
					isDelete = true;
				// 타켓 시간 8~11의 시간에서 휴게시간이 13~15시가 왔을 때는 패쓰 || 반대의케이스도 ㅋ
				}else if(r.getApprEdate().compareTo(addSdate) <= 0 || r.getApprSdate().compareTo(addEdate) >= 0) {
					logger.debug("********************** r IF 2");
					continue;
				//시작시간은 같지만 계획 종료 시간 보다 대체휴일종료 시간이 작을 경우
				}else if((r.getApprSdate().compareTo(addSdate) > 0  || r.getApprSdate().compareTo(addSdate) == 0) && r.getApprEdate().compareTo(addEdate) > 0) {
					logger.debug("********************** r IF 3");
					r.setPlanSdate(addEdate); // 계획의 시작일은 휴일대체 종료로 변경한다
					 
				//종료시간은 같지만 계획 시작시간 보다 대체휴일시작시간이 클경우 && //시작시간만 중간에 껴있을 경우
				}else if(r.getApprSdate().compareTo(addSdate) < 0 && (r.getApprEdate().compareTo(addEdate) == 0 || r.getApprEdate().compareTo(addEdate) < 0) ) {
					logger.debug("********************** r IF 4");
					r.setPlanEdate(addSdate); // 계획의 종료일을 휴일대체 시작일로 변경한다
				 
				//계회의 시종 시간 중간에!! 대체휴일 시종시간이 있을 경우! 거지같넹.. 앞에데이터는 수정하고 뒤에 데이터는 만들어줘야한다.. 
				}else if(r.getApprSdate().compareTo(addSdate) < 0 && r.getApprEdate().compareTo(addEdate) > 0) {
					logger.debug("********************** r IF 5");
					Date oriEdate = r.getApprEdate();
					r.setPlanEdate(addSdate);
					
					WtmWorkDayResult addR = new WtmWorkDayResult();
					addR.setApplId(r.getApplId());
					addR.setTenantId(r.getTenantId());
					addR.setEnterCd(enterCd);
					addR.setYmd(r.getYmd());
					addR.setSabun(r.getSabun());
					addR.setPlanSdate(addEdate);
					addR.setPlanEdate(oriEdate);
	
					Map<String, Object> addMap = new HashMap<>();
					addMap.putAll(pMap);
					
					String shm = sdf.format(addEdate);
					String ehm = sdf.format(oriEdate); 
					addMap.put("shm", shm);
					addMap.put("ehm", ehm);
					Map<String, Object> addPlanMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, addMap, userId);
					addR.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
					addR.setTimeTypeCd(r.getTimeTypeCd());
					addR.setUpdateId(userId);
	
					addR.setApprSdate(null);
					addR.setApprEdate(null);
					addR.setApprMinute(null);
					workDayResultRepo.save(addR);
					
				}
				
				if(!isDelete) {
					String shm = sdf.format(r.getApprSdate());
					String ehm = sdf.format(r.getApprEdate()); 
					pMap.put("shm", shm);
					pMap.put("ehm", ehm);
					Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
					r.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					
					r.setApprSdate(null);
					r.setApprEdate(null);
					r.setApprMinute(null);
					logger.debug("********************** r workDayResultRepo.save " + r.toString());
					workDayResultRepo.save(r);
				}else {
					workDayResultRepo.delete(r);
				}
			}
		}
		
		//신규 타임 블럭 생성 여부에 따라 추가 한다.
		if(isAdd) {
			WtmWorkDayResult addDayResult = new WtmWorkDayResult();
			addDayResult.setApplId(applId);
			addDayResult.setTenantId(tenantId);
			addDayResult.setEnterCd(enterCd);
			addDayResult.setYmd(ymd);
			addDayResult.setSabun(sabun);
			addDayResult.setPlanSdate(addSdate);
			addDayResult.setPlanEdate(addEdate);
			
			Map<String, Object> addMap = new HashMap<>();
			addMap.putAll(pMap);
			
			String shm = sdf.format(addSdate);
			String ehm = sdf.format(addEdate); 
			addMap.put("shm", shm);
			addMap.put("ehm", ehm);
			Map<String, Object> addPlanMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, addMap, userId);
			addDayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
			addDayResult.setTimeTypeCd(addTimeTypeCd);
			addDayResult.setTaaCd(addTaaCd);
			addDayResult.setUpdateId(userId);
			
			addDayResult.setApprSdate(null);
			addDayResult.setApprEdate(null);
			addDayResult.setApprMinute(null);
			logger.debug("********************** r addDayResult save " + addDayResult.toString());
			workDayResultRepo.save(addDayResult); 
		}
		
	}

	@Transactional
	@Override
	public void removeWtmDayResultInBaseTimeType(Long tenantId, String enterCd, String ymd, String sabun,
			String removeTimeTypeCd, String removeTaaCd, Date removeSdate, Date removeEdate, Long applId, String userId) {
		
		//if(otSubsAppls != null && otSubsAppls.size() > 0) {
			String currYmd = null;
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			Map<String, Map<String, Date>> resetBaseTime = new HashMap<String, Map<String, Date>>();
			//for(WtmOtSubsAppl otSubsAppl : otSubsAppls) {
				List<String> timeTypeCd = new ArrayList<>();
				timeTypeCd.add(WtmApplService.TIME_TYPE_BASE);
				timeTypeCd.add(WtmApplService.TIME_TYPE_SUBS); 
				timeTypeCd.add(WtmApplService.TIME_TYPE_TAA);
				timeTypeCd.add(WtmApplService.TIME_TYPE_REGA); 
				
				List<WtmWorkDayResult> workDayResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, ymd, ymd);
				 
				//Date sdate = otSubsAppl.getSubsSdate();
				//Date edate = otSubsAppl.getSubsEdate();
				 
				int cnt = 0;
				Boolean isPrev = null;
				for(WtmWorkDayResult res : workDayResults) {
					 
					if(( res.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) || res.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) || res.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) ) && res.getPlanSdate().compareTo(removeSdate) == 0 && res.getPlanEdate().compareTo(removeEdate) == 0) {
						if(cnt == 0) {
							//시작시간이 대체휴일이면 다음 데이터 여부를 판단하고 다음데이터가 SUBS BASE로 변경하자
							if(workDayResults.size() == (cnt+1) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) ) {
								//뒤에 데이터가 없으면
								logger.debug("workDayResults loop : " + cnt);
								res.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
								res.setTaaCd(null);	// base 수정시 근태코드 클리어
								res.setApplId(applId);
								res.setApprSdate(null);
								res.setApprEdate(null);
								res.setApprMinute(null);
								res.setUpdateId("removeWtmDayResultInBaseTimeType");
								workDayResultRepo.save(res);
								break;
							}else { 
								WtmWorkDayResult modiResult = workDayResults.get(cnt+1);
								modiResult.setPlanSdate(removeSdate);
								modiResult.setApplId(applId);
								
								workDayResultRepo.deleteById(res.getWorkDayResultId());
								workDayResultRepo.save(modiResult);
								break;
							}
						}else {
							// 삭제하려는 데이터면 이전 데이터가 SUBS 인지를 체크 한다.
							if(workDayResults.get(cnt-1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt-1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) || workDayResults.get(cnt-1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)) {
								isPrev = false;
							}else {
								isPrev = true;
							}
							// 삭제하려는 데이터가 마지막인지 확인하자
							if(workDayResults.size() == (cnt+1)) {
								if(isPrev) {
									//이전 데이터로 지우려는 데이터의 종료일로 바꿔주면 땡
									WtmWorkDayResult modiResult = workDayResults.get(cnt-1);
									modiResult.setPlanEdate(removeEdate);
									
									workDayResultRepo.deleteById(res.getWorkDayResultId());
									workDayResultRepo.save(modiResult);
									break;
								}else {
									// SUBS or TAA
									// SUBS(지우려는 데이터) -> BASE 로 변
									res.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
									//res.setApplId(applId);
									res.setTaaCd("");	// base 수정시 근태코드 클리어
									res.setApplId(applId);
									res.setApprSdate(null);
									res.setApprEdate(null);
									res.setApprMinute(null);
									res.setUpdateId("removeWtmDayResultInBaseTimeType2");
									
									workDayResultRepo.save(res);
									break;
								}
							}else {
								//마지막 데이터가 아니면 다음 데이터의 timeTypeCd를 확인하자
								if(workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)) {
									if(isPrev) { 
										//이전 데이터로 지우려는 데이터의 종료일로 바꿔주면 땡
										WtmWorkDayResult modiResult = workDayResults.get(cnt-1);
										modiResult.setPlanEdate(removeEdate);
										
										workDayResultRepo.deleteById(res.getWorkDayResultId());
										workDayResultRepo.save(modiResult);
										break;
									}else { 
										//SUBS or TAA
										// SUBS(지우려는 데이터) -> BASE 로 변
										//SUBS or TAA
										res.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
										res.setTaaCd(null);
										//res.setApplId(applId); 
										workDayResultRepo.save(res);
										break;
									}
								}else { 
									if(isPrev) { 
										//1. BASE
										//2. SUBS TAA
										//3. BASE 인 상황  1,2번을 보내드리고 3번으로 통합하자
										workDayResultRepo.deleteById(workDayResults.get(cnt-1).getWorkDayResultId());
										workDayResultRepo.deleteById(res.getWorkDayResultId());

										WtmWorkDayResult modiResult = workDayResults.get(cnt+1); 
										modiResult.setPlanSdate(workDayResults.get(cnt-1).getPlanSdate());  
										workDayResultRepo.save(modiResult);
										break;
									}else {
										//이후 데이터로 지우려는 데이터의 시작일로 바꿔주면 땡
										WtmWorkDayResult modiResult = workDayResults.get(cnt+1);
										modiResult.setPlanSdate(removeSdate); 
										workDayResultRepo.deleteById(res.getWorkDayResultId());
										workDayResultRepo.save(modiResult);
										break;
									}
									
								} 
							}
							
						}
						
						
					}
					cnt++;
				}
				
				//base minute 다시 계산
				List<WtmWorkDayResult> updatedResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, ymd, ymd);
				if(updatedResults!=null && updatedResults.size()>0) {
					SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
					
					for(WtmWorkDayResult r : updatedResults) {
						Map<String, Object> reCalc = new HashMap<>();
						reCalc.put("tenentId", tenantId);
						reCalc.put("enterCd", enterCd);
						reCalc.put("sabun", sabun);
						reCalc.put("ymd", r.getYmd());
						reCalc.put("shm", sdf.format(r.getPlanSdate()));
						reCalc.put("ehm", sdf.format(r.getPlanEdate()));
						Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
						
						r.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
						workDayResultRepo.save(r);
					}
				}
				 
			//}  		
				
	}
	
	@Override
	public List<Map<String, Object>> getFlexibleListForPlan(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		return flexEmpMapper.getFlexibleListForPlan(paramMap);
	}
	
	@Override
	public Map<String, Object> getFlexibleEmpForPlan(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// 탄근제를 제외한 근무제의 근무 계획 조회
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		Map<String, Object> flexibleEmp = null;
		List<Map<String, Object>> flexibleEmpList = flexEmpMapper.getFlexibleListForPlan(paramMap);
		if(flexibleEmpList!=null && flexibleEmpList.size()>0) {
			for(Map<String, Object> emp : flexibleEmpList) {
				if(emp.get("flexibleEmpId")!=null && !"".equals(emp.get("flexibleEmpId"))) {
					flexibleEmp = new HashMap<String, Object>();
					flexibleEmp.putAll(emp);
					
					List<String> holidays = new ArrayList<String>();
					//휴일 조회
					List<WtmWorkCalendar> calendars = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndHolidayYn(tenantId, enterCd, sabun, emp.get("sYmd").toString(), emp.get("eYmd").toString(), "Y");
					if(calendars!=null && calendars.size()>0) {
						for(WtmWorkCalendar c : calendars) {
							holidays.add(c.getYmd());
						}
					}
					flexibleEmp.put("holidays", holidays);
					
					List<Map<String, Object>> plans = flexEmpMapper.getPlanByFlexibleEmpId(paramMap);
					List<WtmDayWorkVO> dayWorks = getDayWorks(plans, userId);
					flexibleEmp.put("dayWorks", dayWorks);
				}
			}
		}
		
		return flexibleEmp;
	}
	
	@Override
	public Map<String, Object> getFlexibleApplDetForPlan(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// 탄근제의 근무 계획 조회
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		Map<String, Object> flexibleEmp = flexEmpMapper.getElasForPlan(paramMap);
		if(flexibleEmp!=null) {
			List<Map<String, Object>> plans = flexEmpMapper.getElasPlanByFlexibleApplId(paramMap);
			List<WtmDayWorkVO> dayWorks = getDayWorks(plans, userId);
			flexibleEmp.put("dayWorks", dayWorks);
			
			//평균 근무 시간 계산
			Map<String, Object> avgHourMap = flexEmpMapper.getElasAvgHour(paramMap);
			if(avgHourMap!=null) {
				flexibleEmp.put("avgHour", Double.parseDouble(avgHourMap.get("avgHour")+""));
			}
		}
		
		return flexibleEmp;
	}
	
	/**
	 * 유연근무 변경/취소 확인
	 * @param tenantId
	 * @param enterCd
	 * @param workCalendarId
	 * @return
	 */
	@Override
	public Map<String, Object> GetChangeChk(Map<String, Object> paramMap) {
		
		try {
			System.out.println("changeChk serviceImpl start");
			paramMap.put("retCode", "");
			paramMap.put("retMsg", "");
			
			flexEmpMapper.getChangeChk(paramMap);
			
			String retCode = paramMap.get("retCode").toString();
			String retMsg = paramMap.get("retMsg").toString();
			Long retId = Long.parseLong(paramMap.get("retId").toString());
			
			if("OK".equals(retCode)) {
				paramMap.put("retType", "END");
				// 체크 성공 반영하러 보내기
				paramMap.put("hisId", retId);
				paramMap = setChangeFlexible(paramMap);
			} else {
				paramMap.put("retType", "MSG");
				// 검증메시지가 있으면 메시지 호출
				flexEmpMapper.setChangeErrMsg(paramMap);
			}
			System.out.println("changeChk serviceImpl end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramMap;

	}
	
	/**
	 * 유연근무 변경/취소 적용
	 * @param tenantId
	 * @param enterCd
	 * @param flexibleEmpId
	 * @param flexibleStdMgrId
	 * @param sYmd
	 * @param eYmd
	 * @param symd
	 * @param eymd
	 * @param sabun
	 * @param hisId
	 * @param userId
	 * @return
	 */
	@Override
	public Map<String, Object> setChangeFlexible(Map<String, Object> paramMap) {
		
		try {
			System.out.println("setChangeFlexible serviceImpl start");
			// 변경데이터 flexibleemp에 적용하고 reset 부르기
			String changeType = paramMap.get("changeType").toString();
			if("DEL".equals(changeType)) {
				// 유연근무 기간 지우기
				flexEmpMapper.deleteByflexibleEmpId(paramMap);
			} else {
				// 유연근무 기간 변경하기
				flexEmpMapper.updateByflexibleEmpId(paramMap);
				//근무제 기간의 총 소정근로 시간을 업데이트 한다.
				//20200102jyp P_WTM_WORK_CALENDAR_RESET procedure에서 한다.
				//flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(paramMap);
			}
			// 기본근무정산은 유연근무시작일 -1일부터 유연근무종료일 +1일 처리함
			String symd = paramMap.get("orgSymd").toString();
			String eymd = paramMap.get("orgEymd").toString();
			// 직전종료일 +1일을 해줘야함
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date sdate = df.parse(symd);
	        // 날짜 더하기
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(sdate);
	        cal.add(Calendar.DATE, -1);
	        symd = df.format(cal.getTime());
	        Date edate = df.parse(eymd);
	        // 날짜 더하기
	        cal = Calendar.getInstance();
	        cal.setTime(edate);
	        cal.add(Calendar.DATE, 1);
	        eymd = df.format(cal.getTime());
	        
	        paramMap.remove("symd");
	        paramMap.remove("eymd");
	        
	        paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
	        
			// 그리고 리셋하기
			flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
			// 근무시간계산 다시 부르기
			flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(paramMap);
			paramMap.put("retType", "END");
			paramMap.put("retMsg", "근무적용완료");
			flexEmpMapper.setChangeEndMsg(paramMap);
			
			System.out.println("setChangeFlexible serviceImpl end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramMap;
	}
	
	@Override
	public Map<String, Object> calcMinuteExceptBreaktime(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, paramMap.get("ymd").toString());
		
		Map<String, Object> result = null;
		if(calendar!=null && calendar.getTimeCdMgrId()!=null) {
			Long timeCdMgrId = Long.valueOf(calendar.getTimeCdMgrId());
			result = calcMinuteExceptBreaktime(timeCdMgrId, paramMap, userId);
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> calcMinuteExceptBreaktimeForElas(boolean adminYn, Long flexibleApplId, Map<String, Object> paramMap, String userId) {
		Map<String, Object> result = null;
		Long timeCdMgrId = null;
		
		if(adminYn) {
			WtmFlexibleApplyDet flexApplyDet = flexibleApplyDetRepo.findByFlexibleApplyIdAndYmd(flexibleApplId, paramMap.get("ymd").toString());
		
			if(flexApplyDet!=null && flexApplyDet.getTimeCdMgrId()!=null)
				timeCdMgrId = Long.valueOf(flexApplyDet.getTimeCdMgrId());
			
			paramMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
			paramMap.put("key", "FLEXIBLE_APPLY_ID");
			paramMap.put("value", flexibleApplId);
		} else {
			WtmFlexibleApplDet flexApplDet = flexApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, paramMap.get("ymd").toString());
			
			if(flexApplDet!=null && flexApplDet.getTimeCdMgrId()!=null)
				timeCdMgrId = Long.valueOf(flexApplDet.getTimeCdMgrId());
			
			paramMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
			paramMap.put("key", "FLEXIBLE_APPL_ID");
			paramMap.put("value", flexibleApplId);
		}
		
		if(timeCdMgrId!=null) {
			result = calcMinuteExceptBreaktimeForElas(timeCdMgrId, paramMap, userId);
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> calcOtMinuteExceptBreaktimeForElas(boolean adminYn, Long flexibleApplId, String ymd, String sDate, String eDate, String otType, int otMinute, String userId) {
		Map<String, Object> result = null;
		Map<String, Object> otParamMap = new HashMap<String, Object>();
		if(adminYn) {
			otParamMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
			otParamMap.put("key", "FLEXIBLE_APPLY_ID");
			otParamMap.put("value", flexibleApplId);
		} else {
			otParamMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
			otParamMap.put("key", "FLEXIBLE_APPL_ID");
			otParamMap.put("value", flexibleApplId);
		}
		
		otParamMap.put("ymd", ymd);
		otParamMap.put("sDate", sDate);
		otParamMap.put("eDate", eDate);
		otParamMap.put("otType", otType);
		otParamMap.put("minute", otMinute);
		
		Map<String, Object> otMinuteMap = flexEmpMapper.getElasOtHm(otParamMap);
		if(otMinuteMap!=null && otMinuteMap.get("timeCdMgrId")!=null && otMinuteMap.get("sDate")!=null && otMinuteMap.get("eDate")!=null) {
			Long timeCdMgrId = Long.valueOf(otMinuteMap.get("timeCdMgrId").toString());
			String otSdate = otMinuteMap.get("sDate").toString();
			String otEdate = otMinuteMap.get("eDate").toString();
			
			//Map<String, Object> paramMap = new HashMap<String, Object>();
			//paramMap.put("shm", WtmUtil.parseDateStr(WtmUtil.toDate(otSdate, "yyyyMMddHHmmss"), "HHmm"));
			//paramMap.put("ehm", WtmUtil.parseDateStr(WtmUtil.toDate(otEdate, "yyyyMMddHHmmss"), "HHmm"));
			otParamMap.put("shm", WtmUtil.parseDateStr(WtmUtil.toDate(otSdate, "yyyyMMddHHmmss"), "HHmm"));
			otParamMap.put("ehm", WtmUtil.parseDateStr(WtmUtil.toDate(otEdate, "yyyyMMddHHmmss"), "HHmm"));
			
			result = calcMinuteExceptBreaktimeForElas(timeCdMgrId, otParamMap, userId);
			result.put("sDate", otSdate);
			result.put("eDate", otEdate);
		}
		
		return result;
	}
	
	@Override
	public Map<String, Object> calcMinuteExceptBreaktime(Long timeCdMgrId, Map<String, Object> paramMap, String userId) {
		//break_type_cd
		String breakTypeCd = "";
		
		paramMap.put("timeCdMgrId", timeCdMgrId);
			
		WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
		if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
			breakTypeCd = timeCdMgr.getBreakTypeCd();
		
		Map<String, Object> result = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			result = flexEmpMapper.calcMinuteExceptBreaktime(paramMap);
			
		/**
		 * BREAK_TYPE_TIME / BREAK_TYPE_TIMEFIX는 
		 * 휴게시간 없이 전체 인정시간으로 본다 이 메서드를 호출 할 경우 추가적으로 except 데이터를 만들어 주는것을 태워야함. 
		 * 타임블럭단위로 넘어오기때문에 여기서 할 수 없다.. 전체 데이터 합산 기준으로 except 데이터 생성이 필요 
		 */
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			//result = flexEmpMapper.calcTimeTypeApprMinuteExceptBreaktime(paramMap);
			result = flexEmpMapper.calcMinute(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			//result = flexEmpMapper.calcTimeTypeFixMinuteExceptBreaktime(paramMap);
			result = flexEmpMapper.calcMinute(paramMap);
		}
		
		
		paramMap.put("breakTypeCd", breakTypeCd);
		System.out.println("**************** hj : " + paramMap.toString());
		Map<String, Object> breakMinuteMap = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			
			breakMinuteMap = flexEmpMapper.calcTimeBreakMinute(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			
		}
		
		if(breakMinuteMap!=null)
			result.putAll(breakMinuteMap);
		
		return result;
		
	}
	
	@Override
	public Map<String, Object> calcMinuteExceptBreaktimeForElas(Long timeCdMgrId, Map<String, Object> paramMap, String userId) {
		//break_type_cd
		String breakTypeCd = "";
		
		paramMap.put("timeCdMgrId", timeCdMgrId);
		
		WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
		if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
			breakTypeCd = timeCdMgr.getBreakTypeCd();
		
		Map<String, Object> result = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			result = flexEmpMapper.calcMinuteExceptBreaktime(paramMap);
			
		/**
		 * BREAK_TYPE_TIME / BREAK_TYPE_TIMEFIX는 
		 * 휴게시간 없이 전체 인정시간으로 본다 이 메서드를 호출 할 경우 추가적으로 except 데이터를 만들어 주는것을 태워야함. 
		 * 타임블럭단위로 넘어오기때문에 여기서 할 수 없다.. 전체 데이터 합산 기준으로 except 데이터 생성이 필요 
		 */
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			//result = flexEmpMapper.calcTimeTypeApprMinuteExceptBreaktime(paramMap);
			result = flexEmpMapper.calcMinute(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			//result = flexEmpMapper.calcTimeTypeFixMinuteExceptBreaktime(paramMap);
			result = flexEmpMapper.calcMinute(paramMap);
		}
		
		
		paramMap.put("breakTypeCd", breakTypeCd);
		Map<String, Object> breakMinuteMap = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			breakMinuteMap = flexEmpMapper.calcTimeBreakMinuteForElas(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			
		}
		
		if(breakMinuteMap!=null)
			result.putAll(breakMinuteMap);
		
		return result;
		
	}
	
	@Override
	public List<String> getAuth(Long tenantId, String enterCd, String sabun) {
		Map<String, Object> m = new HashMap<String, Object>();
        m.put("tenantId", tenantId);
        m.put("enterCd", enterCd);
        m.put("sabun", sabun);
        
        List<String> rule = null;
        try {
	        ObjectMapper mapper = new ObjectMapper();
	        String encKey = tcms.getConfigValue(tenantId, "SECURITY.AES.KEY", true, "");
	        m.put("encKey", encKey);
	        List<Map<String, Object>> auths = authMgrMapper.findAuthByTenantIdAndEnterCdAndSabun(m);

			if(auths!=null && auths.size()>0) {
				rule = new ArrayList<String>();
				for(Map<String, Object> auth : auths) {
					if(auth.get("ruleText")!=null && !"".equals(auth.get("ruleText"))) {
						List<String> ruleText = mapper.readValue(auth.get("ruleText").toString(), new ArrayList<String>().getClass());
						for(String t : ruleText) {
							if(!rule.contains(t))
								rule.add(t);
						}
					}
				}
			}
			
			return rule;
        } catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
	}
	
	@Override
	public List<String> getLowLevelOrgList(Long tenantId, String enterCd, String sabun, String ymd) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("ymd", ymd);
			
			WtmEmpHis emp = empHisRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
			if(emp!=null && emp.getOrgCd()!=null && !"".equals(emp.getOrgCd()))
				paramMap.put("orgCd", emp.getOrgCd());
			
			List<String> orgList = null;
			
			//하위 조직 조회
			List<Map<String, Object>> lowLevelOrgList = wtmOrgChartMapper.getLowLevelOrg(paramMap); 
			
			if(lowLevelOrgList!=null && lowLevelOrgList.size()>0) {
				orgList = new ArrayList<String>();
				for(Map<String, Object> orgMap : lowLevelOrgList) {
					orgList.add(orgMap.get("orgCd").toString());
				}
			}
			
			//겸직 조회
			List<WtmOrgConc> orgConcList = orgConcRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(tenantId, enterCd, sabun, ymd);
			if(orgConcList!=null && orgConcList.size()>0) {
				if(orgList == null)
					orgList = new ArrayList<String>();
				
				for(WtmOrgConc orgConc : orgConcList) {
					orgList.add(orgConc.getOrgCd());
				}
			}
			
			return orgList;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 근무제 통계 데이터 생성
	@Override
	public Map<String, Object> createWorkTermtimeByEmployee(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("pId", userId);
		//flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(paramMap);
		
		String ymd = "";
		if(paramMap.containsKey("symd")) {
			ymd = paramMap.get("symd")+"";
		}else if(paramMap.containsKey("sYmd")) {
			ymd = paramMap.get("sYmd")+"";
		}else if(paramMap.containsKey("ymd")) {
			ymd = paramMap.get("ymd")+"";
		}
		calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, ymd);
		return flexEmpMapper.getWorkTermMinute(paramMap);
	}
	
	@Override
	public ReturnParam getOtMinute(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//신청 대상자
			List<String> sabunList = null;
			if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
				//유연근무신청 관리자 화면에서 선택한 대상자 리스트
				sabunList = mapper.readValue(paramMap.get("applSabuns").toString(), new ArrayList<String>().getClass());
			} else {
				//개인 신청
				sabunList = new ArrayList<String>();
				sabunList.add(sabun);
			}
			
			//대상자의 잔여 연장근무시간
			if(sabunList!=null && sabunList.size()>0) {
				Map<String, Object> targetList = new HashMap<String, Object>();
				paramMap.put("tenantId", tenantId);
				paramMap.put("enterCd", enterCd);
				paramMap.put("sabuns", sabunList);
				List<Map<String, Object>> emps = otApplMapper.getRestOtMinute(paramMap);
				if(emps!=null && emps.size()>0) {
					for(Map<String, Object> emp : emps) {
						Map<String, Object> restMinuteMap = new HashMap<String, Object>();
						paramMap.put("sabun", emp.get("sabun").toString());
						
						// 신청중인 OT신청서 시간도 차감 하자.
						
						int restOtMin = 0;
						int restWorkMin = 0;
						Integer applOtMin = 0;
						Integer applHolOtMin = 0;
						Integer otMin = 0;
						Integer holOtMin = 0;
						if(emp.get("restOtMinute")!=null && !"".equals(emp.get("restOtMinute"))) {
							restOtMin = Integer.parseInt(emp.get("restOtMinute").toString());
							
						}
						
						Map<String, Object> weekInfo = flexEmpMapper.weekWorkTimeByEmp(paramMap);
						System.out.println(mapper.writeValueAsString(weekInfo));
						//신청중인 연장근무 시간과
						if(weekInfo != null && weekInfo.get("applOtMinute") != null && !weekInfo.get("applOtMinute").equals("")) {
							applOtMin = Integer.parseInt(weekInfo.get("applOtMinute")+"");
							if(applOtMin == null) {
								applOtMin = 0;
							}
						}
						//신청중인 휴일연장근무 시간을 분리... 소정근로시간부터 차감이 필요하기때문이다. 
						if(weekInfo != null && weekInfo.get("applHolOtMinute") != null && !weekInfo.get("applHolOtMinute").equals("")) {
							applHolOtMin = Integer.parseInt(weekInfo.get("applHolOtMinute")+"");
							if(applHolOtMin == null) {
								applHolOtMin = 0;
							}
						}
						//기 신청된 연장근무 와 고정 OT를 더해서 빼야한다.
						if(weekInfo != null && weekInfo.get("otMinute") != null && !weekInfo.get("otMinute").equals("")) {
							otMin = Integer.parseInt(weekInfo.get("otMinute")+"");
							if(otMin == null) {
								otMin = 0;
							}
						}
						//기 신청된 휴일 연장근무는 별도로 빼서 계산한다. 소정근로시간부터 차감해야하기 때문인다.
						//휴일근로 인터페이스도 확인 필요
						if(weekInfo != null && weekInfo.get("holOtMinute") != null && !weekInfo.get("holOtMinute").equals("")) {
							holOtMin = Integer.parseInt(weekInfo.get("holOtMinute")+"");
							if(holOtMin == null) {
								holOtMin = 0;
							}
						}
						applHolOtMin = applHolOtMin + holOtMin;
						applOtMin = applOtMin + otMin;
						
						//휴일근무이며
						if(emp.get("holidayYn") != null && "Y".equals(emp.get("holidayYn"))) {
							//기본근무 / 시차출퇴근 / 근무조 일때는 휴일에 잔여 소정근로 시간을 사용할 수 잇다. 
							if(emp.get("workTypeCd") != null && ("BASE".equals(emp.get("workTypeCd")) || "DIFF".equals(emp.get("workTypeCd")) || "WORKTEAM".equals(emp.get("workTypeCd")) ) ) {
								/*
									한주에 대한 정보 조회 계획 및 인정 근무 시간의 합 - 결근 제외 
								 */
								if(weekInfo != null && weekInfo.get("workMinute") != null && !weekInfo.get("workMinute").equals("")) {
									//한주소정근로시간 40시간   * 60  = 2400
									int weekWorkMinute = Integer.parseInt(weekInfo.get("weekWorkMinute")+"");
									int exMinute = 0;
									int exceptMinute = 0;
									if(weekInfo.get("exMinute") != null && !weekInfo.get("exMinute").equals("")) {
										exMinute = Integer.parseInt(weekInfo.get("exMinute")+"");
									}
									if(weekInfo.get("exceptMinute") != null && !weekInfo.get("exceptMinute").equals("")) {
										exceptMinute = Integer.parseInt(weekInfo.get("exceptMinute")+"");
									}
									System.out.println("weekWorkMinute : " + weekWorkMinute);
									System.out.println("workMinute : " + Integer.parseInt(weekInfo.get("workMinute")+""));
									System.out.println("exMinute : " + exMinute);
//									restWorkMin = weekWorkMinute - Integer.parseInt(weekInfo.get("workMinute")+"") - exMinute;
									restWorkMin = weekWorkMinute - Integer.parseInt(weekInfo.get("workMinute")+"") - exMinute - exceptMinute;
									System.out.println("restMin : " + restWorkMin);
									
								}
								/*
								if(emp.get("restWorkMinute")!=null && !"".equals(emp.get("restWorkMinute"))) {
									int restMin = Integer.parseInt(emp.get("restWorkMinute").toString());
									restMinuteMap.put("restWorkMinute", restMin);
									restMinuteMap.put("guideMessage", "* 잔여소정근로시간이 먼저 차감됩니다.");
								}
								*/
							}
						}
						//신청 중인 OT가 있으면 해당 분을 빼야한다. 
						
						if(applHolOtMin > 0) {
							if(restWorkMin >= applHolOtMin) {
								restWorkMin = restWorkMin - applHolOtMin;
								applHolOtMin = 0;
							}else { 
								applHolOtMin = applHolOtMin - restWorkMin;
							}
						}
						//소정근로에서 빼고 남은 것을 연장근로 시간에 합해 다시 뺀다. 
						applOtMin = applOtMin + applHolOtMin;
						if(applOtMin > 0) {
							if(applOtMin > 0) {
								restOtMin = restOtMin - applOtMin;
								applOtMin = 0;
							}
						
						}
						
						if(restWorkMin > 0) {
							restMinuteMap.put("restWorkMinute", restWorkMin);
							restMinuteMap.put("guideMessage", "* 잔여소정근로시간이 먼저 차감됩니다.");
						}
						restMinuteMap.put("restOtMinute", restOtMin);
						
						targetList.put(emp.get("sabun").toString(), restMinuteMap);
					}
				}
				
				rp.put("targetList", targetList);
				
			}
		
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("잔여 연장근무시간 조회 시 오류가 발생했습니다.");
			return rp;
		}
		
		return rp;
	}
	
	@Override
	public void applyOtSubs(Long tenantId, String enterCd, List<WtmOtAppl> otApplList, boolean isCalcAppr, String userId) {
		logger.debug("applyOtSubs --------------------------------------");
		System.out.println("applyOtSubs --------------------------------------");
		
		for(WtmOtAppl otAppl : otApplList) {
			logger.debug("휴일 대체 생성 [" + tenantId + "@" + enterCd + "@" + otAppl.getSabun() + "] start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			logger.debug("연장근무신청서 : " + otAppl.getOtApplId());
			System.out.println("휴일 대체 생성 [" + tenantId + "@" + enterCd + "@" + otAppl.getSabun() + "] start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("연장근무신청서 : " + otAppl.getOtApplId());
			
			//소급의 경우 인정시간과 연장근로시간을 비교하여 다른 경우 대체휴일 정보를 생성하지 않는다.
			//미래의 연장근로시간의 경우 일마감에서 대체휴일 정보를 생성한다.
			//1. 인정근무시간이 있거나 연장근무일이 오늘 이전이면
			if(!isCalcAppr || (isCalcAppr && otAppl.getOtSdate().compareTo(new Date()) < 0)) {
				 
				Map<String, Object> resultParam = new HashMap<String, Object>();
				resultParam.put("tenantId", tenantId);
				resultParam.put("enterCd", enterCd);
				resultParam.put("sabun", otAppl.getSabun());
				resultParam.put("ymd", otAppl.getYmd());
				
				List<String> timeTypeCds = new ArrayList<String>();
				timeTypeCds.add(WtmApplService.TIME_TYPE_GOBACK);
				resultParam.put("timeTypeCds", timeTypeCds);
				
				List<Map<String, Object>> results = flexEmpMapper.getResultBySabunAndYmdAndTimeTypeCds(resultParam);
				if(results!=null && results.size()>0) {
					String sYmd = WtmUtil.parseDateStr(otAppl.getOtSdate(), "yyyyMMdd");
					String eYmd = WtmUtil.parseDateStr(otAppl.getOtEdate(), "yyyyMMdd");
					
					resultParam.put("sYmd", sYmd);
					resultParam.put("eYmd", eYmd);
					resultParam.put("userId", userId);
					
					logger.debug("연장근무시간 : " + WtmUtil.parseDateStr(otAppl.getOtSdate(), "yyyyMMddHHmmss") + "~" + WtmUtil.parseDateStr(otAppl.getOtEdate(), "yyyyMMddHHmmss"));
					System.out.println("연장근무시간 : " + WtmUtil.parseDateStr(otAppl.getOtSdate(), "yyyyMMddHHmmss") + "~" + WtmUtil.parseDateStr(otAppl.getOtEdate(), "yyyyMMddHHmmss"));
					
					String entrySdate = null;
					String entryEdate = null;
					String unplannedYn = null;
					
					Map<String, Object> result = results.get(0);
					if(result.get("entrySdate")!=null && !"".equals(result.get("entrySdate")))
						entrySdate = result.get("entrySdate").toString();
					if(result.get("entryEdate")!=null && !"".equals(result.get("entryEdate")))
						entryEdate = result.get("entryEdate").toString();
					if(result.get("unplannedYn")!=null && !"".equals(result.get("unplannedYn")))
						unplannedYn = result.get("unplannedYn").toString();
						
					logger.debug("unplannedYn : " + unplannedYn);
					logger.debug("출/퇴근 타각 : " + entrySdate + "~" + entryEdate);
					
					//인정근무시간 계산해야하고 출/퇴근 타각이 있고
					if(isCalcAppr && entrySdate!=null && entryEdate!=null) {
						//2. 출/퇴근 타각이 있는지 체크
						//3. unplanned_yn 체크 : Y이면 외출/복귀 유무 조회
						//unplanned_yn이 Y 이고, 외출/복귀 가 있으면 create_result_n 프로시저 호출 	
						
						//외출/복귀 데이터가 있으면 result 다시 생성
						if(unplannedYn!=null && "Y".equals(unplannedYn) && result.get("timeTypeCd")!=null && !"".equals(result.get("timeTypeCd"))) {
							
							logger.debug("resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt start >>>");
							
							flexEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(resultParam);
							
							logger.debug("resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt end >>>");
							
							logger.debug("addWtmDayResultInBaseTimeType start >>>");
							for(Map<String, Object> r : results) {
								if(r.get("timeTypeCd")!=null && WtmApplService.TIME_TYPE_GOBACK.equals(r.get("timeTypeCd"))) {
									String timeTypeCd = r.get("timeTypeCd").toString();
									String taaCd = r.get("taaCd")!=null?r.get("taaCd").toString():"";
									Date planSdate = WtmUtil.toDate(r.get("planSdate").toString(), "yyyyMMddHHmmss");
									Date planEdate = WtmUtil.toDate(r.get("planEdate").toString(), "yyyyMMddHHmmss");
									
									logger.debug("plan : " + planSdate + "~" + planEdate);
									
									addWtmDayResultInBaseTimeType(
											tenantId
											, enterCd
											, otAppl.getYmd()
											, otAppl.getSabun()
											, timeTypeCd
											, taaCd
											, planSdate
											, planEdate
											, otAppl.getApplId()
											, userId
											, false);
								}
							}
							logger.debug("addWtmDayResultInBaseTimeType end >>>");
						}
						
						logger.debug("calcApprDayInfo start >>>");
						calcApprDayInfo(tenantId, enterCd, sYmd, eYmd, otAppl.getSabun());
						logger.debug("calcApprDayInfo end >>>");
						
						resultParam.put("symd", sYmd);
						resultParam.put("eymd", eYmd);
						resultParam.put("pId", userId);
						
						logger.debug("createWorkTerm start >>>");
						flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(resultParam);
						logger.debug("createWorkTerm end >>>");
					}
					
					//인정시간 만들어지면 인정시간과 ot신청시간 비교
					List<String> otTimeTypeCds = new ArrayList<String>();
					otTimeTypeCds.add(WtmApplService.TIME_TYPE_OT);
					otTimeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
					otTimeTypeCds.add(WtmApplService.TIME_TYPE_NIGHT);
					otTimeTypeCds.add(WtmApplService.TIME_TYPE_EARLY_NIGHT);
					resultParam.put("timeTypeCds", otTimeTypeCds);
					
					Map<String, Object> otMinute = flexEmpMapper.sumResultMinuteByTimeTypeCd(resultParam);
					if(otMinute!=null 
							&& otMinute.containsKey("planMinute") && otMinute.get("planMinute")!=null
							&& otMinute.containsKey("apprMinute") && otMinute.get("apprMinute")!=null) {
						
						int otPlanMinute = Integer.parseInt(otMinute.get("planMinute").toString());
						int otApprMinute = Integer.parseInt(otMinute.get("apprMinute").toString());
						
						logger.debug("otPlanMinute : " + otPlanMinute + "/otApprMinute : " + otApprMinute);
						System.out.println("otPlanMinute : " + otPlanMinute + "/otApprMinute : " + otApprMinute);
						
						if(otPlanMinute <= otApprMinute) {
							resultParam.put("otApplId", otAppl.getOtApplId());
							
							List<Map<String, Object>> subsCreateTarget = otApplMapper.subsCreateTarget(resultParam);

							if(subsCreateTarget!=null && subsCreateTarget.size()>0) {
							
								//대체휴일
								List<WtmOtSubsAppl> subs = otSubsApplRepo.findByApplId(otAppl.getApplId());
								if(subs!=null && subs.size()>0) {
									logger.debug("save subs start >>> ");
									System.out.println("save subs start >>> ");
									for(WtmOtSubsAppl sub : subs) {
										// 20200609 이효정 TAA_CD에도 고정값 SUBS를 추가해야함
										addWtmDayResultInBaseTimeType(tenantId, enterCd, sub.getSubYmd(), otAppl.getSabun(), WtmApplService.TIME_TYPE_SUBS, WtmApplService.TIME_TYPE_SUBS, sub.getSubsSdate(), sub.getSubsEdate(), otAppl.getApplId(), userId);
									}
									logger.debug("save subs end >>> ");
									System.out.println("save subs end >>> ");
								}
							}
						}
						
					}
					
							
				}
				
				logger.debug("휴일 대체 생성 end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			}
		}
			
	}
	
	@Override
	public boolean isRuleTarget(Long tenantId, String enterCd, String sabun, String ruleType, String ruleValue) {
		
		boolean isTarget = false;
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
		
			if("SQL".equalsIgnoreCase(ruleType)) {
				
				String sql = "(";
				
				List<Map<String, Object>> ruleList = mapper.readValue(ruleValue, new ArrayList<Map<String, Object>>().getClass());
				if(ruleList!=null && ruleList.size()>0) {
					int i = 0;
					int groupSeq = 0;
					for(Map<String, Object> r : ruleList) {
						if(r.containsKey("groupSeq") && r.get("groupSeq")!=null && !"".equals(r.get("groupSeq"))) {
							int seq = Integer.parseInt(r.get("groupSeq").toString());
							
							if(i!=0) {
								if(groupSeq != seq) 
									sql += ") or (";
								else 
									sql += " and ";
							}
							
							sql += r.get("item") + " " + r.get("operator") + " " + r.get("itemValue");
							
							groupSeq = seq;
							i++;
						}
						
					}
					
					sql += ")";
					
					System.out.println("sql : " + sql);
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("tenantId", tenantId);
					paramMap.put("enterCd", enterCd);
					paramMap.put("sabun", sabun);
					paramMap.put("d", WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
					paramMap.put("sql", sql);
					
					Map<String, Object> isTargetMap = empHisMapper.getRuleTargetBySql(paramMap);
					if(isTargetMap!=null && isTargetMap.containsKey("isTarget") && isTargetMap.get("isTarget")!=null)
						if("Y".equals(isTargetMap.get("isTarget").toString())) 
							isTarget = true;
				}
				
			} else {
				
				Map<String, Object> ruleMap = mapper.readValue(ruleValue, new HashMap<String, Object>().getClass());
				
				if(ruleMap != null){ 
					WtmEmpHis e = empHisRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, WtmUtil.parseDateStr(new Date(), null));
					if(ruleMap.containsKey("INCLUDE") && ruleMap.get("INCLUDE")!=null && !"".equals(ruleMap.get("INCLUDE"))) {
						boolean isEmpty = true;
						Map<String, Object> inMap = (Map<String, Object>) ruleMap.get("INCLUDE");
						if(inMap!=null) {
							if(inMap.containsKey("EMP") && inMap.get("EMP")!=null && !"".equals(inMap.get("EMP"))) {
								List<Map<String, Object>> empList = (List<Map<String, Object>>) inMap.get("EMP");
								if(empList != null && empList.size() > 0) {
									isEmpty = false;
									for(Map<String, Object> empMap : empList) {
										if(empMap.get("k")!=null && sabun.equals(empMap.get("k"))) {
											isTarget = true;
										}
									}
								}
							}
							if(inMap.containsKey("ORG") && inMap.get("ORG")!=null && !"".equals(inMap.get("ORG"))) { 
								List<Map<String, Object>> orgList = (List<Map<String, Object>>) inMap.get("ORG");
								if(orgList != null && orgList.size() > 0) {
									isEmpty = false;
									for(Map<String, Object> orgMap : orgList) {
										if(e.getOrgCd()!=null && orgMap.get("k")!=null && e.getOrgCd().equals(orgMap.get("k"))) {
											isTarget = true;
											break;
										}
									}
								}
								
							}
							/*
							if(inMap.containsKey("JIKWEE")) {
								List<Map<String, Object>> jikweeList = (List<Map<String, Object>>) exMap.get("JIKWEE");
								if(jikweeList != null && jikweeList.size() > 0) {
									for(Map<String, Object> jikweeMap : jikweeList) {
										if(e.get().equals(jikweeMap.get("k"))) {
											isTarget = true;
											break;
										}
									}
								}
								
							}
							if(inMap.containsKey("JIKGUB")) {
								
							}
							*/
							if(inMap.containsKey("JIKCHAK") && inMap.get("JIKCHAK")!=null && !"".equals(inMap.get("JIKCHAK"))) {
								List<Map<String, Object>> jikchakList = (List<Map<String, Object>>) inMap.get("JIKCHAK");
								if(jikchakList != null && jikchakList.size() > 0) {
									isEmpty = false;
									for(Map<String, Object> jikchakMap : jikchakList) {
										if(e.getDutyCd()!=null && jikchakMap.get("k")!=null && e.getDutyCd().equals(jikchakMap.get("k"))) {
											isTarget = true;
											break;
										}
									}
								}
							}
							if(inMap.containsKey("JOB") && inMap.get("JOB")!=null && !"".equals(inMap.get("JOB"))) {
	
								List<Map<String, Object>> jobList = (List<Map<String, Object>>) inMap.get("JIKCHAK");
								if(jobList != null && jobList.size() > 0) {
									isEmpty = false;
									for(Map<String, Object> jobMap : jobList) {
										if(e.getJobCd()!=null && jobMap.get("k")!=null && e.getJobCd().equals(jobMap.get("k"))) {
											isTarget = true;
											break;
										}
									}
								}
							}
						}
						
						if(!isEmpty) 
							isTarget = true;
						
					} else {
						//INCLUDE 가 아예 등록되지 않으면 모든 사람이 대상자
						isTarget = true;
					}
					
					Map<String, Object> exMap = null;
					if(isTarget && ruleMap.containsKey("EXCLUDE") && ruleMap.get("EXCLUDE")!=null && !"".equals(ruleMap.get("EXCLUDE"))) {
						//여기에등록되어 있으면 포함이 되었더도 안됨 이놈이 우선 
						exMap = (Map<String, Object>) ruleMap.get("EXCLUDE");
						if(exMap!=null && exMap.containsKey("EMP")) {
							List<Map<String, Object>> empList = (List<Map<String, Object>>) exMap.get("EMP");
							if(empList != null && empList.size() > 0) {
								for(Map<String, Object> empMap : empList) {
									if(sabun.equals(empMap.get("k"))) {
										isTarget = false;
										return isTarget;
									}
								}
							}
						}
					}
					
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
		return isTarget;
	}
	
	
	public Date parseStringToDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date result = null;
		try {
			result = sdf.parse(date);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		return result;
	}
	
	@Override
	@Transactional
	public int setApplyForOne(Map<String, Object> searchSabun, List<Map<String, Object>> ymdList) {
		try {
		Long tenantId =  Long.parseLong(searchSabun.get("tenantId").toString());
		String enterCd = searchSabun.get("enterCd").toString();
		String sabun = searchSabun.get("sabun").toString();
		String workTypeCd = searchSabun.get("workTypeCd").toString();
		Long flexibleApplyId = Long.parseLong(searchSabun.get("flexibleApplyId").toString());
		
		WtmFlexibleEmp saveFlexibleEmp = null;
		
		for(int i = 0; i < ymdList.size(); i++) {
			String sYmd = ymdList.get(i).get("symd").toString();
			String eYmd = ymdList.get(i).get("eymd").toString();
			
			searchSabun.put("symd", sYmd);
			searchSabun.put("eymd", eYmd);

			WtmFlexibleEmp flexibleEmp = new WtmFlexibleEmp();
			flexibleEmp.setEnterCd(searchSabun.get("enterCd").toString());
			flexibleEmp.setEymd(searchSabun.get("eymd").toString());
			flexibleEmp.setFlexibleStdMgrId(Long.parseLong(searchSabun.get("flexibleStdMgrId").toString()));
			flexibleEmp.setSabun(searchSabun.get("sabun").toString());
			flexibleEmp.setSymd(searchSabun.get("symd").toString());
			flexibleEmp.setTenantId(Long.parseLong(searchSabun.get("tenantId").toString()));
			flexibleEmp.setUpdateId(searchSabun.get("sabun").toString());
			flexibleEmp.setWorkTypeCd(searchSabun.get("workTypeCd").toString());
			flexibleEmp.setFlexibleNm(searchSabun.get("flexibleNm").toString());
			flexibleEmp.setNote(searchSabun.get("note").toString());
			System.out.println(flexibleEmp.toString());
			saveFlexibleEmp = flexEmpRepo.save(flexibleEmp);
			flexEmpRepo.flush();
//			int cnt = wtmFlexibleApplyMgrMapper.insertApplyEmp(searchSabun);
			Map<String, Object> searchMap = wtmFlexibleApplyMgrMapper.setApplyEmpId(searchSabun);
			searchSabun.put("flexibleEmpId", Long.parseLong(searchMap.get("flexibleEmpId").toString()));
			
			//BASE 뿐만 아니라 WORKTEAM 도 가지고 와야 함.
			//2020.10.15JYP 기본근무 자르는건 리셋으로 넘기
			/*
			List<String> workTypeCds = new ArrayList<String>();
			workTypeCds.add("BASE");
			workTypeCds.add("WORKTEAM");
			
			WtmFlexibleEmp emp = new WtmFlexibleEmp();
			List<WtmFlexibleEmp> empList = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCds(tenantId, enterCd, sabun, sYmd, eYmd, workTypeCds);
			if(empList != null) {
			*/
				/* RESET 에서 하자 .JYP 20.10.15
				for(WtmFlexibleEmp e : empList) {
					//신청기간내에 시작 종료가 포함되어있을 경우
					if(Integer.parseInt(sYmd) <= Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) >= Integer.parseInt(e.getEymd())) {
						flexEmpRepo.delete(e);
						flexEmpRepo.flush();
					//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
					} else if(Integer.parseInt(sYmd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) < Integer.parseInt(e.getEymd())) {
						String meymd = e.getEymd();
						
						e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sYmd, ""), -1),null));
						// System.out.println("save 1 : " + e.toString());
						flexEmpRepo.save(e);
						flexEmpRepo.flush();
						
						WtmFlexibleEmp newEmp = new WtmFlexibleEmp();
						newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
						newEmp.setTenantId(e.getTenantId());
						newEmp.setEnterCd(e.getEnterCd());
						newEmp.setSabun(e.getSabun());
						newEmp.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eYmd, ""), 1),null));
						newEmp.setEymd(meymd);
						newEmp.setUpdateId(sabun);
						newEmp.setWorkTypeCd(e.getWorkTypeCd());
						newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
						// System.out.println("save 2 : " + newEmp.toString());
						flexEmpRepo.save(newEmp);
						flexEmpRepo.flush();
						

					//시작일만 포함되어있을 경우 
					}else if(Integer.parseInt(sYmd) >= Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) < Integer.parseInt(e.getEymd())) {
						//시작일을 신청종료일 다음날로 업데이트 해주자
						e.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eYmd, ""), 1),null));
						// System.out.println("save 3 : " + e.toString());
						flexEmpRepo.save(e);
						flexEmpRepo.flush();
						
					//종료일만 포함되어있을 경우
					}else if(Integer.parseInt(sYmd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) <= Integer.parseInt(e.getEymd())) {
						//종료일을 신청시작일 전날로 업데이트 해주자
						e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sYmd, ""), -1),null));
						// System.out.println("save 4 : " + e.toString());
						flexEmpRepo.save(e);
						flexEmpRepo.flush();
						
					}
				}
				*/
				
				//탄근제의 경우 근무 계획까지 작성하여 신청을 하기 때문에 calendar, result 만들어준다.
				if(workTypeCd.equals("ELAS")) {
					//calendar 있으면 삭제하고 다시 만들어주자.
					//initWtmFlexibleEmpOfWtmWorkDayResult 프로시저에서 calendar 만들어주기 때문에 생략
					/*List<WtmWorkCalendar> calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, sd, ed);
					
					if(calendar!=null && calendar.size()>0) {
						workCalendarRepo.deleteAll(calendar);
						workCalendarRepo.flush();
					}
					flexEmpMapper.createWorkCalendarOfElasApply(flexibleApplyId, sabun, userId);*/
					
					//List<WtmWorkCalendar> calendar2 = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
					
					//result 만들어주자.
					List<WtmWorkDayResult> result = new ArrayList<WtmWorkDayResult>();
					Map<String, Object> pMap = new HashMap<String, Object>();
					pMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
					pMap.put("key", "FLEXIBLE_APPLY_ID");
					pMap.put("value", flexibleApplyId);
					List<Map<String, Object>> dets = flexEmpMapper.getElasWorkDayResult(pMap);
					if(dets!=null && dets.size()>0) {
//						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						
						//result 에 base와 ot, fixot 있으면 삭제하고 다시 만들어주자.
						List<String> timeTypCds = new ArrayList<String>();
						timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
						timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
						timeTypCds.add(WtmApplService.TIME_TYPE_OT);
						timeTypCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
						
						List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sYmd, eYmd);
						if(results!=null && results.size()>0) {
							workDayResultRepo.deleteAll(results);
							workDayResultRepo.flush();
						}
						
						for(Map<String, Object> det : dets) {
							Date s = null;
							Date e = null;
							
							WtmWorkDayResult r = new WtmWorkDayResult();
							r.setTenantId(tenantId);
							r.setEnterCd(enterCd);
							r.setYmd(det.get("ymd").toString());
							r.setSabun(sabun);
							//r.setApplId(applId);
							r.setTimeTypeCd(det.get("timeTypeCd").toString());
							r.setTaaCd(null);
							if(det.get("planSdate")!=null && !"".equals(det.get("planSdate"))) {
								s = parseStringToDate(det.get("planSdate").toString());
								r.setPlanSdate(s);
							}
								
							if(det.get("planEdate")!=null && !"".equals(det.get("planEdate"))) {
								e = parseStringToDate(det.get("planEdate").toString());
								r.setPlanEdate(e);
							}
							
							if(det.get("planMinute")!=null && !"".equals(det.get("planMinute"))) {
								r.setPlanMinute(Integer.parseInt(det.get("planMinute").toString()));
							}
							r.setUpdateDate(new Date());
							r.setUpdateId(sabun);
							
							result.add(r);
						}
						
						if(result.size()>0)
							workDayResultRepo.saveAll(result);
							workDayResultRepo.flush();
					}
					
				} else {
					// 근무제도 시행시 시행할 기간의 근무제도가 기본근무의 정보는 지워야함.
					//유연근무 승인 시 해당 구간 내의 result는 지워야 한다. //리셋 프로시져에서 지우지 않음.  
					//result 에 base와 ot, fixot 있으면 삭제
					List<String> timeTypCds = new ArrayList<String>();
					timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
					timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
					timeTypCds.add(WtmApplService.TIME_TYPE_OT);
					timeTypCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
					
					List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sYmd, eYmd);
					if(results!=null && results.size()>0) {
						workDayResultRepo.deleteAll(results);
						workDayResultRepo.flush();
					}
				}
			//}
		}
		logger.debug("[setApply] updateWorkMinuteOfWtmFlexibleEmp " +tenantId+enterCd+sabun);
		
		searchSabun.put("symd", searchSabun.get("useSymd"));
		searchSabun.put("eymd", searchSabun.get("useEymd"));
		searchSabun.put("pId", searchSabun.get("userId"));

		logger.debug("[setApply] updateStart " +searchSabun.toString());

		//flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(searchSabun);
		flexibleEmpResetSerevice.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, searchSabun.get("useSymd")+"", searchSabun.get("useEymd")+"", "ADMIN");
		logger.debug("[setApply] initWtmFlexibleEmpOfWtmWorkDayResult ");
		
		calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, searchSabun.get("useSymd")+"");
		//flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(searchSabun);
		logger.debug("[setApply] createWorkTermBySabunAndSymdAndEymd ");
		
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//		wtmFlexibleApplyMgrMapper.updateApplyEmp(searchSabun);
//		logger.debug("[setApply] updateApplyEmp ");
		} catch(Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return 0;
		}
		return 1;
	}
	
	@Transactional
	@Override
	public ReturnParam retireEmp(Long tenantId, String enterCd, Long flexibleEmpId, String userId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		ObjectMapper mapper = new ObjectMapper();
		
		WtmFlexibleEmp flexibleEmp = flexEmpRepo.findById(flexibleEmpId).get();
		if(flexibleEmp==null) {
			rp.setFail("근무 정보가 없습니다.");
			return rp;
		}
		
		try {
			System.out.println("flexibleEmp : " + mapper.writeValueAsString(flexibleEmp));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<String> statusCds = new ArrayList<String>();
		//statusCds.add("CA"); //휴직
		//statusCds.add("EA"); //정직
		statusCds.add("RA"); //퇴직
		List<WtmEmpHis> empHis = empHisRepo.findByTenantIdAndEnterCdAndSabunAndStatusCdIn(tenantId, enterCd, flexibleEmp.getSabun(), statusCds);
		if(empHis==null && empHis.size()==0) {
			rp.setFail("직원의 퇴직 정보가 없습니다.");
			return rp;
		}
		
		for(WtmEmpHis h : empHis) {
			//퇴직일
			String retireYmd = h.getEymd();
			
			if("29991231".equals(h.getEymd())) {
				retireYmd = h.getSymd();
			} 
			
			System.out.println("retireYmd : " + retireYmd);
			
			//종료일 퇴직일로 변경
			flexibleEmp.setEymd(retireYmd);
			flexEmpRepo.save(flexibleEmp);
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", flexibleEmp.getSabun());
			paramMap.put("ymd", retireYmd);
			
			//result clear
			flexEmpMapper.deleteWorkDayResultByYmdGreaterThan(paramMap);
			
			//calendar clear
			flexEmpMapper.deleteWorkCalendarByYmdGreaterThan(paramMap);
			
			//퇴직일 이후의 flexibleEmp 데이터 삭제
			flexEmpMapper.deleteFlexEmpByYmdGreaterThan(paramMap);
			
			//workterm clear
			flexEmpMapper.deleteWorkTermByYmdGreaterThan(paramMap);
			
			rp.put("sabun", flexibleEmp.getSabun());
			rp.put("symd", h.getSymd());
			rp.put("eymd", h.getEymd());
			
		}
		
		return rp;
	}

	/**
	 * 조출시간(분), 잔업시간(분)으로 휴게시간 포함 출근, 퇴근 시간 조회 
	 */
	@Override
	public Map<String, Object> calcOtMinuteAddBreaktimeForElas(Long tenantId, String enterCd, Long TimeCdMgrId, String obDate, String otType, int otMinute, String userId) {
		
		Map<String, Object> otParamMap = new HashMap<String, Object>();
		otParamMap.put("tenantId", tenantId);
		otParamMap.put("enterCd",  enterCd);
		otParamMap.put("timeCd",   TimeCdMgrId.intValue());
		otParamMap.put("obDate",   obDate);

		// 탄력 근무 잔업시간(분)으로 e시간 받아 오기
		if(otType!=null && "OTA".equals(otType)) {
			otParamMap.put("addMinute",otMinute);
			flexEmpMapper.getElasOtaHm(otParamMap);
		// 탄력 근무 조출시간(분)으로 s시간 받아 오기
		} else if(otType!=null && "OTB".equals(otType)) {
			otParamMap.put("addMinute",otMinute*-1);  //조출시간 - 로 계산
			flexEmpMapper.getElasOtbHm(otParamMap);
		}
		return otParamMap;
	}

	
	//@Transactional
	@Override
	public ReturnParam finishDay(Map<String, Object> paramMap, Long tenantId, String enterCd, String empNo, String userId) throws Exception{
		
		/*
		 *  WTM_WORK_DAY_RESULT_O 에 데이터가 있는지 확인
		 *  데이터가 있으면 WTM_WORK_DAY_RESULT 의 appr_sdate, appr_edate, appr_minute 를 null로 업데이트 하고 
		 *  재생성 한다
		 */
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("일마감 처리 완료 하였습니다. ");

		if(paramMap != null) {
			
			String paramSymd = paramMap.get("paramSdate").toString();
			String paramEymd = paramMap.get("paramEdate").toString();
			String sabun = paramMap.get("sabun").toString();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			Date sDate = sdf.parse(paramSymd);
			Date eDate = sdf.parse(paramEymd);
			
			try {

				//마감데이터 재생성
				calcApprDayInfo(tenantId, enterCd, paramSymd, paramEymd, sabun);
				
				paramMap.put("tenantId", tenantId);
				paramMap.put("enterCd", enterCd);
				paramMap.put("sabun", sabun);
				paramMap.put("symd", sdf.format(sDate));
				paramMap.put("eymd", sdf.format(eDate));
				paramMap.put("pId", "finishDay");
				
				flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(paramMap);
			} catch (Exception e) {
				rp.setFail("일마감 처리중 오류가 발생하였습니다.");
				e.printStackTrace();
				return rp;
			}
			
			
		}
		
		return rp;
	}
	
	@Override
	public Map<String, Object> checkFinDay(Map<String, Object> paramMap, Long tenantId, String enterCd, String empNo, String userId) throws Exception{
		
		Map<String, Object> returnMap = flexEmpMapper.checkFinDay(paramMap);
		
		return returnMap;
	}
	
	@Transactional
	@Override
	public void resetCalcApprDayInfo(Long tenantId, String enterCd, String ymd, String sabun, List<String> timeTypeCds) {
		if(timeTypeCds == null) {
			timeTypeCds = new ArrayList<String>();
			timeTypeCds.add(WtmApplService.TIME_TYPE_BASE);
			timeTypeCds.add(WtmApplService.TIME_TYPE_FIXOT);
		}
		List<WtmWorkDayResultO> resultOs = workDayResultORepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdIn(tenantId, enterCd, sabun, ymd, timeTypeCds);
		if(resultOs != null && resultOs.size() > 0) {
			for(WtmWorkDayResultO reasultO :  resultOs) {
				List<WtmWorkDayResult> delResult =  workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(reasultO.getTimeTypeCd(), tenantId, enterCd, sabun, ymd);
				
				workDayResultRepo.deleteAll(delResult);

				WtmWorkDayResult result = new WtmWorkDayResult();
				result.setTenantId(reasultO.getTenantId());
				result.setEnterCd(reasultO.getEnterCd());
				result.setYmd(reasultO.getYmd());
				result.setSabun(reasultO.getSabun());
				result.setApplId(reasultO.getApplId());
				result.setTaaCd(reasultO.getTaaCd());
				result.setTimeTypeCd(reasultO.getTimeTypeCd());
				result.setPlanSdate(reasultO.getPlanSdate());
				result.setPlanEdate(reasultO.getPlanEdate());
				result.setPlanMinute(reasultO.getPlanMinute());
				result.setWorkYn(reasultO.getWorkYn());
				
				workDayResultRepo.save(result);
			}
			
		} 
		//calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
	}


	@Override
	public List<Map<String, Object>> getFlexibleEmpImsiList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub 
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);

		List<Map<String, Object>> flexibleList = flexEmpMapper.getFlexibleEmpList(paramMap);
		if(flexibleList!=null && flexibleList.size()>0) {
			for(Map<String, Object> flex : flexibleList) {
				if(flex.get("applId") != null && flex.get("applId").toString().equals(paramMap.get("applId"))) {
					List<Map<String, Object>> plans = flexEmpMapper.getFlexibleEmpImsiList(paramMap);
					flex.put("flexibleEmp", getDayWorks(plans, userId));
				}
			}
		}

		return flexibleList;
	}
	
	
	
	@Override
	@Transactional
	public void createWtmWorkDayResultAsCalendar(WtmFlexibleEmp flexibleEmp) {
		
		WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(flexibleEmp.getFlexibleStdMgrId());
		logger.debug("call createWtmWorkDayResultAsPattern ::");
		List<WtmWorkPattDet> pattDets = workPattDetRepo.findByFlexibleStdMgrId(flexibleEmp.getFlexibleStdMgrId());
		Map<Long, WtmTimeCdMgr> timeCdMgrMap = new HashMap<Long, WtmTimeCdMgr>();
		logger.debug("매번 조회할 수 없으니 패턴 정보를 맵에 담아두자.");
		for(WtmWorkPattDet pattDet : pattDets) {
			if(!timeCdMgrMap.containsKey(pattDet.getTimeCdMgrId())) {
				timeCdMgrMap.put(pattDet.getTimeCdMgrId(), wtmTimeCdMgrRepo.findById(pattDet.getTimeCdMgrId()).get());
			}
		}
		
		List<WtmWorkCalendar> calendars = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(flexibleEmp.getTenantId(), flexibleEmp.getEnterCd(), flexibleEmp.getSabun(), flexibleEmp.getSymd(), flexibleEmp.getEymd());
		if(calendars != null && calendars.size() > 0) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
			for(WtmWorkCalendar calendar : calendars) {
				if(calendar.getHolidayYn() == null || "".equals(calendar.getHolidayYn()) || !"Y".equals(calendar.getHolidayYn())) {
					logger.debug("마감된 즉 인정된 근무정보가 있으면 해당일은 생성하지 않는다.");
					List<WtmWorkDayResult> apprResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNotNull(flexibleEmp.getTenantId(), flexibleEmp.getEnterCd(), flexibleEmp.getSabun(), calendar.getYmd(), calendar.getYmd());
					if(apprResults != null &&  apprResults.size() > 0 ) {
						continue;
					}
					WtmTimeCdMgr timeCdMgr = timeCdMgrMap.get(calendar.getTimeCdMgrId());
					if(timeCdMgr.getWorkShm() != null && timeCdMgr.getWorkEhm() != null
						&& !"".equals(timeCdMgr.getWorkShm()) && !"".equals(timeCdMgr.getWorkEhm())) {
						logger.debug("timeCdMgr is not null ");
						String shm = timeCdMgr.getWorkShm();
						String ehm = timeCdMgr.getWorkEhm();
						String sYmd = calendar.getYmd();
						String eYmd = calendar.getYmd();
						Date sd = null, ed = null;
						try {
							sd = ymdhm.parse(sYmd+shm);
							//종료시분이 시작시분보다 작으면 기준일을 다음날로 본다. 
							if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
								cal.add(Calendar.DATE, 1);
								eYmd = ymd.format(cal.getTime());
							}
							ed = ymdhm.parse(eYmd+ehm);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						WtmWorkDayResult insRes = new WtmWorkDayResult();
						insRes.setTenantId(calendar.getTenantId());
						insRes.setEnterCd(calendar.getEnterCd());
						insRes.setYmd(calendar.getYmd());
						insRes.setSabun(calendar.getSabun());
						insRes.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
						insRes.setPlanSdate(sd);
						insRes.setPlanEdate(ed);
						
						int breakMinute = 0;						
						Map<String, Object> resMap = calcService.calcApprMinute(sd, ed, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						if(resMap.containsKey("apprMinute")) {
							insRes.setPlanMinute(Integer.parseInt(resMap.get("apprMinute")+""));
							breakMinute = Integer.parseInt(resMap.get("breakMinute")+"");
						}
						workDayResultRepo.save(insRes);
						
						
						if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
							String taaInfoCd = "BREAK";
							Integer createMinute = breakMinute;
							if(createMinute != null && createMinute > 0) {
								calcService.createWorkDayResultForBreakTime(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd(), taaInfoCd, "PLAN", createMinute, "createWorkDayResultForBreakTime");
							}
						}

						
						List<String> timeTypeCds = new ArrayList<String>();
						timeTypeCds.add(WtmApplService.TIME_TYPE_FIXOT);
						List<WtmWorkDayResult> delFixResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(flexibleStdMgr.getTenantId(), flexibleStdMgr.getEnterCd(), calendar.getSabun(), timeTypeCds, calendar.getYmd(), calendar.getYmd());
						if(delFixResults != null && delFixResults.size() > 0) {
							logger.debug("fixOt를 삭제한다 : " + delFixResults.size() );
							workDayResultRepo.deleteAll(delFixResults);
						}
						
						if(flexibleStdMgr.getFixotUseType().equals("DAY")) {
							Date calcEdate = null;
							Integer fixOtMinute = 0;
							if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
								
								calcEdate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(ed, flexibleStdMgr.getFixotUseLimit(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());

								if(flexibleStdMgr.getWorkShm() != null && !"".equals(flexibleStdMgr.getWorkShm())
										&& flexibleStdMgr.getWorkEhm() != null && !"".equals(flexibleStdMgr.getWorkEhm())
										) {
									try {
										Date limitSdate = ymdhm.parse(calendar.getYmd()+flexibleStdMgr.getWorkShm());
										Date limitEdate = ymdhm.parse(calendar.getYmd()+flexibleStdMgr.getWorkEhm());
									
										if(limitSdate.compareTo(limitEdate) > 0) {
											logger.debug("제한시간 셋팅이 종료시간 보다 시작시간이 늦을 경우 종료시간을 1일 더해서 다음날로 만든다. sHm : " + flexibleStdMgr.getWorkShm() + " eHm : " + flexibleStdMgr.getWorkEhm());
											Calendar cal1 = Calendar.getInstance();
											cal1.setTime(limitEdate);
											cal1.add(Calendar.DATE, 1);
											limitEdate = cal1.getTime();
										} 
						 
										if(calcEdate.compareTo(limitEdate) > 0) {
											logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
											calcEdate = limitEdate;
										}

									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								fixOtMinute = calcService.WtmCalcMinute(sdf.format(ed), sdf.format(calcEdate), null, null, flexibleStdMgr.getUnitMinute()) -  calcService.getBreakMinuteIfBreakTimeMGR(ed, calcEdate, timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								logger.debug("fixOtMinute = " +  fixOtMinute);
							}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
								Calendar cal1 = Calendar.getInstance();
								cal1.setTime(ed);
								cal1.add(Calendar.MINUTE, flexibleStdMgr.getFixotUseLimit());
								calcEdate = cal.getTime();
								fixOtMinute = flexibleStdMgr.getFixotUseLimit();
							}
							
							WtmWorkDayResult fixRes = new WtmWorkDayResult();
							fixRes.setTenantId(calendar.getTenantId());
							fixRes.setEnterCd(calendar.getEnterCd());
							fixRes.setYmd(calendar.getYmd());
							fixRes.setSabun(calendar.getSabun());
							fixRes.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
							fixRes.setPlanSdate(ed);
							fixRes.setPlanEdate(calcEdate);
							fixRes.setPlanMinute(fixOtMinute);
							fixRes.setUpdateId("ADMIN");
							workDayResultRepo.save(fixRes);
						}
					}else {
						logger.debug("timeCdMgr is null ");
					} 
				}else {
					//휴일은 생성하지 않는다. 
					continue;
				}
					/*
				//logger.debug("chkDate : " + chkDate);
				//logger.debug("applEDate : " + applEDate);
				cal.setTime(chkDate);
				logger.debug("flexibleStdMgr.getHolExceptYn() :" + flexibleStdMgr.getHolExceptYn());
				
				logger.debug("startPattSeq : " + startPattSeq);
				try {
					logger.debug("pattDetMap : " + mapper.writeValueAsString(pattDetMap));
					logger.debug("timeCdMgrMap : " + mapper.writeValueAsString(timeCdMgrMap));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				WtmWorkPattDet det = pattDetMap.get(startPattSeq);
				WtmTimeCdMgr timeCdMgr = timeCdMgrMap.get(startPattSeq); 
				startPattSeq++;
				if(startPattSeq > pattSize) {
					startPattSeq = 1;
				}
				
				try {
					logger.debug("flexibleStdMgr : " + mapper.writeValueAsString(flexibleStdMgr));
					logger.debug("det : " + mapper.writeValueAsString(det));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String sYmd = ymd.format(chkDate);
				
				if("Y".equals(flexibleStdMgr.getHolExceptYn()) && holList.indexOf(sYmd) > -1 ) {
					cal.add(Calendar.DATE, 1);
					//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
					chkDate = cal.getTime();
				}else {
					//휴일이면
					if(!"".equals(timeCdMgr.getHolYn()) &&  "Y".equals(timeCdMgr.getHolYn())) {
						logger.debug("timeCdMgr is null ");
						cal.add(Calendar.DATE, 1);
						//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
						chkDate = cal.getTime();
					}else {
						if(timeCdMgr.getWorkShm() != null && timeCdMgr.getWorkEhm() != null
								&& !"".equals(timeCdMgr.getWorkShm()) && !"".equals(timeCdMgr.getWorkEhm())) {
							logger.debug("timeCdMgr is not null ");
							String shm = timeCdMgr.getWorkShm();
							String ehm = timeCdMgr.getWorkEhm();
							
							String eYmd = sYmd;
							Date sd = null, ed = null;
							try {
								sd = ymdhm.parse(sYmd+shm);
								//종료시분이 시작시분보다 작으면 기준일을 다음날로 본다. 
								if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
									cal.add(Calendar.DATE, 1);
									eYmd = ymd.format(cal.getTime());
									//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
									chkDate = cal.getTime();
								}else {
									cal.add(Calendar.DATE, 1);
									//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
									chkDate = cal.getTime();
								}
								ed = ymdhm.parse(eYmd+ehm);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							WtmFlexibleApplDet applDet = new WtmFlexibleApplDet();
							
							applDet.setFlexibleApplId(flexibleAppl.getFlexibleApplId());
							applDet.setTimeCdMgrId(det.getTimeCdMgrId());
							applDet.setHolidayYn(det.getHolidayYn());
							applDet.setYmd(sYmd);
							applDet.setPlanSdate(sd);
							applDet.setPlanEdate(ed);
							
							Map<String, Object> resMap = calcService.calcApprMinute(sd, ed, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							if(resMap.containsKey("apprMinute")) {
								applDet.setPlanMinute(Integer.parseInt(resMap.get("apprMinute")+""));
							}
							
							saveDetList.add(applDet);
						}else {
							logger.debug("timeCdMgr is null ");
							cal.add(Calendar.DATE, 1);
							//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
							chkDate = cal.getTime();
						}
					}
				}
				*/
			}
		}
		/*
			while(chkDate.compareTo(applEDate) <= 0) {
				
			}
			
			logger.debug("saveDetList.size( : " + saveDetList.size());
			wtmFlexibleApplDetRepo.saveAll(saveDetList);
		}		
		*/	
		
	}
	
	@Override
	public Map<String, Object> getFlexibleStat(Long tenantId, String enterCd, String ymd, String sabun) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		
		Map<String, Object> resMap = flexEmpMapper.getWorktermByFleibleEmp(paramMap);
		if(resMap != null) {
			resMap.put("restOtMinute", Integer.parseInt(resMap.get("totalOtMinute")+"") - Integer.parseInt(resMap.get("apprOtMinute")+""));
			resMap.put("restWorkMinute", Integer.parseInt(resMap.get("totalWorkMinute")+"") - Integer.parseInt(resMap.get("apprWorkMinute")+""));
		}else {
			return null;
		}
		return resMap;
	}
	
}
