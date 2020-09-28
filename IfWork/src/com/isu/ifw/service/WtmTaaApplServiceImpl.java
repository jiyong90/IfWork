package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmTaaAppl;
import com.isu.ifw.entity.WtmTaaApplDet;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmTaaApplDetRepository;
import com.isu.ifw.repository.WtmTaaApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.vo.ReturnParam;

@Service("WtmTaaApplService")
public class WtmTaaApplServiceImpl implements WtmApplService{

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired WtmApplRepository wtmApplRepo;
	
	@Autowired
	private WtmTaaApplRepository wtmTaaApplRepo;
	
	@Autowired
	private WtmTaaCodeRepository wtmTaaCodeRepo;
	
	@Autowired
	private WtmWorkDayResultRepository dayResultRepo;
	
	@Autowired
	private WtmTaaApplDetRepository wtmTaaApplDetRepo;
	
	@Autowired private WtmWorkCalendarRepository workCalendarRepo;
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun,
			Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap,
			String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo,
			Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd,
			Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun,
			String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {
		
		String applSabun = paramMap.get("applSabun")+ "";
		List<Map<String, Object>> works = (List<Map<String, Object>>) paramMap.get("work");
		
		WtmAppl appl = null;
		List<WtmTaaAppl> taaAppls = wtmTaaApplRepo.findByApplId(applId);
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		//기신청 데이터 
		if(taaAppls == null || taaAppls.size() == 0) {
			//logger.debug("works.size() : " + works.size());
			//if(works != null && works.size() > 0) {
				//신청 또는 승인 완료 건에 대해서만
				if(WtmApplService.APPL_STATUS_APPLY_ING.equals(status) || WtmApplService.APPL_STATUS_APPR.equals(status) || WtmApplService.APPL_STATUS_CANCEL.equals(status)) {
					//appl = wtmApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
					appl = wtmApplRepo.findById(applId).get();
					if(appl == null) {
						appl = new WtmAppl();
						appl.setTenantId(tenantId);
						appl.setEnterCd(enterCd);
						appl.setIfApplNo(null);
						appl.setApplYmd(ymd.format(new Date()));
					} else {
						//있으면 문제다. 데이터 동기화 작업이 필요. 99일 경우
						//preApplStatus = appl.getApplStatusCd();
					}
					appl.setApplCd(WtmApplService.TIME_TYPE_TAA);
					appl.setApplSabun(applSabun);
					appl.setApplInSabun(applSabun);
					appl.setApplStatusCd(status);
					appl.setUpdateId("TAA_INTF");
					
					appl = wtmApplRepo.save(appl);
					
					 
					//for(Map<String, Object> w : works) {
						//String sabun = w.get("sabun")+"";
						
						//if(w.containsKey("worksDet") && w.get("worksDet") != null && !"".equals(w.get("worksDet")+"")) {
							
							WtmTaaAppl taaAppl = new WtmTaaAppl();
							taaAppl.setTenantId(tenantId);
							taaAppl.setEnterCd(enterCd);
							taaAppl.setApplId(appl.getApplId());
							taaAppl.setSabun(sabun);
							taaAppl.setIfApplNo(null);
							taaAppl.setUpdateId(userId);
							
							taaAppl = wtmTaaApplRepo.save(taaAppl);
							
							//List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
							//for(Map<String, Object> work : worksDet) {
							
								if(paramMap.containsKey("workTimeCode") && paramMap.containsKey("startYmd") && paramMap.containsKey("endYmd")
										&& paramMap.get("workTimeCode") != null && !"".equals(paramMap.get("workTimeCode"))
										&& paramMap.get("startYmd") != null && !"".equals(paramMap.get("startYmd"))
										&& paramMap.get("endYmd") != null && !"".equals(paramMap.get("endYmd"))
										) {
									String taaCd = paramMap.get("workTimeCode").toString();
									String symd = paramMap.get("startYmd").toString();
									String eymd = paramMap.get("endYmd").toString();
									String shm = "";
									if(paramMap.containsKey("startHm") && paramMap.get("startHm") != null && !"".equals(paramMap.get("startHm"))) {
										shm = paramMap.get("startHm").toString();
									}
									String ehm = "";
									if(paramMap.containsKey("endHm") && paramMap.get("endHm") != null && !"".equals(paramMap.get("endHm"))) {
										ehm = paramMap.get("endHm").toString();
									}
									
									WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);
									if(taaCode == null || taaCode.getRequestTypeCd() == null || taaCode.getRequestTypeCd().equals("N")){
										throw new RuntimeException("근태신청이 불가능한 근태코드입니다. ");
									}
									
									logger.debug("마감여부 체크 : ");
									List<WtmWorkCalendar> calendars = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, symd, eymd);
									if(calendars != null && calendars.size() > 0) {
										for(WtmWorkCalendar c : calendars) {
											if("Y".equals(c.getWorkCloseYn())) {
												throw new RuntimeException("신청 기간 내 마감된 근무일이 존재합니다.");
											}
										}
									}else {
										throw new RuntimeException("캘린더 정보가 없습니다.");
									}
									
									if(Integer.parseInt(symd) > Integer.parseInt(eymd)) {
										throw new RuntimeException("시작일자가 종료일보다 클 수 없습니다.");
									}
										
									
									WtmTaaApplDet taaApplDet = new WtmTaaApplDet();
									taaApplDet.setTaaApplId(taaAppl.getTaaApplId());
									taaApplDet.setTaaCd(taaCd);
									taaApplDet.setSymd(symd);
									taaApplDet.setEymd(eymd);
									taaApplDet.setShm(shm);
									taaApplDet.setEhm(ehm); 
									taaApplDet.setUpdateId("TAA_INTF");
									
									wtmTaaApplDetRepo.save(taaApplDet);
									
								}else {
									throw new RuntimeException("근태정보가 부족합니다.");
								} 
							//}
						//}else {
						//	throw new RuntimeException(sabun + " 님의 근무 상세정보가 없습니다.");
						//}
					//}
					
				}
			//}
		}else {
			//
		}
		return null;
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
