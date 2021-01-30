package com.isu.ifw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.*;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("wtmOtApplService")
public class WtmOtApplServiceImpl implements WtmApplService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	private WtmValidatorService validatorService;
	
	@Autowired
	private WtmApplAfterService wtmApplAfterService;
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	/**
	 * 속성값 조회
	 */
	@Autowired
	WtmPropertieRepository wtmPropertieRepo;
	@Autowired
	WtmEmpHisRepository wtmEmpHisRepo; 
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;
	@Autowired
	WtmOtApplRepository wtmOtApplRepo;
	@Autowired
	WtmOtSubsApplRepository wtmOtSubsApplRepo;
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
	@Autowired
	WtmWorkCalendarRepository wtmWorkCalendarRepository;
	@Autowired
	WtmFlexibleStdMgrRepository wtmFlexibleStdMgrRepo;
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	
	@Autowired
	WtmRuleRepository wtmRuleRepo;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;
	
	@Autowired
	WtmFlexibleApplRepository wtmFlexibleApplRepo;

	@Autowired
	WtmFlexibleStdMgrRepository  flexStdMgrRepo;
	
	@Autowired
	WtmFlexibleApplMapper flexApplMapper;
	
	@Autowired
	WtmFlexibleStdMapper wtmFlexibleStdMapper;
	
	@Autowired
	WtmOtCanApplMapper wtmOtCanApplMapper;
	
	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmInboxService inbox;
	
	@Autowired
	WtmTimeCdMgrRepository wtmTimeCdMgrRepo;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("applId", applId);
			paramMap.put("sabun", sabun);
			List<Map<String, Object>> otApplList = wtmOtApplMapper.otApplfindByApplId(paramMap);

			/*
			ObjectMapper mapper = new ObjectMapper();
			
			System.out.println("otApplList : " + mapper.writeValueAsString(otApplList));
			*/
			logger.debug("###otApplList : " + otApplList.size());
			Map<String, Object> otAppl = null;
			if(otApplList!=null && otApplList.size()>0) {
				
				// otApplList가 1건 이상이면(연장근무신청 관리자 화면에서 신청한 경우) 대상자 리스트를 보여줌
				List<String> sabuns = new ArrayList<String>();
				
				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
				
				if(otAppl==null)
					otAppl = otApplList.get(0);
				
				for(Map<String, Object> o : otApplList) {
					//연장근무신청 관리자 화면에서 신청 시 작성한 내용은 똑같으므로 한 명의 연장근무 신청서만 가져옴
					if(otApplList.size()==1 || sabun.equals(o.get("sabun").toString())) {
						otAppl = o;
						ymd = o.get("ymd").toString();
					}
					sabuns.add(o.get("sabun").toString());
				}
				
				//대상자
				if(sabuns.size()>0) {
					Map<String, Object> targetList = new HashMap<String, Object>();
					Map<String, Object> rMap = new HashMap<String, Object>();
					rMap.put("tenantId", tenantId);
					rMap.put("enterCd", enterCd);
					rMap.put("ymd", ymd);
					rMap.put("sabuns", sabuns);
					List<Map<String, Object>> emps = wtmOtApplMapper.getRestOtMinute(rMap);
					
					if(emps!=null && emps.size()>0) {
						for(Map<String, Object> emp : emps) {
							targetList.put(emp.get("sabun").toString(), emp);
						}
					}
					otAppl.put("targetList", targetList);
				}
				
				//대체휴일
				if(otAppl.get("holidayYn")!=null && "Y".equals(otAppl.get("holidayYn")) && otAppl.get("subYn")!=null && "Y".equals(otAppl.get("subYn"))) {
					//이전 대체휴일
					List<WtmOtSubsAppl> oldOtSubsAppls = otSubsApplRepo.findByApplIdAndCancelYn(applId, "Y");
					if(oldOtSubsAppls!=null && oldOtSubsAppls.size()>0)
						otAppl.put("oldSubs", oldOtSubsAppls);
					
					List<Map<String, Object>> otSubsAppls = wtmOtApplMapper.otSubsApplfindByOtApplId(Long.valueOf(otAppl.get("otApplId").toString()));
					if(otSubsAppls!=null && otSubsAppls.size()>0)
						otAppl.put("subs", otSubsAppls);
				}
				
				boolean isRecovery = false;
				List<WtmApplLineVO> applLine = applMapper.getWtmApplLineByApplId(applId);
				
				//결재요청중일 때 회수 버튼 보여주기
				if(applLine!=null && applLine.size()>0) {
					int seq = 1;
					int rSeq = 1; // 수신처 seq
					boolean isRecIng = false;
					for(WtmApplLineVO l : applLine) {
						if(APPL_LINE_S.equals(l.getApprTypeCd())) {
							if(seq==1 && APPR_STATUS_REQUEST.equals(l.getApprStatusCd()) && sabuns.indexOf(sabun)!=-1 )
								isRecovery = true;
							
							seq++;
						}
						
						if(APPL_LINE_R.equals(l.getApprTypeCd())) {
							if(rSeq==1 && APPR_STATUS_REQUEST.equals(l.getApprStatusCd()))
								isRecIng = true;
							
							rSeq++;
						}
					}
					
					//발신결재가 없는 경우
					if(seq==1 && isRecIng) {
						isRecovery = true;
					}
				}
				
				otAppl.put("recoveryYn", isRecovery);
				otAppl.put("applLine", applLine);
			}
			//System.out.println("otAppl : " + mapper.writeValueAsString(otAppl));
			return otAppl;
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun,
			Map<String, Object> paramMap, String userId) {
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		return wtmOtApplMapper.getPrevOtSubsApplList(paramMap);
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

	@Transactional
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception { 
		
		ReturnParam rp = new ReturnParam();
		/*
		paramMap.put("applId", applId);
		
		String applSabun = paramMap.get("applSabun").toString();
		
		rp = this.validate(tenantId, enterCd, applSabun, "", paramMap);
		
		if(rp.getStatus().equals("FAIL")) {
			throw new Exception(rp.get("message").toString());
		}
		*/
		
		rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<String> apprSabun = new ArrayList();
		if(rp!=null && rp.getStatus()!=null && "OK".equals(rp.getStatus())) {
			applId = Long.valueOf(rp.get("applId").toString());
			List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprTypeCdAscApprSeqAsc(applId);
			 
			if(lines != null && lines.size() > 0) {
				for(WtmApplLine line : lines) {
					
					if(APPL_LINE_I.equals(line.getApprTypeCd())) { //기안
						//첫번째 결재자의 상태만 변경 후 스탑
						line.setApprStatusCd(APPR_STATUS_APPLY);
						line.setApprDate(new Date());
						line = wtmApplLineRepo.save(line);
					} else if(APPL_LINE_S.equals(line.getApprTypeCd()) || APPL_LINE_R.equals(line.getApprTypeCd())) { //결재
						//첫번째 결재자의 상태만 변경 후 스탑
						apprSabun.add(line.getApprSabun());
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						break;
					}
					 
				}
			}
		}
		
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 연장근무신청", "", "Y");
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", apprSabun);
		
		return rp;
	}

	@Transactional
	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("결재가 완료되었습니다.");
		
		paramMap.put("applId", applId);
		
		ObjectMapper mapper = new ObjectMapper();
		
		//신청 대상자
		List<String> applSabuns = null;
		if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
			applSabuns = mapper.readValue(paramMap.get("applSabuns").toString(), new ArrayList<String>().getClass());
		} else {
			//개인 신청
			applSabuns = new ArrayList<String>();
			applSabuns.add(sabun);
		}
		
		if(applSabuns!=null && applSabuns.size()>0) {
			for(String applSabun : applSabuns) {
				rp = this.validate(tenantId, enterCd, applSabun, "", paramMap);
				//rp = validate(applId);
				if(rp.getStatus().equals("FAIL")) {
					throw new Exception(rp.get("message").toString());
				}
			}
		} 
		
		logger.debug("OT 승인 대상자 : " + mapper.writeValueAsString(applSabuns));
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);

		String apprSabun = null;
		//마지막 결재자인지 확인하자
		boolean lastAppr = false;
		if(lines != null && lines.size() > 0) {
			for(int i = 0; i < lines.size(); i++) {
				WtmApplLine line = lines.get(i);
				
				if(line.getApprSeq() == apprSeq && line.getApprSabun().equals(sabun)) {
					line.setApprStatusCd(APPR_STATUS_APPLY);
					line.setApprDate(new Date());
					//결재의견
					if(paramMap != null && paramMap.containsKey("apprOpinion")) {
						line.setApprOpinion(paramMap.get("apprOpinion").toString());
						line.setUpdateId(userId);
					}
					apprSabun = line.getApprSabun();
					line = wtmApplLineRepo.save(line);
					lastAppr = true;
				}else {
					if(lastAppr) {
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						apprSabun = line.getApprSabun();
					}
					lastAppr = false;
				}
			}
		}
		
		
		//신청서 메인 상태값 업데이트
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_APPR:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		
		appl = wtmApplRepo.save(appl);
		
		if(lastAppr) {
			rp = wtmApplAfterService.applyStsAfter(tenantId, enterCd, applId, paramMap,  sabun,  userId);
		}
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.addAll(applSabuns);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "연장근무 신청서가  승인되었습니다.", "N");
			
			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 연장근무신청", "", "N");
			
			rp.put("msgType", "APPR");
		}
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", pushSabun);
		return rp;

	}

	@Transactional
	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		
		ReturnParam rp = new ReturnParam();
		
		if(paramMap == null || !paramMap.containsKey("apprOpinion") && paramMap.get("apprOpinion").equals("")) {
			rp.setFail("사유를 입력하세요.");
			throw new Exception("사유를 입력하세요."); 
		}
		
		List<String> applSabun = new ArrayList();
		applSabun.add(paramMap.get("applSabun").toString());
