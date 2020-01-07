package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmScheduleMapper;

@Service
public class WtmScheduleServiceImpl implements WtmScheduleService {
		
	@Autowired
	WtmScheduleMapper wtmScheduleMapper;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	private WtmFlexibleEmpService WtmFlexibleEmpService;
	
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

}
