package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("inoutService")
public class WtmInoutServiceImpl implements WtmInoutService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmInoutHisMapper inoutHisMapper;
	
	@Autowired
	WtmCalendarMapper wtmCalendarMapper;
	
	@Autowired
	private WtmFlexibleEmpService empService;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Resource
	WtmEmpHisRepository empRepository;

	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	
	@Resource
	WtmWorkCalendarRepository calendarRepository;

	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;

	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
    private org.mybatis.spring.SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired   
	SqlSessionFactory sqlSessionFactory;
	
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
			logger.debug(e.getMessage());
			e.printStackTrace();
		} 
		
		return returnMap;
	}

	@Override
	public Map<String, Object> getMenuContext2(Long tenantId, String enterCd, String sabun) {

		Map <String,Object> paramMap = new HashMap<String, Object>();

		Map <String,Object> menuInOut = new HashMap();
		Map <String,Object> menuGoback = new HashMap();

		Map <String,Object> returnMap = new HashMap();
		returnMap.put("D01", menuInOut);
		returnMap.put("D03", menuGoback);
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
			
		String ymd = null; //기준일 
		String md = null; // 기준일에서 월/일만 뺀 값  
		String inoutType = "NONE";
		String label = "근무계획없음";
		String description = "출근체크 필요시 인사팀에 문의 바랍니다";
		
		try {
			List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
			logger.debug("inoutStatus : " + list.toString());
			
			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
			Date now = new Date();
			String today = format1.format(now);
			
			for(Map<String, Object> time : list) {
				if(!time.containsKey("pSymd") ||  !time.containsKey("pEymd"))
					continue;
				if((time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) && time.get("entrySdate") == null) {
					ymd = time.get("ymd").toString();
					md = time.get("ymd").toString().substring(4, 6) + "/" +time.get("ymd").toString().substring(6, 8);
					inoutType = "IN";
					label =  md +" 출근하기";
					description = "출입 비콘 근처에서 버튼이 활성화됩니다";
					break;
				} else if((time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) && time.get("entryEdate") == null) {
					ymd = time.get("ymd").toString();
					md = time.get("ymd").toString().substring(4, 6) + "/" +time.get("ymd").toString().substring(6, 8);
					inoutType = "OUT";
					label =  md +" 퇴근하기";
					description = "출입 비콘 근처에서 버튼이 활성화됩니다";
					break;
				} else if (time.get("entrySdate") != null && time.get("entryEdate") != null) {
					inoutType = "NONE";
					label =  "근무계획없음";
				}
			}
			
			//외출복귀정보
			String gobackType = "GO";
			String gobackDesc = "-";
			List<Map<String, Object>> list2 = inoutHisMapper.getContext(paramMap);
			for(Map<String, Object> data : list2) {
				if(data.get("inoutTypeCd").equals("GO") || data.get("inoutTypeCd").equals("BACK")) {
					if(data.get("inoutTypeCd").equals("GO")) {
						gobackType = "BACK";
						gobackDesc = "외출 " + data.get("inoutDate").toString();
					}
					else {
						gobackType = "GO";
						gobackDesc = "복귀 " + data.get("inoutDate").toString();
					} 
				}
			}
			menuGoback.put("description", gobackDesc);
			menuGoback.put("actionType", "ACTIVE");
			menuGoback.put("label", gobackType.equals("GO")?"외출하기":"복귀하기");
			menuGoback.put("backgroundColor", gobackType.equals("GO")?"#93DaFF":"#FFF56E");
			
			menuInOut.put("label", label);
			menuInOut.put("description", description);
			menuInOut.put("inoutType", inoutType);
			
		}catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} 
		
		return returnMap;
	}
	
	@Override
	public void updateTimecardCancelWeb(Map<String, Object> paramMap) throws Exception {
		try {
		//일단 타각데이터는 저장하고 empHis랑 비교...
			int cnt = inoutHisMapper.updateWtmInoutHis(paramMap);
			if(cnt <= 0) {
				logger.debug("updateTimeStampFail : " + paramMap.toString());
				throw new Exception("타각데이터 저장에 실패하였습니다.");
			}
			logger.debug("updateTimeStampSuccess : " + paramMap.toString());
				
			WtmEmpHis emp = empRepository.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()), 
					paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
			if(emp == null) {
				logger.debug("사용자 정보 조회 중 오류가 발생하였습니다." + paramMap.toString());
				throw new Exception("사용자 정보 조회 중 오류가 발생하였습니다.");
			}

			wtmCalendarMapper.cancelEntryDateCalendar(paramMap);

			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
			List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()),
					paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("stdYmd").toString());

			//BASE, OT, NIGHT, FIXOT appr update
			if(results != null && results.size() > 0) {
				for(WtmWorkDayResult r : results) {
					if(r.getTimeTypeCd().equals("BASE") 
								|| r.getTimeTypeCd().equals("FIXOT") 
								|| r.getTimeTypeCd().equals("OT") 
								|| r.getTimeTypeCd().equals("NIGHT")) {
						logger.debug("퇴근타각, BASE, OT, NIGHT, FIXOT 인정시간 update " + r.toString());
						r.setApprSdate(null);
						r.setApprEdate(null);
						r.setApprMinute(null);
						wtmWorkDayResultRepo.save(r);
					}
				}
			}
		} catch(Exception e) {
			logger.debug("updateTimeStampFail : " +e.getMessage());
			throw new Exception("저장에 실패하였습니다.");
		}
 