//		String applSabun = paramMap.get("applSabun").toString();
		String apprOpinion = paramMap.get("apprOpinion").toString();
		
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() <= apprSeq) {
					line.setApprStatusCd(APPR_STATUS_REJECT);
					//반려일때는 date가 안들어가도 되는건지?? 확인해보기
					line.setApprDate(new Date());
					if(line.getApprSeq() == apprSeq) {
						line.setApprOpinion(apprOpinion);
					}
				}else {
					line.setApprStatusCd("");
				}
				line.setUpdateId(userId);
				wtmApplLineRepo.save(line);
			}
		}
		
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd(APPL_STATUS_APPLY_REJECT);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);	
		wtmApplRepo.save(appl);
		
		inbox.setInbox(tenantId, enterCd, applSabun, applId, "APPLY", "결재완료", "연장근무 신청서가  반려되었습니다.", "N");
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", applSabun);
		
		return rp;
		
	}

	@Transactional
	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String applSabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
		//Long flexibleStdMgrId = Long.parseLong(paramMap.get("flexibleStdMgrId").toString());
		//신청서 최상위 테이블이다. 
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, status, applSabun, userId);
		
		applId = appl.getApplId();
		
		String ymd = paramMap.get("ymd").toString();
		String otSdate = paramMap.get("otSdate").toString();
		String otEdate = paramMap.get("otEdate").toString();
		String reasonCd = paramMap.get("reasonCd").toString();
		String reason = paramMap.get("reason").toString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		//신청 대상자
		Map<String, Object> sabunList = null;
		if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
			//유연근무신청 관리자 화면에서 선택한 대상자 리스트
			sabunList = mapper.readValue(paramMap.get("applSabuns").toString(), new HashMap<String, Object>().getClass());
		} else {
			//개인 신청
			sabunList = new HashMap<String, Object>();
			sabunList.put(applSabun, "");
		}
		
		if(sabunList!=null && sabunList.keySet().size()>0) {
			for(String sabun : sabunList.keySet()) {
				
				WtmWorkCalendar calendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
				
				String subYn = "";
				if(paramMap.containsKey("subYn")) {
					subYn = paramMap.get("subYn")+"";
				}
				
				//근무제 신청서 테이블 조회
				WtmOtAppl otAppl = saveWtmOtAppl(tenantId, enterCd, applId, applId, ymd,otSdate, otEdate, calendar.getHolidayYn(), subYn,  reasonCd, reason, sabun, userId);
				
				//휴일근무 신청 여부
				if(paramMap.containsKey("holidayYn") && paramMap.get("holidayYn").equals("Y")) {
					//대체휴일이면 subYn = Y
					if(paramMap.containsKey("subYn") && paramMap.get("subYn").equals("Y") && paramMap.containsKey("subs")) {
						List<WtmOtSubsAppl> otSubsAppl = wtmOtSubsApplRepo.findByApplId(applId);
						
						wtmOtSubsApplRepo.deleteAll(otSubsAppl);
						
						List<Map<String, Object>> subs = (List<Map<String, Object>>) paramMap.get("subs");
						if(subs != null && subs.size() > 0) {
							Map<String, Object> resultMap = new HashMap<>();
							Map<String, Object> pMap = new HashMap<>();
							pMap.put("tenantId", tenantId);
							pMap.put("enterCd", enterCd);
							
							for(Map<String, Object> sub : subs) {
								String subYmd = sub.get("subYmd").toString();
								String subsSdate = sub.get("subsSdate").toString();
								String subsEdate = sub.get("subsEdate").toString();
								
								Date sd = WtmUtil.toDate(subsSdate, "yyyyMMddHHmm");
								Date ed = WtmUtil.toDate(subsEdate, "yyyyMMddHHmm");
								
								WtmOtSubsAppl otSub = new WtmOtSubsAppl();
								otSub.setApplId(applId);
								otSub.setOtApplId(otAppl.getOtApplId());
								otSub.setSubYmd(subYmd);
								otSub.setSubsSdate(sd);
								otSub.setSubsEdate(ed);
								
								String sHm = WtmUtil.parseDateStr(sd, "HHmm");
								String eHm = WtmUtil.parseDateStr(ed, "HHmm");
								pMap.put("ymd", subYmd);
								pMap.put("shm", sHm);
								pMap.put("ehm", eHm);
								pMap.put("sabun", appl.getApplSabun());
								
								//현재 신청할 연장근무 시간 계산
								Map<String, Object> calcMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
		                        
		                        resultMap.putAll(calcMap);
		                        
		                        int subsMinute = 0;
		                        if(calcMap!=null && calcMap.containsKey("calcMinute")) {
		                           subsMinute = Integer.parseInt(resultMap.get("calcMinute").toString());
		                           
		           			       WtmWorkCalendar subCalendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, subYmd);

		                           WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(subCalendar.getTimeCdMgrId()).get();
		                           if(timeCdMgr.getBreakTypeCd().equals("TIME")) {
		                        	   logger.debug("subMinute : TIME");
			                           if(calcMap.containsKey("breakMinute") && resultMap.get("breakMinute")!=null && !"".equals(resultMap.get("breakMinute"))) {
				                              subsMinute = subsMinute -  Integer.parseInt(resultMap.get("breakMinute").toString());
				                           }
		                           } else if(timeCdMgr.getBreakTypeCd().equals("MGR")) {
		                        	   logger.debug("subMinute : MGR");
		                           } else {logger.debug("subMinute : ELSE");}
		                        }
		                        
		                        otSub.setSubsMinute(Integer.toString(subsMinute));
		                        
								otSub.setUpdateId(userId);
								wtmOtSubsApplRepo.save(otSub);
							}
						}
					}
				}
				
				String otShm = WtmUtil.parseDateStr(WtmUtil.toDate(otSdate, "yyyyMMddHHmm"), "HHmm");
				String otEhm = WtmUtil.parseDateStr(WtmUtil.toDate(otEdate, "yyyyMMddHHmm"), "HHmm");
				paramMap.put("shm", otShm);
				paramMap.put("ehm", otEhm);
				
				Map<String, Object> calcOtMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, userId);
				if(calcOtMap!=null && calcOtMap.containsKey("calcMinute") && calcOtMap.get("calcMinute")!=null) {
					if(calendar!=null && calendar.getTimeCdMgrId()!=null) {
						Long timeCdMgrId = Long.valueOf(calendar.getTimeCdMgrId());
						
						String breakTypeCd = null;
						WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
						if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
							breakTypeCd = timeCdMgr.getBreakTypeCd();
						
						if("TIME".equals(breakTypeCd)) {
							int calcMinute = Integer.parseInt(calcOtMap.get("calcMinute")+"");
							int breakMinute = 0;
							
							if(calcOtMap.get("breakMinute")!=null && !"".equals(calcOtMap.get("breakMinute")))
								breakMinute = Integer.parseInt(calcOtMap.get("breakMinute")+"");
							
							otAppl.setOtMinute((calcMinute-breakMinute) + "");
						} else {
							otAppl.setOtMinute(calcOtMap.get("calcMinute").toString());
						}
					}
					
					wtmOtApplRepo.save(otAppl);
				}
				//wtmOtApplMapper.calcOtMinute(paramMap);
			}
		}
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, applSabun, userId);
		paramMap.put("applId", appl.getApplId());
		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		
		rp.put("applId", appl.getApplId());
			
		return rp;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
		
		System.out.println("validate ==========");
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		// TODO Auto-generated method stub
		// 중복 신청은 화면에서 제어 하겠지?
		String ymd = paramMap.get("ymd").toString();
		String otSdate = paramMap.get("otSdate").toString();
		String otEdate = paramMap.get("otEdate").toString();
		
		Date td = WtmUtil.toDate(ymd, "yyyyMMdd");
		Date sd = WtmUtil.toDate(otSdate, "yyyyMMddHHmm");
		Date ed = WtmUtil.toDate(otEdate, "yyyyMMddHHmm");
		
		Date chkD = WtmUtil.addDate(sd, 1);

		//연장근무 신청 기간이 1일 이상이어서도 안된다! 미쳐가지고..
		int compare = chkD.compareTo(ed);
		//시작일보다 하루 더한 날과 비교하여 크면 안됨
		if(compare < 0) {
			rp.setFail("연장근무 신청을 하루 이상 신청할 수 없습니다.");
			return rp;
		}
		
		
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		
		//연장근무 신청 기간 내에 기본근로 외 다른 근무계획이 있는지 체크 한다.
		paramMap.put("sdate", sd);
		paramMap.put("edate", ed);
		
