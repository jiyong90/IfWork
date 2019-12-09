package com.isu.ifw.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmFlexibleAppl;
import com.isu.ifw.entity.WtmFlexibleApplDet;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmOtCanAppl;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmAuthMgrMapper;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmOrgChartMapper;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleApplDetRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmOtCanApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.repository.WtmWorkteamMgrRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmDayPlanVO;
import com.isu.ifw.vo.WtmDayWorkVO;
import com.isu.option.service.TenantConfigManagerService;
import com.isu.option.vo.ReturnParam;

@Service("flexibleEmpService")
public class WtmFlexibleEmpServiceImpl implements WtmFlexibleEmpService {

	@Autowired
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
	
	@Autowired
	WtmOtCanApplRepository otCanApplRepo;
	
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
	WtmFlexibleEmpService empService;
	
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
		
		//취소 신청서 있는지 조회
		if(workDayResult!=null && workDayResult.size()>0) {
			for(Map<String, Object> r : workDayResult) {
				if(r.get("applId")!=null && !"".equals(r.get("applId"))) {
					WtmOtCanAppl otCanAppl = otCanApplRepo.findByOtApplId(Long.valueOf(r.get("applId").toString()));
					if(otCanAppl!=null && otCanAppl.getOtCanApplId()!=null) {
						r.put("otCanApplId", otCanAppl.getOtCanApplId());
					}
				}
			}
		}
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
		
