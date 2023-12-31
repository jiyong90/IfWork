package com.isu.ifw.service; 

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.*;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmFlexibleApplDetVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("wtmFlexibleApplService")
public class WtmFlexibleApplServiceImpl implements WtmApplService {

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmValidatorService validatorService;
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmFlexibleApplMapper flexApplMapper;
	
	@Autowired
	WtmFlexibleStdMapper flexStdMapper;

	@Autowired
	WtmFlexibleEmpService flexEmpService;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmFlexibleApplRepository wtmFlexibleApplRepo;
	
	@Autowired
	WtmFlexibleApplDetRepository wtmFlexibleApplDetRepo;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired
	WtmFlexibleDayPlanRepository wtmFlexibleDayPlanRepo;
	
	/**
	 * 속성값 조회
	 */
	@Autowired
	WtmPropertieRepository wtmPropertieRepo;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
	
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	
	@Autowired
	WtmOtApplMapper otApplMapper;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
	
	@Autowired
	WtmOtCanApplMapper otCanMapper;
	
	@Autowired
	WtmEntryApplRepository entryApplRepo;
	
	@Autowired
	WtmWorkPattDetRepository workPattDetRepo;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmFlexibleApplyDetRepository flexibleApplyDetRepo;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Autowired
	WtmInboxService inbox;
	
	@Autowired private WtmCalcService calcService;

	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;

	@Autowired private WtmWorkPattDetRepository workPattRepo;

	@Autowired private WtmDayMgrRepository dayMgrRepo;
	@Autowired private WtmHolidayMgrRepository holidayMgrRepo;
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		Map<String, Object> appl = flexApplMapper.findByApplId(applId);
		
		//탄근제의 경우 주별 근무시간을 보여준다.
		if(appl!=null && appl.get("applCd")!=null && "ELAS".equals(appl.get("applCd"))) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
			paramMap.put("key", "FLEXIBLE_APPL_ID");
			paramMap.put("value", Long.valueOf(appl.get("flexibleApplId").toString()));
			paramMap.put("totalYn", "Y");
			List<Map<String, Object>> totals = flexApplMapper.getElasApplDetail(paramMap);
			
