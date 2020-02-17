package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmPushMgr;
import com.isu.ifw.entity.WtmPushSendHis;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmScheduleMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmPushMgrRepository;
import com.isu.ifw.repository.WtmPushSendHisRepository;
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
	
	@Override
	@Transactional
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
    	System.out.println("********** ymd : " + ymd);
    	
    	Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, -1);
		String beforeYmd = (sdf.format(cal.getTime())).substring(0, 8);
		System.out.println("********** beforeYmd : " + beforeYmd);
    	// 마감구분 A:자정(당일퇴근자 마감), B:익일4시(익일심야근무 퇴근자 마감)
    	String closeType = "A";
    	if("04".equals(ymdh.substring(8, 10))) {
    		closeType = "B";
    	}
    	System.out.println("********** closeType : " + closeType);
    	
    	getDateMap = new HashMap();
    	// beforeYmd = "20200106";
    	getDateMap.put("tenantId", tenantId);
    	getDateMap.put("ymd", beforeYmd);	// 마감은 전일임으로 계산된 전일을 셋팅해야함
    	getDateMap.put("closeType", closeType);
    	
    	// 타각갱신이 완료되면, 출퇴근 기록완성자의 근무시간을 갱신해야한다.
		List<Map<String, Object>> closeList = new ArrayList();
		closeList = wtmScheduleMapper.getWtmCloseDay(getDateMap);
		
		if(closeList != null && closeList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(int i=0; i<closeList.size(); i++) {
        		String enterCd = closeList.get(i).get("enterCd").toString();
        		String sabun = closeList.get(i).get("sabun").toString();
        		String closeYmd = closeList.get(i).get("ymd").toString();
        		System.out.println("********** sabun : " + sabun + ", ymd : " + closeYmd);
        		WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeYmd, closeYmd, sabun);
        		
        		HashMap<String, Object> setTermMap = new HashMap();
        		setTermMap.put("tenantId", tenantId);
        		setTermMap.put("enterCd", enterCd);
        		setTermMap.put("sabun", sabun);
        		setTermMap.put("symd", closeYmd);
        		setTermMap.put("eymd", closeYmd);
        		setTermMap.put("pId", "DAYCLOSE");
        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
			}
			System.out.println("********** size : " + closeList.size() + " end ");
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
				pushList = pushMgrRepository.findBySymdAndEymd(today);
			}

//			logger.debug("pushlist : " + pushList.toString());
//			System.out.println("pushlist : " + pushList.toString());
			
			for(WtmPushMgr push : pushList) {
				List<String> empKeys =  new ArrayList();
				
				String stdType = push.getStdType();
				if("B_IN".equals(stdType) || "A_OUT".equals(stdType)) {
					//기준가져오기
					Map<String, Object> param = new HashMap();
					param.put("stdType", stdType);
					
					param.put("enterCd", push.getEnterCd());
					param.put("stdMinute", push.getStdMinute());
					
					//기준에 맞는 대상자 리스트 가져오기
					List<Map<String, Object>> pushEmps = schedulerMapper.getInoutCheckList(param);
					if(pushEmps != null && pushEmps.size() > 0) {
						logger.debug("3333333333333333333출퇴근미타각 : " + pushEmps.toString());
						//System.out.println(pushEmps.toString());
						List<String> target = new ArrayList();
						for(Map<String, Object> pushEmp : pushEmps) {
							target.add(pushEmp.get("EMP_KEY").toString());
						}
						logger.debug("3333333333333333333대상자 : " + target.toString());
						
						//일단 db 먼저 넣고 나중에 db 내역 보여주는 메뉴 추가하면...
						WtmPushSendHis pushSendHis = new WtmPushSendHis();
						pushSendHis.setEnterCd(push.getEnterCd());
						pushSendHis.setTenantId(push.getTenantId());
						pushSendHis.setStdType(stdType);
						pushSendHis.setSendType("PUSH");
						pushSendHis.setReceiveSabun(target.toString());
						pushSendHis.setReceiveMail(target.toString());
						pushSendHis.setSendMsg(push.getPushMsg());
						pushSendHis.setUpdateId("SYSTEM");
						pushHisRepository.save(pushSendHis);
						logger.debug("3333333333333333333출퇴근미타각 알림 저장 : " + pushSendHis.toString());
						
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
				pushList = pushMgrRepository.findBySymdAndEymd(today);
			}

			for(WtmPushMgr push : pushList) {
				String stdType = push.getStdType();
				if("R_OT".equals(stdType) || "R_WORK".equals(stdType)) {
					Long stdOtTime = Long.valueOf(push.getStdMinute());
					List<Map<String, Object>> otList = new ArrayList();
					
					Map<String, Object> param = new HashMap();
					param.put("stdOtTime", stdOtTime);
					param.put("tenantId", push.getTenantId());
					param.put("enterCd", push.getEnterCd());
					param.put("stdType", stdType);
					param.put("businessPlaceCd", push.getBusinessPlaceCd());
					param.put("ymd", today);
					
					System.out.println("3333333333333333333 : " + param.toString());
					otList = schedulerMapper.getOtList(param);
					
					System.out.println("3333333333333333333근로시간 초과자 리스트 : " + otList.toString());
					logger.debug("3333333333333333333근로시간 초과자 리스트 : " + otList.toString());
//					Map<String, Object> toMail  = new HashMap();
//					List<String> empKeys = new ArrayList();
					String toObj = !push.getPushObj().equals("EMP")?"LEADER":"EMP"; //LEADER, EMAIL
					
					Map<String, Object> toPush  = new HashMap();
					
					for(Map<String, Object> otMap : otList) {
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
						System.out.println("3333333333333333333근로시간 초과자 리스트 : " + toPush.toString()+contents);
						
						logger.debug("3333333333333333333대상자 : " + toPush.toString());
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
							logger.debug("3333333333333333333근로시간 초과자 알림 저장 : " + data.getKey());
							logger.debug("3333333333333333333근로시간 초과자 알림 저장 : " + contents);
							//inboxService.sendPushMessage(push.getTenantId(), push.getEnterCd(), "INFO", data.getKey(), title, contents);
						}
					}
				} 
			}	
		} catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}
}
