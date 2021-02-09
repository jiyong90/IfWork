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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmAsyncLog;
import com.isu.ifw.entity.WtmAsyncLogDet;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmFlexibleApply;
import com.isu.ifw.entity.WtmFlexibleApplyEmpTemp;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmAsyncLogDetRepository;
import com.isu.ifw.repository.WtmAsyncLogRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleApplyEmpTempRepository;
import com.isu.ifw.repository.WtmFlexibleApplyRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;

@Service
public class WtmAsyncService {
	
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	public static final String ASYNC_NAME_FLEXIBLE_APPLY_MGR = "flexibleApplyMgr";
	public static final String ASYNC_NAME_WORKTIME_CLOSE = "worktimeClose";
	public static final String ASYNC_STATUS_ING = "I";
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
		
	@Autowired
	WtmFlexibleEmpService flexEmpService;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
		
	@Autowired private WtmCalcService calcService;

	@Autowired
	WtmEmpHisRepository wtmEmpHisRepo;

	@Autowired
	WtmFlexibleEmpResetService flexibleEmpResetService;
	
	@Autowired private WtmAsyncLogRepository asyncLogRepo;
	@Autowired private WtmAsyncLogDetRepository asyncLogDetRepo;

	@Autowired
	WtmFlexibleApplyRepository flexibleApplyRepo;

	@Autowired
	WtmFlexibleApplyEmpTempRepository flexibleApplyEmpTempRepo;
	
	@Autowired private WtmWorkDayResultRepository workDayResultRepo;
	@Autowired private WtmInterfaceService interfaceService;
	@Autowired private WtmFlexibleEmpResetService flexibleEmpResetSerevice; 

	@Async("threadPoolTaskExecutor")
	@Transactional
	public void createWorkTermtimeByEmployee(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId, boolean initResult) {
		/*
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		paramMap.put("userId", userId);
		paramMap.put("pId", userId); 
		
		if(initResult)
			wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
		*/
		if(initResult) {
			try {
				flexibleEmpResetSerevice.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, symd, eymd, "ADMIN");
				calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, symd, eymd);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}
	
	/**
	 * 일 마감
	 * @param tenantId
	 * @param enterCd
	 * @param userId
	 */
	@Async("threadPoolTaskExecutor")
	public void workdayClose(Long tenantId, String enterCd, String userId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		//Date today = WtmUtil.toDate("20191018", "yyyyMMdd");
		String ymd = sdf.format(today);
		
		flexEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, "");
		
		//Calendar cal = new GregorianCalendar();
		//cal.add(Calendar.DATE, -1);
		//Date yesterday = cal.getTime();
		
		//flexEmpService.calcApprDayInfo(tenantId, enterCd, sdf.format(yesterday), sdf.format(yesterday), "");
		
