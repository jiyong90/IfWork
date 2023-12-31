package com.isu.ifw.service;

import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("mobileApplService")
public class WtmMobileApplServiceImpl implements WtmMobileApplService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmApplMapper applMapper;

	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	@Qualifier("wtmOtApplService")
	WtmApplService otApplService;

	@Autowired
	@Qualifier(value="flexibleEmpService")
	private WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	@Qualifier("wtmEntryApplService")
	WtmApplService entryApplService;

	@Autowired
	@Qualifier("WtmTaaApplService")
	WtmApplService taaApplService;

	@Autowired
	@Qualifier("WtmRegaApplService")
	WtmApplService regaApplService;

	@Autowired
	WtmCalendarService wtmCalendarService;
	
	@Autowired private WtmFlexibleStdMgrRepository flexibleStdMgrRepo;
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;
	
	@Autowired private WtmCalcService calcService;
	
	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;

	@Autowired
	WtmTaaCodeRepository wtmTaaCodeRepo;
	
	@Override
	public ReturnParam requestEntryChgAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		
		Long applId = null;
		if(dataMap.get("applId")!=null && !"".equals(dataMap.get("applId")))
			applId = Long.valueOf(dataMap.get("applId").toString());
		
//		String workTypeCd = null;
//		if(dataMap.get("workTypeCd")!=null && !"".equals(dataMap.get("workTypeCd")))
//			workTypeCd = dataMap.get("workTypeCd").toString();

		String ymd = dataMap.get("ymd").toString().replaceAll("[.-]", "");
		String cSHm = dataMap.get("cSHm")!=null?ymd+dataMap.get("cSHm").toString().replace(":", ""):null;
		String cEHm = dataMap.get("cEHm")!=null?ymd+dataMap.get("cEHm").toString().replace(":", ""):null;


		if(cSHm == null && cEHm == null) {
			rp.setFail("변경시간을 입력바랍니다.");
			return rp;
		}
		dataMap.put("chgSdate", cSHm);
		dataMap.put("chgEdate", cEHm);
		dataMap.put("ymd", ymd);
		dataMap.put("applCd", "ENTRY_CHG");
		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("sabun", sabun);
		
		Map<String, Object> val = applMapper.getApplValidation(dataMap);
		logger.debug("applValidationCheck : " + dataMap.toString() + " , " + val.toString());

		if(val == null) {
			rp.setFail("validation check에 실패하였습니다.");
			return rp;
		}

		if(val.get("entryYn").equals("N")) {
			rp.setFail("신청 가능한 기간은 " + val.get("entryDate").toString() + " 입니다.");
			return rp;
		}
		rp = entryApplService.validate(tenantId, enterCd, sabun, "ENTRY_CHG", dataMap);
			
		if(rp!=null && rp.getStatus()!=null && "FAIL".equals(rp.getStatus())) {
			return rp;
		}
			
		return entryApplService.requestSync(tenantId, enterCd, dataMap, sabun, sabun);
	}
	
	@Override
	public ReturnParam requestOtAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		//ymd, time 중간에 .제거
		setOtData(dataMap);
		
		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);

		//서비스에서 사용하는게 좀 달라서...
		//연장근무 시작,종료 일자 추가
		if(dataMap.containsKey("otSymd") && dataMap.containsKey("otEymd")) {
			dataMap.put("otEdate", dataMap.get("otEymd").toString().replace(".", "").replace("-", "") + dataMap.get("ehm").toString());
			dataMap.put("otSdate", dataMap.get("otSymd").toString().replace(".", "").replace("-", "") + dataMap.get("shm").toString());
		} else {
			dataMap.put("otSdate", dataMap.get("ymd").toString() + dataMap.get("shm").toString());
			dataMap.put("otEdate", dataMap.get("ymd").toString() + dataMap.get("ehm").toString());
		}
		
		ReturnParam rp =  otApplService.validate(tenantId, enterCd, sabun, dataMap.get("applCd").toString(), dataMap);
		if(rp!=null && rp.getStatus()!=null && "FAIL".equals(rp.getStatus())) {
			return rp;
		}
		
		return otApplService.requestSync(tenantId, enterCd, dataMap, sabun, sabun);
	}
	
	@Override
	public ReturnParam validateEntryChgAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		Map<String, Object> resultMap = new HashMap();

		setOtData(dataMap);
		
		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);