//		//연장근무 신청은 기준일 전/후일의 근무계획시간 사이에만 신청할 수 있음
//		Map<String, Object> chekPlan = wtmOtApplMapper.getCheckPlanDate(paramMap);
//		Date tempSd = WtmUtil.toDate(chekPlan.get("minPlan").toString(), "yyyyMMddHHmm");
//		Date tempEd = WtmUtil.toDate(chekPlan.get("maxPlan").toString(), "yyyyMMddHHmm");
//		//시작시각, 종료시각이 무조건 tempSd, tempEd 사이에 있어야 함, 조건문은 나중에 바꿔주시오~
//		if((tempSd.compareTo(sd) > 0 ) || (tempEd.compareTo(sd) < 0) || (tempSd.compareTo(ed) > 0) || (tempEd.compareTo(ed) < 0)) {
//			rp.setFail("선택하신 근무일에 신청할 수 없는 연장근무 시간입니다.!!");
//			return rp;
//		}
		
		Map<String, Object> chekPlan = wtmOtApplMapper.getCheckPlanDateCnt(paramMap);
		if(Integer.parseInt(chekPlan.get("cnt").toString()) > 0) {
			rp.setFail("선택하신 근무일에 신청할 수 없는 연장근무 시간입니다.!!");
			return rp;
		}
		
		Long applId = null;
		if(paramMap.containsKey("applId") && paramMap.get("applId") != null && !paramMap.get("applId").equals("")) {
			applId = Long.parseLong(paramMap.get("applId")+"");
		}
		
		Map<String, Object> resultMap = null;
		if(!"SUBS_CHG".equals(workTypeCd)) {
		
			resultMap = validatorService.checkDuplicateWorktime(tenantId, enterCd, sabun, otSdate, otEdate, applId); 
			/*
			//wtmFlexibleEmpMapper.checkDuplicateWorktime(paramMap);
			//Long timeCdMgrId = Long.parseLong(paramMap.get("timeCdMgrId").toString());
			
	
			int workCnt = 0;
			if(resultMap != null && resultMap.get("workCnt")!=null && !"".equals(resultMap.get("workCnt"))) {
				workCnt = Integer.parseInt(resultMap.get("workCnt").toString());
			}
			if(workCnt > 0) {
				rp.setFail("이미 근무정보(신청중인 근무 포함)가 존재합니다.");
				return rp;
			}*/
			
			if(resultMap!=null && resultMap.get("status")!=null && "FAIL".equals(resultMap.get("status").toString())) {
				rp.setFail(resultMap.get("message").toString());
				return rp;
			}
		
		} else {
			resultMap = new HashMap<String, Object>();
		}
		
		String sHm = WtmUtil.parseDateStr(sd, "HHmm");
		String eHm = WtmUtil.parseDateStr(ed, "HHmm");
		paramMap.put("shm", sHm);
		paramMap.put("ehm", eHm);
		
		paramMap.put("symd", WtmUtil.parseDateStr(sd, "yyyyMMdd"));
		paramMap.put("eymd", WtmUtil.parseDateStr(ed, "yyyyMMdd"));
		
		//잔여 기본근로시간 체크 
		//기본근무/시차/근무조 일경우에 
		//잔여 기본근로시간이 있을 경우 BASE 와 OT를 분리하여 체크 한다. 
		// To-Do
		
		List<String> sabunList = new ArrayList<String>();
		sabunList.add(sabun);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> empParamMap = new HashMap<>();
		empParamMap.put("sabuns", sabunList);
		empParamMap.put("ymd", ymd);
		empParamMap.put("tenantId", tenantId);
		empParamMap.put("enterCd", enterCd);
		try {
			System.out.println(mapper.writeValueAsString(empParamMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String, Object>> emps = wtmOtApplMapper.getRestOtMinute(empParamMap);
		int restMin = 0;
		try {
			logger.debug("### otMinute : " + mapper.writeValueAsString(emps));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(emps!=null && emps.size()>0) {
			for(Map<String, Object> emp : emps) {
				//Map<String, Object> restMinuteMap = new HashMap<String, Object>();
				/*
				if(emp.get("restOtMinute")!=null && !"".equals(emp.get("restOtMinute"))) {
					int restMin = Integer.parseInt(emp.get("restOtMinute").toString());
					restMinuteMap.put("restOtMinute", restMin);
				}
				*/
				//휴일근무이며
				if(emp.get("holidayYn") != null && "Y".equals(emp.get("holidayYn"))) {
					//기본근무 / 시차출퇴근 / 근무조 일때는 휴일에 잔여 기본근로 시간을 사용할 수 잇다. 
					if(emp.get("workTypeCd") != null && ("BASE".equals(emp.get("workTypeCd")) || "DIFF".equals(emp.get("workTypeCd")) || "WORKTEAM".equals(emp.get("workTypeCd")) ) ) {
						/*
							한주에 대한 정보 조회 계획 및 인정 근무 시간의 합 - 결근 제외 
						 */
						Map<String, Object> weekInfo = wtmFlexibleEmpMapper.weekWorkTimeByEmp(paramMap);
						try {
							logger.debug("### otMinute weekInfo : " + mapper.writeValueAsString(weekInfo));
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(weekInfo != null && weekInfo.get("workMinute") != null && !weekInfo.get("workMinute").equals("")) {
							//한주기본근로시간 40시간   * 60  = 2400
							int weekWorkMinute = Integer.parseInt(weekInfo.get("weekWorkMinute")+"");
							int exMinute = 0;
							if(weekInfo.get("exMinute") != null && !weekInfo.get("exMinute").equals("")) {
								exMinute = Integer.parseInt(weekInfo.get("exMinute")+"");
							}
							int restOtMinute = Integer.parseInt(emp.get("restOtMinute").toString());
							int otMinute = Integer.parseInt(weekInfo.get("otMinute").toString());
							restMin = (weekWorkMinute - Integer.parseInt(weekInfo.get("workMinute")+"") - exMinute) + (restOtMinute - otMinute) ;
							//restMinuteMap.put("restWorkMinute", restMin);
						}
					}
				}
			}
		}

		logger.debug("### restMin : "+ restMin);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Map<String, Object> calcMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, sabun);
		//int calcMinute = Integer.parseInt(calcMinuteMap.get("calcMinute").toString());
		//restMin
		//잔여 기본 근로시간으로 모두 계산 시 연장근무 시간 체크를 하지 않는다. 
		boolean isOtCheck = true;
		//잔여 기본 근로 시간이 있을 경우 기본근로 시간을 먼저 구한다. 
		if(restMin > 0) {
			//신청 근무시간 과 잔여 기본근로 시간을 체크하자.
			//분을 구하자
			//int m = (int) ((ed.getTime() - sd.getTime()) / 1000 / 60);
			//잔여 기본근로 시간보다 신청 시간이 작으면
			/*
			if(restMin > m) {
				restMin = m;
				isOtCheck = false;
			} 
			*/

			//신청서의 시간을 구하고
			int calcMinute =Integer.parseInt(calcMinuteMap.get("calcMinute").toString());
			//잔여 기본근로시간보다 큰지를 판단
			if(restMin < calcMinute) {
				restMin = calcMinute - restMin;
				isOtCheck = true;
				
				Map<String, Object> calcMap = new HashMap<>();
				calcMap.put("tenantId", tenantId);
				calcMap.put("enterCd", enterCd);
				calcMap.put("sabun", sabun);
				calcMap.put("ymd", ymd);
				calcMap.put("sDate", sdf.format(sd));
				calcMap.put("addMinute", restMin); 
				calcMap.put("retDate", ""); 
				wtmFlexibleEmpMapper.addMinuteWithBreakMGR(calcMap);
				
				//String sHm = WtmUtil.parseDateStr(sd, "HHmm");
				
				String baseEdateStr = calcMap.get("retDate")+"";
				System.out.println("retDate.toString() : " + calcMap.toString());
				System.out.println("baseEdate : "+ baseEdateStr);
				//근로시간 선 소진 후 잔여 연장 근무시간에 대해서는 체크 해야한다. 
				if(isOtCheck) {
					try {
						paramMap.put("shm", WtmUtil.parseDateStr(sdf.parse(baseEdateStr), "HHmm"));
					} catch (ParseException e) {
						rp.setFail(e.getMessage());
						return rp;
					}
				}

			} else {
				isOtCheck = false;
			}
		}
		logger.debug("### isOtCheck : "+ isOtCheck);
		if(isOtCheck) {
			//현재 신청할 연장근무 시간 계산
			resultMap.putAll(wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, sabun));
			
			Integer calcMinute = Integer.parseInt(resultMap.get("calcMinute").toString());
			Integer breakMinute = 0;
			if(resultMap.containsKey("breakMinute"))
				breakMinute = Integer.parseInt(resultMap.get("breakMinute").toString());
			
			resultMap.putAll(wtmFlexibleEmpMapper.getSumOtMinute(paramMap));
			Integer sumOtMinute = Integer.parseInt(resultMap.get("otMinute").toString());
			
			//회사의 주 시작요일을 가지고 온다.
			paramMap.put("d", ymd);
			
			//7일전 ~ 7일 후 범위 지정
			Date date = WtmUtil.toDate(ymd, "yyyyMMdd");
	        
	        Calendar sYmd = Calendar.getInstance();
	        sYmd.setTime(date);
	        sYmd.add(Calendar.DATE, -7);
	       
	        Calendar eYmd = Calendar.getInstance();
	        eYmd.setTime(date);
	        eYmd.add(Calendar.DATE, 7);
	
			paramMap.put("sYmd", WtmUtil.parseDateStr(sYmd.getTime(), "yyyyMMdd"));
			paramMap.put("eYmd", WtmUtil.parseDateStr(eYmd.getTime(), "yyyyMMdd"));
			
			Map<String, Object> rMap = wtmFlexibleStdMapper.getRangeWeekDay(paramMap);
			if(rMap == null) {
				rp.setFail("기준 일자 정보가 없습니다. 관리자에게 문의하세요.");
				return rp;
			}
			String symd = rMap.get("symd").toString();
			String eymd = rMap.get("eymd").toString();
			
			Integer subCalcMinute = 0; 
			//대체휴일 정보 가져와서 휴일근무일 경우 같은 주의 대체휴일 만큼 빼줘야한다
			if(paramMap.containsKey("holidayYn") && paramMap.get("holidayYn").equals("Y")
					&& paramMap.containsKey("subYn") && paramMap.get("subYn").equals("Y")
					&& paramMap.containsKey("subs")) {
				
				List<Map<String, Object>> subs = (List<Map<String, Object>>) paramMap.get("subs");
				if(subs != null && subs.size() > 0) {
	
					for(Map<String, Object> sub : subs) {
						if(!sub.containsKey("subYmd") || !sub.containsKey("subsSdate") || !sub.containsKey("subsEdate")
								|| sub.get("subYmd").equals("") || sub.get("subsSdate").equals("") || sub.get("subsEdate").equals("")) {
							rp.setFail("대체휴일을 선택하셨을 경우 대체휴일의 정보를 모두 입력해야합니다.");
							return rp;
						}
						String subYmd = sub.get("subYmd").toString();
						String subsSdate = sub.get("subsSdate").toString();
						String subsEdate = sub.get("subsEdate").toString();
						//같은 주에 있는 대체휴일 시간정보만
						if(Integer.parseInt(subYmd) >= Integer.parseInt(symd) && Integer.parseInt(subYmd) <= Integer.parseInt(eymd)) {
							
							
							Date subSd = WtmUtil.toDate(subsSdate, "yyyyMMddHHmm");
							Date subEd = WtmUtil.toDate(subsEdate, "yyyyMMddHHmm");
							 
							
							String subSHm = WtmUtil.parseDateStr(subSd, "HHmm");
							String subEHm = WtmUtil.parseDateStr(subEd, "HHmm");
							paramMap.put("shm", subSHm);
							paramMap.put("ehm", subEHm);
							
							//현재 신청할 연장근무 시간 계산
							Map<String, Object> subMap =  wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, sabun);
							if(subMap != null && !resultMap.get("calcMinute").equals("")) {
								subCalcMinute += Integer.parseInt(resultMap.get("calcMinute").toString());
							}
						}
						paramMap.put("sdate", subsSdate);
						paramMap.put("edate", subsEdate);
						Map<String, Object> resultSubsMap = wtmFlexibleEmpMapper.checkDuplicateSubsWorktime(paramMap);
						//Long timeCdMgrId = Long.parseLong(paramMap.get("timeCdMgrId").toString());
						
						int workSubsCnt = Integer.parseInt(resultSubsMap.get("workCnt").toString());
						if(workSubsCnt > 0) {
							rp.setFail("이미 근무정보(신청중인 근무 포함)가 존재합니다.");
							return rp;
						}
						
					}
				}
			}
			calcMinute = calcMinute - subCalcMinute;
			
			boolean weekOtCheck = true;
			
			//연장근무 가능 시간을 가지고 오자
			WtmFlexibleEmp emp = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
			//선근제 이면
			if(emp.getWorkTypeCd().startsWith("SELE")) {
				//1주의 범위가 선근제 기간내에 있는지 체크
				/*
				if(Integer.parseInt(symd) >= Integer.parseInt(emp.getSymd() ) && Integer.parseInt(eymd) <= Integer.parseInt(emp.getEymd())) {
					//선근제는 주단위 연장근무 시간을 체크하지 않는다.
					weekOtCheck = false;
				}
				*/
				weekOtCheck = false;
			}
			
			logger.debug("### weekOtCheck : "+ weekOtCheck);
			
			if(weekOtCheck) {
				paramMap.putAll(rMap);
	
				//휴일근무의 경우 대체휴일 정보가 같은 주일 경우 퉁친다.	
	 			//근데 4시간 일하고 2시간씩 이번주 차주로 나눠쓰면!!!!! 차주꺼는 연장근무 시간으로 본다 써글
				
				rMap = wtmOtApplMapper.getTotOtMinuteBySymdAndEymd(paramMap);
				int totOtMinute = 0;
				if(rMap != null && rMap.get("totOtMinute") != null && !rMap.get("totOtMinute").equals("")) {
					totOtMinute = Integer.parseInt(rMap.get("totOtMinute")+"");
				}
				Float f = (float) ((totOtMinute + (calcMinute-breakMinute)) / 60.0f);
				//if(f > 12) {
				// 20200714 이효정 연장근무 가능시간은 프로퍼티에서 가져와야함.
				Float baseOt = (float)0;
				try {
					WtmPropertie proOt = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_MAX_WORKTIME_1WEEK_OT");
					baseOt = Float.parseFloat(proOt.getInfoValue());
				} catch (Exception e) {
					baseOt = (float)12;
				}
				if(f > baseOt) {
					f = (float) (((12*60) - totOtMinute) / 60.0);
					Float ff = (f - f.intValue()) * 60;
					// rp.setFail("연장근무 신청 가능 시간은 " + f.intValue() + "시간 " + ff.intValue() + "분 입니다.");
					rp.setFail("연장근무 신청 가능 "+ baseOt.intValue() + "시간을 모두 사용하였습니다.");
					return rp;
				}
			}
			Integer otMinute = emp.getOtMinute();
			logger.debug("### otMinute : " + otMinute);
			logger.debug("### calcMinute : " + calcMinute);
			logger.debug("### sumOtMinute : " + sumOtMinute);
			//otMinute 연장근무 가능 시간이 0이면 체크 하지말자 우선!!
			if(otMinute != null && otMinute > 0) {
				int t = (((sumOtMinute!=null)?sumOtMinute:0) + ((calcMinute!=null)?calcMinute:0));
				//연장 가능 시간보다 이미 신청중이거나 연장근무시간과 신청 시간의 합이 크면 안되유.
				if(otMinute < t ) {
					rp.setFail("연장근무 가능시간은 " + otMinute + " 시간 입니다. 신청 가능 시간은 " + (otMinute-((sumOtMinute!=null)?sumOtMinute:0)) + " 시간 입니다. 추가 신청이 필요한 경우 담당자에게 문의하세요" );
				}
			}
			
			return rp;
		}else {
			return rp;
		}
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub

	}

	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}
	protected WtmAppl saveWtmAppl(Long tenantId, String enterCd, Long applId, String workTypeCd, String applStatusCd, String sabun, String userId) {
		WtmAppl appl = null;
		if(applId != null && !applId.equals("")) {
			appl = wtmApplRepo.findById(applId).get();
		}else {
			appl = new WtmAppl();
		}
		appl.setApplStatusCd(applStatusCd);
		appl.setTenantId(tenantId);
		appl.setEnterCd(enterCd);
		appl.setApplInSabun(sabun);
		appl.setApplSabun(sabun);
		appl.setApplCd(workTypeCd);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId); 
		//appl.
		
		return wtmApplRepo.save(appl);
	}
	
	protected WtmOtAppl saveWtmOtAppl(Long tenantId, String enterCd, Long applId, Long oldOtApplId, String stdYmd, String otSdate, String otEdate, String holidayYn, String subYn, String reasonCd, String reason, String sabun, String userId) {
		 
		//WtmOtAppl otAppl = wtmOtApplRepo.findByApplId(applId);
		WtmOtAppl otAppl = wtmOtApplRepo.findByApplIdAndSabun(applId, sabun);
		if(otAppl == null ) {
			logger.debug("otAppl is null");
			otAppl = new WtmOtAppl();
		}
		
		otAppl.setApplId(applId);
		otAppl.setYmd(stdYmd);
		otAppl.setSabun(sabun);
		otAppl.setOtSdate(WtmUtil.toDate(otSdate, "yyyyMMddHHmm"));
		otAppl.setOtEdate(WtmUtil.toDate(otEdate, "yyyyMMddHHmm"));
		otAppl.setHolidayYn(holidayYn);
		otAppl.setReasonCd(reasonCd);
		otAppl.setReason(reason);
		otAppl.setSubYn(subYn);
		otAppl.setUpdateId(userId);
		logger.debug("otAppl ymd : " + stdYmd +  " , otSdate " + otSdate + " , otEdate " +  otEdate);
		
		return wtmOtApplRepo.save(otAppl);
	}
	
	public ReturnParam preCheckOneByOne(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		rp.put("subsYn", false);
		rp.put("payTargetYn", false);
		
		String ymd = paramMap.get("ymd").toString();
		
		WtmFlexibleEmp emp = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);

		System.out.println("preCheckOneByOne  tenantId:  " + tenantId);
		System.out.println("preCheckOneByOne  enterCd:  " + enterCd);
		System.out.println("preCheckOneByOne  sabun:  " + sabun);
		System.out.println("preCheckOneByOne  workTypeCd:  " + workTypeCd);
		System.out.println("preCheckOneByOne  paramMap:  " + paramMap.toString());

		if(emp!=null) {
			//1. 연장근무 신청 시 기본근로 선 소진 여부를 체크한다.
			//선 소진 여부
			String exhaustionYn = null;
			WtmFlexibleStdMgr flexibleStdMgr = wtmFlexibleStdMgrRepo.findById(emp.getFlexibleStdMgrId()).get();
			if(flexibleStdMgr!=null)
				exhaustionYn = flexibleStdMgr.getExhaustionYn();
	
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			
			//0. 신청 가능한 대상자 인지 확인
			WtmApplCode applCode = wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, workTypeCd);
			
			List<Long> ruleIds = new ArrayList<Long>();
			Map<String, Object> ruleMap = null;
			
			Long targetRuleId = applCode.getTargetRuleId();
			System.out.println("preCheckOneByOne  targetRuleId ::::: " + targetRuleId + " !!!!");
			if(targetRuleId != null) 
				ruleIds.add(targetRuleId);
			
			// 대체휴가 사용여부 체크
			if(applCode.getSubsYn()!=null && "Y".equals(applCode.getSubsYn())) {
				rp.put("subsYn", applCode.getSubsYn());
			}
			// 수당지급 대상자 인지 확인
			Long subsRuleId = applCode.getSubsRuleId();
			System.out.println("preCheckOneByOne  subsRuleId ::::: " + subsRuleId + " !!!!");
			System.out.println("preCheckOneByOne  subsRuleId ::::: " + subsRuleId + " !!!!");
			if(subsRuleId != null)
				ruleIds.add(subsRuleId);
			
			if(ruleIds.size()>0) {
				//WtmRule rule = wtmRuleRepo.findByRuleId(targetRuleId);
				List<WtmRule> rules = wtmRuleRepo.findByRuleIdsIn(ruleIds);
				
				if(rules!=null && rules.size()>0) {
					for(WtmRule rule : rules) {
						String ruleValue  = rule.getRuleValue();
						if(ruleValue != null && !ruleValue.equals("")) {
							try {
								boolean isTarget = wtmFlexibleEmpService.isRuleTarget(tenantId, enterCd, sabun, rule.getRuleType(), ruleValue);
								System.out.println("isTarget:" + isTarget);
								
								if(targetRuleId==rule.getRuleId() && !isTarget) { 
									rp.setFail("연장(휴일)근무 신청 대상자가 아닙니다.");
									return rp;
								} 
								rp.put("payTargetYn", isTarget);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
			if(exhaustionYn!=null && exhaustionYn.equals("Y")) {
				//선소진시
				//코어타임을 제외한 잔여 기본근로시간을 알려준다
				//근무제 기간 내의 총 기본근로 시간
				int workMinute = emp.getWorkMinute();
				paramMap.put("tenantId", tenantId);
				paramMap.put("enterCd", enterCd);
				paramMap.put("sabun", sabun);
				
				
				Map<String, Object> resultMap = wtmFlexibleEmpMapper.getTotalApprMinute(paramMap); //totalApprMinute
				Map<String, Object> resultCoreMap = wtmFlexibleEmpMapper.getTotalCoretime(paramMap); //coreHm
				int apprMinute = Integer.parseInt(resultMap.get("totalApprMinute").toString());
				int coreMinute = 0;
				if(resultCoreMap != null) {
					coreMinute = Integer.parseInt(resultCoreMap.get("coreHm").toString());
				}
				
				//근무제 기간 내 총 기본근로 시간 > 연장근무신청일 포함 이전일의 인정기본근로시간(인정기본근로시간이 없을 경우 계획기본근로 시간) + 연장근무신청일 이후의 코어타임 시간			
				if(workMinute > apprMinute + coreMinute) {
					int baseWorkMinute = workMinute - apprMinute - coreMinute;
					rp.setFail("필수 근무시간을 제외한 " + baseWorkMinute + "분의 기본근로시간을 선 소진 후 연장근무를 신청할 수 있습니다.");
					return rp;
				}
			}
			
			//2.연장근무 가능시간 초과 체크
			
			//회사의 주 시작요일을 가지고 온다.
			ObjectMapper mapper = new ObjectMapper();
			paramMap.put("d", ymd);
			
			//7일전 ~ 7일 후 범위 지정
			Date date = WtmUtil.toDate(ymd, "yyyyMMdd");
	        
	        Calendar sYmd = Calendar.getInstance();
	        sYmd.setTime(date);
	        sYmd.add(Calendar.DATE, -7);
	       
	        Calendar eYmd = Calendar.getInstance();
	        eYmd.setTime(date);
	        eYmd.add(Calendar.DATE, 7);
	
			paramMap.put("sYmd", WtmUtil.parseDateStr(sYmd.getTime(), "yyyyMMdd"));
			paramMap.put("eYmd", WtmUtil.parseDateStr(eYmd.getTime(), "yyyyMMdd"));
			
			Map<String, Object> rMap = wtmFlexibleStdMapper.getRangeWeekDay(paramMap);
			
			String symd = rMap.get("symd").toString();
			String eymd = rMap.get("eymd").toString();
			paramMap.put("symd",symd);
			paramMap.put("eymd",eymd);
			
			boolean weekOtCheck = true;

			logger.debug("emp.getWorkTypeCd() : " + emp.getWorkTypeCd());
			//연장근무 가능 시간을 가지고 오자
			//선근제 이면
			if(emp.getWorkTypeCd().startsWith("SELE")) {
				//1주의 범위가 선근제 기간내에 있는지 체크
				/*  20200707 JYP 선근제도 체크 한다. 단 기간을 주단위가 아닌 선근제 전체 기간으로 체크 한다.
				 */
				if(Integer.parseInt(symd) >= Integer.parseInt(emp.getSymd() ) && Integer.parseInt(eymd) <= Integer.parseInt(emp.getEymd())) {
					//선근제는 주단위 연장근무 시간을 체크하지 않는다.
					weekOtCheck = false;  
					
				}
				
				paramMap.put("symd", emp.getSymd());
				paramMap.put("eymd", emp.getEymd());
				
			}
			
			if(weekOtCheck) {
				paramMap.putAll(rMap);
				
				rMap = wtmOtApplMapper.getTotOtMinuteBySymdAndEymd(paramMap);
				int totOtMinute = 0;
				if(rMap != null && rMap.get("totOtMinute") != null && !rMap.get("totOtMinute").equals("")) {
					totOtMinute = Integer.parseInt(rMap.get("totOtMinute")+"");
				}
				Float f = (float) (totOtMinute / 60);
				// 20200714 이효정 연장근무 가능시간은 프로퍼티에서 가져와야함.
				Float baseOt = (float)0;
				try {
					WtmPropertie proOt = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_MAX_WORKTIME_1WEEK_OT");
					baseOt = Float.parseFloat(proOt.getInfoValue());
				} catch (Exception e) {
					baseOt = (float)12;
				}
				if(f > baseOt) {
					Float ff = (f - f.intValue()) * 60;
					rp.setFail("연장근무 신청 가능 "+ baseOt.intValue() + "시간을 모두 사용하였습니다.");
					// rp.setFail("연장근무 신청 가능 "+ baseOt.intValue() + "시간 중 " + f.intValue() + "시간 " + ff.intValue() + "분 사용하셨습니다.");
					return rp;
				}
			}
			
			Integer otMinute = emp.getOtMinute();
			if(otMinute == null) {
				otMinute = 0;
			}
			
			//우선 오티 시간이 없으면 체크하지 말자
			if(otMinute > 0) {
				rMap = wtmFlexibleEmpMapper.getSumOtMinute(paramMap);
				Integer sumOtMinute = Integer.parseInt(rMap.get("otMinute").toString());
				if(otMinute <= sumOtMinute) {
					rp.setFail("금주 사용가능한 연장근무 시간이 없습니다. 담당자에게 문의하세요.");
					return rp;
				}
			}
		
		}
		
		return rp;
	}

	@Transactional
	@Override
	public void delete(Long applId) {
		
		wtmOtSubsApplRepo.deleteByApplId(applId);
		wtmOtApplRepo.deleteByApplId(applId);
		wtmApplLineRepo.deleteByApplId(applId);
		wtmApplRepo.deleteById(applId);
		
	}
	
	
	
	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String applSabun, String workTypeCd,
			Map<String, Object> paramMap) {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//신청 대상자
			Map<String, Object> sabunList = null;
			if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
				//유연근무신청 관리자 화면에서 선택한 대상자 리스트
				sabunList = mapper.readValue(paramMap.get("applSabuns").toString(), new HashMap<String, Object>().getClass());
			} else {
				//개인 신청
				sabunList = new HashMap<String, Object>();
				sabunList.put(applSabun, "");
			}
			
			if(sabunList!=null && sabunList.keySet().size()>0) {
				for(String s : sabunList.keySet()) {
					rp = preCheckOneByOne(tenantId, enterCd, s, workTypeCd, paramMap);
					if("FAIL".equals(rp.getStatus())) {
						rp.put("checkTarget", sabunList.get(s));
						return rp;
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("연장근무 대상자 체크 시 오류가 발생했습니다.");
			return rp;
		}
		
		return rp;
	}
	
	//모바일 연장근무 신청서가 오류가 난다고 해서 여기까지 찾아왔다니 대단하군!
	//모바일은 applId가 없고 이것저것 달라서 웹이랑 호출하는 서비스가 다르지. 하지만 안에 내용은 똑같아야 하니까 imsi안에 바뀐내용을 여기다 넣어주면 
	//미션 클리어! (아마도)
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception { 
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("신청되었습니다.");
		
		//신청서 최상위 테이블이다. 
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, null, paramMap.get("applCd").toString(), this.APPL_STATUS_APPLY_ING, sabun, userId);
		long applId = appl.getApplId();
		
		String ymd = paramMap.get("ymd").toString();
		String otSdate = paramMap.get("otSdate").toString();
		String otEdate = paramMap.get("otEdate").toString();
		String reasonCd = paramMap.get("gubun").toString();
		String reason = paramMap.get("reason").toString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		WtmWorkCalendar calendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
		String subYn = "";
		if(paramMap.containsKey("subYn")) {
			subYn = paramMap.get("subYn")+"";
		}
		//근무제 신청서 테이블 조회
		WtmOtAppl otAppl = saveWtmOtAppl(tenantId, enterCd, applId, applId, ymd, otSdate, otEdate, calendar.getHolidayYn(), subYn,  reasonCd, reason, sabun, userId);
			
		//휴일근무 신청 여부
		if(paramMap.containsKey("holidayYn") && paramMap.get("holidayYn").equals("Y")) {
		//대체휴일이면 subYn = Y
			if(paramMap.containsKey("subYn") && paramMap.get("subYn").equals("Y") && paramMap.containsKey("subsSymd")) {
				List<WtmOtSubsAppl> otSubsAppl = wtmOtSubsApplRepo.findByApplId(applId);
				
				wtmOtSubsApplRepo.deleteAll(otSubsAppl);
				
//				List<Map<String, Object>> subs = (List<Map<String, Object>>) paramMap.get("subsSymd");
//				if(subs != null && subs.size() > 0) {
				Map<String, Object> resultMap = new HashMap<>();
				Map<String, Object> pMap = new HashMap<>();
				pMap.put("tenantId", tenantId);
				pMap.put("enterCd", enterCd);
					
//					for(Map<String, Object> sub : subs) {
				String subYmd = paramMap.get("subsSymd").toString();
				String subsSdate = paramMap.get("subsSymd").toString() + paramMap.get("subsShm").toString();
				String subsEdate = paramMap.get("subsSymd").toString() + paramMap.get("subsEhm").toString();
						
				Date sd = WtmUtil.toDate(subsSdate, "yyyyMMddHHmm");
				Date ed = WtmUtil.toDate(subsEdate, "yyyyMMddHHmm");
						
				WtmOtSubsAppl otSub = new WtmOtSubsAppl();
				otSub.setApplId(applId);
				otSub.setOtApplId(otAppl.getOtApplId());
				otSub.setSubYmd(subYmd);
				otSub.setSubsSdate(sd);
				otSub.setSubsEdate(ed);
						
				String sHm = WtmUtil.parseDateStr(sd, "HHmm");
				String eHm = WtmUtil.parseDateStr(ed, "HHmm");
				pMap.put("ymd", subYmd);
				pMap.put("shm", sHm);
				pMap.put("ehm", eHm);
				pMap.put("sabun", appl.getApplSabun());
				
				//현재 신청할 연장근무 시간 계산
				Map<String, Object> calcMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId);
				
                resultMap.putAll(calcMap);

                int subsMinute = 0;
                if(calcMap!=null && calcMap.containsKey("calcMinute")) {
                   subsMinute = Integer.parseInt(resultMap.get("calcMinute").toString());
                   
   			       WtmWorkCalendar subCalendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, subYmd);

                   WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(subCalendar.getTimeCdMgrId()).get();
                   if(timeCdMgr.getBreakTypeCd().equals("TIME")) {
                	   logger.debug("subMinute : TIME");
                       if(calcMap.containsKey("breakMinute") && resultMap.get("breakMinute")!=null && !"".equals(resultMap.get("breakMinute"))) {
                              subsMinute = subsMinute -  Integer.parseInt(resultMap.get("breakMinute").toString());
                           }
                   } else if(timeCdMgr.getBreakTypeCd().equals("MGR")) {
                	   logger.debug("subMinute : MGR");
                   } else {logger.debug("subMinute : ELSE");}
                }

                otSub.setSubsMinute(Integer.toString(subsMinute));
                
				otSub.setUpdateId(userId);
				wtmOtSubsApplRepo.save(otSub);
			}

		}
		
		String otShm = WtmUtil.parseDateStr(WtmUtil.toDate(otSdate, "yyyyMMddHHmm"), "HHmm");
		String otEhm = WtmUtil.parseDateStr(WtmUtil.toDate(otEdate, "yyyyMMddHHmm"), "HHmm");
		paramMap.put("shm", otShm);
		paramMap.put("ehm", otEhm);
		
		Map<String, Object> calcOtMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, paramMap, userId);
		if(calcOtMap!=null && calcOtMap.containsKey("calcMinute") && calcOtMap.get("calcMinute")!=null && !"".equals(calcOtMap.get("calcMinute"))) {
			
			if(calendar!=null && calendar.getTimeCdMgrId()!=null) {
				Long timeCdMgrId = Long.valueOf(calendar.getTimeCdMgrId());
				
				String breakTypeCd = null;
				WtmTimeCdMgr timeCdMgr = wtmTimeCdMgrRepo.findById(timeCdMgrId).get();
				if(timeCdMgr!=null && timeCdMgr.getBreakTypeCd()!=null)
					breakTypeCd = timeCdMgr.getBreakTypeCd();
				
				if("TIME".equals(breakTypeCd)) {
					int calcMinute = Integer.parseInt(calcOtMap.get("calcMinute")+"");
					int breakMinute = 0;
					
					if(calcOtMap.get("breakMinute")!=null && !"".equals(calcOtMap.get("breakMinute")))
						breakMinute = Integer.parseInt(calcOtMap.get("breakMinute")+"");
					
					otAppl.setOtMinute((calcMinute-breakMinute) + "");
				} else {
					otAppl.setOtMinute(calcOtMap.get("calcMinute").toString());
				}
			}
			
			wtmOtApplRepo.save(otAppl);
		}

		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(paramMap.get("applLevelCd").toString()), applId, paramMap.get("applCd").toString(), sabun, userId);
		paramMap.put("applId", appl.getApplId());
		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		
		rp.put("applId", appl.getApplId());
			
		
