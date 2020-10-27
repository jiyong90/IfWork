package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.entity.CommManagementInfomation;
import com.isu.ifw.common.repository.CommManagementInfomationRepository;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmPushMgr;
import com.isu.ifw.entity.WtmPushSendHis;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInterfaceMapper;
import com.isu.ifw.mapper.WtmScheduleMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmPushMgrRepository;
import com.isu.ifw.repository.WtmPushSendHisRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.util.WtmUtil;

@Service
public class WtmScheduleServiceImpl implements WtmScheduleService {
		
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	WtmScheduleMapper wtmScheduleMapper;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	private WtmFlexibleEmpService WtmFlexibleEmpService;
	
	@Autowired
	private WtmInterfaceService WtmInterfaceService;
	
	@Resource
	WtmPushMgrRepository pushMgrRepository;

	@Autowired
	WtmScheduleMapper schedulerMapper;

	@Resource	
	WtmPushSendHisRepository pushHisRepository;

	@Autowired
	WtmInboxService inboxService;

	@Resource
	WtmEmpHisRepository empRepository;
	
	@Autowired
	WtmInterfaceMapper wtmInterfaceMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
	@Autowired
	WtmTimeCdMgrRepository wtmTimeCdMgrRepo;
	@Autowired
	WtmCalcService calcService;
	
	@Autowired
	private CommManagementInfomationRepository commManagementInfomationRepository;

	
	@Override
	//@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCloseDay(Long tenantId) throws Exception {
		
