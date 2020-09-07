package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.isu.ifw.entity.WtmFlexibleEmpCalc;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeBreakMgr;
import com.isu.ifw.entity.WtmTimeBreakTime;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmCalcMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmTimeBreakMgrRepository;
import com.isu.ifw.repository.WtmTimeBreakTimeRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.vo.WtmFlexibleInfoVO;

@Service
public class WtmCalcServiceImpl implements WtmCalcService {

	private static ObjectMapper mapper = new ObjectMapper();
	
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	private WtmWorkCalendarRepository workCalandarRepo;
	
	@Autowired
	private WtmWorkDayResultRepository workDayResultRepo;
	
	@Autowired
	private WtmFlexibleStdMgrRepository flexibleStdMgrRepo;
	
	@Autowired
	private WtmFlexibleEmpRepository flexibleEmpRepo;

	@Autowired 
	private WtmTimeBreakMgrRepository timebreakMgrRepo;
	
	@Autowired
	private WtmTimeBreakTimeRepository timebreakTimeRepo;
	
	@Autowired
	private WtmCalcMapper calcMapper;
	
	@Autowired
	private WtmTaaCodeRepository taaCodeRepo;
	
	@Autowired private WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Transactional
	public void P_WTM_WORK_DAY_RESULT_CREATE_F(Long tenantId, String enterCd,  String sabun, String ymd, WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr, String userId) {

		logger.debug("P_WTM_WORK_DAY_RESULT_CREATE_F :: ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		
		ObjectMapper mapper = new ObjectMapper();

		WtmFlexibleEmpCalc flexInfo = flexibleEmpRepo.getTotalWorkMinuteAndRealWorkMinute(tenantId, enterCd, sabun, ymd);
//		WtmFlexibleInfoVO flexInfo = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
		try {
			logger.debug("CREATE_F :: workMinuteMap = " + mapper.writeValueAsString(flexInfo));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(flexInfo != null) {
			// ymd가 속한 근무제의 총 소정근로 시간.
			int workMinute = flexInfo.getWorkMinute();
			// ymd가 속한 근무제의 인정 소정근로 시간.
			int sumWorkMinute = flexInfo.getWorkHour() - flexInfo.getBreakhour();
			//int sumWorkMinute = flexInfo.getSumWorkMinute();
			
			logger.debug("CREATE_F :: workMinute = " + workMinute);
			logger.debug("CREATE_F :: sumWorkMinute = " + sumWorkMinute);
			// 소정근로 시간이 이미 다 찼으면 소정근로 시간을 생성하지 않는다. 
			if( workMinute > sumWorkMinute ) {
					
				List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_BASE);
				if(results != null && results.size() > 0) {
					int cnt = 1;
					logger.debug("CREATE_F :: flexStdMgr.getApplyEntrySdateYn() = " + flexStdMgr.getApplyEntrySdateYn());
					logger.debug("CREATE_F :: flexStdMgr.getApplyEntryEdateYn() = " + flexStdMgr.getApplyEntryEdateYn());
					WtmWorkCalendar calendar = workCalandarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
					for(WtmWorkDayResult result : results) {
						
						logger.debug("CREATE_F :: cnt = " + cnt);
						
						Date sDate = null;
						Date eDate = null;
						
						//타각시간에서 벗어난 데이터는 패스 한다 .
						if(calendar.getEntrySdate().compareTo(result.getPlanEdate()) < 0
								&& calendar.getEntryEdate().compareTo(result.getPlanSdate()) > 0) {
							sDate = result.getPlanSdate();
							eDate = result.getPlanEdate();
							/*
							 * calcaApprDayReset 여기서 했다 아래는
							if(results.size() > 1) {
			
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
								
								
							}else {
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
								
							}
	
							logger.debug("CREATE_F :: CALL P_WTM_WORK_DAY_RESULT_UPDATE_T " );
							logger.debug("CREATE_F :: sDate = " + sDate );
							logger.debug("CREATE_F :: eDate = " + eDate );
							*/
							//안에서 생성한 시간을 더해준다.
							sumWorkMinute = sumWorkMinute + this.P_WTM_WORK_DAY_RESULT_UPDATE_T(flexStdMgr, timeCdMgr, result, sDate, eDate, calendar.getEntrySdate(), calendar.getEntryEdate(),  sumWorkMinute, workMinute, userId);
						}
						cnt++;
					}
				}
			/*	
			}else {
				List<WtmWorkDayResult> result = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_BASE);
				for(WtmWorkDayResult res : result) {
					res.setSabun(res.getSabun()+"_bk");
					res.setUpdateDate(new Date());
					res.setUpdateId(userId);
					
					logger.debug("CREATE_F :: BASE_OVER workDayResultRepo.save : " + res.getSabun()+"_bk  : " + res.getYmd());
					workDayResultRepo.save(res);
				}
				*/
			}
		}
	}
	
	/**
	 * 인정시간 계산 
	 * @param flexStdMgr
	 * @param timeCdMgr
	 * @param result
	 * @param sDate
	 * @param eDate
	 * @param entrySdate
	 * @param entryEdate
	 * @param sumWorkMinute
	 * @param workMinute
	 * @param userId
	 */
	@Transactional
	public int P_WTM_WORK_DAY_RESULT_UPDATE_T(WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr, WtmWorkDayResult result, Date sDate, Date eDate, Date entrySdate, Date entryEdate, int sumWorkMinute, int workMinute, String userId ) {
		// System.out.println("UPDATE_T :: START :: " );
		// System.out.println("UPDATE_T :: entrySdate :: " + entrySdate);
		// System.out.println("UPDATE_T :: sDate :: " + sDate);
		// System.out.println("UPDATE_T :: flexStdMgr :: " + flexStdMgr.toString());
		
		if((workMinute - sumWorkMinute) <= 0) {
			return 0;
		}
		
		Date calcSdate = this.WorkTimeCalcApprDate(entrySdate, sDate, flexStdMgr.getUnitMinute(), "S");
		Date calcEdate = this.WorkTimeCalcApprDate(entryEdate, eDate, flexStdMgr.getUnitMinute(), "E");
		
		logger.debug("UPDATE_T :: calcSdate :: " + calcSdate);
		logger.debug("UPDATE_T :: calcEdate :: " + calcEdate);
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		if( !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_OT)
				&& !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_NIGHT)
				) {
			if( !flexStdMgr.getWorkShm().equals("") && !flexStdMgr.getWorkEhm().equals("") ) {
				try {
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
						logger.debug("시작일 근무 제한 시간 적용. calcSdate : " + calcSdate + " limitSdate : " + limitSdate);
						calcSdate = limitSdate;
					}
					
					if(calcEdate.compareTo(limitEdate) > 0) {
						logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
						calcEdate = limitEdate;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		logger.debug("UPDATE_T :: " + sdf.format(calcSdate) + " : " + sdf.format(calcEdate));
		int apprMinute = this.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute());
		int breakMinute = 0;
		if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
			breakMinute = this.getBreakMinuteIfBreakTimeMGR(calcSdate, calcEdate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
			logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : apprMinute " + apprMinute);
			logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_MGR + " : breakMinute " + breakMinute);
			apprMinute = apprMinute - breakMinute;
			breakMinute = 0;
		}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
			breakMinute = this.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), apprMinute);
			logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : apprMinute " + apprMinute);
			logger.debug("UPDATE_T :: " + WtmApplService.BREAK_TYPE_TIME + " : breakMinute " + breakMinute);
		//}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
		}
		
		//잔여시간이 현재 근무 시간보다 작으면 
		logger.debug("UPDATE_T :: (workMinute - sumWorkMinute) = " + (workMinute - sumWorkMinute));
		logger.debug("UPDATE_T :: (apprMinute - breakMinute) = " +  (apprMinute - breakMinute));
		
		if ((workMinute - sumWorkMinute) < (apprMinute - breakMinute)) {
			//종료시간을 다시 계산해야한다.
			logger.debug("UPDATE_T :: timeCdMgr.getBreakTypeCd() = " +  timeCdMgr.getBreakTypeCd());	
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
				
				calcEdate = this.P_WTM_DATE_ADD_FOR_BREAK_MGR(calcSdate, (workMinute - sumWorkMinute), timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				
				try {
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
						logger.debug("시작일 근무 제한 시간 적용. calcSdate : " + calcSdate + " limitSdate : " + limitSdate);
						calcSdate = limitSdate;
					}
					
					if(calcSdate.compareTo(limitEdate) >= 0) {
						logger.debug("근무 제한 시간보다 이후 시간입니다.");
						return 0;
					}
					
					if(calcEdate.compareTo(limitEdate) > 0) {
						logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
						calcEdate = limitEdate;
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				apprMinute = this.WtmCalcMinute(sdf.format(calcSdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute()) -  this.getBreakMinuteIfBreakTimeMGR(calcSdate, calcEdate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				breakMinute = 0;
				/*
				SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
				Map<String, Object> calcMap = new HashMap<>();
				calcMap.put("tenantId", result.getTenantId());
				calcMap.put("enterCd", result.getEnterCd());
				calcMap.put("sabun", result.getSabun());
				calcMap.put("ymd", result.getYmd());
				calcMap.put("sDate", calcSdate);
				calcMap.put("addMinute", (workMinute - sumWorkMinute)); 
				calcMap.put("retDate", ""); 
				wtmFlexibleEmpMapper.addMinuteWithBreakMGR(calcMap);
				//시간대를 자르
				//잔여소정근로종료시간을 구해서 기본근무시간 정보를 만들어 준다. 
				String baseEdateStr = calcMap.get("retDate")+"";
				try {
					calcEdate = sd.parse(baseEdateStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				*/
				logger.debug("UPDATE_T :: retDate = " +  calcEdate);
			}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(calcSdate);
				cal.add(Calendar.MINUTE, (workMinute - sumWorkMinute) + breakMinute );
				calcEdate = cal.getTime();
				apprMinute = (workMinute - sumWorkMinute) + breakMinute;
			}
		}

		logger.debug("UPDATE_T :: calcSdate = " + calcSdate);
		logger.debug("UPDATE_T :: calcEdate = " + calcEdate);
		logger.debug("UPDATE_T :: apprMinute = " + apprMinute);
		//result.setPlanSdate(calcSdate);
		//result.setPlanEdate(calcEdate);
		//result.setPlanMinute(apprMinute);
		result.setApprSdate(calcSdate);
		result.setApprEdate(calcEdate);
		result.setApprMinute(apprMinute);
		result.setUpdateDate(new Date());
		result.setUpdateId(userId);
		
		logger.debug("UPDATE_T :: workDayResultRepo.save = " + result);
		workDayResultRepo.save(result);
		
		return apprMinute;
	}

	/**
	 * 고정 OT 일괄 소진일 경우에만 사용된다. 
	 * flexStdMgr.getDefaultWorkUseYn().equalsIgnoreCase("Y") && flexStdMgr.getFixotUseType().equalsIgnoreCase("ALL"))
	 * 소정근로시간이 모두 소진 되었을 경우 고정 OT를 생성한다 .
	 * 소정근로시간 생성 시 unplannedYn == 'Y' 일경우만 생성한다.
	 * 
	 */
	@Transactional
	@Override
	public void P_WTM_WORK_DAY_RESULT_CREATE_N(WtmWorkCalendar calendar, WtmFlexibleStdMgr flexibleStdMgr, WtmTimeCdMgr timeCdMgr, Long tenantId, String enterCd,  String sabun, String ymd, int addSumWorkMinute, String userId) {
		
		logger.debug("UPDATE_T :: P_WTM_WORK_DAY_RESULT_CREATE_N");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		paramMap.put("sYmd", ymd);
		paramMap.put("eYmd", ymd);

		/**
		 * 근무제도 기간내 총 소정근로 시간과 ymd 까지의 근무 시간의 합을 구한다.
		 */
		WtmFlexibleEmpCalc flexInfo = flexibleEmpRepo.getTotalWorkMinuteAndRealWorkMinute(tenantId, enterCd, sabun, ymd);
		System.out.println("**************flexInfo " + flexInfo.toString());
		
//		WtmFlexibleInfoVO flexInfo = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
//		logger.debug("flexInfo :: WtmFlexibleInfoVO " + flexInfo.toString());
		if(flexInfo != null) {
			int workMinute = flexInfo.getWorkMinute();
			int sumWorkMinute = (flexInfo.getWorkHour() - flexInfo.getBreakhour()) + addSumWorkMinute;
	
//			// ymd가 속한 근무제의 총 소정근로 시간.
//			int workMinute = flexInfo.getWorkMinute();
//			// ymd가 속한 근무제의 인정 소정근로 시간. -- 9840
//			int sumWorkMinute = (flexInfo.getOtMinute() - Integer.parseInt(flexInfo.getNote())) + addSumWorkMinute;
//			int sumWorkMinute = flexInfo.getSumWorkMinute() + addSumWorkMinute;
			
			WtmFlexibleInfoVO calendarMap = calcMapper.getCalendarInfoByYmdAndEntryIsNotNullAndisNotHoliday(paramMap);
			try {
				logger.debug("CREATE_N :: calendarMap = " + mapper.writeValueAsString(calendarMap));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			logger.debug("CREATE_N :: workMinute = " + workMinute);
			logger.debug("CREATE_N :: sumWorkMinute = " + sumWorkMinute);
			
			// 소정근로 시간이 이미 다 찼으면 소정근로 시간을 생성하지 않는다. 
			if( workMinute > sumWorkMinute ) {

				
				if(calendarMap != null) {
					
					String unplannedYn = calendarMap.getUnplannedYn();

					logger.debug("CREATE_N :: unplannedYn = " + unplannedYn);
					
					if(unplannedYn.equals("Y")) {
						List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
						if(dayResult == null || dayResult.size() == 0) {
							Date entrySdate = calendarMap.getEntrySdate();
							Date entryEdate = calendarMap.getEntryEdate();
							int unitMinute = calendarMap.getUnitMinute();
							Long timeCdMgrId = calendarMap.getTimeCdMgrId();
							
							String breakTypeCd = calendarMap.getBreakTypeCd();
							/**
							 * P_WTM_WORK_DAY_RESULT_CREATE_T
							 */
							int addApprMinute = this.P_WTM_WORK_DAY_RESULT_CREATE_T(flexibleStdMgr, tenantId, enterCd, sabun, ymd, timeCdMgrId, entrySdate, entryEdate, unitMinute, WtmApplService.TIME_TYPE_BASE, breakTypeCd, workMinute, sumWorkMinute, userId);
								
							/**
							 *  if v_work_minute <= v_sum_work_minute then 이부분 해야한다.
							 */
//							
//							flexInfo = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
//							workMinute = flexInfo.getWorkMinute();
//							sumWorkMinute = flexInfo.getSumWorkMinute();
							logger.debug("CREATE_N :: RECALL = ");
							logger.debug("CREATE_N :: workMinute = " + workMinute);
							logger.debug("CREATE_N :: addApprMinute = " + addApprMinute);
							logger.debug("CREATE_N :: sumWorkMinute = " + sumWorkMinute);
							sumWorkMinute = sumWorkMinute + addApprMinute;
							logger.debug("CREATE_N :: sumWorkMinute = " + sumWorkMinute);
							if( addApprMinute > 0 ) {
								
								this.P_WTM_WORK_DAY_RESULT_CREATE_N(calendar, flexibleStdMgr, timeCdMgr, tenantId, enterCd, sabun, ymd, addApprMinute,  "RECALL_" + userId);
								return;
							}
						}
					}
					
				}
			}else {
				// 고정 FIXOT 정보를 생성하자 
				// 
				if(calendarMap != null ) {
					String defaultWorkUseYn = calendarMap.getDefaultWorkUseYn();
					String fixotUseType = calendarMap.getFixotUseType();
					
					logger.debug("CREATE_N :: defaultWorkUseYn = " + defaultWorkUseYn);
					logger.debug("CREATE_N :: fixotUseType = " + fixotUseType);
					if(defaultWorkUseYn.equals("Y") && fixotUseType.equals("ALL")) {
						
						Date fixOtSdate = calendar.getEntrySdate();
						Date fixOtEdate = calendar.getEntryEdate();
						//기본근무 정보들을 가지고 온다.
						List<WtmWorkDayResult> baseResults = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
						if(baseResults != null && baseResults.size() > 0) {
							for(WtmWorkDayResult result : baseResults) {
								//인정시간이 0보다 큰 경우만 인정한다
								if(result.getApprMinute() != null && !result.getApprMinute().equals("") && result.getApprMinute() > 0) {
									//base 구간이 비어 있을 경우는 없다..
									//base 09:00 ~ 10:00
									//rega 10:00 ~ 13:00
									//base 13:00 ~ 15:00 라면 15를 찾아야한디.. 
									// 위의 경우 09:00 ~ 10:00 이게 0은 말이 안된다. 0일 경우엔 타각을 늦게 했겠지.
									if(fixOtSdate == null || fixOtSdate.compareTo(result.getApprEdate()) < 0) {
										fixOtSdate= result.getApprEdate();
									}
									
									
								}
							}
						}
						
						WtmFlexibleInfoVO fixMap = calcMapper.getTotalFixOtMinuteAndRealFixOtkMinute(paramMap);
						try {
							logger.debug("CREATE_N :: fixMap = " + mapper.writeValueAsString(fixMap));
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int limitMinute = 0, useMinute = 0;
						if(fixMap != null && !fixMap.equals("")) {
							limitMinute = fixMap.getFixotUseLimit();
							useMinute = fixMap.getSumFixOtMinute();
						}
						logger.debug("CREATE_N :: limitMinute = " + limitMinute);
						logger.debug("CREATE_N :: useMinute = " + useMinute);
						logger.debug("CREATE_N :: fixOtSdate = " + fixOtSdate);
						logger.debug("CREATE_N :: fixOtEdate = " + fixOtEdate);
						this.createFixOt(calendar, flexibleStdMgr, timeCdMgr, WtmApplService.TIME_TYPE_FIXOT, fixOtSdate, fixOtEdate, limitMinute, useMinute);
						
						/*
						
						//List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, timeTypeCd, symd, eymd)
						 
						//List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
						//List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
						List<String> timeTypes = new ArrayList<String>();
						timeTypes.add(WtmApplService.TIME_TYPE_BASE);
						timeTypes.add(WtmApplService.TIME_TYPE_REGA);
						List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdInAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(timeTypes, tenantId, enterCd, sabun, ymd);
						
						String breakTypeCd = calendarMap.getBreakTypeCd();

						Date entrySdate = calendarMap.getEntrySdate();
						Date entryEdate = calendarMap.getEntryEdate();
						int unitMinute = calendarMap.getUnitMinute();
						Long timeCdMgrId = calendarMap.getTimeCdMgrId();
						
						String workShm = calendarMap.getWorkShm();
						String workEhm = calendarMap.getWorkEhm();

						try {
							logger.debug("CREATE_N :: dayResult = " + mapper.writeValueAsString(dayResult));
						} catch (JsonProcessingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(dayResult != null && dayResult.size() > 0) {
							int cnt = 1;
							for(WtmWorkDayResult result : dayResult) {
								//BASE 뒤에 생성해야하기 떄문에 마지막 데이터 정보로 해보자
								if(cnt < dayResult.size()) {
									cnt++;
									continue;
								}
								int limitFixotMinute = calendarMap.getFixotUseLimit();
								
								String sYmd = calendarMap.getSymd();
								String eYmd = null;
								Calendar cal = Calendar.getInstance();
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
								try {
									cal.setTime(sdf.parse(ymd));
									cal.add(Calendar.DATE, -1);
									//기준일 전날까지 FIXOT를 계산하기 위함
									eYmd = sdf.format(cal.getTime());
									
									
									
								} catch (ParseException e) {
									e.printStackTrace();
								}
								logger.debug("CREATE_N :: eYmd = " + eYmd);
								
								if(eYmd != null) {
									if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
										//call P_WTM_WORK_DAY_RESULT_OTFIX_C(P_FLEXIBLE_EMP_ID, v_ymd, v_ymd, CONCAT('FIXC',P_ID) );
										P_WTM_WORK_DAY_RESULT_OTFIX_C(tenantId, enterCd, sabun, ymd, timeCdMgrId, sYmd, eYmd, result.getPlanEdate(), result.getApprEdate(), entryEdate, limitFixotMinute, unitMinute, userId);
									}else {
										P_WTM_WORK_DAY_RESULT_OTFIX_T_D(tenantId, enterCd, sabun, ymd, timeCdMgrId, sYmd, eYmd, result.getApprEdate(), entryEdate, limitFixotMinute, unitMinute, userId);
									}
								}
							}
						}else {
							this.createFixOt(flexibleStdMgr, tenantId, enterCd, sabun, ymd, timeCdMgrId, entrySdate, entryEdate, unitMinute, breakTypeCd, userId);
						}
						*/
					}
					
				}
			}
		}
		
		
		/*
		// 이미 소정근로시간이 다 찼을 경우 남아있는 남아 있는 계획 시간들은 없애햐한다.
		// 이유는 계획시간으로 남아있으면 인정시간으로 계산하기 때문에 없애야한다. 그냥 지울수 없으니 다른 타입으로 백업을 하자.
		if(workMinute > 0 && workMinute == sumWorkMinute) {
			List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
			if(dayResult != null) {
				dayResult.setApprSdate(null);
				dayResult.setApprEdate(null);
				dayResult.setApprMinute(null);
				dayResult.setUpdateDate(new Date());
				dayResult.setUpdateId(userId);
				
				workDayResultRepo.save(dayResult);
				
			}
		}
		*/
		
	} 
	public void createFixOt(WtmWorkCalendar calendar, WtmFlexibleStdMgr flexibleStdMgr, WtmTimeCdMgr timeCdMgr, String timeTypeCd, Date sDate, Date eDate, int limitMinute, int useMinute) {
		// to-do 제한시간이랑 사용시간 적용
		
		logger.debug("createFixOt start");
		
		//연장근무 시간이 아니면
		if(!timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT)) {
			// 등록된 시간이 시작 시분보다 종료 시분이 적으면 0시가 넘어간 시간이다. 근무일 다음날을 종료 시간으로 셋팅 하기 위함이다.
			String shm = flexibleStdMgr.getWorkShm();
			String ehm = flexibleStdMgr.getWorkEhm();
			SimpleDateFormat yMdHm = new SimpleDateFormat("yyyyMMddHHmm");
			
			if(shm != null && !shm.equals("") && ehm != null && !ehm.equals("")) {
				try {
					Date limitSdate = yMdHm.parse(calendar.getYmd()+shm);
					Date limitEdate = yMdHm.parse(calendar.getYmd()+ehm);
					//  종료시간이 작을 경우 다음날로 본다. 
					if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(limitEdate);
						cal.add(Calendar.DATE, 1);
						limitEdate = cal.getTime();
					}
					
					//근무제한시간보다 종료시간이 클 경우 
					if(eDate.compareTo(limitEdate) > 0) {
						//근무제한시간으로 자른다. 
						eDate = limitEdate;
					}
					
					if(sDate.compareTo(limitSdate) < 0) { // && eDate.compareTo(limitEdate) > -1) {
						sDate = limitSdate;
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndYmdAndSabunAndApprEdateAfterAndApprSdateBeforeAndApprMinuteGreaterThenAndApprMinuteIsNotNullOrderByApprSdateAsc(calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), sDate, eDate, 0);
		
		boolean nextDataCheck = false;
		//이전 데이터 종료일
		Date preEdate = null;

		int calcWorkMinute = (limitMinute - useMinute);
		
		if(calcWorkMinute > 0) {
			
			
			int sumApprMinute = 0;
			int sumBreakMinute = 0;
			//FIXOT가 찢어져서 생성된다.큰일이다. 
			// 찢어져서생성될 FIXOT 의 총 시간을 먼저 구한 다으면 브레이크 타임의 합을 구해 잔여시간 만큼만 다시 만들어줘야한다.. 이럴수가..
			logger.debug("results size : " + results.size());
			if(results.size() > 0) {
				Date loopSdate = sDate;
				for(WtmWorkDayResult r : results) {
					logger.debug("WtmWorkDayResult : " + r);
					
					if(nextDataCheck) {
						if(preEdate.compareTo(r.getApprSdate()) < 0 && eDate.compareTo(r.getApprSdate()) > 0) {
							Date calcSdate = preEdate; 
							Date calcEdate = r.getApprSdate();
							// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
							// 근무시간을 계산 하자
							Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							sumApprMinute += Integer.parseInt(calcMap.get("apprMinute")+"");
							loopSdate = r.getApprEdate();
						}
						nextDataCheck = false;
					}
					if(loopSdate.compareTo(r.getApprSdate()) < 0) {
						Date calcSdate = loopSdate; 
						Date calcEdate = r.getApprSdate();
						
						// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
						// 근무시간을 계산 하자
						Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						sumApprMinute += Integer.parseInt(calcMap.get("apprMinute")+"");
						loopSdate = r.getApprEdate();
						
						if(eDate.compareTo(r.getApprEdate()) > 0) {
							//다음데이터를 확인해야한다. 
							nextDataCheck = true;
							preEdate = r.getApprEdate();
						}
						
					}else if(eDate.compareTo(r.getApprEdate()) > 0) {
						//다음데이터를 확인해야한다. 
						nextDataCheck = true;
						preEdate = r.getApprEdate();
					}
					
				}
				if(nextDataCheck) {
					//다음 데이터를 체크해야하는데 없었을 경우 
					//남은 기간을 생성한다.  
					if(preEdate.compareTo(eDate) < 0) {
						Date calcSdate = preEdate; 
						Date calcEdate = eDate;
						
						// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
						// 근무시간을 계산 하자
						Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						sumApprMinute += Integer.parseInt(calcMap.get("apprMinute")+"");
					}
					nextDataCheck = false;
				}
				if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
					sumBreakMinute = getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), sumApprMinute);
				} 
	
				logger.debug("calcWorkMinute : " + calcWorkMinute);
				logger.debug("sumApprMinute : " + sumApprMinute);
				logger.debug("sumBreakMinute : " + sumBreakMinute);
				
				int createLimitMinute = 0;
				if(calcWorkMinute < (sumApprMinute - sumBreakMinute)) {
					logger.debug("생성하려는 고정 OT 시간이 더 많다. 짤라야 한다.");
					if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
						logger.debug("잔여시간대비 휴게시간을 다시 구한다. : " + calcWorkMinute);
						sumBreakMinute = getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), calcWorkMinute);
						logger.debug("sumBreakMinute : " + sumBreakMinute);
						createLimitMinute = calcWorkMinute + sumBreakMinute;
					}else {
						createLimitMinute = calcWorkMinute;
					}
				}else {
					createLimitMinute = sumApprMinute + sumBreakMinute;
				}
				
				

				loopSdate = sDate;
				logger.debug("createLimitMinute 만큼만 만들자 : " + createLimitMinute);
				for(WtmWorkDayResult r : results) {
					
					//잔여시간이 있어야한다. 
					if(calcWorkMinute > 0) {
					
						if(nextDataCheck) {
			
							//다음 데이터가 eDate 보다 시작 인정시각이 뒤 일수 없다 그럼 쿼리가 이상
							if(preEdate.compareTo(r.getApprSdate()) < 0 && eDate.compareTo(r.getApprSdate()) > 0) {
								Date calcSdate = preEdate; 
								Date calcEdate = r.getApprSdate();
								
								int apprMinute = 0;
								// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
								// 근무시간을 계산 하자
								Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
								
								logger.debug("createFixOt :: calcWorkMinute = " + calcWorkMinute);
								logger.debug("createFixOt :: apprMinute = " + apprMinute);
								//잔여 근무시간이 있을 경우 
								if( createLimitMinute > 0) {
									//잔여 근무시간을 초과하지 않을 경우 
									if( createLimitMinute < (apprMinute )) {
										//데이터를 만든다. 
									//} else {
										//초과할 경우 잔여 시간만큼 생성해야한다. 
										Calendar cal = Calendar.getInstance();
										cal.setTime(calcSdate);
										cal.add(Calendar.MINUTE, createLimitMinute);
										//잔여시간 만큼만 
										calcEdate = cal.getTime();
										
										//다시 계산한다. 
										calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
										apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
				
										logger.debug("createFixOt re:: apprMinute = " + apprMinute);
										createLimitMinute = 0;
									}else {
										createLimitMinute = createLimitMinute - apprMinute;
									}
								}
								
								logger.debug("createFixOt :: createLimitMinute = " + createLimitMinute);
								
								
								WtmWorkDayResult newResult = new WtmWorkDayResult();
								newResult.setTenantId(calendar.getTenantId());
								newResult.setEnterCd(calendar.getEnterCd());
								newResult.setYmd(calendar.getYmd());
								newResult.setSabun(calendar.getSabun());
								newResult.setPlanSdate(calcSdate);
								newResult.setPlanEdate(calcEdate);
								newResult.setApprSdate(calcSdate);
								newResult.setApprEdate(calcEdate);
								newResult.setTimeTypeCd(timeTypeCd);
								
								newResult.setPlanMinute(apprMinute);
								newResult.setApprMinute(apprMinute);
								newResult.setUpdateDate(new Date());
								newResult.setUpdateId("createFixOt1");
								
								workDayResultRepo.save(newResult);
								
								loopSdate = calcEdate;
							}
							nextDataCheck = false;
						}
						
						// 시작시각 보다 빠를 경우 
						logger.debug("sDate.compareTo(r.getApprSdate()) : " + sDate.compareTo(r.getApprSdate()));
						if(loopSdate.compareTo(r.getApprSdate()) < 0) {
							
							Date calcSdate = sDate; 
							Date calcEdate = r.getApprSdate();
							
							int apprMinute = 0;
							// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
							// 근무시간을 계산 하자
							Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
							
							logger.debug("createFixOt :: calcWorkMinute = " + calcWorkMinute);
							logger.debug("createFixOt :: apprMinute = " + apprMinute);
							//잔여 근무시간이 있을 경우 
							if( createLimitMinute > 0) {
								//잔여 근무시간을 초과하지 않을 경우 
								if( createLimitMinute < (apprMinute )) {
									//데이터를 만든다. 
								//} else {
									//초과할 경우 잔여 시간만큼 생성해야한다. 
									Calendar cal = Calendar.getInstance();
									cal.setTime(calcSdate);
									cal.add(Calendar.MINUTE, createLimitMinute);
									//잔여시간 만큼만 
									calcEdate = cal.getTime();
									
									//다시 계산한다. 
									calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
									apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
			
									logger.debug("createFixOt re:: apprMinute = " + apprMinute);
									createLimitMinute = 0;
								}else {
									createLimitMinute = createLimitMinute - apprMinute;
								}
							}
							
							logger.debug("createFixOt :: createLimitMinute = " + createLimitMinute);
							
							WtmWorkDayResult newResult = new WtmWorkDayResult();
							newResult.setTenantId(calendar.getTenantId());
							newResult.setEnterCd(calendar.getEnterCd());
							newResult.setYmd(calendar.getYmd());
							newResult.setSabun(calendar.getSabun());
							newResult.setPlanSdate(calcSdate);
							newResult.setPlanEdate(calcEdate);
							newResult.setApprSdate(calcSdate);
							newResult.setApprEdate(calcEdate);
							newResult.setTimeTypeCd(timeTypeCd);
							
							newResult.setPlanMinute(apprMinute);
							newResult.setApprMinute(apprMinute);
							newResult.setUpdateDate(new Date());
							newResult.setUpdateId("createFixOt2");
							
							workDayResultRepo.save(newResult);
							loopSdate = calcEdate;
							
							//그런데? 종료시간도 포함된 시각이라면?
							//09:00 ~ 15:00 인데 r 이 10:00~11:00 인지 체크
							logger.debug("eDate.compareTo(r.getApprEdate()) : " + eDate.compareTo(r.getApprEdate()));
							if(eDate.compareTo(r.getApprEdate()) > 0) {
								//다음데이터를 확인해야한다. 
								nextDataCheck = true;
								preEdate = r.getApprEdate();
							}
						}else if(eDate.compareTo(r.getApprEdate()) > 0) {
							//다음데이터를 확인해야한다. 
							nextDataCheck = true;
							preEdate = r.getApprEdate();
						}
						
					}else {
						break;
					}

				}
				
				if(nextDataCheck &&  createLimitMinute > 0) {
					//다음 데이터를 체크해야하는데 없었을 경우 
					//남은 기간을 생성한다.  
					if(preEdate.compareTo(eDate) < 0) {
						Date calcSdate = preEdate; 
						Date calcEdate = eDate;
						
						int apprMinute = 0;
						// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
						// 근무시간을 계산 하자
						Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
						
						logger.debug("createFixOt :: calcWorkMinute = " + calcWorkMinute);
						logger.debug("createFixOt :: apprMinute = " + apprMinute);
						//잔여 근무시간이 있을 경우 
						if( createLimitMinute > 0) {
							//잔여 근무시간을 초과하지 않을 경우 
							if( createLimitMinute < (apprMinute )) {
								//데이터를 만든다. 
							//} else {
								//초과할 경우 잔여 시간만큼 생성해야한다. 
								Calendar cal = Calendar.getInstance();
								cal.setTime(calcSdate);
								cal.add(Calendar.MINUTE, createLimitMinute);
								//잔여시간 만큼만 
								calcEdate = cal.getTime();
								
								//다시 계산한다. 
								calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
		
								logger.debug("createFixOt re:: apprMinute = " + apprMinute);
								createLimitMinute = 0;
							}else {
								createLimitMinute = createLimitMinute - apprMinute;
							}
						}
						
						logger.debug("createFixOt :: createLimitMinute = " + createLimitMinute);
						
						WtmWorkDayResult newResult = new WtmWorkDayResult();
						newResult.setTenantId(calendar.getTenantId());
						newResult.setEnterCd(calendar.getEnterCd());
						newResult.setYmd(calendar.getYmd());
						newResult.setSabun(calendar.getSabun());
						newResult.setPlanSdate(calcSdate);
						newResult.setPlanEdate(calcEdate);
						newResult.setApprSdate(calcSdate);
						newResult.setApprEdate(calcEdate);
						newResult.setTimeTypeCd(timeTypeCd);
						
						newResult.setPlanMinute(apprMinute);
						newResult.setApprMinute(apprMinute);
						newResult.setUpdateDate(new Date());
						newResult.setUpdateId("createFixOt-last");
						
						workDayResultRepo.save(newResult);
					}
				}
				
				//if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
					// workDayResult 에 휴게시간을 만들어 준다. 
				//	this.createWorkDayResultForBreakTime(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd(), (timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE))?"BREAK":"BREAK_FIXOT", "APPR", sumBreakMinute, "createWorkDayResultForBreakTime");
				//}
				
				logger.debug("createFixOt end");
			}else {
				P_WTM_WORK_DAY_RESULT_CREATE_T(flexibleStdMgr, calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd(), timeCdMgr.getTimeCdMgrId(), sDate, eDate, flexibleStdMgr.getUnitMinute(), WtmApplService.TIME_TYPE_FIXOT, timeCdMgr.getBreakTypeCd(), limitMinute, useMinute, "fixot");
			}
		}
	}

	@Transactional
	public void createFixOt(WtmFlexibleStdMgr flexibleStdMgr, Long tenantId, String enterCd, String sabun, String ymd, Long timeCdMgrId, Date entrySdate, Date entryEdate, int unitMinute, String breakTypeCd, String userId ) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		WtmFlexibleInfoVO fixMap = calcMapper.getTotalFixOtMinuteAndRealFixOtkMinute(paramMap);
		try {
			logger.debug("CREATE_N :: fixMap = " + mapper.writeValueAsString(fixMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int limitMinute = 0, useMinute = 0;
		if(fixMap != null && !fixMap.equals("")) {
			limitMinute = fixMap.getFixotUseLimit();
			useMinute = fixMap.getSumFixOtMinute();
		}
		logger.debug("CREATE_N :: limitMinute = " + limitMinute);
		logger.debug("CREATE_N :: useMinute = " + useMinute);
		P_WTM_WORK_DAY_RESULT_CREATE_T(flexibleStdMgr, tenantId, enterCd, sabun, ymd, timeCdMgrId, entrySdate, entryEdate, unitMinute, WtmApplService.TIME_TYPE_FIXOT, breakTypeCd, limitMinute, useMinute, userId);
	}
	
	/**
	 * TIMEFIX 
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param ymd
	 * @param timeCdMgrId
	 * @param sYmd
	 * @param eYmd
	 * @param planEdate
	 * @param apprEdate
	 * @param entryEdate
	 * @param limitFixotMinute
	 * @param unitMinute
	 * @param userId
	 */
	@Transactional
	public void P_WTM_WORK_DAY_RESULT_OTFIX_T_D(Long tenantId, String enterCd, String sabun, String ymd, long timeCdMgrId, String sYmd, String eYmd, Date sDate, Date eDate, int limitFixotMinute, int unitMinute, String userId) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", sYmd); // flexibleEmp 의 구간이다 
		paramMap.put("eYmd", eYmd); // flexibleEmp 의 구간이다
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_FIXOT);
		//생성하기 전날까지의 합을 구해하자
		//오늘 날짜의 FIXOT를 다시 계산한다. 
		WtmFlexibleInfoVO fixotSumMap = calcMapper.calcSumApprMinuteForWorkDayResultByApprMinuteIsNotNull(paramMap);
		if(fixotSumMap != null ) {
			int sumFixotMinute = fixotSumMap.getSumFixOtMinute();
			//sumFixotMinute
			int calcFixotMinute = limitFixotMinute - sumFixotMinute;
			
			if( calcFixotMinute <= 0){
				return;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			List<WtmWorkDayResult> delRes = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_FIXOT);				
			if(delRes != null) {
				workDayResultRepo.deleteAll(delRes);
			}
			
			List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndApprEdateAfterAndApprSdateBefore(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_GOBACK, sDate, eDate);
			if(results != null && results.size() > 0) {
				for(WtmWorkDayResult result : results) {
					
					List<WtmWorkDayResult> chkResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndApprEdateAfterAndApprSdateBefore(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_GOBACK, result.getApprEdate(), eDate);
					int cnt = chkResults.size();
					
					int calcMinute = 0;
					
					logger.debug("OTFIX_T_D :: sDate.compareTo(result.getApprSdate()) = " + sDate.compareTo(result.getApprSdate()));
					
					logger.debug("OTFIX_T_D :: sDate = " + sDate);
					logger.debug("OTFIX_T_D :: eDate = " + eDate);
					logger.debug("OTFIX_T_D :: result.getApprSdate() = " + result.getApprSdate());
					logger.debug("OTFIX_T_D :: result.getApprSdate() = " + result.getApprEdate());
					//sDate < result.getApprSDate() && eDate > result.getApprEdate()
					//제외할 시간이 안에 들어가 있는 경우
					if(sDate.compareTo(result.getApprSdate()) < 0 && eDate.compareTo(result.getApprEdate())>0) {
						logger.debug("OTFIX_T_D :: sDate < result.getApprSDate() && eDate > result.getApprEdate()");
						WtmWorkDayResult res = new WtmWorkDayResult();
						res.setTenantId(tenantId);
						res.setEnterCd(enterCd);
						res.setSabun(sabun);
						res.setYmd(ymd);
						res.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
						res.setApplId(null);
						res.setTaaCd(null);
						
						calcMinute = this.WtmCalcMinute(sdf.format(sDate), sdf.format(eDate) , null, null, unitMinute);
						res.setPlanSdate(sDate);
						res.setPlanEdate(result.getApprSdate());
						res.setPlanMinute(calcMinute);
						
						res.setApprSdate(sDate);
						res.setApprEdate(result.getApprSdate());
						res.setApprMinute(calcMinute);
						res.setUpdateDate(new Date());
						res.setUpdateId(userId);
						
						workDayResultRepo.save(res);
						
						//잔여 고정 OT시간이 충분하면
						if(calcFixotMinute >= calcMinute) {
							if(cnt > 0) {
								this.P_WTM_WORK_DAY_RESULT_OTFIX_T_D(tenantId, enterCd, sabun, ymd, timeCdMgrId, sYmd, eYmd, result.getApprEdate(), eDate, limitFixotMinute, unitMinute, userId);
								return;
							}else {
								calcFixotMinute = calcFixotMinute - calcMinute;
								calcMinute = this.WtmCalcMinute(sdf.format(result.getApprEdate()), sdf.format(eDate), null, null, unitMinute);

								WtmWorkDayResult res2 = new WtmWorkDayResult();
								res2.setTenantId(tenantId);
								res2.setEnterCd(enterCd);
								res2.setSabun(sabun);
								res2.setYmd(ymd);
								res2.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
								res2.setApplId(null);
								res2.setTaaCd(null);
								
								if(calcFixotMinute >= calcMinute) {
									res2.setPlanSdate(result.getApprEdate());
									res2.setPlanEdate(eDate);
									res2.setPlanMinute(calcMinute);
									
									res2.setApprSdate(result.getApprEdate());
									res2.setApprEdate(eDate);
									res2.setApprMinute(calcMinute); 
									
								}else {
									Calendar cal = Calendar.getInstance();
									cal.setTime(sDate);
									cal.add(Calendar.MINUTE, calcFixotMinute);

									res2.setPlanSdate(sDate);
									res2.setPlanEdate(cal.getTime());
									res2.setPlanMinute(calcFixotMinute);
									
									res2.setApprSdate(sDate);
									res2.setApprEdate(cal.getTime());
									res2.setApprMinute(calcFixotMinute);
									
								}

								res2.setUpdateDate(new Date());
								res2.setUpdateId(userId);
								
								workDayResultRepo.save(res2);
							}
						}else {
							WtmWorkDayResult res2 = new WtmWorkDayResult();
							res2.setTenantId(tenantId);
							res2.setEnterCd(enterCd);
							res2.setSabun(sabun);
							res2.setYmd(ymd);
							res2.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
							res2.setApplId(null);
							res2.setTaaCd(null);
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(sDate);
							cal.add(Calendar.MINUTE, calcFixotMinute);

							res2.setPlanSdate(sDate);
							res2.setPlanEdate(cal.getTime());
							res2.setPlanMinute(calcFixotMinute);
							
							res2.setApprSdate(sDate);
							res2.setApprEdate(cal.getTime());
							res2.setApprMinute(calcFixotMinute);
							res2.setUpdateDate(new Date());
							res2.setUpdateId(userId);
							
							workDayResultRepo.save(res2);
						}
					//sDate > result.getApprSDate() && eDate > result.getApprEdate()
					//종료일만 담겼을때
					}else if(sDate.compareTo(result.getApprSdate()) > 0 && eDate.compareTo(result.getApprEdate())>0) {
						logger.debug("OTFIX_T_D :: sDate > result.getApprSDate() && eDate > result.getApprEdate() ");
						logger.debug("OTFIX_T_D :: cnt ");
						if(cnt > 0) {
							this.P_WTM_WORK_DAY_RESULT_OTFIX_T_D(tenantId, enterCd, sabun, ymd, timeCdMgrId, sYmd, eYmd, result.getApprEdate(), eDate, limitFixotMinute, unitMinute, userId);
							return;
						}else {
							calcMinute = this.WtmCalcMinute(sdf.format(result.getApprEdate()), sdf.format(eDate), null, null, unitMinute);

							WtmWorkDayResult res2 = new WtmWorkDayResult();
							res2.setTenantId(tenantId);
							res2.setEnterCd(enterCd);
							res2.setSabun(sabun);
							res2.setYmd(ymd);
							res2.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
							res2.setApplId(null);
							res2.setTaaCd(null);
							
							if(calcFixotMinute >= calcMinute) {
								res2.setPlanSdate(result.getApprEdate());
								res2.setPlanEdate(eDate);
								res2.setPlanMinute(calcMinute);
								
								res2.setApprSdate(result.getApprEdate());
								res2.setApprEdate(eDate);
								res2.setApprMinute(calcMinute); 
								
							}else {
								Calendar cal = Calendar.getInstance();
								cal.setTime(sDate);
								cal.add(Calendar.MINUTE, calcFixotMinute);

								res2.setPlanSdate(sDate);
								res2.setPlanEdate(cal.getTime());
								res2.setPlanMinute(calcFixotMinute);
								
								res2.setApprSdate(sDate);
								res2.setApprEdate(cal.getTime());
								res2.setApprMinute(calcFixotMinute);
								
							}

							res2.setUpdateDate(new Date());
							res2.setUpdateId(userId);
							
							workDayResultRepo.save(res2);
							
						}
					//sDate < result.getApprSDate() && eDate < result.getApprEdate()
					//시작일만 걸쳐있을 때
					}else if(sDate.compareTo(result.getApprSdate()) < 0 && eDate.compareTo(result.getApprEdate())<0) {
						logger.debug("OTFIX_T_D :: sDate < result.getApprSDate() && eDate < result.getApprEdate() ");
						calcMinute = this.WtmCalcMinute(sdf.format(sDate), sdf.format(result.getApprSdate()), null, null, unitMinute); 
						WtmWorkDayResult res2 = new WtmWorkDayResult();
						res2.setTenantId(tenantId);
						res2.setEnterCd(enterCd);
						res2.setSabun(sabun);
						res2.setYmd(ymd);
						res2.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
						res2.setApplId(null);
						res2.setTaaCd(null);
						
						if(calcFixotMinute >= calcMinute) {
							res2.setPlanSdate(sDate);
							res2.setPlanEdate(result.getApprSdate());
							res2.setPlanMinute(calcMinute);
							
							res2.setApprSdate(sDate);
							res2.setApprEdate(result.getApprSdate());
							res2.setApprMinute(calcMinute); 
							
						}else {
							Calendar cal = Calendar.getInstance();
							cal.setTime(sDate);
							cal.add(Calendar.MINUTE, calcFixotMinute);

							res2.setPlanSdate(sDate);
							res2.setPlanEdate(cal.getTime());
							res2.setPlanMinute(calcFixotMinute);
							
							res2.setApprSdate(sDate);
							res2.setApprEdate(cal.getTime());
							res2.setApprMinute(calcFixotMinute);
						}

						res2.setUpdateDate(new Date());
						res2.setUpdateId(userId);
						
						workDayResultRepo.save(res2);
						
					}
				}
			}else {
				//TIME TYPE의 경우 별도의 휴게 시간을 적용하지 않는다. 걍 만들면다 온 값으로  
				
				int calcMinute = this.WtmCalcMinute(sdf.format(sDate), sdf.format(eDate), null, null, unitMinute);

				WtmWorkDayResult res = new WtmWorkDayResult();
				res.setTenantId(tenantId);
				res.setEnterCd(enterCd);
				res.setSabun(sabun);
				res.setYmd(ymd);
				res.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
				res.setApplId(null);
				res.setTaaCd(null);
				
				res.setPlanSdate(sDate);
				res.setApprSdate(sDate);
				
				if(calcFixotMinute >= calcMinute) {
					res.setPlanEdate(eDate);
					res.setApprEdate(eDate);
					res.setPlanMinute(calcMinute);
					res.setApprMinute(calcMinute);
				}else {
					Calendar cal = Calendar.getInstance();
					cal.setTime(sDate);
					cal.add(Calendar.MINUTE, calcFixotMinute);
					res.setPlanEdate(cal.getTime());
					res.setApprEdate(cal.getTime());
					res.setPlanMinute(calcFixotMinute);
					res.setApprMinute(calcFixotMinute);
				}
				res.setUpdateDate(new Date());
				res.setUpdateId(userId);
				
				logger.debug("workDayResultRepo.save", res);
				workDayResultRepo.save(res);
				
			}
		}
		
	}
	
	@Transactional
	public void P_WTM_WORK_DAY_RESULT_OTFIX_C(Long tenantId, String enterCd, String sabun, String ymd, long timeCdMgrId, String sYmd, String eYmd, Date planEdate, Date apprEdate, Date entryEdate, int limitFixotMinute, int unitMinute, String userId) {
		logger.debug("고정 OT WTM_WORK_DAY_RESULT 에 만들자. - BREAK_TYPE_MGR MGR 일 경우 이다");
		logger.debug("P_WTM_WORK_DAY_RESULT_OTFIX_C", enterCd, sabun, ymd, timeCdMgrId, sYmd, eYmd, planEdate, apprEdate, entryEdate, limitFixotMinute, unitMinute);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", sYmd); // flexibleEmp 의 구간이다 
		paramMap.put("eYmd", eYmd); // flexibleEmp 의 구간이다
		paramMap.put("timeTypeCd", WtmApplService.TIME_TYPE_FIXOT);
		//생성하기 전날까지의 합을 구해하자
		//오늘 날짜의 FIXOT를 다시 계산한다. 
		WtmFlexibleInfoVO fixotSumMap = calcMapper.calcSumApprMinuteForWorkDayResultByApprMinuteIsNotNull(paramMap);
		if(fixotSumMap != null ) {
			int sumFixotMinute = fixotSumMap.getSumFixOtMinute();
			//sumFixotMinute
			int calcFixotMinute = limitFixotMinute - sumFixotMinute;
			
			logger.debug("sumFixotMinute : " + sumFixotMinute);
			logger.debug("calcFixotMinute : " + calcFixotMinute);
			if( calcFixotMinute <= 0){
				return;
			}
			
			WtmWorkDayResult result = new WtmWorkDayResult();
			result.setTenantId(tenantId);
			result.setEnterCd(enterCd);
			result.setSabun(sabun);
			result.setYmd(ymd);
			result.setApplId(null);
			result.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
			Date setApprSdate = (apprEdate != null && !apprEdate.equals(""))?apprEdate:planEdate;
			//단위 시간 적용을 위해 10분단위면 10분단위로 맞추자
			setApprSdate = this.WorkTimeCalcApprDate(setApprSdate,setApprSdate, unitMinute, "S");
			result.setPlanSdate(setApprSdate);
			result.setApprSdate(setApprSdate);
			
			SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");

			Date setApprEdate = this.WorkTimeCalcApprDate(entryEdate,entryEdate, unitMinute, "E");
			int fixotMinute = this.WtmCalcMinute(HHmm.format(setApprSdate), HHmm.format(setApprEdate), null, null, unitMinute) - this.getBreakMinuteIfBreakTimeMGR(setApprSdate, setApprEdate, timeCdMgrId, unitMinute);
			
			logger.debug("잔여 calcFixotMinute : " + calcFixotMinute);
			logger.debug("생성할 fixotMinute : " + fixotMinute);
			if( calcFixotMinute >= fixotMinute ) {
				calcFixotMinute = calcFixotMinute - fixotMinute;
				
				
				result.setPlanEdate(setApprEdate);
				result.setApprEdate(setApprEdate);
				
				result.setPlanMinute(fixotMinute);
				result.setApprMinute(fixotMinute);
				
				
			}else {
				logger.debug("잔여 calcFixotMinute 만큼만 생성해야한다. ");
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(setApprSdate);
				
				logger.debug("calcFixotMinute : " + calcFixotMinute + " 만큼의 종료시간을 구한다.");
				cal.add(Calendar.MINUTE, calcFixotMinute);
				logger.debug("add minute " + calcFixotMinute + " : " + cal.getTime());
				boolean isLoop = true;
				int preBreakMinute = 0;
				while(isLoop) {
					int breakMinute = this.getBreakMinuteIfBreakTimeMGR(setApprSdate, cal.getTime(), timeCdMgrId, unitMinute);
					//예를 들어 18~19시 고정 OT를 생성한다고 했을 때 18~19사이의 휴게시간을 구해야한다. 
					//18~18:30 이 휴계이 18~19:30으로 만들어줘야한다. 
					//근데 또 18~19:30의 휴게 시간이 기존 30분에서 더 늘었으면 늘은 만큼 또 늘려줘야한다. 
					//늘린 기간의 휴게가 0이 될때까지 돌자
					if(preBreakMinute == breakMinute) {
						isLoop = false;
					}else {
						cal.add(Calendar.MINUTE, breakMinute);
						preBreakMinute = breakMinute;
					}
				}
				//잔여시간 만큼만 
				Date resetApprEdate = cal.getTime();
				result.setPlanEdate(resetApprEdate);
				result.setApprEdate(resetApprEdate);
				//calcFixotMinute 이거 만큼 생성하기 위했기 때문에 다시 계산하지 않는다. 휴게시간만 추가한 종료시간
				result.setPlanMinute(calcFixotMinute);
				result.setApprMinute(calcFixotMinute);
				
			}
			
			result.setUpdateDate(new Date());
			result.setUpdateId(userId);

			logger.debug("OTFIX_C : workDayResultRepo.save", result);
			workDayResultRepo.save(result);
			
		}
	}
	
	@Transactional
	@Override
	public int P_WTM_WORK_DAY_RESULT_CREATE_T(WtmFlexibleStdMgr flexibleStdMgr, Long tenantId, String enterCd, String sabun, String ymd, Long timeCdMgrId, Date entrySdate, Date entryEdate, int unitMinute, String timeTypeCd, String breakTypeCd, int limitMinute, int useMinute, String userId) {
		int resultMinute = 0;
		/**
		 * 단위시간 계산을 위해 자른다. 
		 */
		try {
		ObjectMapper mapper = new ObjectMapper();
		Date sDate = this.WorkTimeCalcApprDate(entrySdate, entrySdate, unitMinute, "S");
		Date eDate = this.WorkTimeCalcApprDate(entryEdate, entryEdate, unitMinute, "E");
		
		SimpleDateFormat yMdHm = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
		
		//근무정보를 가지고 온다. 
		//WtmFlexibleStdMgr flexibleStdMgr = flexibleStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);

		logger.debug("CREATE_T :: timeTypeCd = " + timeTypeCd);
		logger.debug("CREATE_T :: breakTypeCd = " + breakTypeCd);
		try {
			System.out.println("CREATE_T : " + mapper.writeValueAsString(flexibleStdMgr));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//연장근무 시간이 아니면
		if(!timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT)) {
			// 등록된 시간이 시작 시분보다 종료 시분이 적으면 0시가 넘어간 시간이다. 근무일 다음날을 종료 시간으로 셋팅 하기 위함이다.
			String shm = flexibleStdMgr.getWorkShm();
			String ehm = flexibleStdMgr.getWorkEhm();
			
			if(shm != null && !shm.equals("") && ehm != null && !ehm.equals("")) {
				try {
					Date limitSdate = yMdHm.parse(ymd+shm);
					Date limitEdate = yMdHm.parse(ymd+ehm);
					//  종료시간이 작을 경우 다음날로 본다. 
					if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(limitEdate);
						cal.add(Calendar.DATE, 1);
						limitEdate = cal.getTime();
					}
					
					//근무제한시간보다 종료시간이 클 경우 
					if(eDate.compareTo(limitEdate) > 0) {
						//근무제한시간으로 자른다. 
						eDate = limitEdate;
					}
					
					if(sDate.compareTo(limitSdate) < 0) { // && eDate.compareTo(limitEdate) > -1) {
						sDate = limitSdate;
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		int apprMinute = 0;
		int breakMinute = 0;
		// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
		// 근무시간을 계산 하자
		Map<String, Object> calcMap = this.calcApprMinute(sDate, eDate, breakTypeCd, timeCdMgrId, unitMinute);
		apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
		breakMinute = Integer.parseInt(calcMap.get("breakMinute")+"");
		
		int calcWorkMinute = (limitMinute - useMinute);

		logger.debug("CREATE_T :: calcWorkMinute = " + calcWorkMinute);
		logger.debug("CREATE_T :: apprMinute = " + apprMinute);
		logger.debug("CREATE_T :: breakMinute = " + breakMinute);
		//잔여 근무시간이 있을 경우 
		if( calcWorkMinute > 0) {
			//잔여 근무시간을 초과하지 않을 경우 
			if( calcWorkMinute < (apprMinute - breakMinute)) {
				//데이터를 만든다. 
			//} else {
				//초과할 경우 잔여 시간만큼 생성해야한다. 
				Calendar cal = Calendar.getInstance();
				cal.setTime(sDate);
				cal.add(Calendar.MINUTE, calcWorkMinute+breakMinute);
				
				//잔여시간 만큼만 
				eDate = cal.getTime();
				// 20200623 효정수정 apprMinute(일근무 총시간) 변경전에 resultMinute 을 계산한다.
				// EX) 일 총근무시간이 500(총시간은 휴게시간 고려없는상태)일때, 잔여시간(calcWorkMinute) 이 40이고, 휴계시간(breakMinute)이 60인 경우 잔여시간 가산해야할 시간이 (500-(40+60)) 으로 처리되어야함.
				resultMinute = apprMinute - (calcWorkMinute+breakMinute);
				apprMinute = calcWorkMinute+breakMinute;
				
				// 20200623 효정수정 리턴계산값을 고쳐야함. 초과잔여시간으로 
				// resultMinute = calcWorkMinute;
			}else {
				resultMinute = apprMinute - breakMinute;
			}

			WtmWorkDayResult res = new WtmWorkDayResult();
			res.setTenantId(tenantId);
			res.setEnterCd(enterCd);
			res.setSabun(sabun);
			res.setYmd(ymd);
			res.setTimeTypeCd(timeTypeCd);
			res.setApplId(null);
			res.setTaaCd(null);
			res.setPlanSdate(sDate);
			res.setPlanEdate(eDate);
			res.setPlanMinute(apprMinute);
			res.setApprSdate(sDate);
			res.setApprEdate(eDate);
			res.setApprMinute(apprMinute);
			res.setUpdateDate(new Date());
			res.setUpdateId(userId);

			logger.debug("CREATE_T :: workDayResultRepo save = " + ymd + " : " + sabun + " : " + timeTypeCd + " : " + sDate + " : " + eDate + " : " + apprMinute);
			
			
			workDayResultRepo.save(res);
			
			//if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
				// workDayResult 에 휴게시간을 만들어 준다. 
			//	this.createWorkDayResultForBreakTime(tenantId, enterCd, sabun, ymd, (timeTypeCd.equals(WtmApplService.TIME_TYPE_BASE))?"BREAK":"BREAK_FIXOT", "APPR", breakMinute, userId);
			//}
			
		}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		

		return resultMinute;
	}
	
	/**
	 * P_WTM_WORK_DAY_RESULT_TIME_C
	 * check 하자 breakTypeCd가 TIME 일 경우만
	 * 그외 해당 메서드를 호출하지 않으면 좋은데 그렇지 않을 경우를 대비하여 한번더 체크한다. 
	 */
	@Override
	public void createWorkDayResultForBreakTime(Long tenantId, String enterCd, String sabun, String ymd, String taaInfoCd, String type, int breakMinute, String userId ) {
		//WtmWorkCalendar workCalendar = workCalandarRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndBreakTypeCd(tenantId, enterCd, sabun, ymd, WtmApplService.BREAK_TYPE_TIME);
		//if(workCalendar != null) {
			WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, taaInfoCd);
			String taaCd = taaCode.getTaaCd();
			
			if(taaCd != null && !taaCd.equals("")) {
				if(breakMinute > 0) {
					WtmWorkDayResult exceptResult = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndTaaCd(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_EXCEPT, taaCd);
					if(exceptResult == null) {
						exceptResult = new WtmWorkDayResult();
						exceptResult.setTenantId(tenantId);
						exceptResult.setEnterCd(enterCd);
						exceptResult.setSabun(sabun);
						exceptResult.setYmd(ymd);
						exceptResult.setTimeTypeCd(WtmApplService.TIME_TYPE_EXCEPT);
						exceptResult.setTaaCd(taaCd); 
					}
					exceptResult.setPlanMinute(breakMinute);
					if(!type.equals("PLAN")) {
						exceptResult.setApprMinute(breakMinute);
					}
					exceptResult.setUpdateDate(new Date());
					exceptResult.setUpdateId(userId);
					
					logger.debug("createWorkDayResultForBreakTime workDayResultRepo save : ", exceptResult);
					workDayResultRepo.save(exceptResult);
				}
			}
			
		//}
	}
	@Override
	public int WtmCalcMinute(String shm, String ehm, String limitShm, String limitEhm, Integer unitMinute) {
		logger.debug("WtmCalcMinute : " + shm + " : " + ehm + " : "  + limitShm + " : "  + limitEhm + " : " + unitMinute );
		if(shm != null && !shm.equals("") && ehm != null && !ehm.equals("") ) {
			if(unitMinute == null || unitMinute.equals("") || unitMinute < 0) {
				unitMinute = 1;
			}
			String strShm = "";
			String strEhm = "";
			int resM = 0;
			//제한된 시간 구간이 없다면 시작 종료 시간으로만 계산한다.
			if(limitShm == null || limitShm.equals("") || limitEhm == null || limitEhm.equals("") ) {
				//종료 시간이 시작시간보다 적으면 24시간을 더해준다. 
				if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
					strEhm = (Integer.parseInt(ehm) + 2400) + "";
				}else {
					strEhm = ehm;
				}
				strShm = shm;

				logger.debug("WtmCalcMinute strShm : " + strShm);
				logger.debug("WtmCalcMinute strEhm : " + strEhm);
				
				resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)))
						- (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
				
				logger.debug("WtmCalcMinute resM : " + resM);
				
				return resM;
			}else {
				
				if(limitEhm.equals("0000")) {
					limitEhm = "2400";
				}
				//종료시간이 0시면 2400으로 변경해준다.
				if(ehm.equals("0000")) {
					ehm = "2400";
				}
				
				if(Integer.parseInt(shm) > Integer.parseInt(ehm) ) {
					return 0;
				}
				// 0800 ~ 0900   2300 ~ 0900 일때 휴게시간 1시간이 나와야 할 경우
		        // 제한 시분이 다음날로 넘어갈 경우
				if(Integer.parseInt(limitShm) > Integer.parseInt(limitEhm)) {
					
					if( Integer.parseInt(ehm) <= Integer.parseInt(limitShm)) {
						//포함되지 않을 경우
						return 0;
					}
					
					// 24시전까지 먼저 체크한다.
					if( (Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= 2400) 
							|| ( Integer.parseInt(ehm) >= Integer.parseInt(limitEhm) &&  Integer.parseInt(ehm) <= 2400) ) {
						
						if( Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= 2400) {
							strShm = shm;
						}else {
							strShm = limitShm;
						}
						
						if( Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= 2400) {
							strEhm = ehm;
						}else {
							strEhm = "2400";
						}
						
						

						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)))
								- (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));

						logger.debug("000 (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)) :: " + (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))));
						logger.debug("000 (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))) :: " + (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))));
						logger.debug("000 resM :: " + resM);
						
						return resM;
					}else if( (Integer.parseInt(shm) >= 0 && Integer.parseInt(shm) <= Integer.parseInt(limitEhm))
							|| (Integer.parseInt(ehm) >= 0 && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm))
							) {
						//0시부터체크
						strShm = shm;
						if( Integer.parseInt(ehm) >= 0 && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm) ) {
							strEhm = ehm;
						}else {
							strEhm = limitEhm;
						}

						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)))
								- (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));

						logger.debug("111 (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)) :: " + (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))));
						logger.debug("111 (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))) :: " + (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))));
						logger.debug("111 resM :: " + resM);
						
						return resM;
						
					}else {
						return 0;
					}
				}else {
					
					strShm = shm;
					
					if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
						strEhm = (Integer.parseInt(ehm) + 2400)+"";
					}else {
						strEhm = ehm;
					}
					
					if(Integer.parseInt(limitShm) > Integer.parseInt(limitEhm)) {
						limitEhm = (Integer.parseInt(limitEhm) + 2400)+"";
					}else {
						limitEhm = limitEhm;
					}
					
					if( (Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= Integer.parseInt(limitEhm))
							|| (Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm))
					) {
						if(Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= Integer.parseInt(limitEhm)) {
							strShm = shm;
						}else {
							strShm = limitShm;
						}
						
						if(Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm)) {
							strEhm = ehm;
						}else {
							strEhm = limitEhm;
						}

						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)))
								- (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
						logger.debug("222 (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4)) :: " + (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))));
						logger.debug("222 (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))) :: " + (Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4))));
						logger.debug("222 resM :: " + resM);
						return resM;
					}else {
						return 0;
					}
				}
			} 
		}
		return 0;
	}
	
	@Override
	public Date WorkTimeCalcApprDate(Date rDt, Date dt, int unitMinute, String calcType) {

		SimpleDateFormat dH = new SimpleDateFormat("H");
		SimpleDateFormat dM = new SimpleDateFormat("m");
		SimpleDateFormat dYmd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

		//시
		int h = Integer.parseInt(dH.format(rDt));
		//분
		int m = Integer.parseInt(dM.format(rDt));
		
		// 지각일 경우 
		if(calcType.equals("S")) {
			/**
			 * rDt > dt : 1
			 * rDt == dt : 0
			 * rDy < dt : -1
			 */
			
			//타각 시간이 계획시간보다 작을 경우 정상
			if(rDt.compareTo(dt) < 1) {
				return dt;
			}else {
				// 분으로 계산
				//int totMinute = h * m;
				// 지각은 단위시간으로 계산 시 이후 시간으로 해야한다 . 
				// 10분 단위 일 경우 9시 8분일 경우 9시 10분으로 인정되어햔다.
				
				//단위 시간 적용
				int calcM = ((m + unitMinute) - (m + unitMinute)%unitMinute)%60;

				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// 9시 58분의 경우 10시가 되어야 하고
				// 23시 58분의 경우 다음날 0시가 되어야 한다 .
				if(m > calcM) {
					//58 > 0 이라 1시간을 더하자
					cal.add(Calendar.HOUR, 1);
				}
				return cal.getTime();
			}
		} else {
			logger.debug("rDt.compareTo(dt) :: " + rDt.compareTo(dt));
			if(rDt.compareTo(dt) > 0) {
				return dt;
			} else {  
				int namerge = m%unitMinute;
				//단위 시간 적용
				int calcM = m - m%unitMinute;

				logger.debug("calcM : " + calcM);
				logger.debug("namerge : " + namerge);
				/*
				if(namerge > 0)
					calcM += unitMinute;
				logger.debug("calcM : " + calcM);
				*/
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return cal.getTime(); 
			}
		}
	}

	
	/**
	 * BREAK TIME TYPE : TIME 의 휴게시간 구하기
	 * @param sDate
	 * @param eDate
	 * @param timeCdMgrId
	 * @param apprMinute
	 * @return
	 */
	@Override
	public int getBreakMinuteIfBreakTimeTIME(long timeCdMgrId, int apprMinute) {
		
		List<WtmTimeBreakTime> breakTimes = timebreakTimeRepo.findByTimeCdMgrIdOrderByWorkMinuteAsc(timeCdMgrId);
		int breakMinute = 0;
		if(breakTimes != null && breakTimes.size() > 0) {
			//int timesSize = breakTimes.size();
			boolean isLast = false;
			for(int i=0; i< breakTimes.size(); i++) {
				if(breakTimes.size() == (i+1)) {
					isLast = true;
				}else {
					isLast = false;
				}
				WtmTimeBreakTime breakTime = breakTimes.get(i);
			//for(WtmTimeBreakTime breakTime : breakTimes) {
				int wMinute = Integer.parseInt(breakTime.getWorkMinute());
				int bMinute = Integer.parseInt(breakTime.getBreakMinute());
				//이후 설정값을 가지고 오기 위해 다음 데이터가 있는지 확인한다. 
				int nextBMinute = 0;
				if(!isLast) {
					//다음 데이터가 있다.
					nextBMinute = Integer.parseInt(breakTimes.get(i+1).getBreakMinute());
				}
				//근무시간이 설정구간의 분과 다음 설정값의 휴게시간의 합보다 큰지 체크 한다. 
				//다음 설정 값이 있고 현재 기준보다 근무시간이 클 경우 다음 시간을 체크 한다 .
				
				/*
				 * 설정값이 
				 * 근무시간 240 휴게시간 0
				 * 근무시간 480 휴게시간 60
				 * 일 때 실제 근무 시간이 280분일 경우 280 - 60을 할 수 없다. 
				 * 280 > 240+60 의 요건으로 240분에 해당하는 설정 값으로 적용해야한다.  
				 * 
				 */
				if( apprMinute > (wMinute + nextBMinute) && !isLast ) {
					continue;
				}else {
					breakMinute = bMinute;
				}
				
			}
		}
		return breakMinute;
	}
	/**
	 * 시/종 시각 사이에 있는 휴게시간 전부를 가지고 온다. 
	 * @param sDate
	 * @param eDate
	 * @param timeCdMgrId
	 * @return
	 */
	@Override
	public Map<String, Object> getBreakMinuteIfBreakTimeMGR(Date sDate, Date eDate, long timeCdMgrId) {
		int sumBreakMinute = 0;
		String maxEhm = null;
		
		Map<String, Object> resMap = new HashMap<>();
		if(sDate.compareTo(eDate) < 1) {
			SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
			//SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
			String shm = HHmm.format(sDate);
			String ehm = HHmm.format(eDate);
			List<WtmTimeBreakMgr> timeBreakMgrs = timebreakMgrRepo.findByTimeCdMgrId(timeCdMgrId);
			
			for(WtmTimeBreakMgr timeBreakMgr : timeBreakMgrs) {
				logger.debug("timeBreakMgr.getShm() :: "+ timeBreakMgr.getShm());
				logger.debug("timeBreakMgr.getEhm() :: "+ timeBreakMgr.getEhm());
				if(Integer.parseInt(shm) < Integer.parseInt(timeBreakMgr.getEhm())
						&&
						Integer.parseInt(ehm) > Integer.parseInt(timeBreakMgr.getShm())
				) {
					if(maxEhm == null || Integer.parseInt(maxEhm) < Integer.parseInt(timeBreakMgr.getEhm())){
						maxEhm = timeBreakMgr.getEhm();
					}
					//20~60 일 경우 40분이 나와야하는데 60분이 나와서 수정함.
					//20200901 
					//sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), null, null, null);
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(shm, ehm, timeBreakMgr.getShm(), timeBreakMgr.getEhm(), null);
				}
					
			}
			
			//return this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute) - sumBreakMinute;
		}
		logger.debug("sumBreakMinute :: "+ sumBreakMinute);
		resMap.put("maxEhm", maxEhm);
		resMap.put("breakMinute", sumBreakMinute);
		return resMap;
	}
	/**
	 * BREAK TIME TYPE : MGR 의 휴게시간 구하기
	 * @param sDate
	 * @param eDate
	 * @param timeCdMgrId
	 * @param unitMinute
	 * @return
	 */
	@Override
	public int getBreakMinuteIfBreakTimeMGR(Date sDate, Date eDate, long timeCdMgrId, int unitMinute) {
		logger.debug("BREAK TYPE MGR 타입의 휴게시간 계산합니다.", sDate, eDate, timeCdMgrId, unitMinute);
		int sumBreakMinute = 0;
		
		if(sDate.compareTo(eDate) < 1) {
			SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
			SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
			
			List<WtmTimeBreakMgr> timeBreakMgrs = timebreakMgrRepo.findByTimeCdMgrId(timeCdMgrId);
			
			for(WtmTimeBreakMgr timeBreakMgr : timeBreakMgrs) {
				logger.debug("timeBreakMgr.getShm() :: "+ timeBreakMgr.getShm());
				logger.debug("timeBreakMgr.getEhm() :: "+ timeBreakMgr.getEhm());
				if(!yyyyMMdd.format(sDate).equals(yyyyMMdd.format(eDate))) {
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), "0000", unitMinute);
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), "0000", HHmm.format(eDate), unitMinute);
				}else {
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), HHmm.format(eDate), unitMinute);
				}
					
			}
			
			//return this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute) - sumBreakMinute;
		}
		logger.debug("sumBreakMinute :: "+ sumBreakMinute);
		return sumBreakMinute;
		
	}
	
	@Override
	public Map<String, Object> calcApprMinute(Date sDate, Date eDate, String breakTypeCd, long timeCdMgrId, int unitMinute){
		SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
		Map<String, Object> resMap = new HashMap<>();
		int apprMinute = 0;
		int breakMinute = 0;
		
		// 근무시간을 계산 하자
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			
			apprMinute = this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute) -  this.getBreakMinuteIfBreakTimeMGR(sDate, eDate, timeCdMgrId, unitMinute);
			breakMinute = 0;
			
		}else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIME)) {
			apprMinute = this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute);
			
			breakMinute = this.getBreakMinuteIfBreakTimeTIME(timeCdMgrId, apprMinute); 
			
		}else if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
			// 미구현
		}
		
		resMap.put("apprMinute", apprMinute);
		resMap.put("breakMinute", breakMinute);
		return resMap;
	}
	
	/**
	 * 기준시간이 9시고 60분이후 시간을 구하고자 할때 
	 * 9~10시까지의 휴게시간이 있는 지 확인한다.
	 * 30분의 휴게 시간이 있을 경우 
	 * 10~10:30 분까지 휴게시간 여뷰를 체크한다.
	 * 없으면 그대로 전달하지만 있을 경우 다시 재귀호출을 한다.  
	 * @param sDate
	 * @param addMinute
	 * @param timeCdMgrId
	 * @param unitMinute
	 * @return
	 */
	@Override
	public Date P_WTM_DATE_ADD_FOR_BREAK_MGR(Date sDate, int addMinute, long timeCdMgrId, Integer unitMinute) {
		
		boolean isNegative = false;
		if(addMinute < 0) {
			isNegative = true;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(sDate);
		cal.add(Calendar.MINUTE, addMinute);

		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: sDate = "+ sDate);
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: addMinute = "+ addMinute);
		Date eDate = cal.getTime();
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: eDate = "+ eDate);
		
		int breakMinute = 0; 
		String maxEhm =  null;
		
		Date cSdate = sDate;
		Date cEdate = eDate;
		if(isNegative) {
			cSdate = eDate;
			cEdate = sDate;
		}

		// 1220 ~ 1250
		// 30
		// 1250 ~ 1320
		// 10
		// 1320 ~ 1330
		// 0 이럼 되는데.

		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddhhmm");
		Map<String, Object> breakMap = this.getBreakMinuteIfBreakTimeMGR(cSdate, cEdate, timeCdMgrId);
		int addBreakMinute = 0;
		if(breakMap != null) {
			addBreakMinute = (int) breakMap.get("breakMinute");
		}
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: addBreakMinute = "+ addBreakMinute);
		if(addBreakMinute > 0) {
			if(isNegative) {
				return P_WTM_DATE_ADD_FOR_BREAK_MGR(cSdate, addBreakMinute * -1, timeCdMgrId, unitMinute);
			}else {
				return P_WTM_DATE_ADD_FOR_BREAK_MGR(cEdate, addBreakMinute, timeCdMgrId, unitMinute);
			}
		}
		// 주석 처리 위 로직으로 변경 20200901JYP
		
		
		/*
		Map<String, Object> breakMap = this.getBreakMinuteIfBreakTimeMGR(cSdate, cEdate, timeCdMgrId);
		if(breakMap != null) {
			breakMinute = (int) breakMap.get("breakMinute");
			if(breakMinute > 0) {
				maxEhm =  (String) breakMap.get("maxEhm");
			}
		}
		// 20~60 40분의 휴게 시간이 나와야하는데 휴게시간 전체가 나왔구나
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: breakMinute = "+ breakMinute);
		cal = Calendar.getInstance();
		cal.setTime(cSdate);
		int calMinute = (isNegative)?addMinute-breakMinute:addMinute+breakMinute;
		cal.add(Calendar.MINUTE, calMinute);
		
		Date calcEdate = cal.getTime();
		
		if(isNegative) {
			cEdate = cSdate;
			cSdate = calcEdate;
		}else {
			cEdate = calcEdate;
		}
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: cSdate = "+ cSdate + " cEdate = " + cEdate);
		
		if(maxEhm != null) {
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddhhmm");
			breakMap = null;
			int addBreakMinute = 0;
			try {
				// 이상하다 반복된다 여기가 수정이
				breakMap = this.getBreakMinuteIfBreakTimeMGR(ymdhm.parse(ymd.format(cSdate)+maxEhm), cEdate, timeCdMgrId);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(breakMap != null) {
				addBreakMinute = (int) breakMap.get("breakMinute");
			}
			logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: addBreakMinute = "+ addBreakMinute);
			if(addBreakMinute > 0) {
				if(isNegative) {
					return P_WTM_DATE_ADD_FOR_BREAK_MGR(cSdate, addBreakMinute * -1, timeCdMgrId, unitMinute);
				}else {
					return P_WTM_DATE_ADD_FOR_BREAK_MGR(cEdate, addBreakMinute, timeCdMgrId, unitMinute);
				}
			}
		}
		*/
		if(isNegative) {
			return cSdate;
		}else {
			return cEdate;
		}
	}
}
