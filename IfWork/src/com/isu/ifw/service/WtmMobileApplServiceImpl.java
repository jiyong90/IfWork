package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

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
	WtmCalendarService wtmCalendarService;
	
	@Override
	public ReturnParam requestEntryChgAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		Long applId = null;
		if(dataMap.get("applId")!=null && !"".equals(dataMap.get("applId")))
			applId = Long.valueOf(dataMap.get("applId").toString());
		
//		String workTypeCd = null;
//		if(dataMap.get("workTypeCd")!=null && !"".equals(dataMap.get("workTypeCd")))
//			workTypeCd = dataMap.get("workTypeCd").toString();

		String ymd = dataMap.get("ymd").toString().replace(".", "");
		String cSHm = dataMap.get("cSHm")!=null?ymd+dataMap.get("cSHm").toString().replace(":", ""):null;
		String cEHm = dataMap.get("cEHm")!=null?ymd+dataMap.get("cEHm").toString().replace(":", ""):null;
		
		dataMap.put("chgSdate", cSHm);
		dataMap.put("chgEdate", cEHm);
		dataMap.put("ymd", ymd);
		ObjectMapper mapper = new ObjectMapper();
		ReturnParam rp = entryApplService.validate(tenantId, enterCd, sabun, "ENTRY_CHG", dataMap);
			
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
		dataMap.put("otSdate", dataMap.get("ymd").toString() + dataMap.get("shm").toString());
		dataMap.put("otEdate", dataMap.get("ymd").toString() + dataMap.get("ehm").toString());

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
		dataMap.put("ymd", dataMap.get("ymd").toString().replace(".", ""));		
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
			dataMap.put("ymd", dataMap.get("ymd").toString().replace(".", ""));		
			dataMap = wtmCalendarService.getEmpWorkCalendarDayInfo(dataMap);
			dataMap.put("ymd", dataMap.get("ymd").toString().substring(0, 4)+"."+dataMap.get("ymd").toString().substring(4, 6) +"."+dataMap.get("ymd").toString().substring(6, 8));
		}

		return dataMap;
	}

	
	@Override
	public ReturnParam validateOtAppl(String eventSource, Long tenantId, String enterCd, String sabun, Map<String, Object> dataMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		Map<String, Object> resultMap = new HashMap();
		Map<String,Object> itemPropertiesMap = new HashMap();
		Map<String,Object> propertiesMap = new HashMap<String,Object>();

		//ymd, time 중간에 .제거
		setOtData(dataMap);
	
		dataMap.put("tenantId", tenantId);
		dataMap.put("enterCd", enterCd);
		dataMap.put("applSabun", sabun);
		dataMap.put("sabun", sabun);
		
		try {
			if(eventSource.equals("ymd")) {
				//신청 가능한지 확인
				ReturnParam temp = otApplService.preCheck(tenantId, enterCd, sabun, dataMap.get("applCd").toString(), dataMap);
				logger.debug("otApplService.preCheck " + temp.toString());
				if(temp.getStatus().equals("FAIL")) {
					dataMap.put("ymd", "");
					throw new Exception(temp.get("message").toString());
				}
				//휴일인지 확인
				WtmWorkCalendar calendars =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, dataMap.get("ymd").toString(), sabun);
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
				
				if(dataMap.get("subsYn").equals("Y") && (dataMap.get("holidayYn").equals("Y") || dataMap.get("payTargetYn").equals("Y"))) {
					propertiesMap.put("disabled", "false");
					propertiesMap.put("mandatory", "true");
					
					List<Map<String,Object>> itemCollection = new ArrayList<Map<String,Object>>();
					Map<String,Object> item = new HashMap<String,Object>();
					item = new HashMap<String,Object>();
					item.put("text", "위로금/시급지급");
					item.put("value", "N");
					itemCollection.add(item);
	
					item = new HashMap<String,Object>();
					item.put("text", "휴일대체");
					item.put("value", "Y");
					itemCollection.add(item);
					propertiesMap.put("collection", itemCollection);
	
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
				WtmWorkCalendar calendars =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, dataMap.get("subsSymd").toString(), sabun);
				if(calendars != null && calendars.getHolidayYn().equals("Y")) {
					dataMap.put("subsSymd", "");
	//				rp.setFail("해당일은 휴일입니다.");
	//				return rp;
					throw new Exception("해당일은 휴일입니다.");
				}
			}
			
			
			Map<String, Object> otWorkTime = null;
			if(!dataMap.get("shm").equals("") && !dataMap.get("ehm").equals("")) {
				otWorkTime = flexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, dataMap, sabun);
				
				System.out.println("otWorkTime" + otWorkTime.toString()); //{breakMinuteNoPay=30, calcMinute=-30, breakMinutePaid=0, breakMinute=30}
				
				if(otWorkTime != null) {
					dataMap.put("desc", "근로시간 : "+otWorkTime.get("calcMinute").toString() + "분 휴게시간 : " + (!otWorkTime.containsKey("breakMinute")?"0":otWorkTime.get("breakMinute").toString()) + "분");
				}
			}
	
			dataMap.put("calcMinute", otWorkTime != null ? otWorkTime.get("calcMinute").toString(): "0");
			
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
			
			if(!dataMap.get("subsSymd").equals("") && !dataMap.get("subsShm").equals("") && !dataMap.get("subsEhm").equals("")) {
				Map<String, Object> temp = new HashMap();
				temp.put("ymd", dataMap.get("subsSymd"));
				temp.put("shm", dataMap.get("subsShm"));
				temp.put("ehm", dataMap.get("subsEhm"));
				
				Map<String, Object> subWork = flexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, temp, sabun);
				if(subWork != null && !subWork.get("calcMinute").toString().equals(dataMap.get("calcMinute").toString())) {
					throw new Exception("대체 휴일 휴게시간은 " + subWork.get("breakMinute") + "분입니다. 신청해야 하는 휴게 시간은 " + dataMap.get("calcMinute") + "분입니다.");
				}
				
				System.out.println("otWorkTime" + otWorkTime.toString()); //{breakMinuteNoPay=30, calcMinute=-30, breakMinutePaid=0, breakMinute=30}
				
				if(otWorkTime != null && !otWorkTime.get("breakMinute").equals("")) {
					dataMap.put("desc", "근로시간 : "+otWorkTime.get("calcMinute").toString() + "분 휴게시간 : " + otWorkTime.get("breakMinute").toString() + "분");
				}
			}
		} catch(Exception e) {
			rp.setFail(e.getMessage());
		} finally {
			setOtResult(dataMap);
			resultMap.put("itemAttributesMap", itemPropertiesMap);
			resultMap.put("data", dataMap);
			rp.put("result", resultMap);
		}
		return rp;
	}
	
	private void setOtData(Map<String, Object> dataMap) {
		dataMap.put("ymd", dataMap.get("ymd").toString().replace(".", ""));
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
			dataMap.put("subsSymd", dataMap.get("subsSymd").toString().replace(".", ""));
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
	
	
}