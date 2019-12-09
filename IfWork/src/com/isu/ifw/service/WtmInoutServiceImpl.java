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
import com.isu.option.vo.ReturnParam;

@Service("inoutService")
public class WtmInoutServiceImpl implements WtmInoutService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmInoutHisMapper inoutHisMapper;
	
	@Autowired
	WtmCalendarMapper wtmCalendarMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Override
	public Map<String, Object> getMenuContext(Long tenantId, String enterCd, String sabun) {

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
			
			String dIn = "-";
			String dOut = "-";
			String dGoback = "-";
			String type = "GO";
			List<Map<String, Object>> list = inoutHisMapper.getContext(paramMap);
			for(Map<String, Object> data : list) {
				if(data.get("inoutTypeCd").equals("IN")) {
					dIn = data.get("inoutDate").toString();
				} else if(data.get("inoutTypeCd").equals("OUT")){
					dOut = data.get("inoutDate").toString();
				} else if(data.get("inoutTypeCd").equals("GO") || data.get("inoutTypeCd").equals("BACK")) {
					if(data.get("inoutTypeCd").equals("GO")) {
						type = "BACK";
						dGoback = "외출 " + data.get("inoutDate").toString();
					}
					else {
						type = "GO";
						dGoback = "복귀 " + data.get("inoutDate").toString();
					} 
				}
			}
			
			menuIn.put("label", "출근하기");
			menuIn.put("description", dIn);
			menuIn.put("inoutType", "IN");
			
			menuOut.put("label", "퇴근하기");
			menuOut.put("description", dOut);
			menuOut.put("inoutType", "OUT");

			menuGoback.put("description", dGoback);
			menuGoback.put("actionType", "ACTIVE");
			menuGoback.put("label", type.equals("GO")?"외출하기":"복귀하기");
			menuGoback.put("backgroundColor", type.equals("GO")?"#93DaFF":"#FFF56E");

		}catch(Exception e) {
			e.printStackTrace();
		} 
		
		return returnMap;
	}

	@Override
	public ReturnParam updateTimecard(Long tenantId, String enterCd, String sabun, String ymd, String inoutType, String entryType) throws Exception {
		
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String today = format1.format(now);
		int cnt = 0;

		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("inoutType", inoutType);
		paramMap.put("ymd", ymd);
		paramMap.put("now", today);
		paramMap.put("entryType", "MO");
		
//		cnt = inoutHisMapper.saveWtmInoutHis(paramMap);
//		if(cnt <= 0) {
//			return cnt;
//		}
//	
		System.out.println("1111111111111111111111111 " + paramMap.toString());
		Map<String, Object> rt = updateTimeStamp(paramMap);
		System.out.println("1111111111111111111111111 " + rt.toString());

		ReturnParam rp = new ReturnParam();
		if(rt == null || !rt.get("sqlErrm").equals("OK"))
			rp.setFail(rt.get("sqlErrm").toString());
		else 
			rp.setSuccess("타각에 성공하였습니다.");
		
		logger.debug("타각 : " + tenantId + "," + enterCd + "," + sabun + "," + rt.toString());
		
		System.out.println("111111111111111111111111111111111111111111111 " + paramMap.get("rtnYmd").toString());
		//퇴근일때만 인정시간 계산
		if(paramMap.containsKey("rtnYmd") && paramMap.get("rtnYmd") != null && paramMap.get("inoutType").equals("OUT"))
			empService.calcApprDayInfo(Long.parseLong(paramMap.get("tenantId").toString()), 
				paramMap.get("enterCd").toString(), paramMap.get("rtnYmd").toString(),
				paramMap.get("rtnYmd").toString(), paramMap.get("sabun").toString());
		
		return rp;
	}
	
	@Override
	public Map<String, Object> updateTimeStamp(Map<String, Object> paramMap) {
		//근무캘린더에 시간만 업데이트
		try {
			wtmCalendarMapper.updateEntryDate(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return paramMap;
	}

	@Override
	public Map<String, Object> getMyInoutDetail(Long tenantId, String enterCd, String sabun, String inoutTypeCd, String inoutDate) throws Exception {
		Map<String, Object> convertMap = new HashMap();
		convertMap.put("tenantId", tenantId);
		convertMap.put("enterCd", enterCd);
		convertMap.put("sabun", sabun);
		convertMap.put("inoutTypeCd", inoutTypeCd);
		convertMap.put("inoutDate", inoutDate);
		
		return inoutHisMapper.getMyInoutDetail(convertMap);
	}
	
	@Override
	public List<Map<String, Object>> getMyInoutList(Long tenantId, String enterCd, String sabun, String month) throws Exception {
		
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("month", month);
		
		return inoutHisMapper.getMyInoutList(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getMyInoutHistory(Long tenantId, String enterCd, String sabun, String ymd) throws Exception {
	
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		
		List<Map<String, Object>> rs = inoutHisMapper.getMyInoutHistory(paramMap);
		for(Map<String,Object> temp : rs) {
			temp.put("key", temp.get("key2"));
		}
		
		return rs;
	}
	
	@Override
	public ReturnParam cancel(Map<String, Object> paramMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("준비중입니다.");
		
		return rp;
	}

	/*
	@Override
	public int checkGoback(Long tenantId, String enterCd, String sabun) {
		int cnt = 0;
		String type = "GO";
		
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String today = format1.format(now);

		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("entryTypeCd", "MO");
		paramMap.put("now", today);
		
		String entrySdate = null;
		try {
			List<Map<String, Object>> list = inoutHisMapper.getContext(paramMap);
			for(Map<String, Object> data : list) {
				if(data.get("inoutTypeCd").equals("GO")) {
					entrySdate = data.get("inoutDate").toString();
					type = "BACK";
				}
			}
			
			paramMap.put("inoutTypeCd", type);
			
			cnt = inoutHisMapper.saveWtmInoutHis(paramMap);
			if(cnt <= 0) {
				return cnt;
			}

			if(type.equals("BACK")) {
				
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//* 퇴근만 계획봐서 전일/당일 퇴근으로 넣기 야간근무신청해씅ㄹ때만 체크해서 
		//* 외출하기 /복귀하기는 집계는 안돌리고 복귀했을때 day results에 넣기
		return cnt;
	}*/
}