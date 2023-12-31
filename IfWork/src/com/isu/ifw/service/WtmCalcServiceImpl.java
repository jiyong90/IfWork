package com.isu.ifw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmCalcMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorktimeCloseMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.vo.WtmFlexibleInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

	@Autowired private WtmDayMgrRepository dayMgrRepo;
	@Autowired private WtmHolidayMgrRepository holidayMgrRepo;

	@Autowired private WtmWorkTermTimeRepository workTermTimeRepo;

	@Autowired private WtmPropertieRepository propertieRepo;

	@Autowired private WtmOtApplRepository otApplRepo;

	@Autowired private WtmWorktimeDayCloseRepository worktimeDayCloseRepo;
	@Autowired private WtmWorktimeMonCloseRepository worktimeMonCloseRepo;
	@Autowired private WtmWorktimeCloseMapper worktimeCloseMapper;


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
		//WtmFlexibleInfoVO flexInfo = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
		try {
			logger.debug("CREATE_F :: workMinuteMap = " + mapper.writeValueAsString(flexInfo));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(flexInfo != null) {
			// ymd가 속한 근무제의 총 기본근로 시간.
			int workMinute = flexInfo.getWorkMinute();
			// ymd가 속한 근무제의 인정 기본근로 시간.
			int sumWorkMinute = flexInfo.getWorkHour() - flexInfo.getBreakhour();
			//int sumWorkMinute = flexInfo.getSumWorkMinute();

			logger.debug("CREATE_F :: workMinute = " + workMinute);
			logger.debug("CREATE_F :: sumWorkMinute = " + sumWorkMinute);
			// 기본근로 시간이 이미 다 찼으면 기본근로 시간을 생성하지 않는다.
			if( workMinute > sumWorkMinute ) {
				List<String> timeTypeCds = new ArrayList<String>();
				timeTypeCds.add("LLA");
				// 지각 체크를 하지 않을때는 기준시간이 없기 때문에 기준시간을 생성하기 위함
				if(!"".equals(timeCdMgr.getLateChkYn()) && timeCdMgr.getLateChkYn().equals("N") ) {
					timeTypeCds.add("SUBS");
					timeTypeCds.add("TAA");
					timeTypeCds.add("REGA");
				}

//				List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(tenantId, enterCd, sabun, ymd, WtmApplService.TIME_TYPE_BASE);
//				List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(tenantId, enterCd, sabun, ymd);

				/*20210318 지각 및 조퇴 데이터 계획시간이 변경되서 이걸로 변경한다. RCH*/
				List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAnAndTimeTypeCdNotInAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull(tenantId, enterCd, sabun, ymd, timeTypeCds);
				if(results != null && results.size() > 0) {
					int cnt = 1;
					logger.debug("CREATE_F :: flexStdMgr.getApplyEntrySdateYn() = " + flexStdMgr.getApplyEntrySdateYn());
					logger.debug("CREATE_F :: flexStdMgr.getApplyEntryEdateYn() = " + flexStdMgr.getApplyEntryEdateYn());
					WtmWorkCalendar calendar = workCalandarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);

					Boolean lastData = false;
					for(WtmWorkDayResult result : results) {

						logger.debug("CREATE_F :: cnt = " + cnt);

						Date sDate = null;
						Date eDate = null;

						if(results.size() == cnt) {
							lastData = true;
						}
						//타각시간에서 벗어난 데이터는 패스 한다 .
						if(calendar.getEntrySdate().compareTo(result.getPlanEdate()) < 0
								&& calendar.getEntryEdate().compareTo(result.getPlanSdate()) > 0) {

							if(cnt == 1 && "Y".equals(flexStdMgr.getApplyEntrySdateYn())) {
								sDate = calendar.getEntrySdate();
							}else {
								sDate = result.getPlanSdate();
							}

							if(lastData && "Y".equals(flexStdMgr.getApplyEntryEdateYn())) {
								eDate = calendar.getEntryEdate();
							} else {
								eDate = result.getPlanEdate();
							}

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

							if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)) {
								//안에서 생성한 시간을 더해준다.
								sumWorkMinute = sumWorkMinute + this.P_WTM_WORK_DAY_RESULT_UPDATE_T(flexStdMgr, timeCdMgr, result, sDate, eDate, calendar.getEntrySdate(), calendar.getEntryEdate(),  sumWorkMinute, workMinute, userId);
							}else if (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) && result.getTenantId()== 22 && result.getTaaCd() != null
									&& (result.getTaaCd().equals("G23") ||result.getTaaCd().equals("G29") || result.getTaaCd().equals("G30") || result.getTaaCd().equals("G28"))) {
								sumWorkMinute = sumWorkMinute + this.P_WTM_WORK_DAY_RESULT_UPDATE_T(flexStdMgr, timeCdMgr, result, sDate, eDate, calendar.getEntrySdate(), calendar.getEntryEdate(),  sumWorkMinute, workMinute, userId);
							}

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

		Date calcSdate = null;
		Date calcEdate = null;

		//현대 엔지비 인정시간 계산때문에 분리..
		if("Y".equals(flexStdMgr.getApplyEntrySdateYn())) {
			calcSdate = this.WorkTimeCalcApprDate(sDate, sDate, flexStdMgr.getUnitMinute(), "S");
		}else {
			calcSdate = this.WorkTimeCalcApprDate(entrySdate, sDate, flexStdMgr.getUnitMinute(), "S");
		}

		//현대 엔지비 인정시간 계산때문에 분리..
		if("Y".equals(flexStdMgr.getApplyEntryEdateYn())) {
			calcEdate = this.WorkTimeCalcApprDate(eDate, eDate, flexStdMgr.getUnitMinute(), "E");
		} else {
			if(entryEdate.compareTo(eDate) < 0) {
				calcEdate = this.WorkTimeCalcApprDate(entryEdate, eDate, flexStdMgr.getUnitMinute(), "E");
			} else {
				calcEdate = this.WorkTimeCalcApprDate(eDate, eDate, flexStdMgr.getUnitMinute(), "E");
			}
		}


		logger.debug("UPDATE_T :: calcSdate :: " + calcSdate);
		logger.debug("UPDATE_T :: calcEdate :: " + calcEdate);
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		if( !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_OT) && !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_EARLY_OT)
				&& !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_NIGHT) && !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_EARLY_NIGHT)
				&& !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_REGA_NIGHT) && !result.getTimeTypeCd().equalsIgnoreCase(WtmApplService.TIME_TYPE_REGA_OT)
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
			//10560			//10476				//84		//0
		if ((workMinute - sumWorkMinute) < (apprMinute - breakMinute)) {
			//종료시간을 다시 계산해야한다.
			logger.debug("UPDATE_T :: timeCdMgr.getBreakTypeCd() = " +  timeCdMgr.getBreakTypeCd());
			if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {

				// 현대ngv apprMinute시간 수정 20230711
				if(result.getTenantId() == 22L){
					calcEdate = this.P_WTM_DATE_ADD_FOR_BREAK_MGR(calcSdate, (apprMinute - breakMinute), timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				} else {
					calcEdate = this.P_WTM_DATE_ADD_FOR_BREAK_MGR(calcSdate, (workMinute - sumWorkMinute), timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
				}

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
				//잔여기본근로종료시간을 구해서 기본근무시간 정보를 만들어 준다. 
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
		workDayResultRepo.flush();
		return apprMinute;
	}

	/**
	 * 고정 OT 일괄 소진일 경우에만 사용된다. 
	 * flexStdMgr.getDefaultWorkUseYn().equalsIgnoreCase("Y") && flexStdMgr.getFixotUseType().equalsIgnoreCase("ALL"))
	 * 기본근로시간이 모두 소진 되었을 경우 고정 OT를 생성한다 .
	 * 기본근로시간 생성 시 unplannedYn == 'Y' 일경우만 생성한다.
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
		 * 근무제도 기간내 총 기본근로 시간과 ymd 까지의 근무 시간의 합을 구한다.
		 */
		WtmFlexibleEmpCalc flexInfo = flexibleEmpRepo.getTotalWorkMinuteAndRealWorkMinute(tenantId, enterCd, sabun, ymd);
		System.out.println("**************flexInfo " + flexInfo.toString());

//		WtmFlexibleInfoVO flexInfo = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
//		logger.debug("flexInfo :: WtmFlexibleInfoVO " + flexInfo.toString());
		if(flexInfo != null) {
			int workMinute = flexInfo.getWorkMinute();
			int sumWorkMinute = (flexInfo.getWorkHour() - flexInfo.getBreakhour()) + addSumWorkMinute;

//			// ymd가 속한 근무제의 총 기본근로 시간.
//			int workMinute = flexInfo.getWorkMinute();
//			// ymd가 속한 근무제의 인정 기본근로 시간. -- 9840
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

			// 기본근로 시간이 이미 다 찼으면 기본근로 시간을 생성하지 않는다. 
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
					String taaCdNm = "";
					logger.debug("CREATE_N :: defaultWorkUseYn = " + defaultWorkUseYn);
					logger.debug("CREATE_N :: fixotUseType = " + fixotUseType);
					if(defaultWorkUseYn.equals("Y") && fixotUseType.equals("ALL")) {

						Date fixOtSdate = calendar.getEntrySdate();
						Date fixOtEdate = calendar.getEntryEdate();
						Date regaFixOtSdate = calendar.getEntrySdate();
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
						List<WtmWorkDayResult> ngvRegaResults = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_REGA, tenantId, enterCd, sabun, ymd);
						if(ngvRegaResults != null && ngvRegaResults.size() > 0) {
							for(WtmWorkDayResult regaResults : ngvRegaResults) {
								//인정시간이 0보다 큰 경우만 인정한다
								//간주 근무중 국내출장도 추가 20230711
								if(regaResults.getTaaCd() != null  && tenantId == 22L  &&(
										regaResults.getTaaCd().equals("G28") || regaResults.getTaaCd().equals("G29")
												|| regaResults.getTaaCd().equals("G30") || regaResults.getTaaCd().equals("G23"))) {
									if( regaResults.getTaaCd().equals("G28")|| regaResults.getTaaCd().equals("G30")|| regaResults.getTaaCd().equals("G23")) {
										taaCdNm = regaResults.getTaaCd();
										if(regaResults.getApprEdate() == null || regaFixOtSdate != null) {
											if(fixOtSdate.compareTo(regaResults.getPlanSdate()) < 0) {
												regaFixOtSdate = fixOtSdate;
											}else {
												regaFixOtSdate = regaResults.getPlanSdate();
											}
											fixOtEdate = regaResults.getPlanEdate();
										}
									}else if ( regaResults.getTaaCd().equals("G29")) {
										taaCdNm = regaResults.getTaaCd();
										regaFixOtSdate = fixOtSdate;
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

						if(taaCdNm != null && !taaCdNm.equals("") && tenantId == 22L ) {
							if( taaCdNm.equals("G28")|| taaCdNm.equals("G29") || taaCdNm.equals("G30")) {
								this.createFixOt(calendar, flexibleStdMgr, timeCdMgr, WtmApplService.TIME_TYPE_FIXOT, regaFixOtSdate, fixOtEdate, limitMinute, useMinute);
							}
						} else {
							this.createFixOt(calendar, flexibleStdMgr, timeCdMgr, WtmApplService.TIME_TYPE_FIXOT, fixOtSdate, fixOtEdate, limitMinute, useMinute);
						}
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
		// 이미 기본근로시간이 다 찼을 경우 남아있는 남아 있는 계획 시간들은 없애햐한다.
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
		if(!timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT)
				&& !timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)
				&& !timeTypeCd.equals(WtmApplService.TIME_TYPE_REGA_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_REGA_NIGHT)
		) {
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

//		List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndYmdAndSabunAndApprEdateAfterAndApprSdateBeforeAndApprMinuteGreaterThenAndApprMinuteIsNotNullOrderByApprSdateAsc(calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), sDate, eDate, 0);
		List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndYmdAndSabunAndplanEdateAfterAndplanSdateBeforeOrderByApprSdateAsc(calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun(), sDate, eDate);

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
				String preTimeTypeCd = "";
				boolean ngvPreTimeTypeDiv = false;
				boolean isNgvRega = false;
				boolean reApprFixot = true;
				int cnt = 0;
				for(WtmWorkDayResult r : results) {

					cnt++;
					if(preTimeTypeCd.equals("")) {
						preTimeTypeCd = r.getTimeTypeCd();
					}
					if(r.getTaaCd() != null) {
						if(r.getTimeTypeCd() != null && r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) && (r.getTaaCd().equals("G28") || r.getTaaCd().equals("G29") || r.getTaaCd().equals("G30"))) {
							isNgvRega = true;
						}
					}
					logger.debug("WtmWorkDayResult : " + r);
					Date apprSdate = r.getApprSdate()==null?r.getPlanSdate():r.getApprSdate();
					Date apprEdate = r.getApprEdate()==null?r.getPlanEdate():r.getApprEdate();


					if(nextDataCheck) {
						if(preEdate.compareTo(apprSdate ) < 0 && eDate.compareTo(apprSdate) > 0) {
							Date calcSdate = preEdate;
							Date calcEdate = apprSdate;
							// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
							// 근무시간을 계산 하자
							Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							sumApprMinute += Integer.parseInt(calcMap.get("apprMinute")+"");
							loopSdate = apprEdate;
						}
						nextDataCheck = false;
					}
					if((loopSdate.compareTo(apprSdate) <= 0 && ( preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) || isNgvRega ) ) || (results.size() == 1 && loopSdate.compareTo(apprSdate) > 0 && ( preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) || isNgvRega ) ) ) {
						Date calcSdate = loopSdate;
						Date calcEdate = apprSdate;

						if(results.size() == 1 || (loopSdate.compareTo(apprSdate) <= 0 && cnt == 1 )) {
							calcEdate = apprEdate;
						}

						// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
						// 근무시간을 계산 하자
						Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						sumApprMinute += Integer.parseInt(calcMap.get("apprMinute")+"");
						if(results.size() != cnt) {
							loopSdate = apprEdate;
						}

						if(eDate.compareTo(apprEdate) >= 0) {
							//다음데이터를 확인해야한다.
							nextDataCheck = true;
							preEdate = apprEdate;
							preTimeTypeCd = r.getTimeTypeCd();
						}else if(eDate.compareTo(apprEdate) < 0) {
							nextDataCheck = true;
							preEdate = eDate;
							preTimeTypeCd = r.getTimeTypeCd();
						}

					}else if(eDate.compareTo(apprEdate) >= 0) {
						//다음데이터를 확인해야한다.
						nextDataCheck = true;
						preEdate = apprEdate;
						preTimeTypeCd = r.getTimeTypeCd();
					} else if(eDate.compareTo(apprEdate) < 0) {
						nextDataCheck = true;
						preEdate = eDate;
						preTimeTypeCd = r.getTimeTypeCd();
					}

					if(cnt == results.size() && results.size() > 1) {
						preEdate = apprSdate;
					}


				}
				if(nextDataCheck) {
					//다음 데이터를 체크해야하는데 없었을 경우
					//남은 기간을 생성한다.
					if(preEdate.compareTo(eDate) < 0) {
						Date calcSdate = preEdate;
						Date calcEdate = eDate;

						// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
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

				preTimeTypeCd = "";
				cnt = 0 ;
				//  A 오전 , 오후 P,  H AND WORK_YN
				for(WtmWorkDayResult r2 : results) {

					if(r2!= null ) {
						if(r2.getTaaCd()!=null) {
							WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(r2.getTenantId(), r2.getEnterCd(), r2.getTaaCd());
							String getWorkYn = taaCode.getWorkYn();
							String getRequestTypeCd = taaCode.getRequestTypeCd();
							if(getRequestTypeCd.equals("H") && getWorkYn.equals("Y")) {
								ngvPreTimeTypeDiv = true;
								break;
							}
						}
					}
				}
				for(WtmWorkDayResult r : results) {

					cnt++;

					if(preTimeTypeCd.equals("")) {
						preTimeTypeCd = r.getTimeTypeCd();
					}
					Date apprSdate = r.getApprSdate()==null?r.getPlanSdate():r.getApprSdate();
					Date apprEdate = r.getApprEdate()==null?r.getPlanEdate():r.getApprEdate();
					//잔여시간이 있어야한다.
					if(calcWorkMinute > 0) {
						if(nextDataCheck) {
							//다음 데이터가 eDate 보다 시작 인정시각이 뒤 일수 없다 그럼 쿼리가 이상
							if(preEdate.compareTo(apprSdate) < 0 && eDate.compareTo(apprSdate) > 0) {
								Date calcSdate = preEdate;
								Date calcEdate = apprSdate;
								if(results.size() == 1) {
									calcEdate = apprEdate;
								}

								int apprMinute = 0;
								// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
								// 근무시간을 계산 하자
								Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");

								logger.debug("createFixOt :: calcWorkMinute = " + calcWorkMinute);
								logger.debug("createFixOt :: apprMinute = " + apprMinute);
								//잔여 근무시간이 있을 경우
								if( createLimitMinute > 0 &&( preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) || isNgvRega )) {
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

								if(!calcSdate.equals(calcEdate) && apprMinute != 0) {
									workDayResultRepo.save(newResult);
								}

								loopSdate = calcEdate;
								reApprFixot = false;
							}
							nextDataCheck = false;

						}

						// 시작시각 보다 빠를 경우
						logger.debug("sDate.compareTo(apprSdate) : " + sDate.compareTo(apprSdate));
						logger.debug("cnt : " + cnt);
						if(!reApprFixot && r.getTenantId() !=null && r.getTenantId().equals(22L)) {
							isNgvRega = true;
							reApprFixot = true;
						}
						if(r.getTaaCd()!= null && r.getTaaCd().equals("G30") ) {
							isNgvRega = true;
						}
						if(r.getTaaCd()!= null && (r.getTaaCd().equals("G28") || r.getTaaCd().equals("G29") || r.getTaaCd().equals("G30") )
								&&  preTimeTypeCd.equals(WtmApplService.TIME_TYPE_GOBACK) && ( r.getTimeTypeCd() != null && r.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)) ){
							isNgvRega = true;
						}
						if(loopSdate.compareTo(apprSdate) <= 0 || (loopSdate.compareTo(apprSdate) > 0 && results.size() == 1)) {

							Date calcSdate = loopSdate;
							Date calcEdate = apprSdate;
							if(results.size() == 1) {
								calcEdate = apprEdate;
							}

							int apprMinute = 0;
							// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
							// 근무시간을 계산 하자
							Map<String, Object> calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
							if(ngvPreTimeTypeDiv && results.size() != 1 && isNgvRega
									&&  r.getTaaCd()!= null && r.getTaaCd().equals("G29") && apprMinute == 0) {
								calcEdate = apprEdate;
								calcMap = this.calcApprMinute(calcSdate, calcEdate, timeCdMgr.getBreakTypeCd(), calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
							}
							logger.debug("createFixOt :: calcWorkMinute = " + calcWorkMinute);
							logger.debug("createFixOt :: apprMinute = " + apprMinute);
							//잔여 근무시간이 있을 경우

							if( createLimitMinute > 0 && (preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) || isNgvRega )) {
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

							if(preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) ) {
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

								if(!calcSdate.equals(calcEdate) && apprMinute != 0) {
									workDayResultRepo.save(newResult);
								}
							}

							if(preTimeTypeCd.equals(WtmApplService.TIME_TYPE_REGA) && isNgvRega) {
								isNgvRega = false;
								if( r.getTaaCd()!= null && reApprFixot && r.getTaaCd().equals("G29") ) {
									WtmWorkDayResult newResult = new WtmWorkDayResult();
									newResult.setTenantId(calendar.getTenantId());
									newResult.setEnterCd(calendar.getEnterCd());
									newResult.setYmd(calendar.getYmd());
									newResult.setSabun(calendar.getSabun());
									if(!ngvPreTimeTypeDiv) {
										calcEdate = eDate;
										apprMinute = createLimitMinute;
									}
									newResult.setPlanSdate(calcSdate);
									newResult.setPlanEdate(calcEdate);
									newResult.setApprSdate(calcSdate);
									newResult.setApprEdate(calcEdate);
									newResult.setTimeTypeCd(timeTypeCd);

									newResult.setPlanMinute(apprMinute);
									newResult.setApprMinute(apprMinute);
									newResult.setUpdateDate(new Date());
									newResult.setUpdateId("REGA createFixOt2");

									if(!calcSdate.equals(calcEdate) && apprMinute != 0) {
										workDayResultRepo.save(newResult);
									}
								} else if( r.getTaaCd()!= null && (r.getTaaCd().equals("G28") || r.getTaaCd().equals("G29") )){
									WtmWorkDayResult newResult = new WtmWorkDayResult();
									if(!calcEdate.equals(eDate) && r.getTaaCd().equals("G28")) {
										createLimitMinute += apprMinute;
									}
									newResult.setTenantId(calendar.getTenantId());
									newResult.setEnterCd(calendar.getEnterCd());
									newResult.setYmd(calendar.getYmd());
									newResult.setSabun(calendar.getSabun());
									newResult.setPlanSdate(calcSdate);
									newResult.setPlanEdate(eDate);
									newResult.setApprSdate(calcEdate);
									newResult.setApprEdate(eDate);
									newResult.setTimeTypeCd(timeTypeCd);
									newResult.setPlanMinute(createLimitMinute);
									newResult.setApprMinute(createLimitMinute - apprMinute);
									newResult.setUpdateDate(new Date());
									newResult.setUpdateId("REGA createFixOt3");
									boolean passApprMin = true;
									if (r.getApprMinute()!=null && r.getApprMinute() > 0) {
										passApprMin = false;
										if(createLimitMinute > r.getApprMinute()) {
											passApprMin = true;
										}
									}
									if(!calcSdate.equals(eDate) && createLimitMinute != 0 && passApprMin) {
										workDayResultRepo.save(newResult);
									}
								} else if( r.getTaaCd()!= null &&  r.getTaaCd().equals("G30") ){
									WtmWorkDayResult newResult = new WtmWorkDayResult();

									newResult.setTenantId(calendar.getTenantId());
									newResult.setEnterCd(calendar.getEnterCd());
									newResult.setYmd(calendar.getYmd());
									newResult.setSabun(calendar.getSabun());
									newResult.setPlanSdate(calcEdate);
									newResult.setPlanEdate(eDate);
									newResult.setApprSdate(calcEdate);
									newResult.setApprEdate(eDate);
									newResult.setTimeTypeCd(timeTypeCd);
									newResult.setPlanMinute(createLimitMinute);
									newResult.setApprMinute(createLimitMinute);
									newResult.setUpdateDate(new Date());
									newResult.setUpdateId("REGA createFixOt3");
									boolean passApprMin = true;
									if (r.getApprMinute()!=null && r.getApprMinute() > 0) {
										passApprMin = false;
										if(createLimitMinute > r.getApprMinute()) {
											passApprMin = true;
										}
									}
									if(!calcEdate.equals(eDate) && createLimitMinute != 0 && passApprMin) {
										workDayResultRepo.save(newResult);
									}
								}
								if(eDate.compareTo(apprEdate) >= 0) {
									List<WtmWorkDayResult> ngvDelRes = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun()
											, calendar.getYmd(), WtmApplService.TIME_TYPE_BASE);
									if(ngvDelRes != null) {
										workDayResultRepo.deleteAll(ngvDelRes);
									}
								}

								reApprFixot = true;

							}

							loopSdate = calcEdate;

							//그런데? 종료시간도 포함된 시각이라면?
							//09:00 ~ 15:00 인데 r 이 10:00~11:00 인지 체크
							logger.debug("eDate.compareTo(apprEdate) : " + eDate.compareTo(apprEdate));
							if(eDate.compareTo(apprEdate) >= 0) {
								//다음데이터를 확인해야한다.
								nextDataCheck = true;
								preEdate = apprEdate;
								preTimeTypeCd = r.getTimeTypeCd();
							} else if(eDate.compareTo(apprEdate) < 0) {
								nextDataCheck = true;
								preEdate = eDate;
								preTimeTypeCd = r.getTimeTypeCd();
							}
						}else if(eDate.compareTo(apprEdate) >= 0) {
							//다음데이터를 확인해야한다.
							nextDataCheck = true;
							preEdate = apprEdate;
							preTimeTypeCd = r.getTimeTypeCd();
						} else if(eDate.compareTo(apprEdate) < 0) {
							nextDataCheck = true;
							preEdate = eDate;
							preTimeTypeCd = r.getTimeTypeCd();
						}

					}else {
						break;
					}

					if(cnt == results.size() && results.size() > 1) {
						preEdate = apprSdate;
					}


				}

				if(nextDataCheck &&  createLimitMinute > 0) {
					//다음 데이터를 체크해야하는데 없었을 경우
					//남은 기간을 생성한다.
					if(preEdate.compareTo(eDate) <= 0 && (preTimeTypeCd.equals(WtmApplService.TIME_TYPE_BASE) || isNgvRega )) {
						Date calcSdate = preEdate;
						Date calcEdate = eDate;

						int apprMinute = 0;
						// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
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

						if(!calcSdate.equals(calcEdate) && apprMinute != 0) {
							workDayResultRepo.save(newResult);
						}
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
	 * @param sDate
	 * @param eDate
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
			if(!timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT)
					&& !timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)
					&& !timeTypeCd.equals(WtmApplService.TIME_TYPE_REGA_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_REGA_NIGHT)
			) {
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
			// P_LIMIT_MINUTE (총기본근로시간) 에서 P_USE_MINUTE (합산 기본 근로시간) 을 뺀 남은 기본 근로 시간에 대해서만 근무 정보를 생성한다..
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
		logger.debug("### WorkTimeCalcApprDate rDt : " + rDt + " /  dt : " + dt + " / calcType : " + calcType);

		SimpleDateFormat dH = new SimpleDateFormat("H");
		SimpleDateFormat dM = new SimpleDateFormat("m");
		SimpleDateFormat dYmd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat hhMM = new SimpleDateFormat("HHmm");

		//시
		int h = Integer.parseInt(dH.format(rDt));
		//분
		int m = Integer.parseInt(dM.format(rDt));

		//시
		int h2 = Integer.parseInt(dH.format(dt));
		//분
		int m2 = Integer.parseInt(dM.format(dt));

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
			} else {
				// 분으로 계산
				//int totMinute = h * m;
				// 지각은 단위시간으로 계산 시 이후 시간으로 해야한다 . 
				// 10분 단위 일 경우 9시 8분일 경우 9시 10분으로 인정되어햔다.

				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();

				cal1.setTime(dt);
				cal2.setTime(rDt);
				//단위 시간 적용
//				int calcM = (((m2 + unitMinute) - ((m + unitMinute)%unitMinute)%60) - ((unitMinute==1)?1:unitMinute)) % unitMinute  ;
				int calcM = (int)(((rDt.getTime() - dt.getTime())/60000/unitMinute)*unitMinute) + (int)((((rDt.getTime() - dt.getTime())/60000%unitMinute))>0?unitMinute:((rDt.getTime() - dt.getTime())/60000%unitMinute));

//				((30+30) - (30+30)%30)%60;
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(dt);
					cal.add(Calendar.MINUTE,calcM);

//					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 9시 58분의 경우 10시가 되어야 하고
				// 23시 58분의 경우 다음날 0시가 되어야 한다 .
//				if(m > calcM) {
//					//58 > 0 이라 1시간을 더하자
//					cal.add(Calendar.HOUR, 1);
//				}
				return cal.getTime();
			}
		} else {
			logger.debug("rDt.compareTo(dt) :: " + rDt.compareTo(dt));
			if(rDt.compareTo(dt) > 0) {
				return rDt;
			} else {

				int calcM = (int)(((rDt.getTime() - dt.getTime())/60000/unitMinute)*unitMinute) + (int)((((rDt.getTime() - dt.getTime())/60000%unitMinute))<0?unitMinute:((rDt.getTime() - dt.getTime())/60000%unitMinute)) * -1;

				Calendar cal = Calendar.getInstance();

				try {
					cal.setTime(dt);
					cal.add(Calendar.MINUTE,calcM);

//					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (Exception e) {
					e.printStackTrace();
				}

//
//				int namerge = m%unitMinute;
//				//단위 시간 적용
//				int calcM = m - m%unitMinute;
//
//				logger.debug("calcM : " + calcM);
//				logger.debug("namerge : " + namerge);
//				/*
//				if(namerge > 0)
//					calcM += unitMinute;
//				logger.debug("calcM : " + calcM);
//				*/
//				Calendar cal = Calendar.getInstance();
//				try {
//					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
				return cal.getTime();
			}
		}
	}

	/**
	 * BREAK TIME TYPE : TIME 의 휴게시간 구하기
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
			int preMinute = 0;
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
				 * ### BREAK_TYPE_TIME wMinute : 240  + 0"
					### BREAK_TYPE_TIME nextBMinute : 60 "
					### BREAK_TYPE_TIME apprMinute : 382 "
					### BREAK_TYPE_TIME isLast : false "
					### BREAK_TYPE_TIME wMinute : 480  + 60"
					### BREAK_TYPE_TIME nextBMinute : 120 "
					### BREAK_TYPE_TIME apprMinute : 382 "
					### BREAK_TYPE_TIME isLast : false "
					### BREAK_TYPE_TIME wMinute : 600 "
					### BREAK_TYPE_TIME nextBMinute : 0 "
					### BREAK_TYPE_TIME apprMinute : 382 "
					### BREAK_TYPE_TIME isLast : true "

				 */
				logger.debug("### BREAK_TYPE_TIME wMinute : " + wMinute);
				logger.debug("### BREAK_TYPE_TIME bMinute : " + bMinute);
				logger.debug("### BREAK_TYPE_TIME nextBMinute : " + nextBMinute);
				logger.debug("### BREAK_TYPE_TIME apprMinute : " + apprMinute);
				logger.debug("### BREAK_TYPE_TIME isLast : " + isLast);
				if( apprMinute > (wMinute + nextBMinute) && !isLast ) {
					preMinute = wMinute;
					continue;
				}else {
					//245 일 경우 브레이크 타임이 5분이 넘어가야하는데 0으로 넘어가고 있다 
					// 240  0 
					// 480 60 으로 셋팅되어있음.
					int bm = apprMinute - (wMinute + bMinute);
					logger.debug("### BREAK_TYPE_TIME bm : " + bm);
					if(bm < 0) {
						breakMinute = apprMinute - preMinute;
						if(breakMinute > bMinute) {
							breakMinute = bMinute;
						}
						logger.debug("### BREAK_TYPE_TIME breakMinute1 : " + breakMinute);
					}else {
						if((bMinute + nextBMinute) > bm ) {
							breakMinute = bMinute+ bm;
							logger.debug("### BREAK_TYPE_TIME breakMinute2 : " + breakMinute);
						}else {
							breakMinute = bMinute;
							logger.debug("### BREAK_TYPE_TIME breakMinute3 : " + breakMinute);
						}
					}
					/*
					if((bMinute + nextBMinute) > bm ) {
						logger.debug("### BREAK_TYPE_TIME bMinute : " + bMinute);
						logger.debug("### BREAK_TYPE_TIME bm : " + bm);
						if(bm < 0) {
							breakMinute = bm;
						}else {
							breakMinute = bMinute;
						}
						logger.debug("### BREAK_TYPE_TIME breakMinute1 : " + breakMinute);
					}else {
						breakMinute = bm;
						logger.debug("### BREAK_TYPE_TIME breakMinute2 : " + breakMinute);
					}
					*/
					//breakMinute = bMinute + (apprMinute - (wMinute + bMinute));
					break;
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
		int breakMinute = 0;
		String minShm = null;
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

					logger.debug("getBreakMinuteIfBreakTimeMGR maxEhm :: "+ maxEhm);
					logger.debug("getBreakMinuteIfBreakTimeMGR timeBreakMgr.getEhm :: "+ timeBreakMgr.getEhm());
					if(maxEhm == null || Integer.parseInt(maxEhm) < Integer.parseInt(timeBreakMgr.getEhm())){
						maxEhm = timeBreakMgr.getEhm();
					}

					if(minShm == null || Integer.parseInt(minShm) < Integer.parseInt(timeBreakMgr.getShm())){
						minShm = timeBreakMgr.getShm();
					}
					//20~60 일 경우 40분이 나와야하는데 60분이 나와서 수정함.
					//20200901 
					//sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), null, null, null);
					//if(Integer.parseInt(timeBreakMgr.getShm()) <= Integer.parseInt(shm) && Integer.parseInt(timeBreakMgr.getEhm()) >= Integer.parseInt(ehm)) {
					//	sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(shm, ehm, null, null, null);
					//}else {
					breakMinute = breakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), null, null, null);
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(),shm, ehm, null);
					//}
				}

			}

			//return this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute) - sumBreakMinute;
		}
		logger.debug("sumBreakMinute :: "+ sumBreakMinute);
		resMap.put("minShm", minShm);
		resMap.put("maxEhm", maxEhm);
		//마지막에 적용된 휴게시간 정보를 보내자
		resMap.put("breakMinute", sumBreakMinute);
		resMap.put("totBreakMinute", breakMinute);
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
		logger.debug("BREAK TYPE MGR 타입의 휴게시간 계산합니다." + sDate + " ~ " +  eDate + " : " + timeCdMgrId + " : " + unitMinute);
		int sumBreakMinute = 0;

		if(sDate.compareTo(eDate) < 1) {
			SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
			SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

			List<WtmTimeBreakMgr> timeBreakMgrs = timebreakMgrRepo.findByTimeCdMgrId(timeCdMgrId);
			String shm = HHmm.format(sDate);
			String ehm = HHmm.format(eDate);
			for(WtmTimeBreakMgr timeBreakMgr : timeBreakMgrs) {
				logger.debug("timeBreakMgr.getShm() :: "+ timeBreakMgr.getShm());
				logger.debug("timeBreakMgr.getEhm() :: "+ timeBreakMgr.getEhm());
				if(Integer.parseInt(shm) > Integer.parseInt(ehm)){
					String e = "2400";
					if (Integer.parseInt(timeBreakMgr.getEhm()) > Integer.parseInt(shm)
							&& Integer.parseInt(timeBreakMgr.getShm()) < Integer.parseInt(e)
					) {
						sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), shm, e, unitMinute);
						/*
						if (!yyyyMMdd.format(sDate).equals(yyyyMMdd.format(eDate))) {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), "0000", unitMinute);
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), "0000", HHmm.format(eDate), unitMinute);
						} else {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), HHmm.format(eDate), unitMinute);
						}
						*/
					}
					String s = "0000";
					if (Integer.parseInt(timeBreakMgr.getEhm()) > Integer.parseInt(s)
							&& Integer.parseInt(timeBreakMgr.getShm()) < Integer.parseInt(ehm)
					) {
						sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), s, ehm, unitMinute);
						/*
						if (!yyyyMMdd.format(sDate).equals(yyyyMMdd.format(eDate))) {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), "0000", unitMinute);
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), "0000", HHmm.format(eDate), unitMinute);
						} else {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), HHmm.format(eDate), unitMinute);
						}
						 */
					}

				}else {
					if (Integer.parseInt(timeBreakMgr.getEhm()) > Integer.parseInt(shm)
							&& Integer.parseInt(timeBreakMgr.getShm()) < Integer.parseInt(ehm)
					) {
						if (!yyyyMMdd.format(sDate).equals(yyyyMMdd.format(eDate))) {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), "0000", unitMinute);
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), "0000", HHmm.format(eDate), unitMinute);
						} else {
							sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), HHmm.format(eDate), unitMinute);
						}
					}
				}

			}

			//return this.WtmCalcMinute(HHmm.format(sDate), HHmm.format(eDate), null, null, unitMinute) - sumBreakMinute;
		}
		logger.debug("sumBreakMinute :: "+ sumBreakMinute);
		logger.debug("BREAK TYPE MGR 타입의 휴게시간 계산합니다. 끝");
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
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		Map<String, Object> breakMap = this.getBreakMinuteIfBreakTimeMGR(cSdate, cEdate, timeCdMgrId);
		int totBreakMinute = 0;
		int addBreakMinute = 0;

		if(breakMap != null) {
			totBreakMinute = (int) breakMap.get("totBreakMinute");
			maxEhm = breakMap.get("maxEhm") + "";
		}
		Date maxBreakDate = null;
		if(maxEhm != null && !"null".equals(maxEhm) && !"".equals(maxEhm)) {
			try {
				maxBreakDate = ymdhm.parse(ymd.format(cSdate)+maxEhm);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Date reCalcEdate = null;
		if(totBreakMinute > 0) {
			cal.setTime(cEdate);
			if(isNegative) {
				cal.add(Calendar.MINUTE, totBreakMinute * -1);
			}else {
				cal.add(Calendar.MINUTE, totBreakMinute);
			}
			cEdate = cal.getTime();


			Map<String, Object> reBreakMap = null;
			if(isNegative) {
				//reBreakMap = this.getBreakMinuteIfBreakTimeMGR(cSdate, maxBreakDate, timeCdMgrId);
				//이건 망인데..
			}else {
				reBreakMap = this.getBreakMinuteIfBreakTimeMGR(maxBreakDate, cEdate, timeCdMgrId);
			}

			if(reBreakMap != null) {
				addBreakMinute = (int) reBreakMap.get("breakMinute");
			}else {
				addBreakMinute = 0;
			}
		}
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


	/**
	 * 종료시간에서 시작시간을 찾는다
	 * @param eDate
	 * @param addMinute
	 * @param timeCdMgrId
	 * @param unitMinute
	 * @return
	 */
	@Override
	public Date P_WTM_DATE_ADD_FOR_BREAK_MGR2(Date eDate, int addMinute, long timeCdMgrId, Integer unitMinute) {

		boolean isNegative = false;
		if(addMinute > 0) {
			isNegative = true;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(eDate);
		cal.add(Calendar.MINUTE, addMinute);

		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: eDate = "+ eDate);
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: addMinute = "+ addMinute);
		Date sDate = cal.getTime();
		logger.debug("P_WTM_DATE_ADD_FOR_BREAK_MGR :: sDate = "+ sDate);

		int breakMinute = 0;
		String minShm =  null;

		Date cSdate = sDate;
		Date cEdate = eDate;
		if(isNegative) {
			cSdate = eDate;
			cEdate = sDate;
		}

		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		Map<String, Object> breakMap = this.getBreakMinuteIfBreakTimeMGR(cSdate, cEdate, timeCdMgrId);
		int totBreakMinute = 0;
		int addBreakMinute = 0;

		if(breakMap != null) {
			totBreakMinute = (int) breakMap.get("totBreakMinute");
			minShm = breakMap.get("minShm") + "";
		}
		Date minBreakDate = null;
		if(minShm != null && !"null".equals(minShm) && !"".equals(minShm)) {
			try {
				minBreakDate = ymdhm.parse(ymd.format(cSdate)+minShm+"");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Date reCalcEdate = null;
		if(totBreakMinute > 0) {
			cal.setTime(cSdate);
			if(isNegative) {
				cal.add(Calendar.MINUTE, totBreakMinute);
			}else {
				cal.add(Calendar.MINUTE, totBreakMinute * -1);
			}
			cSdate = cal.getTime();


			Map<String, Object> reBreakMap = null;

			reBreakMap = this.getBreakMinuteIfBreakTimeMGR(minBreakDate, cSdate, timeCdMgrId);


			if(reBreakMap != null) {
				addBreakMinute = (int) reBreakMap.get("breakMinute");
			}else {
				addBreakMinute = 0;
			}
		}
		if(addBreakMinute > 0) {
			if(isNegative) {
				return P_WTM_DATE_ADD_FOR_BREAK_MGR2(cEdate, addBreakMinute , timeCdMgrId, unitMinute);
			}else {
				return P_WTM_DATE_ADD_FOR_BREAK_MGR2(cSdate, addBreakMinute * -1, timeCdMgrId, unitMinute);
			}
		}

		if(isNegative) {
			return cEdate;
		}else {
			return cSdate;
		}
	}


	@Override
	public Map<String, Integer> calcDayCnt(Long tenantId, String enterCd, String symd, String eymd) {
		Map<String, Integer> resMap = new HashMap<String, Integer>();
		List<WtmDayMgr> days = dayMgrRepo.findBySunYmdBetween(symd, eymd);
		List<WtmHolidayMgr> holDays = holidayMgrRepo.findByTenantIdAndEnterCdAndHolidayYmdBetween(tenantId, enterCd, symd, eymd);

		int totCnt = 0;
		int holCnt = 0;

		if(days != null && days.size() > 0) {
			boolean isHol = false;
			for(WtmDayMgr day : days) {

				if("Y".equals(day.getHolidayYn())){
					isHol = true;
				}else {
					isHol = false;
				}
				logger.debug("day.getHolidayYn() : " + day.getHolidayYn());
				logger.debug("holDays.size() : " + holDays.size());
				if(holDays != null && holDays.size() > 0) {
					for(WtmHolidayMgr holiday : holDays) {
						if(holiday.getId().getHolidayYmd().equals(day.getSunYmd())){
							logger.debug("holiday.getSunYn() : " + holiday.getSunYn());
							if("Y".equals(holiday.getSunYn())) {
								isHol = true;
							}
						}
					}
				}
				totCnt++;
				if(isHol) {
					holCnt++;
				}

			}

		}
		resMap.put("totDays", totCnt);
		resMap.put("holDays", holCnt);
		return resMap;
	}

	@Transactional
	@Override
	public void P_WTM_FLEXIBLE_EMP_WORKTERM_C(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		//주간 시작 종료일을 구한다. 
		logger.debug(tenantId + " : " + enterCd + " : " + sYmd + " / " + eYmd + " 의 주간 시작 종료일을 구한다.");
		List<Map<String, Object>> weeks = wtmFlexibleEmpMapper.getWeekStartEndYmd(paramMap);
		if(weeks != null && weeks.size() > 0) {
			for(Map<String, Object> weekInfoMap : weeks) {
				if(weekInfoMap != null && weekInfoMap.containsKey("weekSymd") && weekInfoMap.get("weekSymd") != null && !"".equals(weekInfoMap.get("weekSymd")+"")
						&& weekInfoMap.containsKey("weekEymd")  && weekInfoMap.get("weekEymd") != null && !"".equals(weekInfoMap.get("weekEymd")+"")) {
					String symd = weekInfoMap.get("weekSymd") + "";
					String eymd = weekInfoMap.get("weekEymd") + "";

					List<WtmTaaCode> taaCodes = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCdLike(tenantId, enterCd, "BREAK%");
					
					/*
					 * MAX(CASE WHEN  TAA_INFO_CD = 'BREAK' THEN TAA_CD ELSE '' END) AS M
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_FIXOT' THEN TAA_CD ELSE '' END) AS F
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_OT' THEN TAA_CD ELSE '' END) AS O
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_REGA' THEN TAA_CD ELSE '' END) AS R
					 */
					String M = "";
					String F = "";
					String O = "";
					String R = "";

					for(WtmTaaCode taaCode : taaCodes) {
						switch (taaCode.getTaaInfoCd()) {
							case "BREAK":
								M = taaCode.getTaaCd();
								break;
							case "BREAK_FIXOT":
								F = taaCode.getTaaCd();
								break;
							case "BREAK_OT":
								O = taaCode.getTaaCd();
								break;
							case "BREAK_REGA":
								R = taaCode.getTaaCd();
								break;
							default:
								break;
						}
					}

					List<WtmWorkTermTime> times = workTermTimeRepo.findByTenantIdAndEnterCdAndSabunAndWeekEdateGreaterThanEqualAndWeekSdateLessThanEqual(tenantId, enterCd, sabun, symd, eymd);
					if(times != null && times.size() > 0) {
						workTermTimeRepo.deleteAll(times);
						workTermTimeRepo.flush();
					}

					List<WtmFlexibleEmp> emps = flexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, sabun, symd, eymd);
					for(WtmFlexibleEmp emp : emps) {
						String weekSdate = "";
						if(Integer.parseInt(emp.getSymd()) >= Integer.parseInt(symd) && Integer.parseInt(emp.getSymd()) <= Integer.parseInt(eymd) ) {
							weekSdate = emp.getSymd();
						}else if(Integer.parseInt(symd) >= Integer.parseInt(emp.getSymd()) && Integer.parseInt(symd) <= Integer.parseInt(emp.getEymd()) ) {
							weekSdate = symd;
						}

						String weekEdate = "";
						if(Integer.parseInt(emp.getEymd()) >= Integer.parseInt(symd) && Integer.parseInt(emp.getEymd()) <= Integer.parseInt(eymd) ) {
							weekEdate = emp.getEymd();
						}else if(Integer.parseInt(eymd) >= Integer.parseInt(emp.getSymd()) && Integer.parseInt(eymd) <= Integer.parseInt(emp.getEymd()) ) {
							weekEdate = eymd;
						}
						logger.debug("weekSdate : " + weekSdate);
						logger.debug("weekEdate : " + weekEdate);
						if(!"".equals(weekSdate) && !"".equals(weekEdate)) {
							WtmFlexibleStdMgr flexibleStdMgr = flexibleStdMgrRepo.findById(emp.getFlexibleStdMgrId()).get();
							List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, sabun, weekSdate, weekEdate);
							logger.debug("results : " + results.size());

							int workDayCnt = 0;

							int avlMinute = 0;
							int planWorkMinute = 0;
							int planOtMinute = 0;

							int apprWorkMinute = 0;
							int apprOtMinute = 0;

							int planExMinute = 0;
							int planOtExMinute = 0;

							int apprExMinute = 0;
							int apprOtExMinute = 0;

							int nowWorkMinute = 0;
							int nowOtMinute = 0;

							int oMinute = 0;
							int nMinute = 0;
							int eOMinute = 0;
							int eNMinute = 0;
							String tempYmd = null;

							//int holOMinute = 0;

							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
							String today = sdf.format(new Date());
							if(results != null && results.size() > 0) {
								for(WtmWorkDayResult result : results) {
									//WtmWorkCalendar cal = workCalandarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, result.getYmd());
									//주 기간내에 여러개의 근무제가 속할 수 있다. 근무제 기간별로 계산되어야한다. 
									if(Integer.parseInt(result.getYmd()) >= Integer.parseInt(emp.getSymd()) && Integer.parseInt(result.getYmd()) <= Integer.parseInt(emp.getEymd()) ) {
										/*
										 *  SUM(CASE WHEN (X.TIME_TYPE_CD IN ('BASE', 'REGA')) OR (X.TIME_TYPE_CD IN ('TAA') AND X.TAA_TIME_YN = 'Y') THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_WORK_MINUTE
										, SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_OT_MINUTE
										, SUM(CASE WHEN (X.TIME_TYPE_CD IN ('BASE', 'REGA')) OR (X.TIME_TYPE_CD IN ('TAA') AND X.TAA_TIME_YN = 'Y') THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_WORK_MINUTE
										, SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_OT_MINUTE 
										 */
										if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS)
												|| (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) && "Y".equals(flexibleStdMgr.getTaaTimeYn()) )
										){
											if(result.getPlanMinute() == null){
												planWorkMinute = planWorkMinute + 0;
											}else {
												planWorkMinute = planWorkMinute + result.getPlanMinute();
											}

											if(result.getApprMinute() == null){
												apprWorkMinute = apprWorkMinute + 0;
											}else {
												apprWorkMinute = apprWorkMinute + result.getApprMinute();
											}
										}

										if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_NIGHT)
												|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_OT)
										) {

											if(result.getPlanMinute() == null){
												planOtMinute = planOtMinute + 0;
											}else {
												planOtMinute = planOtMinute + result.getPlanMinute();
											}

											if(result.getApprMinute() == null){
												apprOtMinute = apprOtMinute + 0;
											}else {
												apprOtMinute = apprOtMinute + result.getApprMinute();
											}

										}

										if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EXCEPT)) {
											/*
											 * , SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_m, v_r) THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_EX_MINUTE 
			                            	   , SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_m, v_r) THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_EX_MINUTE
											 */
											if(result.getTaaCd().equals(M) || result.getTaaCd().equals(R)) {
												if(result.getPlanMinute() == null){
													planExMinute = planExMinute + 0;
												}else {
													planExMinute = planExMinute + result.getPlanMinute();
												}

												if(result.getApprMinute() == null){
													apprExMinute = apprExMinute + 0;
												}else {
													apprExMinute = apprExMinute + result.getApprMinute();
												}
											}
											/*
				                            	, SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_f, v_o) THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_OT_EX_MINUTE 
			                            		, SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_f, v_o) THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_OT_EX_MINUTE 
											 */
											if(result.getTaaCd().equals(F) || result.getTaaCd().equals(O)) {
												if(result.getPlanMinute() == null){
													planOtExMinute = planOtExMinute + 0;
												}else {
													planOtExMinute = planOtExMinute + result.getPlanMinute();
												}

												if(result.getApprMinute() == null){
													apprOtExMinute = apprOtExMinute + 0;
												}else {
													apprOtExMinute = apprOtExMinute + result.getApprMinute();
												}
											}

										}
										/*
										 * , SUM(CASE WHEN X.TIME_TYPE_CD IN ('BASE', 'REGA') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') <= X.YMD THEN IFNULL(X.APPR_MINUTE, X.PLAN_MINUTE) 
									  				 WHEN X.TIME_TYPE_CD IN ('BASE', 'REGA') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') > X.YMD THEN IFNULL(X.APPR_MINUTE, 0) 
									             ELSE 0 END) AS NOW_WORK_MINUTE
							   		  	  , SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') <= X.YMD THEN IFNULL(X.APPR_MINUTE, X.PLAN_MINUTE) 
									  	  				 WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') > X.YMD THEN IFNULL(X.APPR_MINUTE, 0) 
											          ELSE 0 END) AS NOW_OT_MINUTE
										 */
										// 대체휴무도 인정시간은 생성하진 않지만 소정근로시간에는 포함시키자!
										if( (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_SUBS))
										) {
											//오늘 포함 미래일
											if(Integer.parseInt(today) <= Integer.parseInt(result.getYmd())) {
												//인정시간 우선
												if(result.getApprMinute() == null) {
													if(result.getPlanMinute() == null) {
														nowWorkMinute = nowWorkMinute + 0;
													}else {
														nowWorkMinute = nowWorkMinute + result.getPlanMinute();
													}
												}else {
													nowWorkMinute = nowWorkMinute + result.getApprMinute();
												}
											}else {
												//과거는 인정시간만
												if(result.getApprMinute() == null) {
													nowWorkMinute = nowWorkMinute + 0;
												}else {
													nowWorkMinute = nowWorkMinute + result.getApprMinute();
												}
											}

										}
										if( (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT) ||result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_NIGHT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_OT) )
										) {
											int addMinute = 0;
											//오늘 포함 미래일
											if(Integer.parseInt(today) <= Integer.parseInt(result.getYmd())) {
												//인정시간 우선
												if(result.getApprMinute() == null) {
													if(result.getPlanMinute() == null) {
														nowOtMinute = nowOtMinute + 0;
													}else {
														nowOtMinute = nowOtMinute + result.getPlanMinute();
														addMinute = result.getPlanMinute();
													}
												}else {
													nowOtMinute = nowOtMinute + result.getApprMinute();
													addMinute = result.getPlanMinute();
												}
											}else {
												//과거는 인정시간만
												if(result.getApprMinute() == null) {
													nowOtMinute = nowOtMinute + 0;
												}else {
													nowOtMinute = nowOtMinute + result.getApprMinute();
													addMinute = result.getPlanMinute();
												}
											}

											switch(result.getTimeTypeCd()) {
												case WtmApplService.TIME_TYPE_EARLY_OT :
													eOMinute = eOMinute + addMinute;
													break;
												case WtmApplService.TIME_TYPE_OT :
												case WtmApplService.TIME_TYPE_REGA_OT :
													oMinute = oMinute + addMinute;
													break;
												case WtmApplService.TIME_TYPE_EARLY_NIGHT :
													eNMinute = eNMinute + addMinute;
													break;
												case WtmApplService.TIME_TYPE_NIGHT :
												case WtmApplService.TIME_TYPE_REGA_NIGHT :
													nMinute = nMinute + addMinute;
													break;
												default:
													break;
											}


										}

									}
									// 근무일 카운트는 건별로 보기보다는 날짜로 보기 위해 추가
									if(tempYmd == null || !tempYmd.equals(result.getYmd())) {
										workDayCnt++;
										tempYmd = result.getYmd();
									}
								}
							}
							/*
							(XX.APPR_WORK_MINUTE + XX.APPR_OT_MINUTE - XX.APPR_EX_MINUTE) / XX.WORK_DAYS
							, XX.PLAN_WORK_MINUTE - XX.PLAN_EX_MINUTE
							, XX.PLAN_OT_MINUTE- XX.PLAN_OT_EX_MINUTE 
							, XX.APPR_WORK_MINUTE- XX.APPR_EX_MINUTE
							, XX.APPR_OT_MINUTE - XX.APPR_OT_EX_MINUTE
							, XX.NOW_WORK_MINUTE - XX.PLAN_EX_MINUTE
							, XX.NOW_OT_MINUTE- XX.PLAN_OT_EX_MINUTE
							*/
							if(workDayCnt > 0)
								avlMinute = (apprWorkMinute + apprOtMinute - apprExMinute) / workDayCnt;


							WtmWorkTermTime workTermTime = new WtmWorkTermTime();
							workTermTime.setTenantId(tenantId);
							workTermTime.setEnterCd(enterCd);
							workTermTime.setSabun(sabun);
							workTermTime.setWorkTypeCd(emp.getWorkTypeCd());
							workTermTime.setFlexibleSdate(emp.getSymd());
							workTermTime.setFlexibleEdate(emp.getEymd());
							workTermTime.setWeekSdate(weekSdate);
							workTermTime.setWeekEdate(weekEdate);
							workTermTime.setAvlMinute(avlMinute);
							workTermTime.setPlanWorkMinute(planWorkMinute - planExMinute);
							workTermTime.setPlanOtMinute(planOtMinute - planOtExMinute);
							workTermTime.setApprWorkMinute(apprWorkMinute - apprExMinute);
							workTermTime.setApprOtMinute(apprOtMinute - apprOtExMinute);
							workTermTime.setNowWorkMinute(nowWorkMinute - planExMinute);
							workTermTime.setNowOtMinute(nowOtMinute - planOtExMinute);
							workTermTime.setUpdateDate(new Date());
							workTermTime.setNote("");
							workTermTime.setUpdateId("workTermTime");

							/**
							 * break에 대한 부분은 없다.. 빗썸에서 임의로 사용..
							 */
							workTermTime.setoMinute(oMinute);
							workTermTime.setnMinute(nMinute);
							workTermTime.seteOMinute(eOMinute);
							workTermTime.seteNMinute(eNMinute);
							logger.debug("workTermTime : " + workTermTime.toString());
							workTermTimeRepo.save(workTermTime);
						}


					}
				}else {
					logger.debug("[ERR] 주간 정보를 구하지 못했습니다.");
				}
			}
		}

	}

	@Override
	public Map<String, Integer> calcFlexibleMinuteByTypeForWorkTypeFlex(WtmFlexibleEmp flexEmp) {
		logger.debug("call calcFlexibleMinuteByTypeForWorkTypeFlex");
		/*
		SUBS를 빼자 APPL_ID 를 찾아 연장근무 일이 현재 근무기간에 속하지 않을 경우 빼야한다. 
	    SELECT F_WTM_NVL(SUM(F_WTM_NVL(R.PLAN_MINUTE,0)),0) INTO v_subs_minute
		FROM WTM_FLEXIBLE_EMP E
		JOIN WTM_WORK_DAY_RESULT R
		ON E.TENANT_ID = R.TENANT_ID
		AND E.ENTER_CD = R.ENTER_CD
		AND E.SABUN = R.SABUN
		AND R.YMD BETWEEN E.SYMD AND E.EYMD
		JOIN WTM_OT_APPL O
		ON R.APPL_ID = O.APPL_ID
		WHERE E.FLEXIBLE_EMP_ID = P_SABUN
		AND R.TIME_TYPE_CD = 'SUBS'
		AND O.YMD NOT BETWEEN E.SYMD AND E.EYMD 
		;
	    */
		Map<String, Integer> resMap = new HashMap<String, Integer>();

		Integer subsPlanMinute = 0;

		List<WtmWorkDayResult> subsResult = workDayResultRepo.findByFlexibleEmpIdToSubsPlanMinute(flexEmp.getFlexibleEmpId());
		if(subsResult != null && subsResult.size() > 0) {
			for(WtmWorkDayResult r : subsResult) {
				Long applId = r.getApplId();
				if(applId != null && !"".equals(applId)) {
					List<WtmOtAppl> otAppls = otApplRepo.findByApplId(applId);
					if(otAppls != null && otAppls.size() > 0) {
						for(WtmOtAppl oa : otAppls) {
							if(Integer.parseInt(oa.getYmd()) < Integer.parseInt(flexEmp.getSymd())
									|| Integer.parseInt(oa.getYmd()) > Integer.parseInt(flexEmp.getEymd())) {
								if(r.getPlanMinute() != null && !"".equals(r.getPlanMinute())) {
									subsPlanMinute = subsPlanMinute + r.getPlanMinute();
								}
							}
						}
					}
				}

			}
		}
		//workDayResultRepo.findByFlexibleEmpIdToSubsPlanMinute(flexEmp.getFlexibleEmpId());
		if(subsPlanMinute == null)
			subsPlanMinute = 0;

		WtmPropertie prop1 = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(flexEmp.getTenantId(), flexEmp.getEnterCd(), "OPTION_MAX_WORKTIME_1WEEK");
		Integer weekWorkMinite = Integer.parseInt(prop1.getInfoValue());
		WtmPropertie prop2 = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(flexEmp.getTenantId(), flexEmp.getEnterCd(), "OPTION_MAX_WORKTIME_1WEEK_CALC_TYPE");
		String calcType = prop2.getInfoValue();
		WtmPropertie prop3 = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(flexEmp.getTenantId(), flexEmp.getEnterCd(), "OPTION_MAX_WORKTIME_ADD");
		Integer weekOtMinute = Integer.parseInt(prop3.getInfoValue());

		List<String> workTypeCd =new ArrayList<String>();
		workTypeCd.add("SELE_C");
		workTypeCd.add("SELE_F");
		List<WtmWorkCalendar> cals = workCalandarRepo.findByFlexibleEmpIdAndWorkTypeCdIn(flexEmp.getFlexibleEmpId(), workTypeCd);

		int workDayCnt = 0;
		if(cals != null && cals.size() > 0) {
			for(WtmWorkCalendar c : cals) {
				if(c.getHolidayYn() == null || "".equals(c.getHolidayYn()) || "N".equals(c.getHolidayYn())) {
					workDayCnt++;
				}
			}
		}
		logger.debug("휴일을 제외한 근무일은 " + workDayCnt + " / " + cals.size());
			
		/*
		 * SELECT FLOOR(X.C) + ( ( X.C -  FLOOR(X.C)) * 60 ) FROM (
			SELECT CASE WHEN PP.INFO_VALUE = 'B' THEN SUM(CASE C.HOLIDAY_YN WHEN 'Y' THEN 0 WHEN 'N' THEN 1 END) * 8 * 60
							ELSE FLOOR(SUM(CASE C.HOLIDAY_YN WHEN 'Y' THEN 0 WHEN 'N' THEN 1 END) * F_WTM_TO_NUMBER(P.INFO_VALUE, 'Y') / 7) * 60 END 
									AS C
		 */
		Double workMinute = 0.0;
		Double otMinute = 0.0;
		Double workMinuteTypeA = 0.0;
		Double workMinuteTypeB = 0.0;
		if(workDayCnt > 0) {
			logger.debug("workMinute : " + workMinute);
			if( calcType.equals("D") ) { // 법정 근로시간 산정방법 선택
				// 고용노동부 소정시간 (방법1)
				workMinuteTypeA = Math.floor(Double.parseDouble(cals.size()+"") * Double.parseDouble(weekWorkMinite+"") / Double.parseDouble(7+"") * Double.parseDouble(60+""));
				// 현재 사용하는 소정시간 (방법2)
				workMinuteTypeB = (double) (workDayCnt * 8 * 60);

				logger.debug("workMinuteTypeA : " + workMinuteTypeA +", workMinuteTypeB : "+workMinuteTypeB);
				if(workMinuteTypeA < workMinuteTypeB) {
					calcType = "A";
				}else if(workMinuteTypeA > workMinuteTypeB){
					calcType = "B";
				}
			}

			if(calcType.equals("B") || calcType.equals("C")) {
				workMinute = (double) (workDayCnt * 8 * 60);
			}else {
				workMinute = Math.floor(Double.parseDouble(cals.size()+"") * Double.parseDouble(weekWorkMinite+"") / Double.parseDouble(7+"") * Double.parseDouble(60+""));
			}

			/*
			 * SELECT FLOOR(X.C) + ( ( X.C -  FLOOR(X.C)) * 60 ) FROM (
				SELECT  FLOOR(SUM(CASE C.HOLIDAY_YN WHEN 'Y' THEN 1 WHEN 'N' THEN 1 END) * F_WTM_TO_NUMBER(P.INFO_VALUE, 'Y') / 7) * 60  
										AS C
			 */


			logger.debug("workDayCnt : " + workDayCnt);
			logger.debug("weekWorkMinite : " + weekWorkMinite);
			logger.debug("workMinute : " + workMinute);

			workMinute = (Math.floor(workMinute) + ((workMinute - Math.floor(workMinute)) * 60)) - subsPlanMinute	;
			logger.debug("*** calc workMinute : " + workMinute);

			logger.debug("weekOtMinute : " + weekOtMinute);
			if(calcType.equals("C")) {
				otMinute = Math.floor(Double.parseDouble(cals.size()+"") * Double.parseDouble(weekOtMinute+"") / Double.parseDouble(7+"") * Double.parseDouble(60+""));
			}else {
				otMinute = Math.floor(cals.size() * weekOtMinute / 7) * 60;
			}
			logger.debug("otMinute : " + otMinute);
			otMinute = Math.floor(otMinute) + ( ( otMinute -  Math.floor(otMinute))  * 60 );
			logger.debug("*** calc otMinute : " + otMinute);


		}
		resMap.put("workMinute", workMinute.intValue());
		resMap.put("otMinute", otMinute.intValue());

		return resMap;

	}


	@Override
	public void calcWorktimeClose(WtmWorktimeClose worktimeClose , String sabun) {
		List<WtmWorktimeDayClose> days = null;
		if(sabun == null) {
			days = worktimeDayCloseRepo.findByWorktimeCloseId(worktimeClose.getWorktimeCloseId());
		}else {
			days = worktimeDayCloseRepo.findByWorktimeCloseIdAndSabun(worktimeClose.getWorktimeCloseId(), sabun);
		}
		if(days != null && days.size() > 0) {
			worktimeDayCloseRepo.deleteAll(days);
		}

		Long tenantId = worktimeClose.getTenantId();
		String enterCd = worktimeClose.getEnterCd();
		String sYmd = worktimeClose.getSymd();
		String eYmd = worktimeClose.getEymd();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("symd", sYmd);
		paramMap.put("eymd", eYmd);
		paramMap.put("sabun", sabun);

		List<Map<String, Object>> targets = worktimeCloseMapper.getdayCloseTarget(paramMap);
		if(targets != null && targets.size() > 0) {
			for(Map<String, Object> target : targets) {
				String empSabun = target.get("sabun")+"";
				List<WtmFlexibleEmp> emps = flexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, empSabun, sYmd, eYmd);
				Map<String, String> stdMap = new HashMap<String, String>();
				//쿼리를 최소화 하기 위함. 
				if(emps != null && emps.size() > 0) {
					SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
					for(WtmFlexibleEmp emp: emps) {
						WtmFlexibleStdMgr flexibleStdMgr = flexibleStdMgrRepo.findById(emp.getFlexibleStdMgrId()).get();
						Calendar cal = Calendar.getInstance();
						String startYmd = emp.getSymd();
						if(Integer.parseInt(startYmd) < Integer.parseInt(sYmd)) {
							startYmd = sYmd;
						}
						Date sd = null;
						try {
							sd = ymd.parse(startYmd);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String endYmd = emp.getEymd();
						if(Integer.parseInt(endYmd) > Integer.parseInt(eYmd)) {
							endYmd = eYmd;
						}
						Date ed = null;
						try {
							ed = ymd.parse(endYmd);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						while(sd.compareTo(ed) <= 0) {
							stdMap.put(ymd.format(sd), flexibleStdMgr.getTaaTimeYn());
							stdMap.put(ymd.format(sd)+"workTypeCd", flexibleStdMgr.getWorkTypeCd());
							cal.setTime(sd);
							cal.add(Calendar.DATE, 1);
							sd = cal.getTime();
						}
					}
				}
				ObjectMapper mapper = new ObjectMapper();
				try {
					logger.debug("stdMap : " + mapper.writeValueAsString(stdMap));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<WtmWorkCalendar> cals = workCalandarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, empSabun, sYmd, eYmd);
				//List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, empSabun, sYmd, eYmd);
				logger.debug("cals : " + cals.size());
				//
				//		int workDayCnt = 0;
				//
				//		int avlMinute = 0;
				//		int planWorkMinute = 0;
				//		int planOtMinute = 0;
				//
				//		int apprWorkMinute = 0;
				//		int apprOtMinute = 0;
				//
				//		int planExMinute = 0;
				//		int planOtExMinute = 0;
				//
				//		int apprExMinute = 0;
				//		int apprOtExMinute = 0;
				//
				//		int nowWorkMinute = 0;
				//		int nowOtMinute = 0;
				//
				//		int oMinute = 0;
				//		int nMinute = 0;
				//		int eOMinute = 0;
				//		int eNMinute = 0;
				//
				//int holOMinute = 0;

				//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				//String today = sdf.format(new Date());
				if(cals != null && cals.size() > 0) {
					List<WtmTaaCode> taaCodes = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCdLike(tenantId, enterCd, "BREAK%");
					WtmTaaCode leaveTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LEAVE);
					WtmTaaCode lateTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_LATE);
					WtmTaaCode absenceTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaInfoCd(tenantId, enterCd, WtmTaaCode.TAA_INFO_ABSENCE);
					/*
					 * MAX(CASE WHEN  TAA_INFO_CD = 'BREAK' THEN TAA_CD ELSE '' END) AS M
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_FIXOT' THEN TAA_CD ELSE '' END) AS F
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_OT' THEN TAA_CD ELSE '' END) AS O
					 , MAX(CASE WHEN  TAA_INFO_CD = 'BREAK_REGA' THEN TAA_CD ELSE '' END) AS R
					 */
					String M = "";
					String F = "";
					String O = "";
					String R = "";

					for(WtmTaaCode taaCode : taaCodes) {
						switch (taaCode.getTaaInfoCd()) {
							case "BREAK":
								M = taaCode.getTaaCd();
								break;
							case "BREAK_FIXOT":
								F = taaCode.getTaaCd();
								break;
							case "BREAK_OT":
								O = taaCode.getTaaCd();
								break;
							case "BREAK_REGA":
								R = taaCode.getTaaCd();
								break;
							default:
								break;
						}
					}

					logger.debug("stdMap =>" + stdMap.toString());
					for(WtmWorkCalendar calendar : cals) {
						String taaTimeYn = stdMap.get(calendar.getYmd());
						String workTypeCd = stdMap.get(calendar.getYmd()+"workTypeCd");
						logger.debug("calendar.getYmd() =>" + calendar.getYmd());
						logger.debug("workTypeCd => :" + workTypeCd);

						if(workTypeCd != null && !"".equals(workTypeCd)){
							//WtmWorkCalendar calendar = workCalandarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(result.getTenantId(), result.getEnterCd(), result.getYmd(), result.getSabun());
							List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, empSabun, calendar.getYmd());
							Map<String, Integer> resMap = this.calcResult(results, taaTimeYn, M, F, O, R, lateTaaCode.getTaaCd(), leaveTaaCode.getTaaCd(), absenceTaaCode.getTaaCd());
							WtmWorktimeDayClose dayClose = new WtmWorktimeDayClose();
							WtmWorktimeDayClosePK id = new WtmWorktimeDayClosePK();
							id.setWorktimeCloseId(worktimeClose.getWorktimeCloseId());
							id.setSabun(empSabun);
							id.setYmd(calendar.getYmd());
							dayClose.setId(id);
							dayClose.setHolidayYn(calendar.getHolidayYn());
							dayClose.setAbsenceMinute(resMap.get("absenceMinute"));
							dayClose.setLateMinute(resMap.get("lateMinute"));
							dayClose.setLeaveMinute(resMap.get("leaveMinute"));
							dayClose.setOtMinute( resMap.get("oMinute") + resMap.get("eOMinute") );
							dayClose.setOtnMinute( resMap.get("nMinute") + resMap.get("eNMinute"));
							dayClose.setPayMinute(resMap.get("payMinute"));
							dayClose.setNonpayMinute(resMap.get("nonpayMinute"));
							dayClose.setSubYn(null);
							dayClose.setTimeCdMgrId(calendar.getTimeCdMgrId());
							dayClose.setUpdateId("calcWorktimeClose");
							dayClose.setWorkTypeCd(workTypeCd);
							dayClose.setWorkMinute(resMap.get("apprWorkMinute") - resMap.get("apprExMinute"));
							worktimeDayCloseRepo.save(dayClose);
							worktimeDayCloseRepo.flush();;
						}else{
							logger.debug("DayClose workTypeCd is null : \n" + "sabun:" + empSabun + "\n" + "" + "calendar.getYmd() =>" + calendar.getYmd());
						}


					}
					//	workDayCnt++;
				}
			}
		}
		/*
		(XX.APPR_WORK_MINUTE + XX.APPR_OT_MINUTE - XX.APPR_EX_MINUTE) / XX.WORK_DAYS
		, XX.PLAN_WORK_MINUTE - XX.PLAN_EX_MINUTE
		, XX.PLAN_OT_MINUTE- XX.PLAN_OT_EX_MINUTE 
		, XX.APPR_WORK_MINUTE- XX.APPR_EX_MINUTE
		, XX.APPR_OT_MINUTE - XX.APPR_OT_EX_MINUTE
		, XX.NOW_WORK_MINUTE - XX.PLAN_EX_MINUTE
		, XX.NOW_OT_MINUTE- XX.PLAN_OT_EX_MINUTE
		*/
		/*
		if(workDayCnt > 0)
			avlMinute = (apprWorkMinute + apprOtMinute - apprExMinute) / workDayCnt;
		*/

		//return null;


	}

	public Map<String, Integer> calcResult(List<WtmWorkDayResult> results,String taaTimeYn, String M, String F, String O, String R, String late, String leave, String absence){
		Map<String, Integer> resMap = new HashMap<String, Integer>();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		String today = ymd.format(new Date());

		//int avlMinute = 0;
		int planWorkMinute = 0;
		int planOtMinute = 0;

		int apprWorkMinute = 0;
		int apprOtMinute = 0;

		int planExMinute = 0;
		int planOtExMinute = 0;

		int apprExMinute = 0;
		int apprOtExMinute = 0;

		int nowWorkMinute = 0;
		int nowOtMinute = 0;

		int oMinute = 0;
		int nMinute = 0;
		int eOMinute = 0;
		int eNMinute = 0;

		int lateMinute = 0;
		int leaveMinute = 0;
		int absenceMinute = 0;

		int nonpayMinute = 0;
		int payMinute = 0;

		//String taaTimeYn = stdMap.get(result.getYmd());
		/*
		 *  SUM(CASE WHEN (X.TIME_TYPE_CD IN ('BASE', 'REGA')) OR (X.TIME_TYPE_CD IN ('TAA') AND X.TAA_TIME_YN = 'Y') THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_WORK_MINUTE
		, SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_OT_MINUTE
		, SUM(CASE WHEN (X.TIME_TYPE_CD IN ('BASE', 'REGA')) OR (X.TIME_TYPE_CD IN ('TAA') AND X.TAA_TIME_YN = 'Y') THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_WORK_MINUTE
		, SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_OT_MINUTE 
		 */
		if(results != null && results.size() > 0) {
			for(WtmWorkDayResult result : results) {
				//logger.debug("### result : " + result.toString());
				if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA)
						|| (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA) && "Y".equals(taaTimeYn) )
				){
					if(result.getPlanMinute() == null){
						planWorkMinute = planWorkMinute + 0;
					}else {
						planWorkMinute = planWorkMinute + result.getPlanMinute();
					}

					if(result.getApprMinute() == null){
						apprWorkMinute = apprWorkMinute + 0;
					}else {
						apprWorkMinute = apprWorkMinute + result.getApprMinute();
					}
				}

				if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_OT)
						|| result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_NIGHT)
				) {

					if(result.getPlanMinute() == null){
						planOtMinute = planOtMinute + 0;
					}else {
						planOtMinute = planOtMinute + result.getPlanMinute();
					}

					if(result.getApprMinute() == null){
						apprOtMinute = apprOtMinute + 0;
					}else {
						apprOtMinute = apprOtMinute + result.getApprMinute();
					}

				}

				if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EXCEPT)) {
					/*
					 * , SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_m, v_r) THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_EX_MINUTE 
		        	   , SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_m, v_r) THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_EX_MINUTE
					 */
					if(result.getTaaCd().equals(M) || result.getTaaCd().equals(R)) {
						if(result.getPlanMinute() == null){
							planExMinute = planExMinute + 0;
						}else {
							planExMinute = planExMinute + result.getPlanMinute();
						}

						if(result.getApprMinute() == null){
							apprExMinute = apprExMinute + 0;
						}else {
							apprExMinute = apprExMinute + result.getApprMinute();
						}
					}
					/*
		            	, SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_f, v_o) THEN F_WTM_NVL(X.PLAN_MINUTE,0) ELSE 0 END) AS PLAN_OT_EX_MINUTE 
		        		, SUM(CASE WHEN X.TIME_TYPE_CD IN ('EXCEPT') AND X.TAA_CD IN (v_f, v_o) THEN F_WTM_NVL(X.APPR_MINUTE,0) ELSE 0 END) AS APPR_OT_EX_MINUTE 
					 */
					if(result.getTaaCd().equals(F) || result.getTaaCd().equals(O)) {
						if(result.getPlanMinute() == null){
							planOtExMinute = planOtExMinute + 0;
						}else {
							planOtExMinute = planOtExMinute + result.getPlanMinute();
						}

						if(result.getApprMinute() == null){
							apprOtExMinute = apprOtExMinute + 0;
						}else {
							apprOtExMinute = apprOtExMinute + result.getApprMinute();
						}
					}

				}
				if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_LLA)) {

					//지각시간getFlexibleImsiList
					if(result.getTaaCd().equals(late)) {
						lateMinute = lateMinute + (result.getApprMinute()==null?0:result.getApprMinute());
					}else if(result.getTaaCd().equals(leave)) {
						leaveMinute = leaveMinute + (result.getApprMinute()==null?0:result.getApprMinute());
					}else if(result.getTaaCd().equals(absence)) {
						absenceMinute = absenceMinute + (result.getApprMinute()==null?0:result.getApprMinute());
					}

				}
				/*
				 * , SUM(CASE WHEN X.TIME_TYPE_CD IN ('BASE', 'REGA') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') <= X.YMD THEN IFNULL(X.APPR_MINUTE, X.PLAN_MINUTE) 
			  				 WHEN X.TIME_TYPE_CD IN ('BASE', 'REGA') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') > X.YMD THEN IFNULL(X.APPR_MINUTE, 0) 
			             ELSE 0 END) AS NOW_WORK_MINUTE
				  	  , SUM(CASE WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') <= X.YMD THEN IFNULL(X.APPR_MINUTE, X.PLAN_MINUTE) 
			  	  				 WHEN X.TIME_TYPE_CD IN ('OT', 'FIXOT', 'NIGHT') AND F_WTM_DATE_FORMAT(NOW(), 'YMD') > X.YMD THEN IFNULL(X.APPR_MINUTE, 0) 
					          ELSE 0 END) AS NOW_OT_MINUTE
				 */
				if( (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_BASE) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA))
				) {
					//오늘 포함 미래일
					if(Integer.parseInt(today) <= Integer.parseInt(result.getYmd())) {
						//인정시간 우선
						if(result.getApprMinute() == null) {
							if(result.getPlanMinute() == null) {
								nowWorkMinute = nowWorkMinute + 0;
							}else {
								nowWorkMinute = nowWorkMinute + result.getPlanMinute();
							}
						}else {
							nowWorkMinute = nowWorkMinute + result.getApprMinute();
						}
					}else {
						//과거는 인정시간만
						if(result.getApprMinute() == null) {
							nowWorkMinute = nowWorkMinute + 0;
						}else {
							nowWorkMinute = nowWorkMinute + result.getApprMinute();
						}
					}

				}
				if( (result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_OT) ||result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_OT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_FIXOT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_NIGHT) || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_EARLY_NIGHT)  || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_OT)  || result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_REGA_NIGHT) )
				) {
					int addMinute = 0;
					//오늘 포함 미래일
					if(Integer.parseInt(today) <= Integer.parseInt(result.getYmd())) {
						//인정시간 우선
						if(result.getApprMinute() == null) {
							if(result.getPlanMinute() == null) {
								nowOtMinute = nowOtMinute + 0;
							}else {
								nowOtMinute = nowOtMinute + result.getPlanMinute();
								addMinute = result.getPlanMinute();
							}
						}else {
							nowOtMinute = nowOtMinute + result.getApprMinute();
							addMinute = result.getPlanMinute();
						}
					}else {
						//과거는 인정시간만
						if(result.getApprMinute() == null) {
							nowOtMinute = nowOtMinute + 0;
						}else {
							nowOtMinute = nowOtMinute + result.getApprMinute();
							addMinute = result.getPlanMinute();
						}
					}

					switch(result.getTimeTypeCd()) {
						case WtmApplService.TIME_TYPE_EARLY_OT :
							eOMinute = eOMinute + addMinute;
							break;
						case WtmApplService.TIME_TYPE_OT :
						case WtmApplService.TIME_TYPE_REGA_OT :
							oMinute = oMinute + addMinute;
							break;
						case WtmApplService.TIME_TYPE_EARLY_NIGHT :
							eNMinute = eNMinute + addMinute;
							break;
						case WtmApplService.TIME_TYPE_NIGHT :
						case WtmApplService.TIME_TYPE_REGA_NIGHT :
							nMinute = nMinute + addMinute;
							break;
						default:
							break;
					}
				}

				if(result.getTimeTypeCd().equals(WtmApplService.TIME_TYPE_TAA)) {
					WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(result.getTenantId(), result.getEnterCd(), result.getTaaCd());
					if(taaCode == null || (!"".equals(taaCode.getPayYn()) && "N".equals(taaCode.getPayYn()))) {
						nonpayMinute = nonpayMinute + (result.getApprMinute()==null?0:result.getApprMinute());
					}else {
						payMinute = payMinute + (result.getApprMinute()==null?0:result.getApprMinute());
					}
				}
			}
		}

		//휴일이면서 인정근무시간이 있고 신청서정보 취소 유무 확인 
		//대체휴일여부 어케 구하까~

		resMap.put("planWorkMinute", planWorkMinute);
		resMap.put("planOtMinute", planOtMinute);
		resMap.put("apprWorkMinute", apprWorkMinute);
		resMap.put("apprOtMinute", apprOtMinute);
		resMap.put("planExMinute", planExMinute);
		resMap.put("planOtExMinute", planOtExMinute);
		resMap.put("apprExMinute", apprExMinute);
		resMap.put("apprOtExMinute", apprOtExMinute);
		resMap.put("nowWorkMinute", nowWorkMinute);
		resMap.put("nowOtMinute", nowOtMinute);
		resMap.put("oMinute", oMinute);
		resMap.put("nMinute", nMinute);
		resMap.put("eOMinute", eOMinute);
		resMap.put("eNMinute", eNMinute);
		resMap.put("lateMinute", lateMinute);
		resMap.put("leaveMinute", leaveMinute);
		resMap.put("absenceMinute", absenceMinute);
		resMap.put("nonpayMinute", nonpayMinute);
		resMap.put("payMinute", payMinute);

		return resMap;
	}

	@Override
	public Date F_WTM_DATE_ADD(Date d, int addMinute, WtmTimeCdMgr timeCdMgr, int unitMinute) {
		if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
			return this.P_WTM_DATE_ADD_FOR_BREAK_MGR(d, addMinute, timeCdMgr.getTimeCdMgrId(), unitMinute);
		}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, addMinute);
			return cal.getTime();
		}
		return null;
	}
}
