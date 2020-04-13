package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmOtAppl;
import com.isu.ifw.entity.WtmOtSubsAppl;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.entity.WtmRule;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmOtCanApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmOtApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmRuleRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;

@Service("wtmOtApplService")
public class WtmOtApplServiceImpl implements WtmApplService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	private WtmValidatorService validatorService;
	
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
			
			Map<String, Object> otAppl = null;
			if(otApplList!=null && otApplList.size()>0) {
				
				// otApplList가 1건 이상이면(연장근무신청 관리자 화면에서 신청한 경우) 대상자 리스트를 보여줌
				List<String> sabuns = new ArrayList<String>();
				
				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
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
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			
			//야간근무시간이 포함되어있는 지 확인후 데이터를 찢어넣자
			List<WtmPropertie> properties = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKeyLike(tenantId, enterCd, "OPTION_OT_NIGHT_HHMM_%");
			
			//대상자의 실제 근무 정보를 반영한다.
			List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
			
			logger.debug("연장근무정보 : " + mapper.writeValueAsString(otApplList));
			
			if(otApplList!=null && otApplList.size()>0) {
				
				String n_shm = "";
				String n_ehm = "";
				for(WtmPropertie propertie : properties) {
					if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_S")) {
						n_shm = propertie.getInfoValue();
					}else if(propertie.getInfoKey().equals("OPTION_OT_NIGHT_HHMM_E")) {
						n_ehm = propertie.getInfoValue();
					}
				}
				
				if("".equals(n_shm) || "".equals(n_ehm)) {
					rp.setFail("야간 근무시간 정보가 없습니다. 담당자에게 문의하시기 바랍니다.");
					throw new RuntimeException(rp.get("message").toString());
				}
				
				for(WtmOtAppl otAppl : otApplList) {
					
					Date otNightSdate = format.parse(otAppl.getYmd()+n_shm);
					Date otNightEdate = null;
					
					if(Integer.parseInt(n_shm) > Integer.parseInt(n_ehm)) {
						Date otNightNextDate = WtmUtil.addDate(otNightSdate, 1);
						otNightEdate = format.parse(fmt.format(otNightNextDate) + n_ehm);
					}else {
						otNightEdate = format.parse(otAppl.getYmd()+n_ehm);
					}
					
					logger.debug("야간 근무시간 : " + format.format(otNightSdate) + "~" + format.format(otNightEdate));
					logger.debug("연장 근무 신청 시간 : " + format.format(otAppl.getOtSdate()) + "~" + format.format(otAppl.getOtEdate()));
					
					//신청부터 야간연장신청이다.
					if(otAppl.getOtSdate().compareTo(otNightSdate) == 1 ) {
						//연장야간 종료시간이 야간 종료시간보다 클경우
						if(otAppl.getOtEdate().compareTo(otNightEdate) == 1 ) {

							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							dayResult.setApplId(applId);
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(otAppl.getYmd());
							dayResult.setSabun(otAppl.getSabun());
							dayResult.setPlanSdate(otAppl.getOtSdate());
							dayResult.setPlanEdate(otNightSdate);
							
							Map<String, Object> reCalc = new HashMap<>();
							reCalc.put("tenentId", tenantId);
							reCalc.put("enterCd", enterCd);
							reCalc.put("sabun", otAppl.getSabun());
							reCalc.put("ymd", otAppl.getYmd());
							reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
							reCalc.put("ehm", sdf.format(otNightSdate));
							//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
							Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
							
							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
							 
							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
							dayResult.setUpdateId(userId);
							
							wtmWorkDayResultRepo.save(dayResult);
							

							dayResult = new WtmWorkDayResult();
							dayResult.setApplId(applId);
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(otAppl.getYmd());
							dayResult.setSabun(otAppl.getSabun());
							dayResult.setPlanSdate(otNightSdate);
							dayResult.setPlanEdate(otAppl.getOtEdate());
							 
							reCalc.put("shm", sdf.format(otNightSdate));
							reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
							//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
							addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
							
							dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
							dayResult.setUpdateId(userId);
							
							wtmWorkDayResultRepo.save(dayResult);
						}else {

							//걍 나이트 오티				
							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							dayResult.setApplId(applId);
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(otAppl.getYmd());
							dayResult.setSabun(otAppl.getSabun());
							dayResult.setPlanSdate(otAppl.getOtSdate());
							dayResult.setPlanEdate(otAppl.getOtEdate());
							dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
							dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
							dayResult.setUpdateId(userId);
							
							wtmWorkDayResultRepo.save(dayResult);
						}
						
					//야간시간 포함여부를판단 하자 연장야간 시간 시작시간보다 클경우
					}else if(otAppl.getOtEdate().compareTo(otNightSdate) == 1 ) {
						 
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(otAppl.getOtSdate());
						dayResult.setPlanEdate(otNightSdate);
						
						Map<String, Object> reCalc = new HashMap<>();
						reCalc.put("tenentId", tenantId);
						reCalc.put("enterCd", enterCd);
						reCalc.put("sabun", otAppl.getSabun());
						reCalc.put("ymd", otAppl.getYmd());
						reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
						reCalc.put("ehm", sdf.format(otNightSdate));
						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
						
						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
						 
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
						dayResult.setUpdateId(userId);
						
						wtmWorkDayResultRepo.save(dayResult);
						

						
						dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(otNightSdate);
						dayResult.setPlanEdate(otAppl.getOtEdate());
						 
						reCalc.put("shm", sdf.format(otNightSdate));
						reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
						//addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
						addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
						
						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"")); 
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_NIGHT);
						dayResult.setUpdateId(userId);
						
						wtmWorkDayResultRepo.save(dayResult);
					}else {
						//걍 오티				
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setApplId(applId);
						dayResult.setTenantId(tenantId);
						dayResult.setEnterCd(enterCd);
						dayResult.setYmd(otAppl.getYmd());
						dayResult.setSabun(otAppl.getSabun());
						dayResult.setPlanSdate(otAppl.getOtSdate());
						dayResult.setPlanEdate(otAppl.getOtEdate());
						
						//dayResult.setPlanMinute(Integer.parseInt(otAppl.getOtMinute()));
						Map<String, Object> reCalc = new HashMap<>();
						reCalc.put("tenentId", tenantId);
						reCalc.put("enterCd", enterCd);
						reCalc.put("sabun", otAppl.getSabun());
						reCalc.put("ymd", otAppl.getYmd());
						reCalc.put("shm", sdf.format(otAppl.getOtSdate()));
						reCalc.put("ehm", sdf.format(otAppl.getOtEdate()));
						//Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpMapper.calcMinuteExceptBreaktime(reCalc);
						Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, otAppl.getSabun(), reCalc, userId);
						
						dayResult.setPlanMinute(Integer.parseInt(addPlanMinuteMap.get("calcMinute")+""));
						
						dayResult.setTimeTypeCd(WtmApplService.TIME_TYPE_OT);
						dayResult.setUpdateId(userId);
						
						wtmWorkDayResultRepo.save(dayResult);

					}
					//승인완료 시 해당 대상자의 통계데이터를 갱신하기 위함.
					rp.put("sabun", otAppl.getSabun());
					rp.put("symd", otAppl.getYmd());
					rp.put("eymd", otAppl.getYmd());
					
					logger.debug("연장근무시간 result에 저장 완료");
				}
				
				rp.put("otApplList", otApplList);
			}
			
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
				WtmOtAppl otAppl = saveWtmOtAppl(tenantId, enterCd, applId, applId, otSdate, otEdate, calendar.getHolidayYn(), subYn,  reasonCd, reason, sabun, userId);
				
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
								resultMap.putAll(wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId));
								
								otSub.setSubsMinute(resultMap.get("calcMinute").toString());
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
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		// TODO Auto-generated method stub
		// 중복 신청은 화면에서 제어 하겠지?
		String ymd = paramMap.get("ymd").toString();
		String otSdate = paramMap.get("otSdate").toString();
		String otEdate = paramMap.get("otEdate").toString();
		
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
		
		//연장근무 신청 기간 내에 소정근로 외 다른 근무계획이 있는지 체크 한다.
		paramMap.put("sdate", sd);
		paramMap.put("edate", ed);
		
		
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
			if(Integer.parseInt(symd) >= Integer.parseInt(emp.getSymd() ) && Integer.parseInt(eymd) <= Integer.parseInt(emp.getEymd())) {
				//선근제는 주단위 연장근무 시간을 체크하지 않는다.
				weekOtCheck = false;
			}
		}
		
		
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
			if(f > 12) {
				f = (float) (((12*60) - totOtMinute) / 60.0);
				Float ff = (f - f.intValue()) * 60;
				rp.setFail("연장근무 신청 가능 시간은 " + f.intValue() + "시간 " + ff.intValue() + "분 입니다.");
				return rp;
			}
		}
		
		Integer otMinute = emp.getOtMinute();
		//otMinute 연장근무 가능 시간이 0이면 체크 하지말자 우선!!
		if(otMinute != null && otMinute > 0) {
			int t = (((sumOtMinute!=null)?sumOtMinute:0) + ((calcMinute!=null)?calcMinute:0));
			//연장 가능 시간보다 이미 신청중이거나 연장근무시간과 신청 시간의 합이 크면 안되유.
			if(otMinute < t ) {
				rp.setFail("연장근무 가능시간은 " + otMinute + " 시간 입니다. 신청 가능 시간은 " + (otMinute-((sumOtMinute!=null)?sumOtMinute:0)) + " 시간 입니다. 추가 신청이 필요한 경우 담당자에게 문의하세요" );
			}
		}
		
		return rp;
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
	
	protected WtmOtAppl saveWtmOtAppl(Long tenantId, String enterCd, Long applId, Long oldOtApplId, String otSdate, String otEdate, String holidayYn, String subYn, String reasonCd, String reason, String sabun, String userId) {
		 
		//WtmOtAppl otAppl = wtmOtApplRepo.findByApplId(applId);
		WtmOtAppl otAppl = wtmOtApplRepo.findByApplIdAndSabun(applId, sabun);
		if(otAppl == null ) {
			otAppl = new WtmOtAppl();
		}
		Date sDate = WtmUtil.toDate(otSdate, "yyyyMMddHHmm");
		otAppl.setApplId(applId);
		otAppl.setYmd(WtmUtil.parseDateStr(sDate, null));
		otAppl.setSabun(sabun);
		otAppl.setOtSdate(sDate);
		otAppl.setOtEdate(WtmUtil.toDate(otEdate, "yyyyMMddHHmm"));
		otAppl.setHolidayYn(holidayYn);
		otAppl.setReasonCd(reasonCd);
		otAppl.setReason(reason);
		otAppl.setSubYn(subYn);
		otAppl.setUpdateId(userId);
		
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

		if(emp!=null) {
			//1. 연장근무 신청 시 소정근로 선 소진 여부를 체크한다.
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
			if(targetRuleId != null) 
				ruleIds.add(targetRuleId);
			
			// 대체휴가 사용여부 체크
			if(applCode.getSubsYn()!=null && "Y".equals(applCode.getSubsYn())) {
				rp.put("subsYn", applCode.getSubsYn());
			}	
			
			// 수당지급 대상자 인지 확인
			Long subsRuleId = applCode.getSubsRuleId();
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
				//코어타임을 제외한 잔여 소정근로시간을 알려준다
				//근무제 기간 내의 총 소정근로 시간
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
				
				//근무제 기간 내 총 소정근로 시간 > 연장근무신청일 포함 이전일의 인정소정근로시간(인정소정근로시간이 없을 경우 계획소정근로 시간) + 연장근무신청일 이후의 코어타임 시간			
				if(workMinute > apprMinute + coreMinute) {
					int baseWorkMinute = workMinute - apprMinute - coreMinute;
					rp.setFail("필수 근무시간을 제외한 " + baseWorkMinute + "분의 소정근로시간을 선 소진 후 연장근무를 신청할 수 있습니다.");
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
			
			//연장근무 가능 시간을 가지고 오자
			//선근제 이면
			if(emp.getWorkTypeCd().startsWith("SELE")) {
				//1주의 범위가 선근제 기간내에 있는지 체크
				if(Integer.parseInt(symd) >= Integer.parseInt(emp.getSymd() ) && Integer.parseInt(eymd) <= Integer.parseInt(emp.getEymd())) {
					//선근제는 주단위 연장근무 시간을 체크하지 않는다.
					weekOtCheck = false;
				}
			}
			
			if(weekOtCheck) {
				paramMap.putAll(rMap);
				
				rMap = wtmOtApplMapper.getTotOtMinuteBySymdAndEymd(paramMap);
				int totOtMinute = 0;
				if(rMap != null && rMap.get("totOtMinute") != null && !rMap.get("totOtMinute").equals("")) {
					totOtMinute = Integer.parseInt(rMap.get("totOtMinute")+"");
				}
				Float f = (float) (totOtMinute / 60);
				if(f > 12) {
					Float ff = (f - f.intValue()) * 60;
					rp.setFail("연장근무 신청 가능 시간은 " + f.intValue() + "시간 " + ff.intValue() + "분 입니다.");
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
	
	//모바일용 동기처리
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception { 
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("신청되었습니다.");
		
		//신청서 최상위 테이블이다. 
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, null, paramMap.get("applCd").toString(), this.APPL_STATUS_APPLY_ING, sabun, userId);
		long applId = appl.getApplId();
		
		String ymd = paramMap.get("ymd").toString();
		String otSdate = paramMap.get("ymd").toString() + paramMap.get("shm").toString();
		String otEdate = paramMap.get("ymd").toString() + paramMap.get("ehm").toString();
		String reasonCd = paramMap.get("gubun").toString();
		String reason = paramMap.get("reason").toString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		WtmWorkCalendar calendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
		String subYn = "";
		if(paramMap.containsKey("subYn")) {
			subYn = paramMap.get("subYn")+"";
		}
		//근무제 신청서 테이블 조회
		WtmOtAppl otAppl = saveWtmOtAppl(tenantId, enterCd, applId, applId, otSdate, otEdate, calendar.getHolidayYn(), subYn,  reasonCd, reason, sabun, userId);
			
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
				resultMap.putAll(wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, pMap, userId));
				
				otSub.setSubsMinute(resultMap.get("calcMinute").toString());
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
}