		return flexEmpMapper.getFlexibleDayInfo(paramMap);
	}
	
	@Override
	public Map<String, Object> getFlexibleWorkTimeInfo(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		paramMap.put("taaTimeYn", "");
		paramMap.put("taaWorkYn", "");
		
		Map<String, Object> flexEmp = flexEmpMapper.getFlexibleEmp(paramMap);
		if(flexEmp!=null) {
			if(flexEmp.get("taaTimeYn")!=null) //근태시간포함여부
				paramMap.put("taaTimeYn", flexEmp.get("taaTimeYn").toString());
			if(flexEmp.get("taaWorkYn")!=null) //근태일 근무여부
				paramMap.put("taaWorkYn", flexEmp.get("taaWorkYn").toString());
		}
		
		return flexEmpMapper.getFlexibleWorkTimeInfo(paramMap); 
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
			if(defaultWorkUseYn!=null && defaultWorkUseYn.equals("Y") && fixotUseType!=null && fixotUseType.equalsIgnoreCase("DAY")) {
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
				
			}
			
			//기본근무 정보는 삭제 하고 다시 만들자 그게 속편하다
			workDayResultRepo.deleteByTenantIdAndEnterCdAndYmdAndTimeTypeCdAndSabun(tenantId, enterCd, ymd, timeTypeCd, sabun);
			
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
			if(result!=null && result.get("isValid")!=null && result.get("isValid").equals("0")) {
				throw new RuntimeException(result.get("totalWorktime").toString() + "시간의 소정근로시간을 넘을 수 없습니다.");
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
							Map<String, Object> planMinuteMap = calcElasPlanMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
							applDet.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
						} else {
							applDet.setPlanMinute(null);
						}
						
						if(shm!=null && ehm!=null && vMap.get("otbMinute") != null && !vMap.get("otbMinute").equals("")) {
							paramMap.put("otType", "OTB");
							paramMap.put("sDate", k+shm );
							paramMap.put("eDate", k+ehm );
							paramMap.put("minute", vMap.get("otbMinute"));
							Map<String, Object> otbMinuteMap = calcElasOtMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
							
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
							paramMap.put("otType", "OTA");
							paramMap.put("sDate", k+shm );
							paramMap.put("eDate", k+ehm );
							paramMap.put("minute", vMap.get("otaMinute"));
							
							Map<String, Object> otaMinuteMap = calcElasOtMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
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
				Float H = 0f;
				Float i = 0f;
				if(plan.containsKey("minute") && plan.get("minute") != null) {
					m = plan.get("minute").toString();
					H = Float.parseFloat(m)/60;
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
	
	/**
	 * 타각시간 기준으로 인정시간 계산
	 */
	@Transactional
	@Override
	public void calcApprDayInfo(Long tenantId, String enterCd, String sYmd, String eYmd, String sabun) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);

		List<String> timeTypeCd = new ArrayList<>();
		timeTypeCd.add(WtmApplService.TIME_TYPE_LLA);
		
		//지각 조퇴 무단결근 데이터 삭제
		if(sabun != null && !sabun.equals("")) {
			List<WtmWorkDayResult> result = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, sYmd, eYmd);
			if(result != null && result.size() > 0) {
				workDayResultRepo.deleteAll(result);
			}
		}else {
			List<WtmWorkDayResult> result = workDayResultRepo.findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween(tenantId, enterCd, timeTypeCd, sYmd, eYmd);
			if(result != null && result.size() > 0) {
				workDayResultRepo.deleteAll(result);
			}
			
		}

		WtmTaaCode absenceTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_ABSENCE);
		//코어타임 사용 시 코어타임 필수여부에 따라 근무시간이 코어타임에 미치지 못하면 결근으로 본다.
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", absenceTaaCode.getTaaCd());
		paramMap.put("userId", "SYSTEM");
		//여기서 결근이 들어갈 경우 아래 출퇴근 타각이 모두 있을 때 조퇴처리가 될수 있다. 결근데이터가 있을 경우를 제외해줘야한다.
		flexEmpMapper.createDayResultByTimeTypeAndCheckRequireCoreTimeYn(paramMap);
		
		
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_REGA);
		// 간주근무의 경우 출/퇴근 타각데이터를 계획 데이터로 생성해 준다.
		flexEmpMapper.updateTimeTypePlanToEntryTimeByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
		
		// 출근시간 자동 여부 -- 출근이 자유인 경운 지각이 없다고 본다?? 일단 ㅋ
		// 출근시간 자동에 대해 일괄 업데이트 한다.
		// 어디까지인가? 조출 / 기본근무
		// 출근 타각데이터가 있는건 갱신하지 않는다.
		flexEmpMapper.updateEntrySdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
		
		// 퇴근 시간 자동 여부 (계획시간으로 )
		// 어디까지인가? 기본근무 / 연장
		// 퇴근 타각데이터가 있는건 갱신하지 않는다.
		flexEmpMapper.updateEntryEdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(paramMap);
		
		
		
		// 출근 타각이 없을 경우
		// 출근 또는 출/퇴근 타각이 모두 없을 경우 무단결근
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", absenceTaaCode.getTaaCd());
		paramMap.put("userId", "SYSTEM");
		flexEmpMapper.createDayResultByTimeTypeAndEntryDateIsNull(paramMap);
		

		WtmTaaCode leaveTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LEAVE);
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", leaveTaaCode.getTaaCd());
		
		// 출근 데이터는 있고 퇴근 타각이 없을 경우 조퇴 (시/종 정보 없이 생성)
		flexEmpMapper.createDayResultByTimeTypeAndEntrtEdateIsNull(paramMap);
		
		