		//workterm 호출.
		List<WtmFlexibleEmp> empList = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndYmdBetween(tenantId, enterCd, ymd);
		if(empList!=null && empList.size()>0) {
			for(WtmFlexibleEmp emp : empList) {
				createWorkTermtimeByEmployee(emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), ymd, ymd, userId, false);
			}
		}
		
	}

	@Async("threadPoolTaskExecutor")
	public void asyncFlexibleEmpRest(Long tenantId, String enterCd, String ymd, List<WtmEmpHis> empList) throws Exception {

		for (WtmEmpHis empHis : empList){
			logger.debug("### EMP_RESET EMP::" + empHis.getSabun());
			System.out.println("### EMP_RESET EMP::" + empHis.getSabun());
			try {
				System.out.println("P_WTM_FLEXIBLE_EMP_RESET start");
				flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, empHis.getSabun(), ymd, ymd.substring(0,4)+"1231", empHis.getSabun());
				System.out.println("P_WTM_FLEXIBLE_EMP_RESET end");
				calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, empHis.getSabun(), ymd.substring(0,4)+"0101", ymd.substring(0,4)+"1231");
				System.out.println("P_WTM_FLEXIBLE_EMP_WORKTERM_C start");
			
			}catch(Exception e) {
				e.printStackTrace();
				logger.debug("### EMP_RESET ERROR::" + empHis.getSabun() + " // " + e.getMessage());
				
			}
		}
	}


	public void cacelFlexibleByEmpId(Long tenantId, String enterCd, List<Long> flexibleEmpIds, String userId){

		WtmAsyncLog log = asyncLogRepo.findByTenantIdAndEnterCdAndAsyncNm(tenantId, enterCd, this.ASYNC_NAME_FLEXIBLE_APPLY_MGR);
		SimpleDateFormat ymdhis = new SimpleDateFormat("yyyyMMddHHmmss");
		String ymdhisStr = ymdhis.format(new Date());

		if(flexibleEmpIds != null && flexibleEmpIds.size() > 0) {
			for(Long flexibleEmpId : flexibleEmpIds) {
				WtmFlexibleEmp emp = wtmFlexibleEmpRepo.findById(flexibleEmpId).get();
				String sYmd = emp.getSymd();
				String eYmd = emp.getEymd();
				String sabun = emp.getSabun();

				wtmFlexibleEmpRepo.delete(emp);

				try {
					flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, sYmd, eYmd, userId);
				} catch (Exception e) {

					WtmAsyncLogDet logDet = new WtmAsyncLogDet();
					logDet.setAsyncLogId(log.getAsyncLogId());
					logDet.setAsyncYmdhis(ymdhisStr);
					logDet.setAsyncKey(emp.getFlexibleEmpId()+"");
					logDet.setAsyncDesc(e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	@Async("threadPoolTaskExecutor")
	public void cancelFlexibleEmpById(Long tenantId, String enterCd, List<Long> flexibleEmpIds, String userId, WtmFlexibleApply flexibleApply) {


		String resultMsg = "";

		try {
			WtmAsyncLog log = asyncLogRepo.findByTenantIdAndEnterCdAndAsyncNm(tenantId, enterCd, this.ASYNC_NAME_FLEXIBLE_APPLY_MGR);
			SimpleDateFormat ymdhis = new SimpleDateFormat("yyyyMMddHHmmss");
			String ymdhisStr = ymdhis.format(new Date());
			if(log == null) {
				log = new WtmAsyncLog();
				log.setTenantId(tenantId);
				log.setEnterCd(enterCd);
				log.setAsyncNm(this.ASYNC_NAME_FLEXIBLE_APPLY_MGR);
			}else {
				if(log.equals(this.ASYNC_STATUS_ING)) {
					WtmAsyncLogDet logDet = new WtmAsyncLogDet();
					logDet.setAsyncLogId(log.getAsyncLogId());
					logDet.setAsyncYmdhis(ymdhisStr);
					logDet.setAsyncKey("cancelFlexibleEmpById");
					logDet.setAsyncDesc("작동중인 취기본보가 있습니다.");
					asyncLogDetRepo.save(logDet);
					return;
				}
			}
			log.setAsyncStatus(this.ASYNC_STATUS_ING);
			log = asyncLogRepo.save(log);


			if(flexibleEmpIds != null && flexibleEmpIds.size() > 0) {
				for(Long flexibleEmpId : flexibleEmpIds) {
					WtmFlexibleEmp emp = wtmFlexibleEmpRepo.findById(flexibleEmpId).get();
					String sYmd = emp.getSymd();
					String eYmd = emp.getEymd();
					String sabun = emp.getSabun();

					wtmFlexibleEmpRepo.delete(emp);

					try {
						flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, sYmd, eYmd, userId);
						
						try {
							List<String> timeTypeCds = new ArrayList<String>();
							timeTypeCds.add(WtmApplService.TIME_TYPE_TAA);
							timeTypeCds.add(WtmApplService.TIME_TYPE_REGA);
							//timeTypeCds.add(WtmApplService.TIME_TYPE_BASE);
							List<WtmWorkDayResult> taaResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCds, sYmd, eYmd);
							if(taaResults != null && taaResults.size() > 0) {
								List<String> taaYmd = new ArrayList<String>();
								for(WtmWorkDayResult r : taaResults) {
									if(taaYmd.indexOf(r.getYmd()) == -1) {
										taaYmd.add(r.getYmd());
									}
								}
								workDayResultRepo.deleteAll(taaResults);
								workDayResultRepo.flush();
								for(String ymd : taaYmd) {
									interfaceService.resetTaaResult(tenantId, enterCd, sabun, ymd);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						};
					} catch (Exception e) {

						WtmAsyncLogDet logDet = new WtmAsyncLogDet();
						logDet.setAsyncLogId(log.getAsyncLogId());
						logDet.setAsyncYmdhis(ymdhisStr);
						logDet.setAsyncKey(emp.getFlexibleEmpId()+"");
						logDet.setAsyncDesc(e.getMessage());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			log.setAsyncStatus("");
			log = asyncLogRepo.save(log);

			WtmAsyncLogDet logDet = new WtmAsyncLogDet();
			logDet.setAsyncLogId(log.getAsyncLogId());
			logDet.setAsyncYmdhis(ymdhisStr);
			logDet.setAsyncKey("OK");
			logDet.setAsyncDesc("cancelFlexibleEmpById");
			asyncLogDetRepo.save(logDet);

			resultMsg = "취소 완료";


		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			flexibleApply.setNote(resultMsg);
			flexibleApply.setApplyYn(WtmApplService.WTM_FLEXIBLE_APPLY_N);
			flexibleApplyRepo.save(flexibleApply);

			WtmFlexibleApplyEmpTemp empTemp = new WtmFlexibleApplyEmpTemp();
			empTemp.setTenantId(tenantId);
			empTemp.setEnterCd(enterCd);
			empTemp.setFlexibleApplyId(flexibleApply.getFlexibleApplyId());
			empTemp.setApplyYn(WtmApplService.WTM_FLEXIBLE_APPLY_N);
			//  확정취소시  WTM_FLEXIBLE_APPLY_EMP_TEMP << 여기에 applyYn도  N으로 업뎃
			int empTempCnt = flexibleApplyEmpTempRepo.updateApplyYnByFlexibleApplId(empTemp);

			logger.info("empTempCnt :" + empTempCnt);
		}

	}


	public void startAsync(WtmAsyncLog log) {
		log.setAsyncStatus(this.ASYNC_STATUS_ING);
		asyncLogRepo.save(log);
		asyncLogRepo.flush();
	}
	public void endAsync(WtmAsyncLog log) {
		log.setAsyncStatus("");
		asyncLogRepo.save(log);
		asyncLogRepo.flush();
	}
	
	@Transactional
	public WtmAsyncLog findByTenantIdAndEnterCdAndAsyncKey(Long tenantId, String enterCd, String asyncNm) {
		WtmAsyncLog log = asyncLogRepo.findByTenantIdAndEnterCdAndAsyncNm(tenantId, enterCd, asyncNm);
		if(log == null) {
			log = new WtmAsyncLog();
			log.setTenantId(tenantId);
			log.setEnterCd(enterCd);
			log.setAsyncNm(asyncNm);
			log.setAsyncStatus(""); 
			return asyncLogRepo.save(log);
		}
		return log;
	}
	
	@Transactional
	public WtmAsyncLogDet saveLogDet(Long asyncLogId, String ymdhisStr, String asyncKey, String asyncDesc) {
		WtmAsyncLogDet logDet = new WtmAsyncLogDet();
		logDet.setAsyncLogId(asyncLogId);
		logDet.setAsyncYmdhis(ymdhisStr);
		logDet.setAsyncKey(asyncKey);
		logDet.setAsyncDesc(asyncDesc);
		return asyncLogDetRepo.save(logDet);
	}

		
}
