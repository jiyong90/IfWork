package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmEntryApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmValidatorMapper;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service
public class WtmValidatorServiceImpl implements WtmValidatorService  {

	@Autowired
	WtmFlexibleStdMapper flexStdMapper;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmValidatorMapper validatorMapper;
	
	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;

	@Autowired
	WtmTaaCodeRepository taaCodeRepo;
	
	@Autowired
	WtmEntryApplMapper entryApplMapper;
	
	@Autowired
	WtmPropertieRepository propertieRepo;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmApplRepository applRepo;
	
	@Override
	public ReturnParam validTaa(Long tenantId, String enterCd, String sabun,
			String timeTypeCd, String taaCd,
			String symd, String shm, String eymd, String ehm, Long applId, String locale) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		paramMap.put("applId", applId);
		paramMap.put("sabun", sabun); 
		if(taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_H)) {
			//근태가 시간단위 일때 시분 정보가 누락되지 않았는지 체크한다.
			if(shm.equals("") || ehm.equals("")) {
				rp.setFail("시간단위 근태의 경우 시간 정보가 필요합니다.");
				return rp;
			}
			paramMap.put("timeTypeCd", timeTypeCd);
			paramMap.put("requestTypeCd", taaCode.getRequestTypeCd());
			paramMap.put("sdate", symd + shm);
			paramMap.put("edate", eymd + ehm);
			Map<String, Object> m = validatorMapper.checkDuplicateTaaByTaaTypeH(paramMap);
			int cnt = Integer.parseInt(m.get("cnt").toString());
			
			if(cnt > 0) {
				rp.setFail("이미 해당일에 중복된 근태 정보가 존재합니다.");
				return rp;
			}
			
			
		}else {
			//신청하려는근태 코드와 타입 종일 반일(오전/오후) 등 해당 건은 같은날 중복해서 들어갈 수 없다 체케럽
			paramMap.put("timeTypeCd", timeTypeCd);
			paramMap.put("requestTypeCd", taaCode.getRequestTypeCd());
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			Map<String, Object> m = validatorMapper.checkDuplicateTaa(paramMap);
			int cnt = Integer.parseInt(m.get("cnt").toString());
			
			if(cnt > 0) {
				rp.setFail("이미 해당일에 중복된 근태 정보가 존재합니다.");
				return rp;
			}
		} 
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		paramMap.put("sabun", sabun); 
		int workCnt = validatorMapper.getWorkCnt(paramMap);
		
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("workdayCnt", workCnt);
		rp.put("result", resultMap);
		
		return rp;
	}	
	
	
	@Override
	public ReturnParam checkDuplicateFlexibleWork(Long tenantId, String enterCd, String sabun, String symd, String eymd, Long applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		paramMap.put("applId", applId);
		
		Map<String, Object> m = flexStdMapper.checkRequestDate(paramMap);
		int cnt = Integer.parseInt(m.get("cnt").toString());
		if(cnt > 0) {
			rp.setFail("신청중인 또는 이미 적용된 근무정보가 있습니다.");
			return rp;
		}
		return rp;
	}

	@Override
	public ReturnParam checkDuplicateWorktime(Long tenantId, String enterCd, String sabun, String sdate, String edate, Long applId){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sdate", sdate);
		paramMap.put("edate", edate);
		paramMap.put("applId", applId);

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> m = flexEmpMapper.checkDuplicateWorktime(paramMap);
		int cnt = Integer.parseInt(m.get("workCnt").toString());
		if(cnt > 0) {
			rp.setFail("신청중인 또는 이미 적용된 근무정보가 있습니다.");
			return rp;
		}
		return rp;
		 
	}
	
	@Override
	public ReturnParam checkDuplicateEntryAppl(Long tenantId, String enterCd, String sabun, String ymd, Long applId){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		paramMap.put("applId", applId);

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> m = entryApplMapper.checkDuplicateEntryAppl(paramMap);
		int cnt = Integer.parseInt(m.get("chgCnt").toString());
		if(cnt > 0) {
			rp.setFail("이미 신청중인 근태사유서가 존재합니다.");
			return rp;
		}
		
		return rp;
	}
	
	@Override
	public ReturnParam worktimeValid(Long tenantId, String enterCd, String applNo, List<Map<String, Object>> works, String applSabun) {
		ReturnParam rp = new ReturnParam();
		
		if(works!=null && works.size()>0) {
			for(Map<String, Object> w : works) {
				
				if(w.get("sabun")!=null && !"".equals(w.get("sabun")) && w.get("worksDet")!=null && !"".equals(w.get("worksDet"))) {
					String sabun = w.get("sabun").toString();
					List<Map<String, Object>> worksDet = (List<Map<String, Object>>)w.get("worksDet");
					
					if(worksDet!=null && worksDet.size()>0) {
						for(Map<String, Object> work : worksDet) {
							if(work.get("startYmd")==null || "".equals(work.get("startYmd")) ||work.get("endYmd")==null || "".equals(work.get("endYmd"))) {
								rp.setFail(sabun+"의 일자 정보가 없습니다.");
								return rp;
							}
							
							if(work.get("workTimeCode")==null || "".equals(work.get("workTimeCode"))) {
								rp.setFail(sabun+"의 근태 코드 정보가 없습니다.");
								return rp;
							}
							
							String workTimeCode = work.get("workTimeCode").toString();
							
							String symd = work.get("startYmd").toString();
							String eymd = work.get("endYmd").toString();
							
							if(symd.length() != eymd.length()) {
								rp.setFail("시작일과 종료일의 날짜 포맷이 맞지 않습니다.");
								return rp;
							}
							
							if(symd.length()!=8) {
								rp.setFail("일자는 8자리 입니다.");
								return rp;
							} 
							
							Date sd = WtmUtil.toDate(symd, "yyyyMMdd");
							Date ed = WtmUtil.toDate(eymd, "yyyyMMdd");
							
							if(sd.compareTo(ed) > 0) {
								rp.setFail("시작일자가 종료일자보다 큽니다.");
								return rp;
							}
							
							String shm = "";
							String ehm = "";
							if(work.get("startHm")!=null && !"".equals(work.get("startHm"))) 
								shm = work.get("startHm").toString();
							if(work.get("endHm")!=null && !"".equals(work.get("endHm"))) 
								ehm = work.get("endHm").toString();
							
							/*
							Long applId = null;
							if(applNo!=null && !"".equals(applNo)) {
								System.out.println("=== applNo ===> "+applNo);
								WtmAppl wtmAppl = applRepo.findByIfApplNo(Long.valueOf(applNo));
								System.out.println("=== wtmAppl ===> "+wtmAppl);	
								if(wtmAppl!=null)
									applId = wtmAppl.getApplId();
							}
							*/
							
							String applId = applNo; 
							
							//신청서 중복 체크
							rp = checkDuplicateTaaAppl(tenantId, enterCd, sabun, workTimeCode, symd, eymd, shm, ehm, applId);
							
							if(rp.getStatus()!=null && !"OK".equals(rp.getStatus()))
								return rp;
							
						}
						
						//근무시간 체크
						return checkWorktimeTaaAppl(tenantId, enterCd, sabun, worksDet);
						
					}
				}
				
			}
		}
	
		return rp;
	}
	
	protected ReturnParam checkDuplicateTaaAppl(Long tenantId, String enterCd, String sabun, String workTimeCode, String symd, String eymd, String shm, String ehm, String applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("taaCd", workTimeCode);
		paramMap.put("applId", applId);
		
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		
		paramMap.put("shm", shm);
		paramMap.put("ehm", ehm);
		
		Map<String, Object> m = validatorMapper.checkDuplicateTaaAppl(paramMap);
		if(m!=null && m.get("cnt")!=null) {
			int cnt = Integer.parseInt(m.get("cnt").toString());
			if(cnt > 0) {
				rp.setFail(m.get("empNm").toString()+"("+sabun+") " + WtmUtil.parseDateStr(WtmUtil.toDate(symd,"yyyyMMdd"), "yyyy-MM-dd") + "~" +  WtmUtil.parseDateStr(WtmUtil.toDate(eymd,"yyyyMMdd"), "yyyy-MM-dd") +"의 신청중인 또는 이미 적용된 근무정보가 있습니다.");
				return rp;
			}
		}
		
		return rp;
	}
	
	protected ReturnParam checkWorktimeTaaAppl(Long tenantId, String enterCd, String sabun, List<Map<String, Object>> appl) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		String sDate = null;
		String eDate = null;
		int i = 0;
		for(Map<String, Object> a : appl) {
			String symd = a.get("startYmd").toString();
			String eymd = a.get("endYmd").toString();
			
			if(i==0) {
				sDate = symd;
				eDate = eymd;
			} else {
				if(symd.compareTo(sDate) == -1)
					sDate = symd;
				if(eymd.compareTo(eDate) == 1)
					eDate = eymd;
			}
			i++;
		}
		
		Map<String, Object> paramMap = null;
		List<Map<String, Object>> applMinutes = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> a : appl) {
			String workTimeCode = a.get("workTimeCode").toString();
			String symd = a.get("startYmd").toString();
			String eymd = a.get("endYmd").toString();
			String shm = a.get("startHm").toString();
			String ehm = a.get("endHm").toString();
			
			WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, workTimeCode);
			if(taaCode==null) {
				rp.setFail("근태 코드 정보가 없습니다.");
				return rp;
			}
			
			int applMinute = 0;
			
			if((shm==null || "".equals(shm)) && (ehm==null || "".equals(ehm))) {
				//간주근무시간
				if(taaCode.getWorkApprHour()!=null && !"".equals(taaCode.getWorkApprHour())) {
					applMinute = taaCode.getWorkApprHour() * 60;
				} else {
					//일 기본근무시간
					applMinute = 8 * 60;
					WtmPropertie propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_DEFAULT_WORKTIME");
					if(propertie!=null && propertie.getInfoValue()!=null && !"".equals(propertie.getInfoValue()) && !"0".equals(propertie.getInfoValue())) {
						applMinute = Integer.parseInt(propertie.getInfoValue()) * 60;
					}
				}
			} else {
				
				//시간이 들어오는 경우는 symd == eymd 
				
				Date sd = WtmUtil.toDate(symd + shm, "yyyyMMddHHmm");
				Date ed = WtmUtil.toDate(symd + ehm, "yyyyMMddHHmm");
				
				applMinute = (int)(ed.getTime() - sd.getTime()) / (60*1000);
			}
			
			System.out.println("applMinute : " + applMinute);
			
			paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			
			paramMap.put("applMinute", applMinute);
			
			Date s = WtmUtil.toDate(symd, "yyyyMMdd");
			Date e = WtmUtil.toDate(eymd, "yyyyMMdd");
			
			do {
				String ymd = WtmUtil.parseDateStr(s, "yyyyMMdd");
				paramMap.put("ymd", ymd);
				
				WtmWorkCalendar workCalendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
				if("Y".equals(taaCode.getHolInclYn()) || ("N".equals(taaCode.getHolInclYn()) && "N".equals(workCalendar.getHolidayYn()) )) {
					
					//1.근무 계획 시간보다 근태 신청 시간이 더 큰지 체크
					Map<String, Object> m = validatorMapper.checkApplMinute(paramMap);
					
					if(m==null) {
						rp.setFail( WtmUtil.parseDateStr(s, "yyyy-MM-dd") +"의 근무 시간 정보가 존재하지 않습니다.");
						return rp;
					} 
					
					if("N".equals(m.get("isValid").toString())) {
						rp.setFail(m.get("empNm").toString()+"("+sabun+") "+ WtmUtil.parseDateStr(s, "yyyy-MM-dd") +"의 신청 시간이 근무 시간을 초과할 수 없습니다.");
						return rp;
					}
					
					Map<String, Object> applMinMap = new HashMap<String, Object>();
					applMinMap.put("ymd", ymd);
					applMinMap.put("applMinute", applMinute);
					applMinutes.add(applMinMap);
					
					// 날짜 더하기
			        Calendar cal = Calendar.getInstance();
			        cal.setTime(s);
			        cal.add(Calendar.DATE, 1);
			        s = cal.getTime();
		        
				}
				
			} while(s.compareTo(e) == 0);
			
		}
		
		//2.선근제의 경우에는 해당 선근제 근무 기간 내의 소정근로시간을 넘지 않는지 체크
		paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", sDate);
		paramMap.put("eymd", eDate);
		paramMap.put("applMinutes", applMinutes);
		List<Map<String, Object>> results = validatorMapper.checkTotalWorkMinuteForSele(paramMap);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("results : " + mapper.writeValueAsString(results));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(results!=null && results.size()>0) {
			for(Map<String, Object> r : results) {
				if(r.get("isValid")!=null && "N".equals(r.get("isValid"))) {
					System.out.println("workMinute: " + r.get("workMinute").toString());
					System.out.println("totalWorkMinute: " + r.get("totalWorkMinute").toString());
					
					Double h = 0d;
					Double m = 0d;
					
					h = Double.parseDouble(r.get("workMinute").toString())/60;
					h = Math.ceil(h*100)/100.0;
					m = (h - h.intValue()) * 60;
					
					rp.setFail("선택근무제 총 근무 시간("+((h.intValue()>0)?String.format("%02d", h.intValue()):"00")+"시간"+((m.intValue()>0)?String.format("%02d", m.intValue())+"분":"")+")을 초과할 수 없습니다.");
					return rp;
				}
			}
			
		}
		
		return rp;
	}
	
}