//		paramMap.put("timeTypeCd", timeTypeCd);
		//소정근로시간의 경우 출퇴근 타각기록으로만 판단 >> 결근 데이터가 있는 날은 빼야한다.
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", absenceTaaCode.getTaaCd());
		flexEmpMapper.updateApprDatetimeByYmdAndSabun(paramMap);
		
		// 이곳은 출/퇴근 타각데이터가 있는 사람에 한한다.. 

		// 계획 종료 시간 보다 인정종료시간이 빠를 경우 BASE중에 
		// 조퇴 데이터 생성
		flexEmpMapper.createDayResultByTimeTypeAndApprEdateLessThanPlanEdate(paramMap);
		
		// 계획 시작 시간보다 인정시작시간이 늦을 경우 BASE중에 
		// 지각 데이터 생성
		WtmTaaCode lateTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LATE);
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_LLA);
		paramMap.put("taaCd", lateTaaCode.getTaaCd());
		flexEmpMapper.createDayResultByTimeTypeAndPlanSdateLessThanApprSdate(paramMap);
		
		
		
		//고정OT 일괄소진의 경우 고정 OT데이터를 삭제후 다시 만들어 준다.
		//근무 기간 내에 고정 OT정보를 확인부터 하자.
		//고정OT 일괄소진의 경우 계획데이터만 있을 수 없다 마감시 인정 시간을 바로 산정한다. 
		
		
		
		// BREAK_TYPE_CD가 MGR인것만 계산
		flexEmpMapper.updateApprMinuteByYmdAndSabun(paramMap);
		
		// BREAK_TYPE_CD가 TIME인것만 계산
		flexEmpMapper.updateTimeTypeApprMinuteByYmdAndSabun(paramMap);
		
		// BREAK_TYPE_CD가 TIMEFIX인것만 계산
		flexEmpMapper.updateTimeFixTypeApprMinuteByYmdAndSabun(paramMap);
		
		
		/**
		 * 고정 OT 일괄 소진에 대한 부분
		 */
		List<WtmFlexibleEmp> emps = flexEmpRepo.findAllTypeFixotByTenantIdAndEnterCdAndSabunAndSymdAndEymdAnd(tenantId, enterCd, sabun, sYmd, eYmd);
		if(emps != null && emps.size() >0) {
			for( WtmFlexibleEmp emp : emps) {
				paramMap.put("flexibleEmpId", emp.getFlexibleEmpId());
				flexEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(paramMap);
			}
		}
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
	 * @param workCalendarId
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
			
			workDayResult = flexEmpMapper.getWorkDayResultByCalendarId(paramMap);
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
	public void saveEmpDayResults(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) throws Exception {
		if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
			List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
			List<Map<String, Object>> day = new ArrayList();
			String retMsg = "";
			if(iList != null && iList.size() > 0) {
				for(Map<String, Object> l : iList) {
					
					l.put("shm", l.get("planSdate").toString().substring(8,12));
					l.put("ehm", l.get("planEdate").toString().substring(8,12));
					
					Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), l, userId);
					
					l.put("planMinute", (Integer.parseInt(planMinuteMap.get("calcMinute")+"")));
					l.put("updateId", userId);
					l.put("tenantId", tenantId);
					l.put("enterCd", enterCd);
					
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

					// 근무검증
					String timeTypeCd = l.get("timeTypeCd").toString();
					ReturnParam rp = new ReturnParam();
					Map<String, Object> chkMap = new HashMap();
					
					if("BASE".equals(timeTypeCd)) {
						Map<String, Object> result2 = flexEmpMapper.checkBaseWorktimeMgr(l);
						if(result2!=null && result2.get("isValid")!=null && result2.get("isValid").equals("0")) {
							retMsg = l.get("sabun").toString() + "," + l.get("ymd").toString() + ", "+ result2.get("totalWorktime").toString() + "시간의 소정근로시간을 넘을 수 없습니다.";
						}
					} else {
						// ot시간검증
						chkMap.put("ymd", l.get("ymd").toString());
						chkMap.put("otSdate", l.get("planSdate").toString());
						chkMap.put("otEdate", l.get("planEdate").toString());
						rp = applService.validate( tenantId,  enterCd,  l.get("sabun").toString(),  timeTypeCd, chkMap);
						if(rp.getStatus().equals("FAIL")) {
							retMsg = l.get("sabun").toString() + "," + l.get("ymd").toString() + ", "+ rp.get("message").toString();
						}
					}
					if(!"".equals(retMsg)) {
						// 오류내용 저장하기
						throw new RuntimeException(retMsg);
					}
					
					// 문제가 없으면 근무계획시간 합산
					chkMap.put("tenantId", tenantId);
					chkMap.put("enterCd", enterCd);
					chkMap.put("sabun", l.get("sabun").toString());
					chkMap.put("symd", l.get("ymd").toString());
					chkMap.put("eymd", l.get("ymd").toString());
					chkMap.put("pId", userId);
					flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(chkMap);
					
					retMsg = ""; // 메시지초기화
				}
			}
		}
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
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
		}
		
		List<Map<String, Object>> flexibleList = flexEmpMapper.getFlexibleEmpWebList(paramMap);
		
		return flexibleList;
	}

	@Transactional
	@Override
	public void addWtmDayResultInBaseTimeType(Long tenantId, String enterCd, String ymd, String sabun, String addTimeTypeCd, String addTaaCd,
			Date addSdate, Date addEdate, Long applId, String userId) {
	 
		List<WtmWorkDayResult> base = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_BASE, ymd, ymd);
		
		//List<WtmWorkDayResult> days = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);

		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		 
		Map<String, Object> pMap = new HashMap<>();
		pMap.put("tenantId", tenantId);
		pMap.put("enterCd", enterCd);
		pMap.put("sabun", sabun);
		pMap.put("ymd", ymd);
		
		for(WtmWorkDayResult r : base) {
			//근무 계획 시작시간과 종료시간의 범위를 절대 벗어날수 없다. 그렇다 한다. ㅋ
			boolean isDelete = false;
			//시종시간이 동일하면 기본근무 계획시간을 지운다.
			if(r.getPlanSdate().compareTo(addSdate) == 0 && r.getPlanEdate().compareTo(addEdate) == 0) {
				isDelete = true;
			//시작시간은 같지만 계획 종료 시간 보다 대체휴일종료 시간이 작을 경우
			}else if(r.getPlanSdate().compareTo(addSdate) == 0 && r.getPlanEdate().compareTo(addEdate) > 0) {
				r.setPlanSdate(addEdate); // 계획의 시작일은 휴일대체 종료로 변경한다
				 
			//종료시간은 같지만 계획 시작시간 보다 대체휴일시작시간이 클경우
			}else if(r.getPlanSdate().compareTo(addSdate) < 0 && r.getPlanEdate().compareTo(addEdate) == 0) {
				r.setPlanEdate(addSdate); // 계획의 종료일을 휴일대체 시작일로 변경한다
				
			//계회의 시종 시간 중간에!! 대체휴일 시종시간이 있을 경우! 거지같넹.. 앞에데이터는 수정하고 뒤에 데이터는 만들어줘야한다.. 
			}else if(r.getPlanSdate().compareTo(addSdate) < 0 && r.getPlanEdate().compareTo(addEdate) > 0) {
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
				addR.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
				addR.setUpdateId(userId);
				
				workDayResultRepo.save(addR);
				
			}
			if(!isDelete) {
				String shm = sdf.format(r.getPlanSdate());
				String ehm = sdf.format(r.getPlanEdate()); 
				pMap.put("shm", shm);
				pMap.put("ehm", ehm);
				Map<String, Object> planMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
				r.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+"")); 
				workDayResultRepo.save(r);
			}else {
				workDayResultRepo.delete(r);
			}
		}
		

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
		
		String shm = sdf.format(addEdate);
		String ehm = sdf.format(addEdate); 
		addMap.put("shm", shm);
		addMap.put("ehm", ehm);
		Map<String, Object> addPlanMinuteMap = calcMinuteExceptBreaktime(tenantId, enterCd, sabun, addMap, userId);
		addDayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
		addDayResult.setTimeTypeCd(addTimeTypeCd);
		addDayResult.setTaaCd(addTaaCd);
		addDayResult.setUpdateId(userId);
		
		workDayResultRepo.save(addDayResult); 
	}

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
				
				List<WtmWorkDayResult> workDayResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCd, ymd, ymd);
				 
				//Date sdate = otSubsAppl.getSubsSdate();
				//Date edate = otSubsAppl.getSubsEdate();
				 
				int cnt = 0;
				Boolean isPrev = null;
				for(WtmWorkDayResult res : workDayResults) {
					 
					if(( res.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) || res.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) ) && res.getPlanSdate().compareTo(removeSdate) == 0 && res.getPlanEdate().compareTo(removeEdate) == 0) {
						if(cnt == 0) {
							//시작시간이 대체휴일이면 다음 데이터 여부를 판단하고 다음데이터가 SUBS BASE로 변경하자
							if(workDayResults.size() == (cnt+1) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) ) {
								//뒤에 데이터가 없으면
								res.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
								res.setApplId(applId);
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
							if(workDayResults.get(cnt-1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt-1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA)) {
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
									res.setApplId(applId);

									workDayResultRepo.save(res);
									break;
								}
							}else {
								//마지막 데이터가 아니면 다음 데이터의 timeTypeCd를 확인하자
								if(workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS) || workDayResults.get(cnt+1).getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA)) {
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
										res.setApplId(applId); 
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
				flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(paramMap);
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
		WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, paramMap.get("ymd").toString());
		
		Map<String, Object> result = null;
		if(calendar!=null && calendar.getTimeCdMgrId()!=null) {
			Long timeCdMgrId = Long.valueOf(calendar.getTimeCdMgrId());
			result = calcMinuteExceptBreaktime(timeCdMgrId, paramMap, userId);
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
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			result = flexEmpMapper.calcTimeTypeApprMinuteExceptBreaktime(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			result = flexEmpMapper.calcTimeTypeFixMinuteExceptBreaktime(paramMap);
		}
		return result;
		
	}
	
	@Override
	public Map<String, Object> calcElasPlanMinuteExceptBreaktime(Long flexibleApplId, Map<String, Object> paramMap, String userId) {
		//break_type_cd
		String breakTypeCd = "";
		WtmFlexibleApplDet flexApplDet = flexApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, paramMap.get("ymd").toString());
		
		if(flexApplDet!=null && flexApplDet.getTimeCdMgrId()!=null) {
			Long timeCdMgrId = Long.valueOf(flexApplDet.getTimeCdMgrId());
			
			WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
			if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
				breakTypeCd = timeCdMgr.getBreakTypeCd();
		}
		
		Map<String, Object> result = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			result = flexEmpMapper.calcElasPlanMinuteExceptBreaktime(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			result = flexEmpMapper.calcTimeTypeElasPlanMinuteExceptBreaktime(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			result = flexEmpMapper.calcTimeTypeFixElasPlanMinuteExceptBreaktime(paramMap);
		}
		
		return result;
		
	}
	
	@Override
	public Map<String, Object> calcElasOtMinuteExceptBreaktime(Long flexibleApplId, Map<String, Object> paramMap, String userId) {
		String breakTypeCd = "";
		WtmFlexibleApplDet flexApplDet = flexApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, paramMap.get("ymd").toString());
		
		if(flexApplDet!=null && flexApplDet.getTimeCdMgrId()!=null) {
			Long timeCdMgrId = Long.valueOf(flexApplDet.getTimeCdMgrId());
			
			WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
			if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
				breakTypeCd = timeCdMgr.getBreakTypeCd();
		}
		
		Map<String, Object> result = null;
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			result = flexEmpMapper.calcElasOtMinuteExceptBreaktime(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			result = flexEmpMapper.calcTimeTypeElasOtMinuteExceptBreaktime(paramMap);
		} else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			result = flexEmpMapper.calcTimeTypeFixElasOtMinuteExceptBreaktime(paramMap);
		}
		
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
			//하위 조직 조회
			List<Map<String, Object>> lowLevelOrgList = wtmOrgChartMapper.getLowLevelOrg(paramMap); 
			List<String> orgList = null;
			
			if(lowLevelOrgList!=null && lowLevelOrgList.size()>0) {
				orgList = new ArrayList<String>();
				for(Map<String, Object> orgMap : lowLevelOrgList) {
					orgList.add(orgMap.get("orgCd").toString());
				}
			}
			
			return orgList;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