			if(totals!=null && totals.size()>0) {
				/*ObjectMapper mapper = new ObjectMapper();
				try {
					System.out.println(mapper.writeValueAsString(totals));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} */
				
				paramMap.put("totalYn", "N");
				for(Map<String, Object> t : totals) {
					paramMap.put("symd", t.get("startYmd").toString());
					List<Map<String, Object>> details = flexApplMapper.getElasApplDetail(paramMap);
					t.put("details", details);
				}
				
				appl.put("elasDetails", totals);
			}
		}
		
		appl.put("applLine", applMapper.getWtmApplLineByApplId(applId));
		
		return appl;
	}
	
	@Override
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		
		return flexApplMapper.getLastAppl(paramMap);
	}
	
	@Transactional
	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String status, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		try {
			WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
			Long flexibleStdMgrId = Long.parseLong(paramMap.get("flexibleStdMgrId").toString());
			//신청서 최상위 테이블이다. 
			WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, status, sabun, userId);
			
			applId = appl.getApplId();
			
			applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, sabun, userId);
			
			String sYmd = paramMap.get("sYmd").toString();
			String eYmd = paramMap.get("eYmd").toString();
			
			//근무제 신청서 테이블 조회
			WtmFlexibleAppl flexibleAppl = saveWtmFlexibleAppl(tenantId, enterCd, applId, flexibleStdMgrId, sYmd, eYmd, "", sabun, userId);
			
			//탄근제의 경우 추가로 근무제 패턴을 flexibleApplDet 저장
			if(workTypeCd.equals("ELAS")) {
				List<WtmFlexibleApplDet> flexibleApplDet = saveWtmFlexibleApplDet(tenantId, enterCd, flexibleAppl.getFlexibleApplId(), flexibleStdMgrId, sYmd, eYmd, sabun, userId);
				//updateWtmFlexibleApplDet(flexibleApplDet, userId);
			}
				
			rp.put("applId", appl.getApplId());
			rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("저장 시 오류가 발생했습니다.");
		}
		
		return rp;
		
	}
	
	@Transactional
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
		
		
		Long flexibleStdMgrId = Long.parseLong(paramMap.get("flexibleStdMgrId").toString());
		String reason = paramMap.get("reason").toString();
		//신청서 최상위 테이블이다. 
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		applId = appl.getApplId();
		
		String sYmd = paramMap.get("sYmd").toString();
		String eYmd = paramMap.get("eYmd").toString();
		
		//근무제 신청서 테이블 조회
		saveWtmFlexibleAppl(tenantId, enterCd, applId, flexibleStdMgrId, sYmd, eYmd, reason, sabun, userId);
		
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, sabun, userId);
		
		paramMap.put("applId", applId);
		ReturnParam rp = validate(tenantId, enterCd, sabun, workTypeCd, paramMap);

		if(rp.getStatus().equals("FAIL")) {
			throw new RuntimeException(rp.get("message").toString());
		}
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprTypeCdAscApprSeqAsc(applId);
		List<String> apprSabun = new ArrayList();
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
		
		//inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 근무제신청", "", "Y");
		 
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", apprSabun);
		
		return rp;
	}

	protected WtmAppl saveWtmAppl(Long tenantId, String enterCd, Long applId, String workTypeCd, String applStatusCd, String sabun, String userId) {
		WtmAppl appl = null;
		if(applId != null && !applId.equals("")) {
			Optional<WtmAppl> oa = wtmApplRepo.findById(applId);
			if(oa != null)
				appl = oa.get();
			else
				appl = new WtmAppl();
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
	
	
	
	/**
	 * paramMap - apprOpinion 결재 시 의견(optional)
	 */
	@Transactional
	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();

		WtmFlexibleAppl flexibleAppl = wtmFlexibleApplRepo.findByApplId(applId);
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		
		rp = validatorService.checkDuplicateFlexibleWork(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd(), applId);
		//rp = checkRequestDate(applId);
		if(rp.getStatus().equals("FAIL")) {
			throw new Exception(rp.get("message")+"");
		}
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		
		String apprSabun = null;
		//마지막 결재자인지 확인하자
		boolean lastAppr = false;
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
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
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_APPR:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);

		appl = wtmApplRepo.save(appl);

		if(lastAppr) {
			//대상자의 실제 근무 정보를 반영한다.
			WtmFlexibleEmp emp = new WtmFlexibleEmp();
			
			//사전 체크에서 유연근무끼리의 중복은 이미 막힌다.
			//중복은 기본근무와의 중복이 있다.
			//유연근무제 기간과 중복되는 데이터를 찾아 시작일과 종료일을 갱신해주자 
			List<WtmFlexibleEmp> empList = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCd(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd(), "BASE");
			if(empList != null) {
				for(WtmFlexibleEmp e : empList) {
					//신청기간내에 시작 종료가 포함되어있을 경우
					if(Integer.parseInt(flexibleAppl.getSymd()) <= Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) >= Integer.parseInt(e.getEymd())) {
						wtmFlexibleEmpRepo.delete(e);
					//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
					}else if(Integer.parseInt(flexibleAppl.getSymd()) > Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) < Integer.parseInt(e.getEymd())) {
						String eymd = e.getEymd();
						
						e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getSymd(), ""), -1),null));
						wtmFlexibleEmpRepo.save(e);
						WtmFlexibleEmp newEmp = new WtmFlexibleEmp();
						newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
						newEmp.setTenantId(e.getTenantId());
						newEmp.setEnterCd(e.getEnterCd());
						newEmp.setSabun(e.getSabun());
						newEmp.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getEymd(), ""), 1),null));
						newEmp.setEymd(eymd);
						newEmp.setUpdateId(userId);
						newEmp.setWorkTypeCd(e.getWorkTypeCd());
						newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
						wtmFlexibleEmpRepo.save(newEmp);

					//시작일만 포함되어있을 경우 
					}else if(Integer.parseInt(flexibleAppl.getSymd()) >= Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) < Integer.parseInt(e.getEymd())) {
						//시작일을 신청종료일 다음날로 업데이트 해주자
						e.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getEymd(), ""), 1),null));
						wtmFlexibleEmpRepo.save(e);
					//종료일만 포함되어있을 경우
					}else if(Integer.parseInt(flexibleAppl.getSymd()) > Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) <= Integer.parseInt(e.getEymd())) {
						//종료일을 신청시작일 전날로 업데이트 해주자
						e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getSymd(), ""), -1),null));
						wtmFlexibleEmpRepo.save(e);
						
					}
						
				}
			}
			
			emp.setEnterCd(enterCd);
			emp.setTenantId(tenantId);
			emp.setFlexibleStdMgrId(flexibleAppl.getFlexibleStdMgrId());
			emp.setSabun(appl.getApplSabun());
			emp.setSymd(flexibleAppl.getSymd());
			emp.setEymd(flexibleAppl.getEymd());
			emp.setUpdateId(userId);
			emp.setWorkTypeCd(appl.getApplCd());
			
			emp = wtmFlexibleEmpRepo.save(emp);
			
			//유연근무 승인 시 해당 구간 내의 result는 지워야 한다. //리셋 프로시져에서 지우지 않음.  
			//result 에 base와 ot, fixot 있으면 삭제
			List<String> timeTypCds = new ArrayList<String>();
			timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
			timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
			timeTypCds.add(WtmApplService.TIME_TYPE_OT);
			timeTypCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
			
			List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, appl.getApplSabun(), timeTypCds, flexibleAppl.getSymd(), flexibleAppl.getEymd());
			if(results!=null && results.size()>0) {
				wtmWorkDayResultRepo.deleteAll(results);
				wtmWorkDayResultRepo.flush();
			}
			
			//승인완료 시 해당 대상자의 통계데이터를 갱신하기 위함.
			rp.put("sabun", emp.getSabun());
			rp.put("symd", emp.getSymd());
			rp.put("eymd", emp.getEymd());
			
			paramMap.put("userId", userId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("tenantId", tenantId);
			paramMap.put("sabun", appl.getApplSabun());
			
			/*List<WtmWorkDayResult> days = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, sabun, "BASE",flexibleAppl.getSymd(), flexibleAppl.getEymd() );
			
			//선근제면 초기화 하자
			if(stdMgr.getWorkTypeCd().startsWith("SELE")) {
				//기본근무  정보가 있었기 때문에 지워주자
				wtmWorkDayResultRepo.deleteAll(days);
			}*/
			
			WtmFlexibleStdMgr stdMgr = flexStdMgrRepo.findById(flexibleAppl.getFlexibleStdMgrId()).get();
			paramMap.put("flexibleEmpId", emp.getFlexibleEmpId());
			paramMap.put("flexibleStdMgrId", stdMgr.getFlexibleStdMgrId());
			
			paramMap.put("symd", emp.getSymd());
			paramMap.put("eymd", emp.getEymd());
			//근무제 기간의 총 기본근로 시간을 업데이트 한다.
			//20200102jyp P_WTM_WORK_CALENDAR_RESET procedure에서 한다. 
			//flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(paramMap);

			//탄근제의 경우 근무 계획까지 작성하여 신청을 하기 때문에
			//calendar, result 만들어준다.
			if(appl.getApplCd().equals("ELAS")) {
				//calendar 있으면 삭제하고 다시 만들어주자.
				/*List<WtmWorkCalendar> calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
				
				if(calendar!=null && calendar.size()>0) {
					workCalendarRepo.deleteAll(calendar);
					workCalendarRepo.flush();
				}
				wtmFlexibleEmpMapper.createWorkCalendarOfElas(flexibleAppl.getFlexibleApplId(), userId);*/
				
				//List<WtmWorkCalendar> calendar2 = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
				
				
				//result 만들어주자.
				List<WtmWorkDayResult> result = new ArrayList<WtmWorkDayResult>();
				Map<String, Object> pMap = new HashMap<String, Object>();
				pMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
				pMap.put("key", "FLEXIBLE_APPL_ID");
				pMap.put("value", flexibleAppl.getFlexibleApplId());
				List<Map<String, Object>> dets = wtmFlexibleEmpMapper.getElasWorkDayResult(pMap);
				if(dets!=null && dets.size()>0) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					
					//result 에 base와 ot 있으면 삭제하고 다시 만들어주자.
					/*List<WtmWorkDayResult> base = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), TIME_TYPE_BASE, flexibleAppl.getSymd(), flexibleAppl.getEymd());
					if(base!=null && base.size()>0) {
						wtmWorkDayResultRepo.deleteAll(base);
						wtmWorkDayResultRepo.flush();
					}
					
					List<WtmWorkDayResult> ot = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), TIME_TYPE_OT, flexibleAppl.getSymd(), flexibleAppl.getEymd());
					if(ot!=null && ot.size()>0) {
						wtmWorkDayResultRepo.deleteAll(ot);
						wtmWorkDayResultRepo.flush();
					}*/
					
					//탄근제 신청 시 입력한 계획을 옮기다
					for(Map<String, Object> det : dets) {
						Date s = null;
						Date e = null;
						
						WtmWorkDayResult r = new WtmWorkDayResult();
						r.setTenantId(tenantId);
						r.setEnterCd(enterCd);
						r.setYmd(det.get("ymd").toString());
						r.setSabun(appl.getApplSabun());
						r.setApplId(applId);
						r.setTimeTypeCd(det.get("timeTypeCd").toString());
						r.setTaaCd(null);
						
						if(det.get("planSdate")!=null && !"".equals(det.get("planSdate"))) {
							s = sdf.parse(det.get("planSdate").toString());
							r.setPlanSdate(s);
						}
						
						if(det.get("planEdate")!=null && !"".equals(det.get("planEdate"))) {
							e = sdf.parse(det.get("planEdate").toString());
							r.setPlanEdate(e);
						}
						
						if(det.get("planMinute")!=null && !"".equals(det.get("planMinute"))) {
							r.setPlanMinute(Integer.parseInt(det.get("planMinute").toString()));
						}
						
						r.setUpdateDate(new Date());
						r.setUpdateId(userId);
						
						result.add(r);
					}
					
					if(result.size()>0)
						wtmWorkDayResultRepo.saveAll(result);
				}
				
			}



			String symd = emp.getSymd().substring(0, 4)+"0101";
			String eymd = emp.getEymd().substring(0, 4)+"1231";

			calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, symd, eymd);
			
		}
		
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.add(appl.getApplSabun());
			//inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "유연근무제 신청서가  승인되었습니다.", "N");
		
			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			//inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 유연근무제신청", "", "N");
		
			rp.put("msgType", "APPR");
		}
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", pushSabun);
		
		return rp;
		
	}
	
	 
	
	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId)  throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	@Transactional
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Long applId = null;
		if(paramMap != null && paramMap.containsKey("applId") && !paramMap.equals("")) {
			applId = Long.parseLong(paramMap.get("applId").toString());
		}
		//신청 시 날짜 중복되지 않도록 체크 한다.
		//rp = checkRequestDate(applId);

		String sYmd = paramMap.get("sYmd").toString();
		String eYmd = paramMap.get("eYmd").toString();
		
		rp = validatorService.checkDuplicateFlexibleWork(tenantId, enterCd, sabun, sYmd, eYmd, applId);
		
		if(rp.getStatus().equals("FAIL")) {
			return rp;
		}
		
		//근무 상세에 대한 기본근로시간 체크 (탄근제)
		//근무제로 판단하지 않고 신청 시 신청에 딸린 계획데이터가 있을경우 체크하즈아.
		WtmPropertie propertie = null;
		String defultWorktime = "8";
		String max2weekWithin = "48";
		String max2weekMorethen = "52";
		String maxAdd = "0";
		propertie = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_DEFAULT_WORKTIME");
		if(propertie != null)
			defultWorktime = propertie.getInfoValue();
		
		propertie = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_MAX_WORKTIME_2WEEK_WITHIN");
		if(propertie != null)
			max2weekWithin = propertie.getInfoValue();
		
		propertie = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_MAX_WORKTIME_2WEEK_MORETHEN");
		if(propertie != null)
			max2weekMorethen = propertie.getInfoValue();
		
		propertie = wtmPropertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_MAX_WORKTIME_ADD");
		if(propertie != null)
			maxAdd = propertie.getInfoValue();
		
		/**
		 * 선근제도 DET 없지만 있으면 탄근제 처럼 만들어주면 될것 같다. 
		 */
		if(workTypeCd.startsWith("SELE")) { // || workTypeCd.equals("ELAS")) {
			//선근제
			
			Map<String, Object> pMap = new HashMap<String, Object>();
			int day = 0;
			if(paramMap != null && paramMap.containsKey("adminYn") && "Y".equals(paramMap.get("adminYn"))) {
				if(paramMap != null && paramMap.containsKey("flexibleApplyId") && !"".equals(paramMap.get("flexibleApplyId"))) {
					Long flexibleApplyId = Long.parseLong(paramMap.get("flexibleApplyId").toString());
					
					pMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
					pMap.put("key", "FLEXIBLE_APPLY_ID");
					pMap.put("value", flexibleApplyId);
					
					List<WtmFlexibleApplyDet> days = flexibleApplyDetRepo.findByFlexibleApplyId(flexibleApplyId);
					day = days.size();
				}
			} else {
				if(paramMap != null && paramMap.containsKey("flexibleApplId") && !"".equals(paramMap.get("flexibleApplId"))) {
					Long flexibleApplId = Long.parseLong(paramMap.get("flexibleApplId").toString());
					
					pMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
					pMap.put("key", "FLEXIBLE_APPL_ID");
					pMap.put("value", flexibleApplId);
					
					List<WtmFlexibleApplDet> days = wtmFlexibleApplDetRepo.findByFlexibleApplId(flexibleApplId);
					day = days.size();
				}
			}
			
			if(day!=0) {
				int sumWorkMinute = 0;
				List<Map<String, Object>> weekList = wtmFlexibleEmpMapper.getElasWeekHour(pMap);
				
				if(weekList!=null && weekList.size()>0) {
					for(Map<String, Object> w : weekList) {
						int workMinute = 0;
						if(w.get("workMinute")!=null && !"".equals(w.get("workMinute"))) {
							workMinute = Integer.parseInt(w.get("workMinute").toString());
							sumWorkMinute += workMinute;
						}
					}
					
					logger.debug("sumWorkMinute : "+ sumWorkMinute);
					logger.debug("day : "+ day);
					if(sumWorkMinute>0 && sumWorkMinute>(day*8*60)) {
						rp.setFail("기본 근무는 평균 40시간을 초과할 수 없습니다.");
						return rp;
					} 
				}
			}
			
		}else if(workTypeCd.equals("ELAS")) {
			//탄근제
			//근로시간은 평균 40 시간, OT시간은 주 12시간 초과 시 신청할 수 없고
			//2주 이내 탄근제는 주간 최대 근무시간은 48시간, 2주 이상 탄근제는 주간 최대 근무시간 52시간 
			Map<String, Object> pMap = new HashMap<String, Object>();
			int day = 0;
			if(paramMap != null && paramMap.containsKey("adminYn") && "Y".equals(paramMap.get("adminYn"))) {
				if(paramMap != null && paramMap.containsKey("flexibleApplyId") && !"".equals(paramMap.get("flexibleApplyId"))) {
					Long flexibleApplyId = Long.parseLong(paramMap.get("flexibleApplyId").toString());
					
					pMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
					pMap.put("key", "FLEXIBLE_APPLY_ID");
					pMap.put("value", flexibleApplyId);
					
					List<WtmFlexibleApplyDet> days = flexibleApplyDetRepo.findByFlexibleApplyId(flexibleApplyId);
					day = days.size();
				}
			} else {
				if(paramMap != null && paramMap.containsKey("flexibleApplId") && !"".equals(paramMap.get("flexibleApplId"))) {
					Long flexibleApplId = Long.parseLong(paramMap.get("flexibleApplId").toString());
					
					pMap.put("tableName", "WTM_FLEXIBLE_APPL_DET");
					pMap.put("key", "FLEXIBLE_APPL_ID");
					pMap.put("value", flexibleApplId);
					
					List<WtmFlexibleApplDet> days = wtmFlexibleApplDetRepo.findByFlexibleApplId(flexibleApplId);
					day = days.size();
				}
			}
			
			if(day!=0) {
				int sumWorkMinute = 0;
				List<Map<String, Object>> weekList = wtmFlexibleEmpMapper.getElasWeekHour(pMap);
				
				if(weekList!=null && weekList.size()>0) {
					for(Map<String, Object> w : weekList) {
						int workMinute = 0;
						if(w.get("workMinute")!=null && !"".equals(w.get("workMinute"))) {
							workMinute = Integer.parseInt(w.get("workMinute").toString());
							sumWorkMinute += workMinute;
							/**
							 * 수정
							 */
							if(workTypeCd.equals("ELAS")) {
								if(day>14 && ((float)workMinute/60) > Integer.parseInt(max2weekMorethen)) {
									rp.setFail("2주 이상 탄근제는 주간 최대 "+max2weekMorethen+"시간을 초과할 수 없습니다.");
									return rp;
								}
								if(day<=14 && ((float)workMinute/60) > Integer.parseInt(max2weekWithin)) {
									rp.setFail("2주 이내 탄근제는 주간 최대 "+max2weekWithin+"시간을 초과할 수 없습니다.");
									return rp;
								}
							}
							
						}
						
						int otMinute = 0;
						if(w.get("otMinute")!=null && !"".equals(w.get("otMinute"))) 
							otMinute = Integer.parseInt(w.get("otMinute").toString());
						
						if(((float)otMinute/60) > 12) {
							rp.setFail("연장 근무는 주 12시간을 초과할 수 없습니다.");
							return rp;
						}
					}
					
					logger.debug("sumWorkMinute : "+ sumWorkMinute);
					logger.debug("day : "+ day);
					if(sumWorkMinute>0 && ((float)sumWorkMinute/day*7)/60>40) {
						rp.setFail("기본 근무는 평균 40시간을 초과할 수 없습니다.");
						return rp;
					} 
				}
			}
			
		}else if(workTypeCd.equals("DIFF")) {
			//시차
			
		}else {
			rp.setFail("");
		}
		//기본근로시간 체크
		//2주이내 48체크	
		//2주이상 52시간 체크
			
		
		return rp;
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub
		
	}

	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}

	protected WtmFlexibleAppl saveWtmFlexibleAppl(Long tenantId, String enterCd, Long applId, Long flexibleStdMgrId, String sYmd, String eYmd, String reason, String sabun, String userId) {
		 
		WtmFlexibleAppl flexibleAppl = wtmFlexibleApplRepo.findByApplId(applId);
		if(flexibleAppl == null) {
			flexibleAppl = new WtmFlexibleAppl();
		}
		flexibleAppl.setFlexibleStdMgrId(flexibleStdMgrId);
		flexibleAppl.setApplId(applId);
		String ym = sYmd.substring(0, 4);
		flexibleAppl.setYm(ym);
		flexibleAppl.setSymd(sYmd);
		flexibleAppl.setEymd(eYmd);
		flexibleAppl.setReason(reason);
		flexibleAppl.setUpdateId(userId);
		//flexibleAppl.setSabun(sabun);
		flexibleAppl.setWorkDay("0");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		paramMap.put("flexibleStdMgrId", flexibleStdMgrId);
		if(sYmd != null && !sYmd.equals("") && eYmd != null && !eYmd.equals("")) {
			Map<String, Object> m = applMapper.calcWorkDay(paramMap);
			if(m != null) {
				flexibleAppl.setWorkDay(m.get("workCnt").toString());
			}
		}
		
		return wtmFlexibleApplRepo.save(flexibleAppl);
	}
	 
	protected List<WtmFlexibleApplDet> saveWtmFlexibleApplDet(Long tenantId, String enterCd, Long flexibleApplId, Long flexibleStdMgrId, String sYmd, String eYmd, String sabun, String userId) {
		
		WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findById(flexibleStdMgrId).get();
		
		// 공휴일 제외 여부
		String holExceptYn = "N";
		if(flexibleStdMgr!=null && flexibleStdMgr.getHolExceptYn()!=null && !"".equals(flexibleStdMgr.getHolExceptYn())) 
			holExceptYn = flexibleStdMgr.getHolExceptYn();

		// 근무제 패턴으로 정해놓은 일 수  
		int maxPattDet = 0;
		WtmWorkPattDet workPattDet = workPattDetRepo.findTopByFlexibleStdMgrIdOrderBySeqDesc(flexibleStdMgrId);
		if(workPattDet!=null && workPattDet.getSeq()!=null) 
			maxPattDet = workPattDet.getSeq();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		paramMap.put("flexibleStdMgrId", flexibleStdMgrId);
		paramMap.put("holExceptYn", holExceptYn);
		paramMap.put("maxPattDet", maxPattDet);
		
		List<WtmFlexibleApplDet> workList = new ArrayList<WtmFlexibleApplDet>();
		List<WtmFlexibleApplDetVO> patterns = flexApplMapper.getWorkPattern(paramMap);

		Map<Long, WtmTimeCdMgr> timeMap = new HashMap<Long, WtmTimeCdMgr>();

		if(patterns!=null && patterns.size()>0) {
			
			System.out.println("sYmd ::: " + sYmd);
			System.out.println("eYmd ::: " + eYmd);
			
			//지정한 날짜 이외의 데이터는 삭제
			wtmFlexibleApplDetRepo.deleteByFlexibleApplIdAndYmdNotBetween(flexibleApplId, sYmd, eYmd);
			
			for(WtmFlexibleApplDetVO p : patterns) {
				WtmFlexibleApplDet fd = wtmFlexibleApplDetRepo.findByFlexibleApplIdAndYmd(flexibleApplId, p.getYmd());
				
				if(fd==null) {
					fd = new WtmFlexibleApplDet();
				}
				
				fd.setFlexibleApplId(flexibleApplId);
				fd.setYmd(p.getYmd());
				fd.setTimeCdMgrId(p.getTimeCdMgrId());
				fd.setHolidayYn(p.getHolidayYn());


				WtmTimeCdMgr timeCdMgr = null;

				if(timeMap.containsKey(p.getTimeCdMgrId())) {
					timeCdMgr = timeMap.get(p.getTimeCdMgrId());
				}else {
					timeCdMgr = timeCdMgrRepo.findById(p.getTimeCdMgrId()).get();
					timeMap.put(timeCdMgr.getTimeCdMgrId(), timeCdMgr);
				}

				if(timeCdMgr.getWorkShm() != null && timeCdMgr.getWorkEhm() != null && !"".equals(timeCdMgr.getWorkShm()) && !"".equals(timeCdMgr.getWorkEhm()) && !"Y".equals(timeCdMgr.getHolYn())) {
					logger.debug("timeCdMgr is not null ");
					String shm = timeCdMgr.getWorkShm();
					String ehm = timeCdMgr.getWorkEhm();
					String d = p.getYmd();
					//String sYmd = calendar.getYmd();
					//String eYmd = calendar.getYmd();
					Date sd = null, ed = null;
					SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
					SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
					Calendar cal = Calendar.getInstance();

					try {
						sd = ymdhm.parse(d+shm);
						ed = ymdhm.parse(d+ehm);
						//종료시분이 시작시분보다 작으면 기준일을 다음날로 본다.
						if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
							cal.setTime(ed);
							cal.add(Calendar.DATE, 1);
							ed = cal.getTime();
							//eYmd = ymd.format(cal.getTime());
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					fd.setPlanSdate(sd);
					fd.setPlanEdate(ed);

					Map<String, Object> resMap = calcService.calcApprMinute(sd, ed, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
					if(resMap.containsKey("apprMinute")) {
						fd.setPlanMinute(Integer.parseInt(resMap.get("apprMinute")+""));
						//breakMinute = Integer.parseInt(resMap.get("breakMinute")+"");
					}

					/*
					 * timeCdMgr otbMinute 조출 이 있을 경우 생성해준다.
					 */
					if(timeCdMgr.getOtbMinute() != null && !timeCdMgr.getOtbMinute().equals("")) {
						Date earlyOtSdate = calcService.F_WTM_DATE_ADD(sd, timeCdMgr.getOtbMinute() * -1, timeCdMgr, flexibleStdMgr.getUnitMinute());
						fd.setOtbSdate(earlyOtSdate);
						fd.setOtbEdate(sd);
						//int breakMinute = 0;
						Map<String, Object> resOtMap = calcService.calcApprMinute(earlyOtSdate, sd, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						if(resOtMap.containsKey("apprMinute")) {
							fd.setOtbMinute(Integer.parseInt(resOtMap.get("apprMinute")+""));
							//breakMinute = Integer.parseInt(resMap.get("breakMinute")+"");
						}
					}
					/*
					 * timeCdMgr otAMinute 잔업 이 있을 경우 생성해준다.
					 */
					if(timeCdMgr.getOtaMinute() != null && !timeCdMgr.getOtaMinute().equals("")) {
						Date otEdate = calcService.F_WTM_DATE_ADD(ed, timeCdMgr.getOtaMinute(), timeCdMgr, flexibleStdMgr.getUnitMinute());

						fd.setOtaSdate(ed);
						fd.setOtaEdate(otEdate);
						//int breakMinute = 0;
						Map<String, Object> resOtMap = calcService.calcApprMinute(ed, otEdate, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
						if(resOtMap.containsKey("apprMinute")) {
							fd.setOtaMinute(Integer.parseInt(resOtMap.get("apprMinute")+""));
							//breakMinute = Integer.parseInt(resMap.get("breakMinute")+"");
						}
					}


					fd.setUpdateDate(new Date());
					fd.setUpdateId(userId);

					workList.add(fd);

				} else {
					fd.setUpdateDate(new Date());
					fd.setUpdateId(userId);

					workList.add(fd);
				}

//				//임시저장된 근무계획의 timeCdMgrId와 패턴의 timeCdMgrId이 다를 경우엔
//				//계획된 시간은 두고 패턴의 timeCdMgrId를 보도록 함.
//				Date planSdate = null;
//				if(fd.getPlanSdate()==null && p.getPlanSdate()!=null && !"".equals(p.getPlanSdate())) {
//					planSdate = WtmUtil.toDate(p.getPlanSdate(), "yyyyMMddHHmm");
//					fd.setPlanSdate(planSdate);
//				}
//
//				Date planEdate = null;
//				if(fd.getPlanEdate()==null && p.getPlanEdate()!=null && !"".equals(p.getPlanEdate())) {
//					planEdate = WtmUtil.toDate(p.getPlanEdate(), "yyyyMMddHHmm");
//					fd.setPlanEdate(planEdate);
//				}
//
//				if((fd.getOtbMinute()==null || fd.getOtbMinute()==0) && p.getOtbMinute()!=0)
//					fd.setOtbMinute(p.getOtbMinute());
//
//				if((fd.getOtaMinute()==null || fd.getOtaMinute()==0) && p.getOtaMinute()!=0)
//					fd.setOtaMinute(p.getOtaMinute());
//
//				fd.setUpdateDate(new Date());
//				fd.setUpdateId(userId);
//				workList.add(fd);
			}
			
			wtmFlexibleApplDetRepo.saveAll(workList);
		}
		
		return workList;
	}
	
	protected void updateWtmFlexibleApplDet(List<WtmFlexibleApplDet> applDets, String userId) {
		
		if(applDets!=null && applDets.size()>0) {
			for(WtmFlexibleApplDet d : applDets) {
				Date planSdate = d.getPlanSdate();
				Date planEdate = d.getPlanEdate();
				
				if(planSdate!=null && planEdate!=null) {
					String pSdate = WtmUtil.parseDateStr(planSdate, "yyyyMMddHHmm");
					String pEdate = WtmUtil.parseDateStr(planEdate, "yyyyMMddHHmm");
					
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("ymd", d.getYmd());
					
					paramMap.put("shm", WtmUtil.parseDateStr(planSdate, "HHmm"));
					paramMap.put("ehm", WtmUtil.parseDateStr(planEdate, "HHmm"));
					Map<String, Object> planMinuteMap = flexibleEmpService.calcMinuteExceptBreaktimeForElas(false, d.getFlexibleApplId(), paramMap, userId);
					d.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					
					if(d.getOtbSdate()==null && d.getOtbEdate()==null && d.getOtbMinute()!=null && d.getOtbMinute()!=0) {
						Map<String, Object> otbMinuteMap = flexibleEmpService.calcOtMinuteExceptBreaktimeForElas(false, d.getFlexibleApplId(), d.getYmd(), pSdate, pEdate, "OTB", d.getOtbMinute(), userId);
						
						if(otbMinuteMap!=null) {
							Date otbSdate = WtmUtil.toDate(otbMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
							Date otbEdate = WtmUtil.toDate(otbMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
							
							d.setOtbSdate(otbSdate);
							d.setOtbEdate(otbEdate);
							d.setOtbMinute(Integer.parseInt(otbMinuteMap.get("calcMinute").toString()));
						}	
					}
					
					if(d.getOtaSdate()==null && d.getOtaEdate()==null && d.getOtaMinute()!=null && d.getOtaMinute()!=0) {
						Map<String, Object> otaMinuteMap = flexibleEmpService.calcOtMinuteExceptBreaktimeForElas(false, d.getFlexibleApplId(), d.getYmd(), pSdate, pEdate, "OTA", d.getOtaMinute(), userId);
						
						if(otaMinuteMap!=null) {
							Date otaSdate = WtmUtil.toDate(otaMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
							Date otaEdate = WtmUtil.toDate(otaMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
							
							d.setOtaSdate(otaSdate);
							d.setOtaEdate(otaEdate);
							d.setOtaMinute(Integer.parseInt(otaMinuteMap.get("calcMinute").toString()));
						}
					}
						
				}
				
			}
			
			wtmFlexibleApplDetRepo.saveAll(applDets);
		}
		
	}

	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
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
	public void delete(Long applId) {
		wtmFlexibleEmpMapper.deleteByApplId(applId);
		wtmFlexibleApplRepo.deleteByApplId(applId);
		wtmApplLineRepo.deleteByApplId(applId);
		wtmApplRepo.deleteById(applId);
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun,
			String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprovalApplList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		return null;
	}


	public void createWtmFlexibleApplDetAsPattern(Long tenantId, String enterCd, WtmFlexibleAppl flexibleAppl) {
		logger.debug("call createWtmFlexibleApplDetAsPattern ::");
		if(flexibleAppl == null)
			return;
		
		List<WtmFlexibleApplDet> ds = wtmFlexibleApplDetRepo.findByFlexibleApplId(flexibleAppl.getFlexibleApplId());

		logger.debug("ds size : " + ds.size());
		if(ds != null && ds.size() > 0) {
			wtmFlexibleApplDetRepo.deleteAll(ds);
			//logger.debug("return");
			//return;
		}
		logger.debug("????");
		
		Long flexibleStdMgrId = flexibleAppl.getFlexibleStdMgrId();
		logger.debug("flexibleStdMgrId :: " + flexibleStdMgrId);
		List<WtmWorkPattDet> pattDets = workPattRepo.findByFlexibleStdMgrId(flexibleStdMgrId);
		int pattSize = 0;
		if(pattDets != null)
			pattSize = pattDets.size();
		
		logger.debug("pattSize :: " + pattSize);
		
		// 패턴이 없으면 생성하지 않는다. 
		if(pattSize > 0) {
			WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(flexibleStdMgrId);
			//근무제도 시작일 부터 신청일의 시작일의 일수를 구한다. 근무제도 패턴 기준은 근무제도의 생성일 부터이다. 
			
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
			
			Date stdMgrSdate = null, applSDate = null, applEDate = null;
			try {
				stdMgrSdate = ymd.parse(flexibleStdMgr.getUseSymd());
				applSDate = ymd.parse(flexibleAppl.getSymd());
				applEDate = ymd.parse(flexibleAppl.getEymd());
					 
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long diff = applSDate.getTime() - stdMgrSdate.getTime();
			long days = diff / (24*60*60*1000) + 1;
			
			logger.debug("days : " + days);
			
			Integer startPattSeq = (int) (days % pattSize);
			
			Map<Integer, WtmWorkPattDet> pattDetMap = new HashMap<>();
			Map<Integer, WtmTimeCdMgr> timeCdMgrMap = new HashMap<>();
			for(WtmWorkPattDet pattDet : pattDets) {
				pattDetMap.put(pattDet.getSeq(), pattDet);
				WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(pattDet.getTimeCdMgrId()).get();
				timeCdMgrMap.put(pattDet.getSeq(), timeCdMgr);
			}
			
			List<WtmDayMgr> dayMgrs = dayMgrRepo.findBySunYmdBetween(flexibleAppl.getSymd(), flexibleAppl.getEymd());
			List<WtmHolidayMgr> hols = holidayMgrRepo.findByTenantIdAndEnterCdAndHolidayYmdBetween(tenantId, enterCd, flexibleAppl.getSymd(), flexibleAppl.getEymd());
			
			List<String> holList = new ArrayList<String>();
			if(dayMgrs != null && dayMgrs.size() > 0) {
				for(WtmDayMgr d : dayMgrs) {
					if(!"".equals(d.getHolidayYn()) && "Y".equals(d.getHolidayYn())) {
						holList.add(d.getSunYmd());
					}
				}
			}
			if(hols != null && hols.size() > 0) {
				for(WtmHolidayMgr d : hols) {
					holList.add(d.getId().getHolidayYmd());
				}
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(applSDate);
			
			Date chkDate = applSDate;
			List<WtmFlexibleApplDet> saveDetList = new ArrayList<>();
			logger.debug("chkDate.compareTo(applEDate) : " + chkDate.compareTo(applEDate));
			ObjectMapper mapper = new ObjectMapper();
			if(startPattSeq == 0) {
				startPattSeq = pattSize;
			}
			
			while(chkDate.compareTo(applEDate) <= 0) {
				logger.debug("chkDate : " + chkDate);
				logger.debug("applEDate : " + applEDate);
				cal.setTime(chkDate);
				logger.debug("flexibleStdMgr.getHolExceptYn() :" + flexibleStdMgr.getHolExceptYn());
				
				logger.debug("startPattSeq : " + startPattSeq);
				try {
					logger.debug("pattDetMap : " + mapper.writeValueAsString(pattDetMap));
					logger.debug("timeCdMgrMap : " + mapper.writeValueAsString(timeCdMgrMap));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				WtmWorkPattDet det = pattDetMap.get(startPattSeq);
				WtmTimeCdMgr timeCdMgr = timeCdMgrMap.get(startPattSeq); 
				startPattSeq++;
				if(startPattSeq > pattSize) {
					startPattSeq = 1;
				}
				
				try {
					logger.debug("flexibleStdMgr : " + mapper.writeValueAsString(flexibleStdMgr));
					logger.debug("det : " + mapper.writeValueAsString(det));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String sYmd = ymd.format(chkDate);
				
				if("Y".equals(flexibleStdMgr.getHolExceptYn()) && holList.indexOf(sYmd) > -1 ) {
					cal.add(Calendar.DATE, 1);
					//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
					chkDate = cal.getTime();
				}else {
					//휴일이면
					if(!"".equals(timeCdMgr.getHolYn()) &&  "Y".equals(timeCdMgr.getHolYn())) {
						logger.debug("timeCdMgr is null ");
						cal.add(Calendar.DATE, 1);
						//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
						chkDate = cal.getTime();
					}else {
						if(timeCdMgr.getWorkShm() != null && timeCdMgr.getWorkEhm() != null
								&& !"".equals(timeCdMgr.getWorkShm()) && !"".equals(timeCdMgr.getWorkEhm())) {
							logger.debug("timeCdMgr is not null ");
							String shm = timeCdMgr.getWorkShm();
							String ehm = timeCdMgr.getWorkEhm();
							
							String eYmd = sYmd;
							Date sd = null, ed = null;
							try {
								sd = ymdhm.parse(sYmd+shm);
								//종료시분이 시작시분보다 작으면 기준일을 다음날로 본다. 
								if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
									cal.add(Calendar.DATE, 1);
									eYmd = ymd.format(cal.getTime());
									//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
									chkDate = cal.getTime();
								}else {
									cal.add(Calendar.DATE, 1);
									//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
									chkDate = cal.getTime();
								}
								ed = ymdhm.parse(eYmd+ehm);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							WtmFlexibleApplDet applDet = new WtmFlexibleApplDet();
							
							applDet.setFlexibleApplId(flexibleAppl.getFlexibleApplId());
							applDet.setTimeCdMgrId(det.getTimeCdMgrId());
							applDet.setHolidayYn(det.getHolidayYn());
							applDet.setYmd(sYmd);
							applDet.setPlanSdate(sd);
							applDet.setPlanEdate(ed);
							
							Map<String, Object> resMap = calcService.calcApprMinute(sd, ed, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							if(resMap.containsKey("apprMinute")) {
								applDet.setPlanMinute(Integer.parseInt(resMap.get("apprMinute")+""));
							}
							
							saveDetList.add(applDet);
						}else {
							logger.debug("timeCdMgr is null ");
							cal.add(Calendar.DATE, 1);
							//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
							chkDate = cal.getTime();
						}
					}
					
				}
			}
			
			logger.debug("saveDetList.size( : " + saveDetList.size());
			wtmFlexibleApplDetRepo.saveAll(saveDetList);
			
		}		
		
		
	}

}