//		if(eventSource.equals("ymd")) {
		dataMap.put("ymd", dataMap.get("ymd").toString().replace(".", "").replace("-", ""));
		dataMap = wtmCalendarService.getEmpWorkCalendarDayInfo(dataMap);
		if(dataMap == null) {
			rp.setFail("해당일은 근무시간 정정이 불가합니다.");
			return rp;
		}
//			ReturnParam rp = entryApplService.validate(tenantId, enterCd, sabun, "ENTRY_CHG", dataMap);
//			if(rp!=null && rp.getStatus()!=null && "OK".equals(rp.getStatus())) {
//				entryApplService.request(tenantId, enterCd, applId, workTypeCd, paramMap, sabun, userId);
//			}
//		}
		dataMap.put("ymd", dataMap.get("ymd").toString().substring(0, 4)+"."+dataMap.get("ymd").toString().substring(4, 6) +"."+dataMap.get("ymd").toString().substring(6, 8));
	
		resultMap.put("data", dataMap);
		rp.put("result", resultMap);
		return rp;
	}
	
	@Override
	public Map<String, Object> init(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		if (dataMap.get("applCd").toString().equals("ENTRY_CHG")) {
			dataMap.put("tenantId", tenantId);
			dataMap.put("enterCd", enterCd);
			dataMap.put("sabun", sabun);
			dataMap.put("ymd", dataMap.get("ymd").toString().replace(".", "").replace("-", ""));
			dataMap = wtmCalendarService.getEmpWorkCalendarDayInfo(dataMap);
			dataMap.put("ymd", dataMap.get("ymd").toString().substring(0, 4)+"."+dataMap.get("ymd").toString().substring(4, 6) +"."+dataMap.get("ymd").toString().substring(6, 8));
		}

		return dataMap;
	}

	
	@Override
	public ReturnParam validateOtAppl(String eventSource, Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {

		ReturnParam rp = new ReturnParam();
		try {
		rp.setSuccess("");

		Map<String, Object> resultMap = new HashMap();
		Map<String,Object> itemPropertiesMap = new HashMap();
		Map<String,Object> propertiesMap = new HashMap<String,Object>();

		if(dataMap.get("ymd") == null  || "".equals(dataMap.get("ymd").toString())) {
			return rp;
		}
		
		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);
		setOtData(dataMap);
		
		
		try {
			WtmWorkCalendar calendars =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, dataMap.get("ymd").toString(), sabun);
			WtmFlexibleStdMgr flexibleStdMgr = flexibleStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, dataMap.get("ymd").toString());
			WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(calendars.getTimeCdMgrId()).get();
			
			if(eventSource.equals("ymd")) {
				//신청 가능한지 확인
				ReturnParam temp = otApplService.preCheck(tenantId, enterCd, sabun, dataMap.get("applCd").toString(), dataMap);
				logger.debug("otApplService.preCheck " + temp.toString());
				if(temp.getStatus().equals("FAIL")) {
					dataMap.put("ymd", "");
					throw new Exception(temp.get("message").toString());
				}
				
				dataMap.put("otSymd", dataMap.get("ymd"));
				dataMap.put("otEymd", dataMap.get("ymd"));
				//휴일인지 확인
				
				dataMap.put("holidayYn", calendars.getHolidayYn());
				dataMap.put("timeCdMgrId", calendars.getTimeCdMgrId());
				
				
				Map<String, Object> workHourMap = flexibleEmpService.calcMinuteExceptBreaktime(calendars.getTimeCdMgrId(), dataMap, sabun);
				if(workHourMap == null) {
					throw new Exception();
				}
				logger.debug("workHourMap " + workHourMap.toString());
				
				//신청 가능한 날인지 먼저 체크하고 아니면 리턴
				
				//대체 휴가 사용여부
				dataMap.put("subsYn", temp.get("subsYn"));
				//수당지급대상자인지
				dataMap.put("payTargetYn", temp.get("payTargetYn"));
				
				if(dataMap.get("holidayYn").equals("Y") && (dataMap.get("subsYn").equals("Y")  || dataMap.get("payTargetYn").equals("Y"))) {
					propertiesMap.put("disabled", "false");
					propertiesMap.put("mandatory", "true");
					
					List<Map<String,Object>> itemCollection = new ArrayList<Map<String,Object>>();
					Map<String,Object> item = new HashMap<String,Object>();
					item = new HashMap<String,Object>();
					item.put("text", "선택");
					item.put("value", "");
					itemCollection.add(item);

 					item = new HashMap<String,Object>();
					item.put("text", "휴일대체");
					item.put("value", "Y");
					itemCollection.add(item);
					if(dataMap.get("payTargetYn").equals("Y")) {
						item = new HashMap<String,Object>();
						item.put("text", "위로금/시급지급");
						item.put("value", "N");
						itemCollection.add(item);
					}
					propertiesMap.put("collection", itemCollection);
					
					itemPropertiesMap.put("subsSymd", propertiesMap);
					itemPropertiesMap.put("subsShm", propertiesMap);
					itemPropertiesMap.put("subsEhm", propertiesMap);
					itemPropertiesMap.put("subYn", propertiesMap);
				} else {
					propertiesMap.put("disabled", "true");
					propertiesMap.put("mandatory", "false");
					
					itemPropertiesMap.put("subYn", propertiesMap);
					itemPropertiesMap.put("subsSymd", propertiesMap);
					itemPropertiesMap.put("subsShm", propertiesMap);
					itemPropertiesMap.put("subsEhm", propertiesMap);
					
					dataMap.put("subYn", "");
					dataMap.put("subsSymd", "");
					dataMap.put("subsShm", "");
					dataMap.put("subsEhm", "");
				}
			} else if(eventSource.equals("subYn")) {
				if(dataMap.get("subYn").equals("Y")) {
					propertiesMap.put("disabled", "false");
					propertiesMap.put("mandatory", "true");
					
					itemPropertiesMap.put("subsSymd", propertiesMap);
					itemPropertiesMap.put("subsShm", propertiesMap);
					itemPropertiesMap.put("subsEhm", propertiesMap);
					
				} else {
					propertiesMap.put("disabled", "true");
					propertiesMap.put("mandatory", "false");
					
					itemPropertiesMap.put("subsSymd", propertiesMap);
					itemPropertiesMap.put("subsShm", propertiesMap);
					itemPropertiesMap.put("subsEhm", propertiesMap);
	
					dataMap.put("subsSymd", "");
					dataMap.put("subsShm", "");
					dataMap.put("subsEhm", "");
				}
			} else if(eventSource.equals("subsSymd")) {
				//휴일인지 확인
				WtmWorkCalendar cal =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, dataMap.get("subsSymd").toString(), sabun);
				if(cal != null && cal.getHolidayYn().equals("Y")) {
					dataMap.put("subsSymd", "");
	//				rp.setFail("해당일은 휴일입니다.");
	//				return rp;
					throw new Exception("해당일은 휴일입니다.");
				}
			} else if(eventSource.equals("otSymd") || eventSource.equals("otEymd")) {
				rp.setSuccess("연장근무 시작/종료 일자는 기준일과 근무일자가 다른경우에 변경하시면 됩니다. (근무일이 자정을 넘어서는경우, 주/야 교대 근무자인경우)");
			}
			
			Date sdate = null;
			Date edate = null;
					
			if(!dataMap.get("shm").equals("") && !dataMap.get("ehm").equals("")) {
				String otSdate = dataMap.get("otSymd").toString().replace(".", "").replace("-", "")+dataMap.get("shm").toString();
				String otEdate = dataMap.get("otEymd").toString().replace(".", "").replace("-", "")+dataMap.get("ehm").toString();
				
				sdate = WtmUtil.toDate(otSdate, "yyyyMMddHHmm");
				edate = WtmUtil.toDate(otEdate, "yyyyMMddHHmm");
				
				Date chkD = WtmUtil.addDate(sdate, 1);

				// 시작종료 시간 확인
				int compareOtTerm = sdate.compareTo(edate);
				if(compareOtTerm > 0) {
					rp.setFail("연장근무 시작시간이 종료시간보다 클 수 없습니다.");
					return rp;
				}

				//연장근무 신청 기간이 1일 이상이어서도 안된다! 미쳐가지고..
				int compare = chkD.compareTo(edate);
				//시작일보다 하루 더한 날과 비교하여 크면 안됨
				if(compare < 0) {
					rp.setFail("연장근무 신청을 하루 이상 신청할 수 없습니다.");
					return rp;
				}
				
				//연장근무 신청은 기준일 전/후일의 근무계획시간 사이에만 신청할 수 있음
				Map<String, Object> chekPlan = wtmOtApplMapper.getCheckPlanDate(dataMap);
				Date tempSd = WtmUtil.toDate(chekPlan.get("minPlan").toString(), "yyyyMMddHHmm");
				Date tempEd = WtmUtil.toDate(chekPlan.get("maxPlan").toString(), "yyyyMMddHHmm");
				//시작시각, 종료시각이 무조건 tempSd, tempEd 사이에 있어야 함, 조건문은 나중에 바꿔주시오~
				if((tempSd.compareTo(sdate) > 0 ) || (tempEd.compareTo(sdate) < 0) || (tempSd.compareTo(edate) > 0) || (tempEd.compareTo(edate) < 0)) {
					rp.setFail("선택하신 근무일에 신청할 수 없는 연장근무 시간입니다.!!");
					return rp;
				}
			}
			Map<String, Object> otWorkTime = null;
			Map<String, Object> calcMap = null;
			int totalMinute = 0;
			if(dataMap.containsKey("otSymd") && dataMap.containsKey("otEymd") && !dataMap.get("otSymd").equals("") && !dataMap.get("otEymd").equals("")) {
				otWorkTime = new HashMap();
				if(!dataMap.get("shm").equals("") && !dataMap.get("ehm").equals("")) {
					
					//SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
					
					otWorkTime = calcService.calcApprMinute(sdate, edate, timeCdMgr.getBreakTypeCd(), calendars.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
					/*
					int apprMinute = calcService.WtmCalcMinute(dataMap.get("shm").toString(), dataMap.get("ehm").toString(), null, null, null);
					int breakMinute = 0;
					if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
						
						breakMinute = calcService.getBreakMinuteIfBreakTimeMGR(sdf.parse(dataMap.get("otSymd").toString().replace(".", "")+dataMap.get("shm").toString()), sdf.parse(dataMap.get("otEymd").toString().replace(".", "")+dataMap.get("ehm").toString()), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						apprMinute = apprMinute - breakMinute;
						breakMinute = 0;
					}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
						breakMinute = calcService.getBreakMinuteIfBreakTimeTIME(timeCdMgr.getTimeCdMgrId(), apprMinute);
						apprMinute = apprMinute - breakMinute;
					//}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIMEFIX)) {
					}
					*/
					int apprMinute = Integer.parseInt(otWorkTime.get("apprMinute").toString());
					int breakMinute =  Integer.parseInt(otWorkTime.get("breakMinute").toString());
					otWorkTime.put("calcMinute", apprMinute - breakMinute);
					System.out.println("otWorkTime" + otWorkTime.toString()); //{breakMinuteNoPay=30, calcMinute=-30, breakMinutePaid=0, breakMinute=30}
					
					
					//otWorkTime = flexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, dataMap, sabun);									
					
					if(otWorkTime != null) {
						dataMap.put("desc", "근로시간 : "+ apprMinute + "분 휴게시간 : " + breakMinute + "분");
						dataMap.put("breakMinute", otWorkTime.get("breakMinute"));
//						totalMinute = Integer.parseInt(calcMap.get("calcMinute").toString()) + Integer.parseInt(calcMap.get("breakMinute").toString());
						totalMinute = apprMinute - breakMinute ;
					}
				}
			}
			
			
			//{applSabun=18014, sYmd=20200718, otSymd=20200725, eYmd=20200801, subsShm=, breakTypeCd=TIME, applLevelCd=1, tenantId=2, symd=20200720, desc=근로시간 : 600분 휴게시간 : 120분, eymd=20200726, ehm=1900, shm=0900, ymd=20200725, gubun=40, holidayYn=Y, d=20200725, subsSymd=, subsEhm=, calcMinute=600, applCd=OT, timeCdMgrId=26, sabun=18014, applNm=연장/휴일근로신청, otEymd=20200725, subYn=, enterCd=ISU_ST, payTargetYn=false, subsYn=Y}
			dataMap.put("calcMinute", otWorkTime != null ? totalMinute : "0");
			dataMap.put("subYn", dataMap.containsKey("subYn") ? dataMap.get("subYn").toString():"");
			
			Map<String, Object> val = applMapper.getApplValidation(dataMap);
			logger.debug("applValidationCheck : " + dataMap.toString() + " , " + val.toString());
			System.out.println("applValidationCheck : " + dataMap.toString() + " , " + val.toString());
			if(val == null) {
				throw new Exception("validation check에 실패하였습니다.");
			}
	
			if(val.get("valDate").equals("N") && val.containsKey("pDate")) {
				dataMap.put("ymd", "");
				throw new Exception("신청 가능한 기간은 " + val.get("pDate").toString() + " 입니다.");
			} else if(val.get("valTime").equals("N") && val.containsKey("pTime")) {
				throw new Exception("신청 가능한 시간은 " + val.get("pTime").toString() + " 입니다.");
			} else if(val.get("valUnit").equals("N") && val.containsKey("pUnit")) {
				throw new Exception("신청 가능한 시간단위는 " + val.get("pUnit").toString() + "분 입니다.");
			} else if(val.get("valHunit").equals("N") && val.containsKey("pHunit")) {
				throw new Exception("신청 가능한 최대 시간은 " + val.get("pHunit").toString() + "분 입니다.");
			} else if(val.get("valSdate").equals("N") && val.containsKey("pSdate")) {
				throw new Exception("대체 휴일 신청 가능 기간은 " + val.get("pSdate").toString() + " 입니다.");
			}
			
			if(!dataMap.get("subsSymd").toString().equals("") && !dataMap.get("subsShm").toString().equals("") && !dataMap.get("subsEhm").toString().equals("")) {
				Map<String, Object> temp = new HashMap();
				temp.put("ymd", dataMap.get("subsSymd"));
				temp.put("shm", dataMap.get("subsShm"));
				temp.put("ehm", dataMap.get("subsEhm"));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				
				Date sDate = sdf.parse( dataMap.get("subsSymd").toString()+dataMap.get("subsShm").toString());
				Date eDate = sdf.parse( dataMap.get("subsSymd").toString()+dataMap.get("subsEhm").toString());
				
				//Map<String, Object> subWork = flexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, temp, sabun);
				Map<String, Object> subWork = calcService.calcApprMinute(sDate, eDate, timeCdMgr.getBreakTypeCd(), calendars.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
				
				subWork.put("calcMinute", Integer.parseInt(subWork.get("apprMinute").toString()) - Integer.parseInt(subWork.get("breakMinute").toString()));
				
				if(subWork != null && !subWork.get("calcMinute").toString().equals(dataMap.get("calcMinute").toString())) {
					throw new Exception("대체 휴일 휴게시간은 " + subWork.get("breakMinute") + "분입니다. 신청해야 하는 근로 시간은 " + dataMap.get("calcMinute") + "분입니다.");
				}
				
				System.out.println("otWorkTime" + otWorkTime.toString()); //{breakMinuteNoPay=30, calcMinute=-30, breakMinutePaid=0, breakMinute=30}
				
//				if(otWorkTime != null && !otWorkTime.get("breakMinute").equals("")) {
//					dataMap.put("desc", "근로시간 : "+otWorkTime.get("calcMinute").toString() + "분 휴게시간 : " + otWorkTime.get("breakMinute").toString() + "분");
//				}
			}
		} catch(Exception e) {
			if(e.getMessage() != null && !"".equals(e.getMessage())) {
				rp.setFail(e.getMessage());
			}
			
			
		} finally {
			setOtResult(dataMap);
			resultMap.put("itemAttributesMap", itemPropertiesMap);
			resultMap.put("data", dataMap);
			rp.put("result", resultMap);
		}
			return rp;
		}catch(Exception e) {
			e.printStackTrace();
			rp.setFail(e.getMessage());
		}
		return rp;
	}
	
	private void setOtData(Map<String, Object> dataMap) {
		dataMap.put("ymd", dataMap.get("ymd").toString().replaceAll("[.-]", ""));
		//입력 항목으로 validation check
		if(!dataMap.containsKey("ehm")) {
			dataMap.put("ehm", "");
		} else {
			dataMap.put("ehm", dataMap.get("ehm").toString().replace(":", ""));
		}
		if(!dataMap.containsKey("shm")) {
			dataMap.put("shm", "");
		} else {
			dataMap.put("shm", dataMap.get("shm").toString().replace(":", ""));
		}
		if(!dataMap.containsKey("subsSymd")) {
			dataMap.put("subsSymd", "");
		} else {
			dataMap.put("subsSymd", dataMap.get("subsSymd").toString().replaceAll("[.-]", ""));
		}
		if(!dataMap.containsKey("subsShm")) {
			dataMap.put("subsShm", "");
		} else {
			dataMap.put("subsShm", dataMap.get("subsShm").toString().replace(":", ""));
		}
		if(!dataMap.containsKey("subsEhm")) {
			dataMap.put("subsEhm", "");
		} else {
			dataMap.put("subsEhm", dataMap.get("subsEhm").toString().replace(":", ""));
		}
	}
	
	private void setOtResult(Map<String, Object> dataMap) {
		if(dataMap.containsKey("ymd") && !dataMap.get("ymd").equals("")) {
			dataMap.put("ymd", dataMap.get("ymd").toString().substring(0, 4)+"."+dataMap.get("ymd").toString().substring(4, 6) +"."+dataMap.get("ymd").toString().substring(6, 8));
		}
		if(dataMap.containsKey("shm") && !dataMap.get("shm").equals("")) {
			dataMap.put("shm", dataMap.get("shm").toString().substring(0, 2)+":"+dataMap.get("shm").toString().substring(2, 4));
		}
		if(dataMap.containsKey("ehm") && !dataMap.get("ehm").equals("")) {
			dataMap.put("ehm", dataMap.get("ehm").toString().substring(0, 2)+":"+dataMap.get("ehm").toString().substring(2, 4));
		}
		if(dataMap.containsKey("subsSymd") && !dataMap.get("subsSymd").equals("")) {
			dataMap.put("subsSymd", dataMap.get("subsSymd").toString().substring(0, 4)+"."+dataMap.get("subsSymd").toString().substring(4, 6) +"."+dataMap.get("subsSymd").toString().substring(6, 8));
		}
		if(dataMap.containsKey("subsShm") && !dataMap.get("subsShm").equals("")) {
			dataMap.put("subsShm", dataMap.get("subsShm").toString().substring(0, 2)+":"+dataMap.get("subsShm").toString().substring(2, 4));
		}
		if(dataMap.containsKey("subsEhm") && !dataMap.get("subsEhm").equals("")) {
			dataMap.put("subsEhm", dataMap.get("subsEhm").toString().substring(0, 2)+":"+dataMap.get("subsEhm").toString().substring(2, 4));
		}
	}

	@Override
	public ReturnParam validateAnnualAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		Map<String, Object> resultMap = new HashMap();

		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);

		String symd = "";
		String eymd = "";
		String note = "";

		if(dataMap.get("symd") != null && dataMap.get("eymd")  != null && dataMap.get("reason") != null) {
			if(dataMap.get("symd") == null) {
				rp.setFail("휴가시작일을 입력해주세요");
				return rp;
			} else {
				symd = dataMap.get("symd").toString().replace(".", "").replace("-", "");
			}

			if(dataMap.get("eymd")  == null) {
				rp.setFail("휴가종료일을 입력해주세요");
				return rp;
			} else {
				eymd = dataMap.get("eymd").toString().replace(".", "").replace("-", "");
			}

			if(dataMap.get("reason") == null || "".equals(dataMap.get("reason"))) {
				rp.setFail("사유를 입력해주세요");
				return rp;
			} else {
				note = dataMap.get("reason").toString();
			}

			WtmTaaCode wtmTaaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, dataMap.get("gubun").toString());

			String requestTypeCd = "";
			if(wtmTaaCode != null) {
				requestTypeCd = wtmTaaCode.getRequestTypeCd();
			}

			List<String> symdArr          = new ArrayList<String>();
			List<String> eymdArr          = new ArrayList<String>();
			List<String> requestTypeCdArr = new ArrayList<String>();
			List<String> taaTypeCdArr     = new ArrayList<String>();

			symdArr.add(symd);
			eymdArr.add(eymd);
			requestTypeCdArr.add(requestTypeCd);
			taaTypeCdArr.add(dataMap.get("gubun").toString());

			Map<String, Object> valiMap = new HashMap<String, Object>();
			valiMap.put("startYmdArr", symdArr);
			valiMap.put("endYmdArr", eymdArr);
			valiMap.put("requestTypeCdArr", requestTypeCdArr);
			valiMap.put("taaTypeCdArr", taaTypeCdArr);
			valiMap.put("startHm", ""); //  validation 기본 키값
			valiMap.put("endHm", "");   //  validation 기본 키값
			valiMap.put("note", note);

			rp = taaApplService.validate(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_ANNUAL, valiMap);

			resultMap.put("data", dataMap);
			rp.put("result", resultMap);
		}

		return rp;
	}

	@Override
	public ReturnParam validateRegaAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		Map<String, Object> resultMap = new HashMap();

		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);

		String symd = "";
		String eymd = "";
		String note = "";

		if(dataMap.get("symd") != null && dataMap.get("eymd")  != null ) {
			if (dataMap.get("symd") == null) {
				rp.setFail("휴가시작일을 입력해주세요");
				return rp;
			} else {
				symd = dataMap.get("symd").toString().replace(".", "").replace("-", "");
			}

			if (dataMap.get("eymd") == null) {
				rp.setFail("휴가종료일을 입력해주세요");
				return rp;
			} else {
				eymd = dataMap.get("eymd").toString().replace(".", "").replace("-", "");
			}

//			if (dataMap.get("reason") == null || "".equals(dataMap.get("reason"))) {
//				rp.setFail("사유를 입력해주세요");
//				return rp;
//			} else {
//				note = dataMap.get("reason").toString();
//			}
			WtmTaaCode wtmTaaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, dataMap.get("gubun").toString());

			String requestTypeCd = "";
			if(wtmTaaCode != null) {
				requestTypeCd = wtmTaaCode.getTaaTypeCd();
			}

			List<String> symdArr          = new ArrayList<String>();
			List<String> eymdArr          = new ArrayList<String>();
			List<String> requestTypeCdArr = new ArrayList<String>();
			List<String> taaTypeCdArr     = new ArrayList<String>();

			symdArr.add(symd);
			eymdArr.add(eymd);
			requestTypeCdArr.add(requestTypeCd);
			taaTypeCdArr.add(dataMap.get("gubun").toString());

			Map<String, Object> valiMap = new HashMap<String, Object>();
			valiMap.put("taaSdateArr", symdArr);
			valiMap.put("taaEdateArr", eymdArr);
			valiMap.put("workTimeCode", dataMap.get("gubun").toString());
			valiMap.put("requestCd", "");
			valiMap.put("note", note);
			valiMap.put("applCd", dataMap.get("applCd").toString());

			rp = regaApplService.validate(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_REGA, valiMap);

			resultMap.put("data", dataMap);
			rp.put("result", resultMap);
		}