		// 인터페이스용 변수
		String ifType = "dayClose";
    	String nowDataTime = null;
		HashMap<String, Object> getDateMap = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		Date today = new Date();
		String ymdh = sdf.format(today);
		String ymd = ymdh.substring(0, 8);
    	// logger.debug("********** ymd : " + ymd);
    	
    	Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, -1);
		String beforeYmd = (sdf.format(cal.getTime())).substring(0, 8);
		// logger.debug("********** beforeYmd : " + beforeYmd);
    	// 마감구분 A:자정(당일퇴근자 마감), B:익일4시(익일심야근무 퇴근자 마감)
    	String closeType = "A";
    	if(tenantId == 52 && "04".equals(ymdh.substring(8, 10))) {
    		// 태평양물산은 새벽4시마감
    		closeType = "B";
    	} else if(Integer.parseInt(ymdh.substring(8, 10)) >= 10 && Integer.parseInt(ymdh.substring(8, 10)) <= 16) {
    		closeType = "B";
    	}
    	
    	// logger.debug("********** closeType : " + closeType);
    	//closeType = "A";
    	getDateMap = new HashMap();
    	// beforeYmd = "20200710";
    	// closeType = "A";
    	getDateMap.put("tenantId", tenantId);
    	getDateMap.put("ymd", beforeYmd);	// 마감은 전일임으로 계산된 전일을 셋팅해야함
    	getDateMap.put("closeType", closeType);
    	
    	// 현퇴사용여부, 현출사용여부를 조회하자
    	String taaLocalOut = ""; // 현퇴근태코드
    	String taaLocalIn = "";	// 현출근태코드
    	HashMap<String, Object> getTaaMap = null;
    	getTaaMap = (HashMap<String, Object>) wtmScheduleMapper.getTaaLocalCode(getDateMap);
    	if(getTaaMap != null && getTaaMap.containsKey("localIn") && !getTaaMap.get("localIn").equals("")) {
    		taaLocalIn = getTaaMap.get("localIn").toString();
    	}
    	if(getTaaMap != null && getTaaMap.containsKey("localOut") && !getTaaMap.get("localOut").equals("")) {
    		taaLocalOut = getTaaMap.get("localOut").toString();
    	}
    	// 현퇴코드가 있으면 마감일 현퇴 퇴근시간 갱신
    	if(!"".equals(taaLocalOut)) {
    		getDateMap.put("taaLocalOut", taaLocalOut);
    		logger.debug("schedule_closeday taaLocalOut : "+ getDateMap.toString());
    		int cnt = wtmScheduleMapper.setUpdateLocalOut(getDateMap);
    		logger.debug("schedule_closeday taaLocalOut cnt : "+ cnt);
    	}
    	
    	// 타각갱신이 완료되면, 출퇴근 기록완성자의 근무시간을 갱신해야한다.
		List<Map<String, Object>> closeList = new ArrayList();
		closeList = wtmScheduleMapper.getWtmCloseDay(getDateMap);
		logger.debug("schedule_closeday 1 ymdh: " + ymdh + ", " + getDateMap.toString());
		if(closeList != null && closeList.size() > 0) {
			System.out.println("schedule_closeday 2 " + closeList.size());
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(int i=0; i<closeList.size(); i++) {
        		String enterCd = closeList.get(i).get("enterCd").toString();
        		String sabun = closeList.get(i).get("sabun").toString();
        		String closeYmd = closeList.get(i).get("ymd").toString();
        		//Integer gooutCnt = Integer.parseInt(closeList.get(i).get("gooutCnt").toString());
        		
        		HashMap<String, Object> setTermMap = new HashMap();
        		setTermMap.put("tenantId", tenantId);
        		setTermMap.put("enterCd", enterCd);
        		setTermMap.put("sabun", sabun);
        		setTermMap.put("ymd", closeYmd);
        		setTermMap.put("symd", closeYmd);
        		setTermMap.put("eymd", closeYmd);
        		setTermMap.put("pId", "DAYCLOSE");
        		/*
        		if(gooutCnt > 0) {
					// create result 호출
        			WtmFlexibleEmp flexEmp = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, closeYmd);
    				if(flexEmp == null) {
    					continue;
    				}
        			WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findById(flexEmp.getFlexibleStdMgrId()).get();
        			// 20200709 일단 급하니깐 테넌트로 분기하자
        			if(tenantId == 22) {
        				// 그럼 임시로 f를 불러보자 ngv P_WTM_WORK_DAY_RESULT_CREATE_F 호출시 base 인정근무생성완료

            			Long timeCdMgrId = Long.parseLong(closeList.get(i).get("timeCdMgrId").toString());
            			WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
            			calcService.P_WTM_WORK_DAY_RESULT_CREATE_F(tenantId, enterCd, sabun,  closeYmd, flexStdMgr, timeCdMgr, sabun);
        			} else {
        			// ngv는 P_WTM_WORK_DAY_RESULT_CREATE_N 으로 인정근무가 안만들어짐
        			calcService.P_WTM_WORK_DAY_RESULT_CREATE_N(flexStdMgr, tenantId, enterCd, sabun, closeYmd, 0, sabun);
        			}
        			
        			// wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
        			
        			// 외출횟수만큼 근무시간을 짤라야함 외출정보를 조회하자
        			
        			List<Map<String, Object>> goOutList = new ArrayList();
        			goOutList = wtmScheduleMapper.setCalcGobackList(setTermMap);
        			if(goOutList != null && goOutList.size() > 0) {
        				for(Map<String, Object> f : goOutList) {
        					
        					logger.debug("goout send: " + f.toString());
		        			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		    				WtmFlexibleEmpService.addApprWtmDayResultInBaseTimeType(
		    						  tenantId
		    						, enterCd
		    						, closeYmd
		    						, sabun
		    						, f.get("timeTypeCd").toString()
		    						, ""
		    						, dt.parse(f.get("planSdate").toString())
		    						, dt.parse(f.get("planEdate").toString())
		    						, null
		    						, "0"
		    						, false);
        				}
        			}
        			
        		}
        		*/
        		logger.debug("schedule_closeday tenantId : "+ tenantId + " enterCd : " + enterCd + " sabun : " + sabun + ", ymd : " + closeYmd + ", closeType : " + closeType);
        		WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeYmd, closeYmd, sabun);
        		
        		calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, closeYmd, closeYmd);
        		//wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
			}
			logger.debug("schedule_closeday tenantId : "+ tenantId + " tot cnt" + closeList.size() + " end ");
		}
		
		// 마감일+1 현출 갱신
		// 현출코드가 있으면 마감일익일 현출 출근시간 갱신
    	if(!"".equals(taaLocalIn)) {
    		getDateMap.put("taaLocalIn", taaLocalIn);
    		cal.setTime(today);
    		String nextYmd = (sdf.format(cal.getTime())).substring(0, 8);
    		// nextYmd = "20200708";	// 임시용
    		getDateMap.put("nextYmd", nextYmd);
    		logger.debug("schedule_closeday taaLocalIn : "+ getDateMap.toString());
    		int cnt = wtmScheduleMapper.setUpdateLocalIn(getDateMap);
    		logger.debug("schedule_closeday taaLocalIn cnt : "+ cnt);
    	}
	}
	
	@Override
	//@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCloseDayUnplaned(Long tenantId) throws Exception {
		
		// 인터페이스용 변수
		String ifType = "dayClose";
    	String nowDataTime = null;
		HashMap<String, Object> getDateMap = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		Date today = new Date();
		String ymdh = sdf.format(today);
		String ymd = ymdh.substring(0, 8);
    	// logger.debug("********** ymd : " + ymd);
    	
    	Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, -1);
		String beforeYmd = (sdf.format(cal.getTime())).substring(0, 8);
		// logger.debug("********** beforeYmd : " + beforeYmd);
    	// 마감구분 A:자정(당일퇴근자 마감), B:익일4시(익일심야근무 퇴근자 마감)
    	String closeType = "A";
    	if(tenantId == 52 && "04".equals(ymdh.substring(8, 10))) {
    		// 태평양물산은 새벽4시마감
    		closeType = "B";
    	} else if(Integer.parseInt(ymdh.substring(8, 10)) >= 10 && Integer.parseInt(ymdh.substring(8, 10)) <= 16) {
    		closeType = "B";
    	}
    	
    	// logger.debug("********** closeType : " + closeType);
    	
    	getDateMap = new HashMap();
    	// beforeYmd = "20200703";
    	// closeType = "A";
    	getDateMap.put("tenantId", tenantId);
    	getDateMap.put("ymd", beforeYmd);	// 마감은 전일임으로 계산된 전일을 셋팅해야함
    	getDateMap.put("closeType", closeType);
    	
    	// 타각갱신이 완료되면, 출퇴근 기록완성자의 근무시간을 갱신해야한다.
		List<Map<String, Object>> closeList = new ArrayList();
		closeList = wtmScheduleMapper.getWtmCloseDayUnplaned(getDateMap);
		
		if(closeList != null && closeList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			String userId = "CLOSE";
			for(Map<String, Object> l : closeList) {
				l.put("tenantId", tenantId);
				
				String enterCd = l.get("enterCd").toString();
        		String sabun = l.get("sabun").toString();
        		String closeymd = l.get("ymd").toString();
        		Integer gooutCnt = Integer.parseInt(l.get("gooutCnt").toString());
        		
				l.put("symd", l.get("ymd").toString());
				l.put("eymd", l.get("ymd").toString());
				l.put("sYmd", l.get("ymd").toString());
				l.put("eYmd", l.get("ymd").toString());
				l.put("pId", userId);
				l.put("userId", userId);
				/*
				if(gooutCnt > 0) {
					// create result 호출
        			WtmFlexibleEmp flexEmp = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, closeymd);
    				if(flexEmp == null) {
    					continue;
    				}
        			WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findById(flexEmp.getFlexibleStdMgrId()).get();
        			
        			calcService.P_WTM_WORK_DAY_RESULT_CREATE_N(flexStdMgr, tenantId, enterCd, sabun, closeymd, 0, sabun);
        			
        			// wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
        			
        			// 외출횟수만큼 근무시간을 짤라야함 외출정보를 조회하자
        			List<Map<String, Object>> goOutList = new ArrayList();
        			goOutList = wtmScheduleMapper.setCalcGobackList(l);
        			if(goOutList != null && goOutList.size() > 0) {
        				for(Map<String, Object> f : goOutList) {
        					logger.debug("goout send: " + f.toString());
		        			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		    				WtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
		    						  tenantId
		    						, enterCd
		    						, closeymd
		    						, sabun
		    						, f.get("timeTypeCd").toString()
		    						, ""
		    						, dt.parse(f.get("planSdate").toString())
		    						, dt.parse(f.get("planEdate").toString())
		    						, null
		    						, "0"
		    						, false);
        				}
        			}
        		}
        		*/
				// 일마감생성
				WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeymd, closeymd, sabun);
				// 근무계획시간 합산
				calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, closeymd, closeymd);
				//wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(l);
			}
			logger.debug("schedule_closeUnplaned tenantId : "+ tenantId + " tot cnt" + closeList.size() + " end ");
		}
	}

	@Override
	public void sendIntfData(Long tenantId, String ifType) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("ifType", ifType);
		Map<String, Object> result = new HashMap();
		result.put("lastDate", "20200101090000");
		
		Map<String, Object> temp = wtmInterfaceMapper.getIfLastDate(paramMap);
		if(temp != null && temp.containsKey("lastDate")) {
			result.put("lastDate", temp.get("lastDate"));
		}
		result.put("tenantId", tenantId);
		result.put("ifType", ifType);
		result.put("ifItem", ifType);
		
		List<Map<String, Object>> dataList = new ArrayList();
		if(ifType.toUpperCase().equals("OT")) {
			dataList = schedulerMapper.getIntfOtList(result);
		} else if(ifType.toUpperCase().equals("COMP")) {
			dataList = schedulerMapper.getIntfCompList(result);
		}
		try {
			//intf쪽으로 전송
			if(dataList != null && dataList.size() > 0) {
				String url = "";
			
				List<CommManagementInfomation> info = commManagementInfomationRepository.findByTenantIdAndInfoKeyLike(tenantId, "HR.IT_URL");
				if(info != null && info.size() > 0) {
					url = info.get(0).getInfoData();
					url += "/intf/data/"+ifType;
				}
				
				ObjectMapper mapper = new ObjectMapper();
	        	Map<String, Object> eParam = new HashMap<>();
	        	eParam.put("data", dataList);
	    		logger.debug("================================");
	    		logger.debug(mapper.writeValueAsString(dataList));
	    		logger.debug("================================");
	        	//exchangeService.exchange(url, HttpMethod.POST, null, eParam);
	        	
	    		HttpHeaders headers = new HttpHeaders();
	    		headers.add("Content-Type", "application/json; charset=UTF-8");
	    	
	    		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(eParam, headers);

    			ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);
    			if(res.getStatusCode().value() == HttpServletResponse.SC_OK) {
    				result.put("ifStatus", "OK");
    				result.put("ifMsg", ifType + " : " + dataList.size() + " 전송완료");
    			}
	    	} else {
	    		result.put("ifStatus", "OK");
	    		result.put("ifMsg", "갱신자료없음");
	    	}
		} catch(Exception e) {
			result.put("ifStatus", "ERR");
			result.put("ifMsg", e.getMessage());
			e.printStackTrace();
		} finally {
			result.put("updateDate", WtmUtil.parseDateStr(new Date(), "yyyyMMddHHmmss"));
			result.put("ifEndDate", WtmUtil.parseDateStr(new Date(), "yyyyMMddHHmmss"));
			wtmInterfaceMapper.insertIfHis(result);
		}
	}


	
	
	@Override
	public void sendPushMessageMin(Long tenantId, String enterCd) {
		
		String f = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		String today = sdf.format(new Date());
		
		List<WtmPushMgr> pushList = new ArrayList();
		try {
			if(tenantId != null && enterCd != null) {
				pushList = pushMgrRepository.findByTenantIdAndEnterCdAndSymdAndEymd(tenantId, enterCd, today);
			} else {
				logger.debug("sendPush FAIL");
				throw new Exception();
//				pushList = pushMgrRepository.findBySymdAndEymd(today);
			}

			for(WtmPushMgr push : pushList) {
				List<String> empKeys =  new ArrayList();
				
				String stdType = push.getStdType();
				if("B_IN".equals(stdType) || "A_OUT".equals(stdType) || "B_OUT".equals(stdType)) {
					//기준가져오기
					Map<String, Object> param = new HashMap();
					param.put("stdType", stdType);
					param.put("businessPlaceCd", push.getBusinessPlaceCd());
					param.put("enterCd", push.getEnterCd());
					param.put("stdMinute", push.getStdMinute());
					
					//기준에 맞는 대상자 리스트 가져오기
					List<Map<String, Object>> pushEmps = schedulerMapper.getInoutCheckList(param);
					if(pushEmps != null && pushEmps.size() > 0) {
						//logger.debug(pushEmps.toString());
						List<String> target = new ArrayList();
						for(Map<String, Object> pushEmp : pushEmps) {
							target.add(pushEmp.get("EMP_KEY").toString());
						}
						
						//일단 db 먼저 넣고 나중에 db 내역 보여주는 메뉴 추가하면...
						WtmPushSendHis pushSendHis = new WtmPushSendHis();
						pushSendHis.setEnterCd(push.getEnterCd());
						pushSendHis.setTenantId(push.getTenantId());
						pushSendHis.setStdType(stdType);
						pushSendHis.setSendType("PUSH");
						pushSendHis.setReceiveSabun(""); //너무 길어질 수 있어서 뺌
						pushSendHis.setReceiveMail(""); //너무 길어질 수 있어서 뺌
						pushSendHis.setSendMsg(push.getPushMsg());
						pushSendHis.setUpdateId("SYSTEM");
						pushHisRepository.save(pushSendHis);
						logger.debug("[출퇴근미타각 알림 저장] " + pushSendHis.toString());
						
						inboxService.sendPushMessage(push.getTenantId(), push.getEnterCd(), "INFO", target, push.getTitle(), push.getPushMsg());
					}
				}
			}	
		} catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendPushMessageDay(Long tenantId, String enterCd) {
		
		String f = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		String today = sdf.format(new Date());
		
		List<WtmPushMgr> pushList = new ArrayList();
		try {
			if(tenantId != null && enterCd != null) {
				pushList = pushMgrRepository.findByTenantIdAndEnterCdAndSymdAndEymd(tenantId, enterCd, today);
			} else {
				logger.debug("sendPush FAIL");
				throw new Exception();
//				pushList = pushMgrRepository.findBySymdAndEymd(today);
			}

			for(WtmPushMgr push : pushList) {
				String stdType = push.getStdType();
				if("R_OT".equals(stdType) || "R_WORK".equals(stdType) || "R_TOT".equals(stdType) ) {
					Long stdOtTime = Long.valueOf(push.getStdMinute());
					List<Map<String, Object>> otList = new ArrayList();
					
					Map<String, Object> param = new HashMap();
					param.put("stdOtTime", stdOtTime);
					param.put("tenantId", push.getTenantId());
					param.put("enterCd", push.getEnterCd());
					param.put("stdType", stdType);
					param.put("businessPlaceCd", push.getBusinessPlaceCd());
					param.put("ymd", today);
					
					otList = schedulerMapper.getOtList(param);

					String toObj = !push.getPushObj().equals("EMP")?"LEADER":"EMP"; //LEADER, EMAIL
					
					Map<String, Object> toPush  = new HashMap();
					
					for(Map<String, Object> otMap : otList) {
						logger.debug("[근로시간 초과자 리스트] " + otMap.toString());

						String names = "";
						if(otMap.get(toObj) != null && !otMap.get(toObj).equals("")) {
							String empNames = "";
							if(toPush.containsKey(otMap.get(toObj).toString())) {
								empNames = toPush.get(otMap.get(toObj)).toString() + ", " + otMap.get("EMP_NM").toString();
							} else {
								empNames = otMap.get("EMP_NM").toString();
							}
							toPush.put(otMap.get(toObj).toString(), empNames);
						}
					}
					//메일전송, db 저장
					String title = "근무시간 관리 알림 서비스";
					String fromEmail = "SYSTEM";
					
					for( Map.Entry<String, Object> data : toPush.entrySet() ) { 
						String contents = push.getPushMsg();
						if(contents.contains("[[NAME]]")) {
							contents = contents.replace("[[NAME]]", "[" + data.getValue() + "]");
						}
						//일단 db 먼저 넣고 나중에 db 내역 보여주는 메뉴 추가하면...
						WtmPushSendHis pushSendHis = new WtmPushSendHis();
						pushSendHis.setEnterCd(push.getEnterCd());
						pushSendHis.setTenantId(push.getTenantId());
						pushSendHis.setStdType(stdType);
						pushSendHis.setSendType("PUSH");
						pushSendHis.setReceiveSabun(data.getKey());
						pushSendHis.setReceiveMail(data.getKey());
						pushSendHis.setSendMsg(contents);
						pushSendHis.setUpdateId("SYSTEM");
						pushHisRepository.save(pushSendHis);
						if(push.getMobileYn().equals("Y")) {
							logger.debug("[근로시간 초과자 리스트] " + pushSendHis.toString());
//							inboxService.sendPushMessage(push.getTenantId(), push.getEnterCd(), "INFO", data.getKey(), title, contents);
						}
					}
				} 
			}	
		} catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendPushMessageDay2(Long tenantId, String enterCd) {
		
		String f = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(f);
		String today = sdf.format(new Date());
		
		List<WtmPushMgr> pushList = new ArrayList();
		try {
			if(tenantId != null && enterCd != null) {
				pushList = pushMgrRepository.findByTenantIdAndEnterCdAndSymdAndEymd(tenantId, enterCd, today);
			} else {
				logger.debug("sendPush FAIL");
				throw new Exception();
//				pushList = pushMgrRepository.findBySymdAndEymd(today);
			}

			for(WtmPushMgr push : pushList) {
				String stdType = push.getStdType();
				if("R_OT".equals(stdType) || "R_WORK".equals(stdType) || "R_TOT".equals(stdType) ) {
					Long stdOtTime = Long.valueOf(push.getStdMinute());
					Long stdOtTime2 = Long.valueOf(push.getStdMinute2());
					List<Map<String, Object>> otList = new ArrayList();
					
					Map<String, Object> param = new HashMap();
					param.put("stdOtTime", stdOtTime);
					param.put("stdOtTime2", stdOtTime2);
					param.put("tenantId", push.getTenantId());
					param.put("enterCd", push.getEnterCd());
					param.put("stdType", stdType);
					param.put("businessPlaceCd", push.getBusinessPlaceCd());
					param.put("ymd", today);
					
					//logger.debug("===param " + param.toString());
					if("R_OT".equals(stdType) || "R_WORK".equals(stdType)) {
						otList = schedulerMapper.getOtList2(param);
					} else if("R_TOT".equals(stdType)) {
						otList = schedulerMapper.getTotList(param);
					}

					String toObj = !push.getPushObj().equals("EMP")?"LEADER":"EMP"; //LEADER, EMAIL
					
					Map<String, Object> toPush  = new HashMap();
					
					for(Map<String, Object> otMap : otList) {
						logger.debug("[근로시간 초과자 리스트] " + otMap.toString());

						String names = "";
						if(otMap.get(toObj) != null && !otMap.get(toObj).equals("")) {
							String empNames = "";
							if(toPush.containsKey(otMap.get(toObj).toString())) {
								empNames = toPush.get(otMap.get(toObj)).toString() + ", " + otMap.get("EMP_NM").toString();
							} else {
								empNames = otMap.get("EMP_NM").toString();
							}
							toPush.put(otMap.get(toObj).toString(), empNames);
						}
					}
					//메일전송, db 저장
					String title = "근무시간 관리 알림 서비스";
					String fromEmail = "SYSTEM";
					
					for( Map.Entry<String, Object> data : toPush.entrySet() ) { 
						String contents = push.getPushMsg();
						if(contents.contains("[[NAME]]")) {
							contents = contents.replace("[[NAME]]", "[" + data.getValue() + "]");
						}
						//일단 db 먼저 넣고 나중에 db 내역 보여주는 메뉴 추가하면...
						WtmPushSendHis pushSendHis = new WtmPushSendHis();
						pushSendHis.setEnterCd(push.getEnterCd());
						pushSendHis.setTenantId(push.getTenantId());
						pushSendHis.setStdType(stdType);
						pushSendHis.setSendType("PUSH");
						pushSendHis.setReceiveSabun(data.getKey());
						pushSendHis.setReceiveMail(data.getKey());
						pushSendHis.setSendMsg(contents);
						pushSendHis.setUpdateId("SYSTEM");
						pushHisRepository.save(pushSendHis);
						if(push.getMobileYn().equals("Y")) {
							logger.debug("[근로시간 초과자 리스트] " + pushSendHis.toString());
							inboxService.sendPushMessage(push.getTenantId(), push.getEnterCd(), "INFO", data.getKey(), title, contents);
						}
					}
				} 
			}	
		} catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setTaaReset() throws Exception {
		logger.debug("[근태재갱신] ********************************");
		HashMap<String, Object> getDateMap = new HashMap();
    	
    	// 근무제도 확정시 근태상태가 00으로 갱신된 근태정보를 읽어오자
		List<Map<String, Object>> closeList = new ArrayList();
		closeList = wtmScheduleMapper.getTaaReset();
		// 새로 근태갱신을 하자 99상태로 loop 돌리자
		if(closeList != null && closeList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(int i=0; i<closeList.size(); i++) {
				HashMap<String, Object> reqMap = new HashMap<>();
				reqMap = (HashMap<String, Object>) closeList.get(i);
				// 적용되었던 과거 근태를 삭제하자
				logger.debug("setTaaApplIf call : " + reqMap.toString());
				int cnt = wtmScheduleMapper.setDeleteTaaOld(reqMap);
				logger.debug("setDeleteTaaOld : " + cnt);
				WtmInterfaceService.setTaaApplIf(reqMap); //근태정보 인터페이스
				logger.debug("setTaaApplIf END");
			}
		}
	}
}
