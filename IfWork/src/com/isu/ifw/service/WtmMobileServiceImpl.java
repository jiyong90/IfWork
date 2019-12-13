package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmCode;
import com.isu.ifw.entity.WtmCodeGrp;
import com.isu.ifw.entity.WtmTimeBreakMgr;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.repository.WtmCodeGrpRepository;
import com.isu.ifw.repository.WtmCodeRepository;
import com.isu.ifw.vo.ReturnParam;

@Service("mobileService")
public class WtmMobileServiceImpl implements WtmMobileService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Override
	public List<Map<String, Object>> getTermList(Long tenantId, String enterCd, String sabun, String month) {

		Map <String,Object> paramMap = new HashMap<String, Object>();

		Map <String,Object> menuIn = new HashMap();
		Map <String,Object> menuOut = new HashMap();
		Map <String,Object> menuGoback = new HashMap();
		
		Map <String,Object> returnMap = new HashMap();
		returnMap.put("D01", menuIn);
		returnMap.put("D02", menuOut);
		returnMap.put("D03", menuGoback);
		
		//tenant 어디서 가져올지
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
			
//		String ymd = null; //기준일 
//		String md = null; // 기준일에서 월/일만 뺀 값  
//		String inoutType = "NONE";
//		String label = "근무계획없음";
//		String description = "출근체크 필요시 인사팀에 문의 바랍니다";
		
		try {
			//근무계획으로 출퇴근 활성화
			/*
			List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
			System.out.println("inoutStatus : " + list.toString());
			
			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
			Date now = new Date();
			String today = format1.format(now);
			
			for(Map<String, Object> time : list) {
				if(time.get("pSymd").equals(today) && time.get("entrySdate") == null) {
					ymd = time.get("ymd").toString();
					md = time.get("ymd").toString().substring(4, 6) + "/" +time.get("ymd").toString().substring(6, 8);
					inoutType = "IN";
					label =  md +" 출근하기";
					description = "출입 비콘 근처에서 버튼이 활성화됩니다";
				} else if(time.get("pEymd").equals(today) && time.get("entryEdate") == null) {
					ymd = time.get("ymd").toString();
					md = time.get("ymd").toString().substring(4, 6) + "/" +time.get("ymd").toString().substring(6, 8);
					inoutType = "OUT";
					label =  md +" 퇴근하기";
					description = "출입 비콘 근처에서 버튼이 활성화됩니다";
				}
			}*/
			
		

		}catch(Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
}