//		rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		List<String> apprSabun = new ArrayList();
		if(rp!=null && rp.getStatus()!=null && "OK".equals(rp.getStatus())) {
			applId = Long.valueOf(rp.get("applId").toString());
			List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprTypeCdAscApprSeqAsc(applId);
			 
			if(lines != null && lines.size() > 0) {
				for(WtmApplLine line : lines) {
					
					if(APPL_LINE_I.equals(line.getApprTypeCd())) { //기안
						//첫번째 결재자의 상태만 변경 후 스탑
						line.setApprStatusCd(APPR_STATUS_APPLY);
						line.setApprDate(new Date());
						line = wtmApplLineRepo.save(line);
					} else if(APPL_LINE_S.equals(line.getApprTypeCd()) || APPL_LINE_R.equals(line.getApprTypeCd())) { //결재
						//첫번째 결재자의 상태만 변경 후 스탑
						apprSabun.add(line.getApprSabun());
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						break;
					}
					 
				}
			}
		}
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 연장근무신청", "", "Y");

		return rp;
		//push전송 추가
	}
	
	
	/**
	 * 연장/휴일근무 신청내역조회 결재 상태 변경 
	 * @author 유충현
	 */
	@Transactional
	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId, Map<String, Object> convertMap) {
		 ReturnParam rp = new ReturnParam();
		 rp.setSuccess("정상적으로 저장되었습니다.");
			
		 try {
			 if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");

				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> map : iList) {
						map.put("tenantId", tenantId);
						map.put("enterCd", enterCd);
						map.put("userId", userId);
						map.put("sabun", sabun);
						
						//WTM_APPL 테이블 결재 상태값 변경
						wtmOtApplMapper.saveApplSts(map);
						
						//결재 상태값 변경 후 처리
						Long applId = Long.valueOf(map.get("applId").toString());
						
						//결재완료일때
						if(APPL_STATUS_APPR.equals(map.get("applStatusCd").toString())) {
							rp = wtmApplAfterService.applyStsAfter(tenantId, enterCd, applId, map, sabun, userId);
							
						//취소처리완료	
						} else if(APPL_STATUS_CANCEL.equals(map.get("applStatusCd").toString())) {
							rp = wtmApplAfterService.applyOtCanAdminAfter(tenantId, enterCd, applId, map, sabun, userId);
						
						}
						
						if(rp.getStatus().equals("FAIL")) {
							throw new Exception(rp.get("message").toString());
						}
						
						
						//WTM_APPL 테이블 결재 상태값 변경
						map.put("appr_status_cd", APPR_STATUS_APPLY);
						wtmOtApplMapper.saveApplLineSts(map);
					}
				}
			 }
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
			
			rp.setFail(e.getMessage());
			return rp;
		} finally {
			MDC.clear();
			logger.debug("getEntryList End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
		}
		
		return rp;
	}
}