//		if(cnt <= 0) {
//			throw new Exception("캘린더 정보 업데이트에 실패하였습니다.");
//		}
	}	
	
	@Override
	public Map<String, Object> getMenuContextWeb(Long tenantId, String enterCd, String sabun) {

		Map <String,Object> paramMap = new HashMap<String, Object>();
		Map <String,Object> returnMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);

		String ymd = null;
		Date entrySdate = null;
		Date entryEdate = null;
		Date exceptDate = null;
		String label = " - ";
		String inoutType = "NONE";
		String desc = "출근체크 필요시 인사팀에 문의 바랍니다";
		
		try {
			List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
			logger.debug("getMenuContextWeb inoutStatus : " + list.toString());
			
			SimpleDateFormat format1 = new SimpleDateFormat ("yyyyMMdd");
			SimpleDateFormat format2 = new SimpleDateFormat ("yyyy-MM-dd");
			SimpleDateFormat format3 = new SimpleDateFormat ("HH:mm");

			Date now = new Date();
			String today = format1.format(now);
			
			for(Map<String, Object> time : list) {
				if(!time.containsKey("pSymd") ||  !time.containsKey("pEymd"))
					continue;
				if((time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) && time.get("entrySdate") == null) {
					ymd = time.get("ymd").toString();
					if(time.get("holydayYn") != null && time.get("holydayYn").toString().equals("Y")) {
						inoutType = "HOL";
						label = " - ";
						desc = "휴일";
					} else {
						inoutType = "IN";
						desc = "근무일";
						label = "출근하기";
					}
					break;
				} else if((time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) && time.get("entryEdate") == null) {
					ymd = time.get("ymd").toString();
					inoutType = "OUT";
					entrySdate = (Date) time.get("entrySdate");
					desc = "근무중";
					label = "퇴근하기";
					break;
				} else if (time.get("entrySdate") != null && time.get("entryEdate") != null) {
					ymd = time.get("ymd").toString();
					inoutType = "END";
					desc = "근무종료";
					label = "퇴근취소";
					entrySdate = (Date) time.get("entrySdate");
					entryEdate = (Date) time.get("entryEdate");
				}
			}
			
			//외출복귀정보
			String gobackType = "GO";
			String gobackDesc = "-";
			List<Map<String, Object>> list2 = inoutHisMapper.getContext(paramMap);
			for(Map<String, Object> data : list2) {
				if(data.get("inoutTypeCd").equals("GO") || data.get("inoutTypeCd").equals("BACK")) {
					if(data.get("inoutTypeCd").equals("GO")) {
						gobackType = "BACK";
						gobackDesc = "외출 " + data.get("inoutDate").toString();
					}
					else {
						gobackType = "GO";
						gobackDesc = "복귀 " + data.get("inoutDate").toString();
					} 
					if(data.get("iDate") != null)
						exceptDate = (Date) data.get("iDate");
				}
			}
			
			returnMap.put("exceptDesc", gobackDesc);
			returnMap.put("exceptType", gobackType);
			
			returnMap.put("ymd", ymd);
			returnMap.put("label", label);
			returnMap.put("desc", desc);
			returnMap.put("inoutType", inoutType);
			returnMap.put("entrySymd", entrySdate==null?"":format2.format(entrySdate));
			returnMap.put("entryEymd", entryEdate==null?"":format2.format(entryEdate));
			returnMap.put("exceptYmd", exceptDate==null?"":format2.format(exceptDate));
			returnMap.put("entryStime", entrySdate==null?"":format3.format(entrySdate));
			returnMap.put("entryEtime", entryEdate==null?"":format3.format(entryEdate));
			returnMap.put("exceptTime", exceptDate==null?"":format3.format(exceptDate));
			
		}catch(Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		} 
		logger.debug("11111111111111111 " + returnMap.toString());
		return returnMap;
	}
	
	@Override
	public void updateTimecardCancel(Map<String, Object> paramMap, String unplanned) throws Exception {
		try {
			if(updateTimeStamp(paramMap)) {
				logger.debug("updateTimeStampSuccess : " + paramMap.toString());
			} else {
				logger.debug("updateTimeStampFail : " + paramMap.toString());
				throw new Exception("저장에 실패하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("updateTimeStampFail : " +e.getMessage());
			throw new Exception("저장에 실패하였습니다.");
		}
 
		int cnt = wtmCalendarMapper.cancelEntryDateCalendar(paramMap);
		
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()),
				paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("stdYmd").toString());

		//BASE, FIXOT 데이터만 삭제
		if(unplanned.equals("Y")) {
			if(results != null && results.size() > 0) {
				for(WtmWorkDayResult r : results) {
					if(r.getTimeTypeCd().equals("BASE") || r.getTimeTypeCd().equals("FIXOT")) {
						logger.debug("퇴근타각, BASE, FIXOT 삭제 " + r.toString());
						wtmWorkDayResultRepo.deleteById(r.getWorkDayResultId());
					}
				}
			}
		}
		
		//BASE, OT, NIGHT, FIXOT appr update
		if(results != null && results.size() > 0) {
			for(WtmWorkDayResult r : results) {
				if(r.getTimeTypeCd().equals("BASE") 
							|| r.getTimeTypeCd().equals("FIXOT") 
							|| r.getTimeTypeCd().equals("OT") 
							|| r.getTimeTypeCd().equals("NIGHT")) {
					logger.debug("퇴근타각, BASE, OT, NIGHT, FIXOT 인정시간 update " + r.toString());
					r.setApprSdate(null);
					r.setApprEdate(null);
					r.setApprMinute(null);
					wtmWorkDayResultRepo.save(r);
				}
			}
		}
		
//		if(cnt <= 0) {
//			throw new Exception("캘린더 정보 업데이트에 실패하였습니다.");
//		}
	}	

	@Override
	public void updateTimecard(Map<String, Object> paramMap) throws Exception {

		//캘린더에 초가 들어가면 안된...
		paramMap.put("inoutDateTime", paramMap.get("inoutDate").toString().substring(0,12)+"00");
		
		//1.무조건 타각 저장
		try {
			if(insertTimeStamp(paramMap)) {
				logger.debug("insertTimeStampSuccess : " + paramMap.toString());
			} else {
				logger.debug("insertTimeStampFail : " + paramMap.toString());
				throw new Exception("저장에 실패하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("insertTimeStampFail : " +e.getMessage());
			throw new Exception("저장에 실패하였습니다.");
		}
		
		//2.근무일과 타각상태 가져오기
		List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
		logger.debug("inoutStatus : " + list.toString());
		
		String today = paramMap.get("inoutDate").toString().substring(0, 8);
		String stdYmd = today;
		String inoutType = "NONE";
		String entrySdate = null;
		String entryEdate = null;
		
		for(int i = 0; i < list.size(); i++) {
			Map<String, Object> time = list.get(i);

			entrySdate = time.get("entrySdate")!=null?time.get("entrySdate").toString():null;
			entryEdate = time.get("entryEdate")!=null?time.get("entryEdate").toString():null;

			if(time.get("pSymd").toString().equals(today) && 
					time.get("holydayYn").toString().equals("Y") && time.get("pSdate") == null && time.get("pEdate") == null) {
				throw new Exception("휴일 근무계획이 없습니다.");
			}
			
			if(time.get("pSymd").equals(today) && time.get("entrySdate") != null && paramMap.get("inoutType").equals("IN")) {
				//출근이 있는데 한번 더
				logger.debug("출근 타각시간이 존재하므로 반영하지 않습니다." + paramMap.toString());
				return;
			}else if(time.get("pSymd").equals(today) && time.get("entrySdate") == null) {
				//정상출근
				stdYmd = time.get("ymd").toString();
				inoutType = "IN";
				break;
			}else if(time.get("pSymd").equals(today) && time.get("entrySdate") != null && time.get("entryEdate") == null && paramMap.get("inoutType").equals("OUT")) {
				//토글이라 가정하고, 퇴근이 중복해서 들어오지 않는다고 생각하자...
				//내일 퇴근인데 오늘 그냥 일찍 퇴근
				stdYmd = time.get("ymd").toString();
				inoutType = "OUT";
				break;
			} else if(time.get("pEymd").equals(today) && time.get("entrySdate") == null && paramMap.get("inoutType").equals("IN")) {
				//어제 출근인데 오늘 까먹고 오늘 출근 찍을때 
				stdYmd = time.get("ymd").toString();
				inoutType = "IN";
				break;
			} else if(time.get("pEymd").equals(today) && time.get("entrySdate") != null && paramMap.get("inoutType").equals("OUT")) {
//				if(paramMap.get("inoutType").equals("IN")) 
//					continue;
//				if(time.get("entrySdate") == null)
//					continue;
//				if(list.size() > i+1) { //야간조인데 전날 퇴근을 안찍고 오늘 출근을 찍은 경우, 다음 출근이 있는지 확인...
//					Map<String, Object> temp = list.get(i+1);
//					if(time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) {
//						continue;
//					}
//				}
				stdYmd = time.get("ymd").toString();
				inoutType = "OUT";
				break;
			} 
		}
		
		//3.출근타각이 있으면 반영안됨(삼화 인터페이스 두번 들어올 수 있음)
		if("IN".equals(paramMap.get("inoutType").toString()) && entrySdate !=null) {
			logger.debug("출근 타각시간이 존재하므로 반영하지 않습니다." + paramMap.toString());
			return;
		}
		
		String gobackType = "GO";
		//3.외출 복귀 마지막 상태 가져오기
		Map<String, Object> goback = inoutHisMapper.getGoBackStatus(paramMap);
		if(goback == null || "GO".equals(goback.get("inoutType").toString())) {
			gobackType = "GO";
		} else {
			gobackType = "BACK";
			paramMap.put("exceptSYmd", goback.get("exceptSYmd"));
		}

		//4.퇴근일때 복귀도 강제로 생성
		if("OUT".equals(paramMap.get("inoutType").toString()) && gobackType.equals("BACK")) {
			Map<String, Object> tempMap = new HashMap();
			tempMap.putAll(paramMap);
			tempMap.put("entryTypeCd", "API");
			tempMap.put("inoutType", gobackType);
			logger.debug("퇴근할때 강제 복귀 생성 " + paramMap.toString());
			updateTimecardExcept(tempMap, "N");
		}
		paramMap.put("stdYmd", stdYmd);
		//6.캘린더 업데이트
		int cnt = wtmCalendarMapper.updateEntryDateCalendar(paramMap);
		if(cnt <= 0) {
			throw new Exception("캘린더 정보 업데이트에 실패하였습니다.");
		}
	}
	
	@Override
	public void updateTimecardExcept(Map<String, Object> paramMap, String unplanned) throws Exception {

		//캘린더에 초가 들어가면 안된...
		paramMap.put("inoutDateTime", paramMap.get("inoutDate").toString().substring(0,12)+"00");
		
		//1. 근무일과 타각상태 가져오기
		List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
		logger.debug("inoutStatus : " + list.toString());
		
		String today = paramMap.get("inoutDate").toString().substring(0, 8);
		String stdYmd = today;
		String inoutType = "NONE";
		
		for(Map<String, Object> time : list) {
			if(unplanned.equals("N")) {
				if(time.get("pSymd").equals(today) || time.get("pEymd").equals(today)) {
					if(time.get("entrySdate") == null && time.get("entryEdate") == null) {
						continue;
					}else if(time.get("entrySdate") == null && time.get("entryEdate") == null) {
						stdYmd = time.get("ymd").toString();
						inoutType = "IN";
						break;
					} else if(time.get("entrySdate") != null &&time.get("entryEdate") == null) {
						stdYmd = time.get("ymd").toString();
						inoutType = "OUT";
						break;
					}
				}  
			} else {
				if(today.equals(time.get("ymd").toString())) {
					if(time.get("entrySdate") == null) {
						stdYmd = time.get("ymd").toString();
						inoutType = "IN";
						break;
					} else if(time.get("entryEdate") == null) {
						stdYmd = time.get("ymd").toString();
						inoutType = "OUT";
						break;
					} 
				}
			}
		}
		
		String gobackType = "GO";
		//외출 복귀 마지막 상태 가져오기
		Map<String, Object> goback = inoutHisMapper.getGoBackStatus(paramMap);
		if(goback == null || "GO".equals(goback.get("inoutType").toString())) {
			gobackType = "GO";
		} else {
			gobackType = "BACK";
			paramMap.put("exceptSYmd", goback.get("exceptSYmd"));
		}

		if(inoutType.equals("IN")) {
			throw new Exception("출근 전에는 외출/복귀 메뉴를 사용할 수 없습니다.");
		} else if(inoutType.equals("NONE")) {
			throw new Exception("퇴근 후에는 외출/복귀 메뉴를 사용할 수 없습니다.");
		}
		paramMap.put("inoutType", gobackType);
		
		//2.일단 타각 데이터만 저장
		try {
			if(insertTimeStamp(paramMap)) {
				logger.debug("insertTimeStampSuccess : " + paramMap.toString());
			} else {
				logger.debug("insertTimeStampFail : " + paramMap.toString());
				throw new Exception("저장에 실패하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("insertTimeStampFail : " +e.getMessage());
			throw new Exception("저장에 실패하였습니다.");
		}
		
		paramMap.put("ymd", stdYmd);
		//3.복귀인 경우 day result 생성
		if(gobackType.equals("BACK")) {
			// 외출복귀 시간을 조회한다.
			try {
				SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");

				WtmWorkDayResult except = new WtmWorkDayResult();
				except.setTenantId(Long.parseLong(paramMap.get("tenantId").toString()));
				except.setEnterCd(paramMap.get("enterCd").toString());
				except.setSabun(paramMap.get("sabun").toString());
				except.setYmd(paramMap.get("ymd").toString());
				except.setTimeTypeCd("GOBACK");
				except.setEnterCd(paramMap.get("enterCd").toString());
				except.setPlanSdate(dt.parse(paramMap.get("exceptSYmd").toString()));
				except.setPlanEdate(dt.parse(paramMap.get("inoutDateTime").toString()));
				except.setApprSdate(dt.parse(paramMap.get("exceptSYmd").toString()));
				except.setApprEdate(dt.parse(paramMap.get("inoutDateTime").toString()));
				except.setUpdateId(paramMap.get("sabun").toString());

				wtmWorkDayResultRepo.save(except);
			
			
			// 복귀일때 근무시간 짜르기가 필요함.	
			// 외출복귀 시간을 조회한다.
				Map <String,Object> exceptMap = new HashMap<String, Object>();					
				SimpleDateFormat dt2 = new SimpleDateFormat("yyyyMMddHHmmss");
				empService.addWtmDayResultInBaseTimeType(	
						Long.parseLong(paramMap.get("tenantId").toString())
						, paramMap.get("enterCd").toString()		
						, paramMap.get("ymd").toString()
						, paramMap.get("sabun").toString()
						, "GOBACK"
						, ""
						, dt2.parse(paramMap.get("exceptSYmd").toString())
						, dt2.parse(paramMap.get("inoutDateTime").toString())
						, null
						, "0"
						, false);
			} catch(Exception e) {
				logger.debug(e.getMessage());
				throw new Exception("외출복귀 기록 중 오류가 발생했습니다.");
			}
		}
	}
	
	@Override
	public void updateTimecardUnplanned(Map<String, Object> paramMap) throws Exception {

		//캘린더에 초가 들어가면 안된...
		paramMap.put("inoutDateTime", paramMap.get("inoutDate").toString().substring(0,12)+"00");
		
		//1. 무조건 타각 저장
		try {
			if(insertTimeStamp(paramMap)) {
				logger.debug("insertTimeStampSuccess : " + paramMap.toString());
			} else {
				logger.debug("insertTimeStampFail : " + paramMap.toString());
				throw new Exception("저장에 실패하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("insertTimeStampFail : " +e.getMessage());
			throw new Exception("저장에 실패하였습니다.");
		}
		
		//2. 근무일과 타각상태 가져오기
		List<Map<String, Object>> list = inoutHisMapper.getInoutStatus(paramMap);
		logger.debug("inoutStatus : " + list.toString());

		String today = paramMap.get("inoutDate").toString().substring(0, 8);
		String stdYmd = today;
		String inoutType = "NONE";
		String entrySdate = null;
		String entryEdate = null;
		
		for(Map<String, Object> time : list) {
			if(today.equals(time.get("ymd").toString())) {
				entrySdate = time.get("entrySdate")!=null?time.get("entrySdate").toString():null;
				entryEdate = time.get("entryEdate")!=null?time.get("entryEdate").toString():null;
				
				if(time.get("entrySdate") == null) {
					stdYmd = time.get("ymd").toString();
					inoutType = "IN";
					break;
				} else if(time.get("entryEdate") == null) {
					stdYmd = time.get("ymd").toString();
					inoutType = "OUT";
					break;
				} 
			}
		}
		
		//3.출근타각이 있으면 반영안됨 (브로제)
		if("IN".equals(paramMap.get("inoutType").toString()) && entrySdate !=null) {
			throw new Exception("출근 타각시간이 존재하므로 반영하지 않습니다.");
		}
		
		String gobackType = "GO";
		//4.외출 복귀 마지막 상태 가져오기(5번에서 사용)
		Map<String, Object> goback = inoutHisMapper.getGoBackStatus(paramMap);
		if(goback == null || "GO".equals(goback.get("inoutType").toString())) {
			gobackType = "GO";
		} else {
			gobackType = "BACK";
		}
		
		//5.퇴근일때 복귀도 강제로 생성
		if("OUT".equals(paramMap.get("inoutType").toString()) && gobackType.equals("BACK")) {
			Map<String, Object> tempMap = new HashMap();
			tempMap.putAll(paramMap);
			tempMap.put("entryTypeCd", "API");
			tempMap.put("inoutType", gobackType);
			logger.debug("퇴근할때 강제 복귀 생성 " + paramMap.toString());
			updateTimecardExcept(tempMap, "Y");
		}
		paramMap.put("stdYmd", stdYmd);

		//6.캘린더 업데이트
		int cnt = wtmCalendarMapper.updateEntryDateCalendar(paramMap);
		if(cnt <= 0) {
			throw new Exception("캘린더 정보 업데이트에 실패하였습니다.");
		}
	}

	@Transactional
	@Async("threadPoolTaskExecutor")
	public void inoutPostProcess(Map<String, Object> paramMap, String unplanned) {
		try {
			logger.debug("inoutPostProcess1");
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
			List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()),
					paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("stdYmd").toString());
			
			List<WtmWorkDayResult> gobacks = new ArrayList();
//			delete 공통
//			있으면 crete_n, 자르기,
//			공통 calc, term
			
			//BASE, FIXOT 데이터만 삭제
			logger.debug("inoutPostProcess2 " + unplanned);

			if(unplanned.equals("Y")) {
//				SqlSession sqlSession = sqlSessionFactory.openSession();
				if(results != null && results.size() > 0) {
					for(WtmWorkDayResult r : results) {
//						if(r.getTimeTypeCd().equals("BASE") || r.getTimeTypeCd().equals("FIXOT") || r.getTimeTypeCd().equals("EXCEPT")) {
							logger.debug("퇴근타각, BASE, FIXOT 삭제 " + r.toString());

							flexEmpMapper.deleteResult(paramMap);
							
							logger.debug("inoutPostProcess3 delete " + r.toString());
//						} else 
						if (r.getTimeTypeCd().equals("GOBACK")) {
							gobacks.add(r);
						}
					}
				}
				
				if(gobacks != null && gobacks.size() > 0) {

					logger.debug("inoutPostProcess4 외출복귀몇개 " + gobacks.size());

					List<WtmFlexibleEmp> emps = flexEmpRepo.findAllTypeFixotByTenantIdAndEnterCdAndSabunAndSymdAndEymdAnd(
							Long.parseLong(paramMap.get("tenantId").toString()), 
							paramMap.get("enterCd").toString(), 
							paramMap.get("sabun").toString(), 
							paramMap.get("stdYmd").toString(), 
							paramMap.get("stdYmd").toString());
					
					if(emps == null || emps.size() == 0) {
						throw new Exception("WtmFlexibleEmp 없음. 계산불가");
					}
					
					logger.debug("inoutPostProcess5 CREATE_N 시작 " + emps.get(0).getFlexibleEmpId());

					paramMap.put("flexibleEmpId", emps.get(0).getFlexibleEmpId());
					paramMap.put("sYmd", paramMap.get("stdYmd").toString());
					paramMap.put("eYmd", paramMap.get("stdYmd").toString());
					paramMap.put("userId", paramMap.get("sabun").toString());
					flexEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(paramMap);
					
					logger.debug("inoutPostProcess6 CREATE_N 끗 " + paramMap.toString());

					for(WtmWorkDayResult except : gobacks) {
						empService.addWtmDayResultInBaseTimeType(except.getTenantId(),
								except.getEnterCd(), 
								except.getYmd(),
								except.getSabun(),
								except.getTimeTypeCd(),
								"",
								except.getApprSdate(),
								except.getApprEdate(),
								null,
								"0",
								false);

						logger.debug("inoutPostProcess6 외출복귀나누기 " + except.toString());
					}

//					flexEmpMapper.updateResultAppr(paramMap);
				
					logger.debug("inoutPostProcess6 result에 appr비우기" + paramMap.toString());
//					List<WtmWorkDayResult> results2 = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()),
//							paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("stdYmd").toString());
//	
//					int cnt = 0;
//					for(WtmWorkDayResult r : results2) {
//						if(r.getTimeTypeCd().equals("GOBACK")) 
//							continue;
//						r.setApprSdate(null);
//						r.setApprEdate(null);
//						r.setApprMinute(null);
//						wtmWorkDayResultRepo.save(r);
//						cnt++;
//					}
//					logger.debug("inoutPostProcess6 result에 appr비우기 끗 " + cnt);
				}
//				sqlSession.commit();
				//TransactionSynchronization.
			}

			
//			transactionManager.commit(status);
		
			logger.debug("inoutPostProcess7 calc 시작 " + paramMap.get("sabun").toString());
			empService.calcApprDayInfo(Long.parseLong(paramMap.get("tenantId").toString()), 
					paramMap.get("enterCd").toString(), paramMap.get("stdYmd").toString(),
					paramMap.get("stdYmd").toString(), paramMap.get("sabun").toString());
			
			logger.debug("inoutPostProcess8 calc 종료 " + paramMap.get("sabun").toString());
			
			Map<String, Object> tempTimeMap = new HashMap();
			tempTimeMap.put("tenantId", Long.parseLong(paramMap.get("tenantId").toString()));
			tempTimeMap.put("enterCd", paramMap.get("enterCd").toString());
			tempTimeMap.put("sabun", paramMap.get("sabun").toString());
			tempTimeMap.put("symd", paramMap.get("stdYmd").toString());
			tempTimeMap.put("eymd", paramMap.get("stdYmd").toString());
			tempTimeMap.put("pId", paramMap.get("sabun").toString());

			wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(tempTimeMap);
			logger.debug("inoutPostProcess9 워크텀 끗 " + tempTimeMap.toString());

		} catch(Exception e) {
			logger.debug("****인정시간 계산 중 오류가 발생했습니다. " + paramMap.toString() + ", " + e.getMessage());
		}
	}

	@Override
	public boolean updateTimeStamp(Map<String, Object> paramMap) throws Exception {
		try {
			//일단 타각데이터는 저장하고 empHis랑 비교...
			int cnt = inoutHisMapper.updateWtmInoutHis(paramMap);
			if(cnt <= 0) {
				throw new Exception("타각데이터 저장에 실패하였습니다.");
			}
			
			WtmEmpHis emp = empRepository.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()), 
					paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
			if(emp == null) {
				throw new Exception("사용자 정보 조회 중 오류가 발생하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("insertexception " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		return true;
	}

	@Override
	public boolean insertTimeStamp(Map<String, Object> paramMap) throws Exception {
		try {
			//일단 타각데이터는 저장하고 empHis랑 비교...
			int cnt = inoutHisMapper.saveWtmInoutHis(paramMap);
			if(cnt <= 0) {
				throw new Exception("타각데이터 저장에 실패하였습니다.");
			}
			
			WtmEmpHis emp = empRepository.findByTenantIdAndEnterCdAndSabunAndYmd(Long.parseLong(paramMap.get("tenantId").toString()), 
					paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
			if(emp == null) {
				throw new Exception("사용자 정보 조회 중 오류가 발생하였습니다.");
			}
		} catch(Exception e) {
			logger.debug("insertexception " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		return true;
	}

	@Override
	public Map<String, Object> getMyInoutDetail(Map<String, Object> paramMap) throws Exception {
		
		return inoutHisMapper.getMyInoutDetail(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getMyInoutList(Map<String, Object> paramMap) throws Exception {
	
		return inoutHisMapper.getMyInoutList(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getTeamInoutList(Map<String, Object> paramMap) throws Exception {
	
		//겸직 하위 조직 조회
		paramMap.put("orgList", empService.getLowLevelOrgList(Long.parseLong(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("ymd").toString()));

		return inoutHisMapper.getTeamInoutList(paramMap);
	}
		
	@Override
	public List<Map<String, Object>> getMyInoutHistory(Map<String, Object> paramMap) throws Exception {
	
		return inoutHisMapper.getMyInoutHistory(paramMap);
	}
	
	@Override
	public ReturnParam cancel(Map<String, Object> paramMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("준비중입니다.");
		
		return rp;
	}

	@Override
	public ReturnParam updateGoBack(Map<String, Object> paramMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
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