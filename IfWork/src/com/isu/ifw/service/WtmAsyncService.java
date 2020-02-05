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

import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmOtAppl;
import com.isu.ifw.entity.WtmOtSubsAppl;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.util.WtmUtil;

@Service
public class WtmAsyncService {
	
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
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
		
	
	
		@Async("threadPoolTaskExecutor")
		public void createWorkTermtimeByEmployee(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			paramMap.put("pId", userId);
			wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(paramMap);
		}
		
		@Async("threadPoolTaskExecutor")
		@Transactional
		public void initWtmFlexibleEmpOfWtmWorkDayResult(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			paramMap.put("userId", userId);
			paramMap.put("pId", userId); 
			
			wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
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
					createWorkTermtimeByEmployee(emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), ymd, ymd, userId);
				}
			}
			
		}
		
		@Async("threadPoolTaskExecutor")
		public void applyOtSubs(Long tenantId, String enterCd, List<WtmOtAppl> otApplList, boolean isCalcAppr, String userId) {
			for(WtmOtAppl otAppl : otApplList) {
				logger.debug("휴일 대체 생성 [" + tenantId + "@" + enterCd + "@" + otAppl.getSabun() + "] start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				logger.debug("연장근무신청서 : " + otAppl.getOtApplId());
				
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
										
										flexEmpService.addWtmDayResultInBaseTimeType(
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
							flexEmpService.calcApprDayInfo(tenantId, enterCd, sYmd, eYmd, otAppl.getSabun());
							logger.debug("calcApprDayInfo end >>>");
							
							resultParam.put("symd", sYmd);
							resultParam.put("eymd", eYmd);
							
							logger.debug("createWorkTerm start >>>");
							flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(resultParam);
							logger.debug("createWorkTerm end >>>");
						}
						
						//인정시간 만들어지면 인정시간과 ot신청시간 비교
						List<String> otTimeTypeCds = new ArrayList<String>();
						otTimeTypeCds.add(WtmApplService.TIME_TYPE_OT);
						otTimeTypeCds.add(WtmApplService.TIME_TYPE_NIGHT);
						resultParam.put("timeTypeCds", otTimeTypeCds);
						
						Map<String, Object> otMinute = flexEmpMapper.sumResultMinuteByTimeTypeCd(resultParam);
						if(otMinute!=null 
								&& otMinute.containsKey("planMinute") && otMinute.get("planMinute")!=null
								&& otMinute.containsKey("apprMinute") && otMinute.get("apprMinute")!=null) {
							
							int otPlanMinute = Integer.parseInt(otMinute.get("planMinute").toString());
							int otApprMinute = Integer.parseInt(otMinute.get("apprMinute").toString());
							
							logger.debug("otPlanMinute : " + otPlanMinute + "/otApprMinute : " + otApprMinute);
							
							if(otPlanMinute == otApprMinute) {
								//대체휴일
								List<WtmOtSubsAppl> subs = otSubsApplRepo.findByApplId(otAppl.getApplId());
								if(subs!=null && subs.size()>0) {
									logger.debug("save subs start >>> ");
									for(WtmOtSubsAppl sub : subs) { 
										flexEmpService.addWtmDayResultInBaseTimeType(tenantId, enterCd, sub.getSubYmd(), otAppl.getSabun(), WtmApplService.TIME_TYPE_SUBS, "", sub.getSubsSdate(), sub.getSubsEdate(), otAppl.getApplId(), userId);
									}
									logger.debug("save subs end >>> ");
								}
							}
							
						}
						
								
					}
					
					logger.debug("휴일 대체 생성 end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				}
			}
				
		}
		
}
