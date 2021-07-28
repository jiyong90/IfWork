package com.isu.ifw.service;

import java.text.SimpleDateFormat;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmOtAppl;
import com.isu.ifw.entity.WtmOtCanAppl;
import com.isu.ifw.entity.WtmOtSubsAppl;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.entity.WtmTaaApplDet;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.repository.WtmOtApplRepository;
import com.isu.ifw.repository.WtmOtCanApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmTaaApplDetRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("WtmApplAfterService")
public class WtmApplAfterServiceImpl implements WtmApplAfterService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmApplMapper applMapper;
	
	/**
	 * 속성값 조회
	 */
	@Autowired
	WtmPropertieRepository wtmPropertieRepo;
	@Autowired
	WtmOtApplRepository wtmOtApplRepo;
	@Autowired
	WtmOtSubsApplRepository wtmOtSubsApplRepo;
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;
	
	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmOtCanApplRepository wtmOtCanApplRepo;
	
	@Autowired private WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;

	@Autowired private WtmTaaApplDetRepository taaApplDetRepo;

	@Autowired
	private WtmFlexibleEmpService wtmflexibleEmpService;

	@Autowired
	private WtmCalcService wtmCalcService;

	@Override
	public ReturnParam applyStsAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("결재가 완료되었습니다.");
		
		ObjectMapper mapper = new ObjectMapper();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		
		//야간근무시간이 포함되어있는 지 확인후 데이터를 찢어넣자
		List<WtmPropertie> properties = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKeyLike(tenantId, enterCd, "OPTION_OT_NIGHT_HHMM_%");
		
		//대상자의 실제 근무 정보를 반영한다.
		List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
		
		logger.debug("연장근무정보 : " + mapper.writeValueAsString(otApplList));
		
		if(otApplList!=null && otApplList.size()>0) {
			
			String n_shm = "";
			String n_ehm = "";
			String en_shm = "";
			String en_ehm = "";
			for(WtmPropertie propertie : properties) {
				if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_S")) {
					n_shm = propertie.getInfoValue(); 
				}else if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_E")) {
					n_ehm = propertie.getInfoValue();
					//조출 심야..ㅡㅡ^
					en_shm = "0000";
					en_ehm = propertie.getInfoValue();
				}
			}
			
			if("".equals(n_shm) || "".equals(n_ehm)) {
				rp.setFail("야간 근무시간 정보가 없습니다. 담당자에게 문의하시기 바랍니다.");
				throw new RuntimeException(rp.get("message").toString());
			}
			
			for(WtmOtAppl otA : otApplList) {
				//otA.getYmd()
				WtmWorkCalendar c = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, otA.getSabun(), otA.getYmd());
				WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(c.getTimeCdMgrId()).get();
				WtmOtAppl otAppl = new WtmOtAppl();
				otAppl.setApplId(otA.getApplId());
				otAppl.setCancelYn(otA.getCancelYn());
				otAppl.setHolidayYn(otA.getHolidayYn());
				otAppl.setOldOtApplId(otA.getOldOtApplId());
				otAppl.setOtApplId(otA.getOtApplId());
				otAppl.setOtEdate(otA.getOtEdate());
				otAppl.setOtSdate(otA.getOtSdate());
				otAppl.setOtMinute(otA.getOtMinute());
				otAppl.setReason(otA.getReason());
				otAppl.setReasonCd(otA.getReasonCd());
				otAppl.setRetOtMinute(otA.getRetOtMinute());
				otAppl.setSabun(otA.getSabun());
				otAppl.setSubYn(otA.getSubYn());
				otAppl.setYmd(otA.getYmd());
				
				Date otNightSdate = format.parse(otAppl.getYmd()+n_shm);
				Date otNightEdate = null;
				
				Date eOtNightSdate = null;
				Date eOtNightEdate = null;
				
				if(Integer.parseInt(n_shm) > Integer.parseInt(n_ehm)) {
					Date otNightNextDate = WtmUtil.addDate(otNightSdate, 1);
					otNightEdate = format.parse(fmt.format(otNightNextDate) + n_ehm);
					
					eOtNightSdate = format.parse(otAppl.getYmd()+"0000");
					eOtNightEdate = format.parse(otAppl.getYmd()+n_ehm);
				}else {
					otNightEdate = format.parse(otAppl.getYmd()+n_ehm);
				}

				logger.debug("조출 야간 근무시간 : " + format.format(eOtNightSdate) + "~" + format.format(eOtNightEdate));
				logger.debug("야간 근무시간 : " + format.format(eOtNightSdate) + "~" + format.format(eOtNightEdate));
				logger.debug("연장 근무 신청 시간 : " + format.format(otAppl.getOtSdate()) + "~" + format.format(otAppl.getOtEdate()));
				
				//잔여 기본근로시간 체크 
				//기본근무/시차/근무조 일경우에 
				//잔여 기본근로시간이 있을 경우 BASE 와 OT를 분리하여 체크 한다.  
				List<String> sabunList = new ArrayList<String>();
				sabunList.add(sabun);
				Map<String, Object> empParamMap = new HashMap<>();
				empParamMap.put("sabuns", sabunList);
				empParamMap.put("ymd", otAppl.getYmd());
				empParamMap.put("tenantId", tenantId);
				empParamMap.put("enterCd", enterCd);
				try {
					System.out.println(mapper.writeValueAsString(empParamMap));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<Map<String, Object>> emps = wtmOtApplMapper.getRestOtMinute(empParamMap);
				
				int restMin = 0;
				try {
					System.out.println(mapper.writeValueAsString(emps));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(emps!=null && emps.size()>0) {
					for(Map<String, Object> emp : emps) {
						//Map<String, Object> restMinuteMap = new HashMap<String, Object>();
						/*
						if(emp.get("restOtMinute")!=null && !"".equals(emp.get("restOtMinute"))) {
							int restMin = Integer.parseInt(emp.get("restOtMinute").toString());
							restMinuteMap.put("restOtMinute", restMin);
						}
						*/
						//휴일근무이며
						if(emp.get("holidayYn") != null && "Y".equals(emp.get("holidayYn"))) {
							//기본근무 / 시차출퇴근 / 근무조 일때는 휴일에 잔여 기본근로 시간을 사용할 수 잇다. 
							if(emp.get("workTypeCd") != null && ("BASE".equals(emp.get("workTypeCd")) || "DIFF".equals(emp.get("workTypeCd")) || "WORKTEAM".equals(emp.get("workTypeCd")) ) ) {
								/*
									한주에 대한 정보 조회 계획 및 인정 근무 시간의 합 - 결근 제외 
								 */
								//주간 시작일과 종료일을 구한다.
								//회사별 properties 테이블 기준으로 조회한다.
								Map<String, Object> weekDate = wtmOtApplMapper.getWeekSdateEdate(empParamMap);
								if(weekDate != null) {
									paramMap.put("weekSdate", weekDate.get("weekSdate").toString());
									paramMap.put("weekEdate", weekDate.get("weekEdate").toString());
								}

								Map<String, Object> weekInfo = wtmFlexibleEmpMapper.weekWorkTimeByEmp(paramMap);
								
								if(weekInfo != null && weekInfo.get("workMinute") != null && !weekInfo.get("workMinute").equals("")) {
									//한주기본근로시간 40시간   * 60  = 2400
									int weekWorkMinute = Integer.parseInt(weekInfo.get("weekWorkMinute")+"");
									int exMinute = 0;
									if(weekInfo.get("exMinute") != null && !weekInfo.get("exMinute").equals("")) {
										exMinute = Integer.parseInt(weekInfo.get("exMinute")+"");
									}
									
									System.out.println("weekWorkMinute : " + weekWorkMinute);
									System.out.println("workMinute : " + Integer.parseInt(weekInfo.get("workMinute")+""));
									System.out.println("exMinute : " + exMinute);
									
									restMin = weekWorkMinute - Integer.parseInt(weekInfo.get("workMinute")+"") - exMinute ;
									System.out.println("restMin : " + restMin);
									//restMinuteMap.put("restWorkMinute", restMin);
									
									//EX_MINUTE
								}
								/*	
								if(emp.get("restWorkMinute")!=null && !"".equals(emp.get("restWorkMinute"))) {
									restMin = Integer.parseInt(emp.get("restWorkMinute").toString());
									restMinuteMap.put("restWorkMinute", restMin);
								}
								*/
							}
						}
					}
				}
				boolean isOtSave = true;
				//잔여기본근로시간이 있을 경우 BASE로 넣어줘야한다 나머지는 OT로 
				//유급 휴일은 무조건 OT로 인정해야한다.
				//tenantId = 92 는 한성모터스이며 기본근로 생성을 하지 않는다.
				if(restMin > 0 && !"Y".equals(timeCdMgr.getPaidYn()) && 92 != tenantId) {
					//dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
					Map<String, Object> reCalc = new HashMap<>();
					reCalc.put("tenentId", tenantId);
					reCalc.put("enterCd", enterCd);
					reCalc.put("sabun", otAppl.getSabun());
					reCalc.put("ymd", otAppl.getYmd());
					reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
					reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
					//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
					Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
					//신청 시간의 인정시간(분)을 구한다.
					int calcM = Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"");
					
					//잔여기본근로시간과 비교
					if(restMin >= calcM) {
						//잔여기본근로시간 보다 신청 시간이 작거나 같을 경우 
						//모두 BASE 		
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(otAppl.getOtSdate());
						dayResult.setPlanEdate(otAppl.getOtEdate());
						dayResult.setPlanMinute(calcM);
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
						dayResult.setUpdateId(userId);
						wtmWorkDayResultRepo.save(dayResult);
						isOtSave = false;
					}else {
						//BASE 만큼 만들어 주구 나머지를 isOtSave 로직을 태우자 (시작시간을 변경해서 넘기자)
						//잔여기본근로시간 보다 신청 시간이 작거나 같을 경우 
						//모두 BASE  	
						SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
						Map<String, Object> calcMap = new HashMap<>();
						calcMap.put("tenantId", tenantId);
						calcMap.put("enterCd", enterCd);
						calcMap.put("sabun", sabun);
						calcMap.put("ymd", otAppl.getYmd());
						calcMap.put("sDate", sd.format(otAppl.getOtSdate()));
						calcMap.put("addMinute", restMin); 
						calcMap.put("retDate", ""); 
						wtmFlexibleEmpMapper.addMinuteWithBreakMGR(calcMap);
						//시간대를 자르
						//잔여기본근로종료시간을 구해서 기본근무시간 정보를 만들어 준다. 
						String baseEdateStr = calcMap.get("retDate")+"";
						Date baseEdate = sd.parse(baseEdateStr);
						
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(otAppl.getOtSdate());
						dayResult.setPlanEdate(baseEdate);
						dayResult.setPlanMinute(restMin);
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_BASE);
						dayResult.setUpdateId(userId);
						wtmWorkDayResultRepo.save(dayResult);
						
						//BASE를 생성 후 OT데이터 생성을 위해 시작시간을 바꿔준다.
						otAppl.setOtSdate(baseEdate);
						isOtSave = true;
					}
					
				}
				if(isOtSave) {
					Date otSdate = otAppl.getOtSdate();
					Date otEdate = otAppl.getOtEdate();
					
					if(eOtNightSdate.compareTo(otSdate) > 0) {
						logger.debug("### 출근이 전날이다 이상해.. : " + eOtNightSdate + " ~ " + otSdate);
						otSdate = eOtNightSdate;
					}
					
					if(eOtNightSdate.compareTo(otSdate) < 0 &&
							eOtNightEdate.compareTo(otSdate) > 0) {
						logger.debug("### EARLYNIGHT : " + otSdate + " ~ " + otEdate);
						
						Date insSdate = otSdate;
						Date insEdate = eOtNightEdate;
						//EARLYNIGHT 만
						if(eOtNightEdate.compareTo(otEdate) > 0) {
							logger.debug("### EARLYNIGHT 만 생성");
							insEdate = otEdate;
							otSdate = null;
							otEdate = null;
						}else {
							otSdate = eOtNightEdate;
						}
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(insSdate);
						dayResult.setPlanEdate(insEdate);
						
						Map<String, Object> reCalc = new HashMap<>();
						reCalc.put("tenentId", tenantId);
						reCalc.put("enterCd", enterCd);
						reCalc.put("sabun", otAppl.getSabun());
						reCalc.put("ymd", otAppl.getYmd());
						reCalc.put("shm", sdf.format(insSdate));
						reCalc.put("ehm", sdf.format(insEdate));
						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
						
						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
						 
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_EARLY_NIGHT);
						dayResult.setUpdateId(userId);
						
						wtmWorkDayResultRepo.save(dayResult);
					}
					// otSdate 와 otEdate 이 null아니면 OT + NIGHT / NIGHT 만 체크하면 된다.
					if(otSdate != null && otEdate != null) {
					
						//야간 전에 연장이 끝남 OT만 생성
	//					if(otNightSdate.compareTo(otAppl.getOtEdate()) == 1) {
						if(otNightSdate.compareTo(otEdate) <  0 && 
								otNightEdate.compareTo(otSdate) > 0) {
							
							//NIGHT만
							if(otNightSdate.compareTo(otSdate) <= 0 
									&& otNightEdate.compareTo(otEdate) == 1) {
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(otAppl.getYmd());
								dayResult.setSabun(otAppl.getSabun());
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otEdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", otAppl.getSabun());
								reCalc.put("ymd", otAppl.getYmd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
	
							}
							//OT + NIGHT
							else if(otNightSdate.compareTo(otSdate) == 1) {
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(otAppl.getYmd());
								dayResult.setSabun(otAppl.getSabun());
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otNightSdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", otAppl.getSabun());
								reCalc.put("ymd", otAppl.getYmd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otNightSdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
								
	
								dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(otAppl.getYmd());
								dayResult.setSabun(otAppl.getSabun());
								dayResult.setPlanSdate(otNightSdate);
								dayResult.setPlanEdate(otEdate);
								 
								reCalc.put("shm", sdf.format(otNightSdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
								
							//NIGHT + OT
							} else {
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(otAppl.getYmd());
								dayResult.setSabun(otAppl.getSabun());
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otNightEdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", otAppl.getSabun());
								reCalc.put("ymd", otAppl.getYmd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otNightEdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
								
	
								dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(otAppl.getYmd());
								dayResult.setSabun(otAppl.getSabun());
								dayResult.setPlanSdate(otNightEdate);
								dayResult.setPlanEdate(otEdate);
								 
								reCalc.put("shm", sdf.format(otNightEdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
							}
						}else {
							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							dayResult.setApplId(applId);
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(otAppl.getYmd());
							dayResult.setSabun(otAppl.getSabun());
							dayResult.setPlanSdate(otSdate);
							dayResult.setPlanEdate(otEdate);
							
							//dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
							Map<String, Object> reCalc = new HashMap<>();
							reCalc.put("tenentId", tenantId);
							reCalc.put("enterCd", enterCd);
							reCalc.put("sabun", otAppl.getSabun());
							reCalc.put("ymd", otAppl.getYmd());
							reCalc.put("shm", sdf.format(otSdate));
							reCalc.put("ehm", sdf.format(otEdate));
							//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
							Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
							
							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
							
							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
							dayResult.setUpdateId(userId);
							
							wtmWorkDayResultRepo.save(dayResult);
						}
					}
				
//					
//					//신청부터 야간연장신청이다.
//					if(otAppl.getOtSdate().compareTo(otNightSdate) == 1 ) {
//								
//						if(otAppl.getOtEdate().compareTo(otNightEdate) == 1 ) {
//							WtmWorkDayResult dayResult = new WtmWorkDayResult();
//							dayResult.setApplId(applId);
//							dayResult.setTenantId(tenantId);
//							dayResult.setEnterCd(enterCd);
//							dayResult.setYmd(otAppl.getYmd());
//							dayResult.setSabun(otAppl.getSabun());
//							dayResult.setPlanSdate(otAppl.getOtSdate());
//							dayResult.setPlanEdate(otNightSdate);
//							
//							Map<String, Object> reCalc = new HashMap<>();
//							reCalc.put("tenentId", tenantId);
//							reCalc.put("enterCd", enterCd);
//							reCalc.put("sabun", otAppl.getSabun());
//							reCalc.put("ymd", otAppl.getYmd());
//							reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
//							reCalc.put("ehm", sdf.format(otNightSdate));
//							//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
//							Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
//							
//							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
//							 
//							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
//							dayResult.setUpdateId(userId);
//							
//							wtmWorkDayResultRepo.save(dayResult);
//							
//
//							dayResult = new WtmWorkDayResult();
//							dayResult.setApplId(applId);
//							dayResult.setTenantId(tenantId);
//							dayResult.setEnterCd(enterCd);
//							dayResult.setYmd(otAppl.getYmd());
//							dayResult.setSabun(otAppl.getSabun());
//							dayResult.setPlanSdate(otNightSdate);
//							dayResult.setPlanEdate(otAppl.getOtEdate());
//							 
//							reCalc.put("shm", sdf.format(otNightSdate));
//							reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
//							//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
//							addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
//							
//							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
//							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
//							dayResult.setUpdateId(userId);
//							
//							wtmWorkDayResultRepo.save(dayResult);
//						}else {
//
//							//걍 나이트 오티				
//							WtmWorkDayResult dayResult = new WtmWorkDayResult();
//							dayResult.setApplId(applId);
//							dayResult.setTenantId(tenantId);
//							dayResult.setEnterCd(enterCd);
//							dayResult.setYmd(otAppl.getYmd());
//							dayResult.setSabun(otAppl.getSabun());
//							dayResult.setPlanSdate(otAppl.getOtSdate());
//							dayResult.setPlanEdate(otAppl.getOtEdate());
//							dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
//							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
//							dayResult.setUpdateId(userId);
//							
//							wtmWorkDayResultRepo.save(dayResult);
//						}
//						
//					//야간시간 포함여부를판단 하자 연장야간 시간 시작시간보다 클경우
//					}else if(otAppl.getOtEdate().compareTo(otNightSdate) == 1 ) {
//						 
//						WtmWorkDayResult dayResult = new WtmWorkDayResult();
//						dayResult.setApplId(applId);
//						dayResult.setTenantId(tenantId);
//						dayResult.setEnterCd(enterCd);
//						dayResult.setYmd(otAppl.getYmd());
//						dayResult.setSabun(otAppl.getSabun());
//						dayResult.setPlanSdate(otAppl.getOtSdate());
//						dayResult.setPlanEdate(otNightSdate);
//						
//						Map<String, Object> reCalc = new HashMap<>();
//						reCalc.put("tenentId", tenantId);
//						reCalc.put("enterCd", enterCd);
//						reCalc.put("sabun", otAppl.getSabun());
//						reCalc.put("ymd", otAppl.getYmd());
//						reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
//						reCalc.put("ehm", sdf.format(otNightSdate));
//						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
//						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
//						
//						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
//						 
//						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
//						dayResult.setUpdateId(userId);
//						
//						wtmWorkDayResultRepo.save(dayResult);
//						
//
//						
//						dayResult = new WtmWorkDayResult();
//						dayResult.setApplId(applId);
//						dayResult.setTenantId(tenantId);
//						dayResult.setEnterCd(enterCd);
//						dayResult.setYmd(otAppl.getYmd());
//						dayResult.setSabun(otAppl.getSabun());
//						dayResult.setPlanSdate(otNightSdate);
//						dayResult.setPlanEdate(otAppl.getOtEdate());
//						 
//						reCalc.put("shm", sdf.format(otNightSdate));
//						reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
//						//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
//						addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
//						
//						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
//						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
//						dayResult.setUpdateId(userId);
//						
//						wtmWorkDayResultRepo.save(dayResult);
//					}else {
//						//걍 오티				
//						WtmWorkDayResult dayResult = new WtmWorkDayResult();
//						dayResult.setApplId(applId);
//						dayResult.setTenantId(tenantId);
//						dayResult.setEnterCd(enterCd);
//						dayResult.setYmd(otAppl.getYmd());
//						dayResult.setSabun(otAppl.getSabun());
//						dayResult.setPlanSdate(otAppl.getOtSdate());
//						dayResult.setPlanEdate(otAppl.getOtEdate());
//						
//						//dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
//						Map<String, Object> reCalc = new HashMap<>();
//						reCalc.put("tenentId", tenantId);
//						reCalc.put("enterCd", enterCd);
//						reCalc.put("sabun", otAppl.getSabun());
//						reCalc.put("ymd", otAppl.getYmd());
//						reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
//						reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
//						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
//						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
//						
//						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
//						
//						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
//						dayResult.setUpdateId(userId);
//						
//						wtmWorkDayResultRepo.save(dayResult);
//
//					}
				}
				//승인완료 시 해당 대상자의 통계데이터를 갱신하기 위함.
				rp.put("sabun", otAppl.getSabun());
				rp.put("symd", otAppl.getYmd());
				rp.put("eymd", otAppl.getYmd());

				logger.debug("연장근무시간 result에 저장 완료");

				String stdYmd = paramMap.get("ymd").toString();
				Date today = new Date();
				SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyyMMdd");
				String ymd = sdfYmd.format(today);
				if(Integer.parseInt(ymd) > Integer.parseInt(stdYmd)){ 	// 소급이면 마감돌리기
					Map<String, Object> pMap = new HashMap<String, Object>();
					pMap.put("tenantId", tenantId);
					pMap.put("enterCd", enterCd);
					pMap.put("stdYmd", rp.get("symd")+"");
					pMap.put("sabun", rp.get("sabun")+"");

					wtmflexibleEmpService.calcApprDayInfo(tenantId, enterCd, stdYmd, stdYmd, sabun);
					wtmCalcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, stdYmd, stdYmd);
				}
			}
			
			rp.put("otApplList", otApplList);
		}
			
		return rp;

	}

	@Override
	public ReturnParam applyCanAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		paramMap.put("applId", applId);
		
		//취소하는 근무시간 정보를 지운다.
		List<WtmOtCanAppl> otCanApplList = wtmOtCanApplRepo.findByApplId(applId);
		
		if(otCanApplList!=null && otCanApplList.size()>0) {
			for(WtmOtCanAppl otCanAppl : otCanApplList) {
				Long deletedApplId = null;
				
				/**
				 * 기본근무 , 근무조, 시차 출퇴근일 경우 연장근무 신청 시 잔여 기본근로시간이 남았을 경우 기본근무시간을 생성할 수 있다.
				 *  취소 신청에서 신청서아이디가 잇는 기본근무시간이 있는지를 확인한다.
				 */
				//휴게시간도 지워줌
				List<String> deleteTimeTypeCds = new ArrayList<String>();
				deleteTimeTypeCds.add("BREAK_OT");
				deleteTimeTypeCds.add("BREAK_NIGHT");
				List<WtmWorkDayResult> results =  wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, deleteTimeTypeCds, otCanAppl.getYmd(), otCanAppl.getYmd());
				wtmWorkDayResultRepo.deleteAll(results);
				
				WtmWorkDayResult dayResult = wtmWorkDayResultRepo.findById(otCanAppl.getWorkDayResultId()).orElse(null);
				//지우려는 정보의 신청정보가 있다면 관련된 정보도 같이 지워준다 대체휴일과 같은 정보..
				if(dayResult != null) {
					if(dayResult.getApplId() != null) {
						deletedApplId = dayResult.getApplId(); 
					}
					
					wtmWorkDayResultRepo.delete(dayResult);
					 
					rp.put("sabun", dayResult.getSabun());
					rp.put("symd", dayResult.getYmd());
					rp.put("eymd", dayResult.getYmd());
				}
				
				if(deletedApplId != null) {
					//대체 휴일 정보를 찾자
					List<WtmOtSubsAppl> otSubsAppls = wtmOtSubsApplRepo.findByApplId(deletedApplId);
					if(otSubsAppls != null && otSubsAppls.size() > 0) {
						String currYmd = null;
						paramMap.put("tenantId", tenantId);
						paramMap.put("enterCd", enterCd);
						Map<String, Map<String, Date>> resetBaseTime = new HashMap<String, Map<String, Date>>();
						for(WtmOtSubsAppl otSubsAppl : otSubsAppls) {
							wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(tenantId, enterCd, otSubsAppl.getSubYmd(), otCanAppl.getSabun(), WtmApplService.TIME_TYPE_SUBS, "", otSubsAppl.getSubsSdate(), otSubsAppl.getSubsEdate(), deletedApplId, userId);
						}
					
					}
				}
			}
		}
		return rp;
	}
	
	
	/**
	 * 관리자 연장근무 신청 취소 완료로 변경
	 */
	@Override
	public ReturnParam applyOtCanAdminAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		paramMap.put("applId", applId);
		
		rp.setSuccess("");
		
		//취소하는 근무시간 정보를 지운다.
		List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
		
		if(otApplList!=null && otApplList.size()>0) {
			for(WtmOtAppl otCanAppl : otApplList) {
				
				List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndApplId(tenantId, enterCd, otCanAppl.getSabun(), otCanAppl.getApplId());
				wtmWorkDayResultRepo.deleteAll(results);
				
			}
		} 
		return rp;
	}
	
	@Override
	public ReturnParam applyRegaOtAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		logger.debug("### applyRegaOtAfter");
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("결재가 완료되었습니다.");
		
		ObjectMapper mapper = new ObjectMapper();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		
		//야간근무시간이 포함되어있는 지 확인후 데이터를 찢어넣자
		List<WtmPropertie> properties = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKeyLike(tenantId, enterCd, "OPTION_OT_NIGHT_HHMM_%");
		
		//대상자의 실제 근무 정보를 반영한다
		List<WtmTaaApplDet> taaApplDets = taaApplDetRepo.findByApplId(applId);
		//List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
		
		
		logger.debug("### REGA 연장근무정보 : " + mapper.writeValueAsString(taaApplDets));
		
		if(taaApplDets!=null && taaApplDets.size()>0) {
			
			String n_shm = "";
			String n_ehm = "";
			String en_shm = "";
			String en_ehm = "";
			for(WtmPropertie propertie : properties) {
				if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_S")) {
					n_shm = propertie.getInfoValue(); 
				}else if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_E")) {
					n_ehm = propertie.getInfoValue();
					//조출 심야..ㅡㅡ^
					en_shm = "0000";
					en_ehm = propertie.getInfoValue();
				}
			}
			
			if("".equals(n_shm) || "".equals(n_ehm)) {
				rp.setFail("야간 근무시간 정보가 없습니다. 담당자에게 문의하시기 바랍니다.");
				throw new RuntimeException(rp.get("message").toString());
			}
			/**
			 * REGA는 symd와 eymd가 같다
			 */
			for(WtmTaaApplDet det : taaApplDets) {
				//otA.getYmd()
				WtmWorkCalendar c = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, det.getSymd());
				WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(c.getTimeCdMgrId()).get(); 
				
				Date otNightSdate = format.parse(det.getSymd()+n_shm);
				Date otNightEdate = null;
				
				Date eOtNightSdate = null;
				Date eOtNightEdate = null;
				
				if(Integer.parseInt(n_shm) > Integer.parseInt(n_ehm)) {
					Date otNightNextDate = WtmUtil.addDate(otNightSdate, 1);
					otNightEdate = format.parse(fmt.format(otNightNextDate) + n_ehm);
					
					eOtNightSdate = format.parse(det.getSymd()+"0000");
					eOtNightEdate = format.parse(det.getSymd()+n_ehm);
				}else {
					otNightEdate = format.parse(det.getSymd()+n_ehm);
				}

				logger.debug("조출 야간 근무시간 : " + format.format(eOtNightSdate) + "~" + format.format(eOtNightEdate));
				logger.debug("야간 근무시간 : " + format.format(otNightSdate) + "~" + format.format(otNightEdate));
				logger.debug("연장 근무 신청 시간 : " + det.getSymd() + " " + det.getShm() + "~" + det.getEymd() + " " + det.getEhm() );
				
				boolean isOtSave = true; 
				if(isOtSave) {
					Date otSdate = format.parse(det.getSymd()+det.getShm());
					Date otEdate = format.parse(det.getEymd()+det.getEhm());
					
					if(eOtNightSdate.compareTo(otSdate) > 0) {
						logger.debug("### 출근이 전날이다 이상해.. : " + eOtNightSdate + " ~ " + otSdate);
						otSdate = eOtNightSdate;
					}
					
					if(eOtNightSdate.compareTo(otSdate) < 0 &&
							eOtNightEdate.compareTo(otSdate) > 0) {
						logger.debug("### EARLYNIGHT : " + otSdate + " ~ " + otEdate);
						
						Date insSdate = otSdate;
						Date insEdate = eOtNightEdate;
						//EARLYNIGHT 만
						if(eOtNightEdate.compareTo(otEdate) > 0) {
							logger.debug("### EARLYNIGHT 만 생성");
							insEdate = otEdate;
							otSdate = null;
							otEdate = null;
						}else {
							otSdate = eOtNightEdate;
						}
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(det.getSymd());
						dayResult.setSabun(sabun);
						dayResult.setPlanSdate(insSdate);
						dayResult.setPlanEdate(insEdate);
						
						Map<String, Object> reCalc = new HashMap<>();
						reCalc.put("tenentId", tenantId);
						reCalc.put("enterCd", enterCd);
						reCalc.put("sabun", sabun);
						reCalc.put("ymd", det.getSymd());
						reCalc.put("shm", sdf.format(insSdate));
						reCalc.put("ehm", sdf.format(insEdate));
						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
						
						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
						 
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_NIGHT);
						dayResult.setUpdateId(userId);
						
						wtmWorkDayResultRepo.save(dayResult);
					}
					// otSdate 와 otEdate 이 null아니면 OT + NIGHT / NIGHT 만 체크하면 된다.
					if(otSdate != null && otEdate != null) {
					
						//야간 전에 연장이 끝남 OT만 생성
	//					if(otNightSdate.compareTo(otAppl.getOtEdate()) == 1) {
						if(otNightSdate.compareTo(otEdate) <  0 && 
								otNightEdate.compareTo(otSdate) > 0) {
							
							//NIGHT만
							if(otNightSdate.compareTo(otSdate) <= 0 
									&& otNightEdate.compareTo(otEdate) == 1) {
								logger.debug("### NIGHT 만 생성");
								logger.debug("### otSdate : " + otSdate);
								logger.debug("### otEdate : " + otEdate);
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(det.getSymd());
								dayResult.setSabun(sabun);
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otEdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", sabun);
								reCalc.put("ymd", det.getSymd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_NIGHT);
								dayResult.setUpdateId(userId);
								
								wtmWorkDayResultRepo.save(dayResult);
	
							}
							//OT + NIGHT
							else if(otNightSdate.compareTo(otSdate) == 1) {

								logger.debug("### OT + NIGHT 만 생성");
								
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(det.getSymd());
								dayResult.setSabun(sabun);
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otNightSdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", sabun);
								reCalc.put("ymd", det.getSymd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otNightSdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_OT);
								dayResult.setUpdateId(userId);
								
								dayResult = wtmWorkDayResultRepo.save(dayResult);
								
								logger.debug("### setPlanSdate : " + otSdate);
								logger.debug("### setPlanEdate : " + otNightSdate);
								logger.debug("### dayResult : " + dayResult.toString());
	
								dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(det.getSymd());
								dayResult.setSabun(sabun);
								dayResult.setPlanSdate(otNightSdate);
								dayResult.setPlanEdate(otEdate);
								 
								reCalc.put("shm", sdf.format(otNightSdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_NIGHT);
								dayResult.setUpdateId(userId);
								
								dayResult = wtmWorkDayResultRepo.save(dayResult);

								logger.debug("### setPlanSdate : " + otNightSdate);
								logger.debug("### setPlanEdate : " + otEdate);
								logger.debug("### dayResult : " + dayResult.toString());
								
							//NIGHT + OT
							} else {
								logger.debug("### NIGHT + OT 생성");
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(det.getSymd());
								dayResult.setSabun(sabun);
								dayResult.setPlanSdate(otSdate);
								dayResult.setPlanEdate(otNightEdate);
								
								Map<String, Object> reCalc = new HashMap<>();
								reCalc.put("tenentId", tenantId);
								reCalc.put("enterCd", enterCd);
								reCalc.put("sabun", sabun);
								reCalc.put("ymd", det.getSymd());
								reCalc.put("shm", sdf.format(otSdate));
								reCalc.put("ehm", sdf.format(otNightEdate));
								//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
								 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_NIGHT);
								dayResult.setUpdateId(userId);
								
								dayResult = wtmWorkDayResultRepo.save(dayResult);

								logger.debug("### setPlanSdate : " + otSdate);
								logger.debug("### setPlanEdate : " + otNightEdate);
								logger.debug("### dayResult : " + dayResult.toString());
	
								dayResult = new WtmWorkDayResult();
								dayResult.setApplId(applId);
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(enterCd);
								dayResult.setYmd(det.getSymd());
								dayResult.setSabun(sabun);
								dayResult.setPlanSdate(otNightEdate);
								dayResult.setPlanEdate(otEdate);
								 
								reCalc.put("shm", sdf.format(otNightEdate));
								reCalc.put("ehm", sdf.format(otEdate));
								//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
								addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
								
								dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
								dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_OT);
								dayResult.setUpdateId(userId);
								
								dayResult= wtmWorkDayResultRepo.save(dayResult);
								

								logger.debug("### setPlanSdate : " + otNightEdate);
								logger.debug("### setPlanEdate : " + otEdate);
								logger.debug("### dayResult : " + dayResult.toString());
							}
						}else {

							logger.debug("### OT 생성");
							
							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							dayResult.setApplId(applId);
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(det.getSymd());
							dayResult.setSabun(sabun);
							dayResult.setPlanSdate(otSdate);
							dayResult.setPlanEdate(otEdate);
							
							//dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
							Map<String, Object> reCalc = new HashMap<>();
							reCalc.put("tenentId", tenantId);
							reCalc.put("enterCd", enterCd);
							reCalc.put("sabun", sabun);
							reCalc.put("ymd", det.getSymd());
							reCalc.put("shm", sdf.format(otSdate));
							reCalc.put("ehm", sdf.format(otEdate));
							//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
							Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, userId);
							
							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
							
							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_REGA_OT);
							dayResult.setUpdateId(userId);
							
							dayResult = wtmWorkDayResultRepo.save(dayResult);

							logger.debug("### setPlanSdate : " + otSdate);
							logger.debug("### setPlanEdate : " + otEdate);
							logger.debug("### dayResult : " + dayResult.toString());
							
						}
					}
				 
				}
				//승인완료 시 해당 대상자의 통계데이터를 갱신하기 위함.
				rp.put("sabun", sabun);
				rp.put("symd", det.getSymd());
				rp.put("eymd", det.getEymd());
				
				logger.debug("연장근무시간 result에 저장 완료");
			}
			
			rp.put("regaApplList", taaApplDets);
		}
			
		return rp;

	}
	
}