//		if(note == null || "".equals(note)) {
//			throw new Exception("사유를 입력해주세요.");
//		}



		return rp;
	}

	@Override
	public ReturnParam requestAnnualAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setFail("저장 시 오류가 발생했습니다.");

		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);

		Long applId = null;
		if(dataMap.get("applId")!=null && !"".equals(dataMap.get("applId"))) {
			applId = Long.valueOf(dataMap.get("applId").toString());
		}

		String note = dataMap.get("reason").toString();
		if(note == null || "".equals(note)) {
			throw new Exception("사유를 입력해주세요.");
		}

		WtmTaaCode wtmTaaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, dataMap.get("gubun").toString());

		String requestTypeCd = "";
		if(wtmTaaCode != null) {
			requestTypeCd = wtmTaaCode.getRequestTypeCd();
		}

		List<String> symdArr          = new ArrayList<String>();
		List<String> eymdArr          = new ArrayList<String>();
		List<String> requestTypeCdArr = new ArrayList<String>();
		List<String> taaTypeCdArr     = new ArrayList<String>();

		symdArr.add(dataMap.get("symd").toString().replace(".", "").replace("-", ""));
		eymdArr.add(dataMap.get("eymd").toString().replace(".", "").replace("-", ""));
		requestTypeCdArr.add(requestTypeCd);
		taaTypeCdArr.add(dataMap.get("gubun").toString());

		Map<String, Object> valiMap = new HashMap<String, Object>();
		valiMap.put("startYmdArr", symdArr);
		valiMap.put("endYmdArr", eymdArr);
		valiMap.put("requestTypeCdArr", requestTypeCdArr);
		valiMap.put("taaTypeCdArr", taaTypeCdArr);
		valiMap.put("startHm", ""); //  validation 기본 키값
		valiMap.put("endHm", "");   //  validation 기본 키값
		valiMap.put("note", note);
		valiMap.put("applCd", dataMap.get("applCd").toString());

		rp =  taaApplService.validate(tenantId, enterCd, sabun, dataMap.get("applCd").toString(), valiMap);
		if(rp!=null && rp.getStatus()!=null && "FAIL".equals(rp.getStatus())) {
			return rp;
		}
		return taaApplService.request(tenantId, enterCd, applId, WtmApplService.TIME_TYPE_ANNUAL, valiMap, sabun, sabun);
	}


	@Override
	public ReturnParam requestRegaAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setFail("저장 시 오류가 발생했습니다.");

		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);

		Long applId = null;
		if(dataMap.get("applId")!=null && !"".equals(dataMap.get("applId"))) {
			applId = Long.valueOf(dataMap.get("applId").toString());
		}

		String note = dataMap.get("reason").toString();
		if(note == null || "".equals(note)) {
			throw new Exception("사유를 입력해주세요.");
		}

		WtmTaaCode wtmTaaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, dataMap.get("gubun").toString());

		String requestTypeCd = "";
		if(wtmTaaCode != null) {
			requestTypeCd = wtmTaaCode.getRequestTypeCd();
		}

		List<String> symdArr          = new ArrayList<String>();
		List<String> eymdArr          = new ArrayList<String>();
		List<String> requestTypeCdArr = new ArrayList<String>();
		List<String> taaTypeCdArr     = new ArrayList<String>();

		symdArr.add(dataMap.get("symd").toString());
		eymdArr.add(dataMap.get("eymd").toString());
		requestTypeCdArr.add(requestTypeCd);
		taaTypeCdArr.add(dataMap.get("gubun").toString());

		Map<String, Object> valiMap = new HashMap<String, Object>();
		valiMap.put("taaSdateArr", symdArr);
		valiMap.put("taaEdateArr", eymdArr);
		valiMap.put("workTimeCode", dataMap.get("gubun").toString());
		valiMap.put("requestCd", "");
		valiMap.put("note", note);
		valiMap.put("applCd", dataMap.get("applCd").toString());

		rp =  regaApplService.validate(tenantId, enterCd, sabun, dataMap.get("applCd").toString(), valiMap);
		if(rp!=null && rp.getStatus()!=null && "FAIL".equals(rp.getStatus())) {
			return rp;
		}
		return regaApplService.request(tenantId, enterCd, applId, WtmApplService.TIME_TYPE_REGA, valiMap, sabun, sabun);
	}

	
}