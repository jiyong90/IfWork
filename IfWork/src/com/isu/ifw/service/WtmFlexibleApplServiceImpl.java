package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmFlexibleAppl;
import com.isu.ifw.entity.WtmFlexibleApplDet;
import com.isu.ifw.entity.WtmFlexibleDayPlan;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkPattDet;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmOtCanApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEntryApplRepository;
import com.isu.ifw.repository.WtmFlexibleApplDetRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmFlexibleDayPlanRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmApplLineVO;
import com.isu.ifw.vo.WtmFlexibleApplDetVO;
import com.isu.option.vo.ReturnParam;

@Service("wtmFlexibleApplService")
public class WtmFlexibleApplServiceImpl implements WtmApplService {

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
	
	@Override
	public Map<String, Object> getAppl(Long applId) {
		Map<String, Object> appl = flexApplMapper.findByApplId(applId);
		appl.put("applLine", applMapper.getWtmApplLineByApplId(applId));
		return appl;
	}
	
	@Override
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		return applMapper.getWtmApplLine(paramMap);
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
			
			String sYmd = paramMap.get("sYmd").toString();
			String eYmd = paramMap.get("eYmd").toString();
			
			//근무제 신청서 테이블 조회
			WtmFlexibleAppl flexibleAppl = saveWtmFlexibleAppl(tenantId, enterCd, applId, flexibleStdMgrId, sYmd, eYmd, "", sabun, userId);
			
			//탄근제의 경우 추가로 근무제 패턴을 flexibleApplDet 저장
			if(workTypeCd.equals("ELAS")) {
				List<WtmFlexibleApplDet> flexibleApplDet = saveWtmFlexibleApplDet(tenantId, enterCd, flexibleAppl.getFlexibleApplId(), flexibleStdMgrId, sYmd, eYmd, sabun, userId);
			}
				
			saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, sabun, userId);
		
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
	public void request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
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
		
		saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, sabun, userId);
		
		paramMap.put("applId", applId);
		ReturnParam rp = validate(tenantId, enterCd, sabun, workTypeCd, paramMap);

		if(rp.getStatus().equals("FAIL")) {
			throw new RuntimeException(rp.get("message").toString());
		}
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);

		 
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				//첫번째 결재자의 상태만 변경 후 스탑
				line.setApprStatusCd(APPR_STATUS_REQUEST);
				line = wtmApplLineRepo.save(line);
				break;
				 
			}
		}
		
		
		 
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
	
	
	
	/**
	 * paramMap - apprOpinion 결재 시 의견(optional)
	 */
	@Transactional
	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();

		WtmFlexibleAppl flexibleAppl = wtmFlexibleApplRepo.findByApplId(applId);
		rp = validatorService.checkDuplicateFlexibleWork(tenantId, enterCd, sabun, flexibleAppl.getSymd(), flexibleAppl.getEymd(), applId);
		//rp = checkRequestDate(applId);
		if(rp.getStatus().equals("FAIL")) {
			throw new Exception(rp.get("message")+"");
		}
		
		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		//마지막 결재자인지 확인하자
		boolean lastAppr = false;
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() == apprSeq && line.getApprSabun().equals(sabun)) {
					line.setApprStatusCd(APPR_STATUS_APPLY);
					line.setApprDate(WtmUtil.parseDateStr(new Date(), null));
					//결재의견
					if(paramMap != null && paramMap.containsKey("apprOpinion")) {
						line.setApprOpinion(paramMap.get("apprOpinion").toString());
						line.setUpdateId(userId);
					}
					line = wtmApplLineRepo.save(line);
					lastAppr = true;
				}else {
					if(lastAppr) {
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
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
			//대상자의 실제 근무 정보를 반영한다.
			WtmFlexibleEmp emp = new WtmFlexibleEmp();
			
			//사전 체크에서 유연근무끼리의 중복은 이미 막힌다.
			//중복은 기본근무와의 중복이 있다.
			//유연근무제 기간과 중복되는 데이터를 찾아 시작일과 종료일을 갱신해주자 
			List<WtmFlexibleEmp> empList = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
			if(empList != null) {
				for(WtmFlexibleEmp e : empList) {
					//신청기간내에 시작 종료가 포함되어있을 경우
					if(Integer.parseInt(flexibleAppl.getSymd()) <= Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) >= Integer.parseInt(e.getEymd())) {
						wtmFlexibleEmpRepo.delete(e);
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
						
					//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
					}else if(Integer.parseInt(flexibleAppl.getSymd()) > Integer.parseInt(e.getSymd()) && Integer.parseInt(flexibleAppl.getEymd()) < Integer.parseInt(e.getEymd())) {
						String eymd = e.getEymd();
						
						e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getSymd(), ""), -1),null));
						wtmFlexibleEmpRepo.save(e);
						WtmFlexibleEmp newEmp = new WtmFlexibleEmp();
						newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
						newEmp.setTenantId(e.getTenantId());
						newEmp.setEnterCd(e.getEnterCd());
						newEmp.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(flexibleAppl.getEymd(), ""), 1),null));
						newEmp.setEymd(eymd);
						newEmp.setUpdateId(userId);
						wtmFlexibleEmpRepo.save(newEmp);
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
			//근무제 기간의 총 소정근로 시간을 업데이트 한다.
			flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(paramMap);

			//탄근제의 경우 근무 계획까지 작성하여 신청을 하기 때문에
			//calendar, result 만들어준다.
			if(appl.getApplCd().equals("ELAS")) {
				//calendar 있으면 삭제하고 다시 만들어주자.
				List<WtmWorkCalendar> calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
				
				if(calendar!=null && calendar.size()>0) {
					workCalendarRepo.deleteAll(calendar);
					workCalendarRepo.flush();
				}
				
				//List<WtmWorkCalendar> calendar2 = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
				
				wtmFlexibleEmpMapper.createWorkCalendarOfElas(flexibleAppl.getFlexibleApplId(), userId);
				
				//result 만들어주자.
				List<WtmWorkDayResult> result = new ArrayList<WtmWorkDayResult>();
				List<Map<String, Object>> dets = wtmFlexibleEmpMapper.getElasWorkDayResult(flexibleAppl.getFlexibleApplId());
				if(dets!=null && dets.size()>0) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					
					//result 에 base와 ot 있으면 삭제하고 다시 만들어주자.
					List<WtmWorkDayResult> base = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), TIME_TYPE_BASE, flexibleAppl.getSymd(), flexibleAppl.getEymd());
					if(base!=null && base.size()>0) {
						wtmWorkDayResultRepo.deleteAll(base);
						wtmWorkDayResultRepo.flush();
					}
					
					List<WtmWorkDayResult> ot = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), TIME_TYPE_OT, flexibleAppl.getSymd(), flexibleAppl.getEymd());
					if(ot!=null && ot.size()>0) {
						wtmWorkDayResultRepo.deleteAll(ot);
						wtmWorkDayResultRepo.flush();
					}
					
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
			
		}
		
		return rp;
		
	}
	
	 
	
	@Override
	public void reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId)  throws Exception {
		// TODO Auto-generated method stub
	}

	
	@Override
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
		Long flexibleApplId = null;
		if(paramMap != null && paramMap.containsKey("flexibleApplId") && !paramMap.equals("")) {
			flexibleApplId = Long.parseLong(paramMap.get("flexibleApplId").toString());
		}
		List<WtmFlexibleApplDet> days = wtmFlexibleApplDetRepo.findByFlexibleApplId(flexibleApplId);
		//근무 상세에 대한 소정근로시간 체크 (탄근제)
		//근무제로 판단하지 않고 신청 시 신청에 딸린 계획데이터가 있을경우 체크하즈아.
		if(days != null && days.size() > 0) {
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
			
			if(workTypeCd.startsWith("SELE")) {
				//선근제
				
			}else if(workTypeCd.equals("ELAS")) {
				//탄근제
				//근로시간은 평균 40 시간, OT시간은 주 12시간 초과 시 신청할 수 없고
				//2주 이내 탄근제는 주간 최대 근무시간은 48시간, 2주 이상 탄근제는 주간 최대 근무시간 52시간 
				int sumWorkMinute = 0;
				List<Map<String, Object>> weekList = wtmFlexibleEmpMapper.getElasWeekHour(flexibleApplId);
				
				if(weekList!=null && weekList.size()>0) {
					for(Map<String, Object> w : weekList) {
						int workMinute = 0;
						if(w.get("workMinute")!=null && !"".equals(w.get("workMinute"))) {
							workMinute = Integer.parseInt(w.get("workMinute").toString());
							sumWorkMinute += workMinute;
							
							if(days.size()>14 && (workMinute/60) > Integer.parseInt(max2weekMorethen)) {
								rp.setFail("2주 이상 탄근제는 주간 최대 "+max2weekMorethen+"시간을 초과할 수 없습니다.");
								return rp;
							}
							if(days.size()<=14 && (workMinute/60) > Integer.parseInt(max2weekWithin)) {
								rp.setFail("2주 이내 탄근제는 주간 최대 "+max2weekWithin+"시간을 초과할 수 없습니다.");
								return rp;
							}
						}
						
						int otMinute = 0;
						if(w.get("otMinute")!=null && !"".equals(w.get("otMinute"))) 
							otMinute = Integer.parseInt(w.get("otMinute").toString());
						
						if((otMinute/60) > 12) {
							rp.setFail("연장 근무는 주 12시간을 초과할 수 없습니다.");
							return rp;
						}
					}
					
					if(sumWorkMinute>0 && (sumWorkMinute/days.size()*7)/60>40) {
						rp.setFail("소정 근무는 평균 40시간을 초과할 수 없습니다.");
						return rp;
					} 
				}
				
			}else if(workTypeCd.equals("DIFF")) {
				//시차
				
			}else {
				rp.setFail("");
			}
			//소정근로시간 체크
			//2주이내 48체크	
			//2주이상 52시간 체크
			
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
		if(patterns!=null && patterns.size()>0) {
			for(WtmFlexibleApplDetVO p : patterns) {
				WtmFlexibleApplDet fd = new WtmFlexibleApplDet();
				fd.setFlexibleApplId(flexibleApplId);
				fd.setYmd(p.getYmd());
				fd.setTimeCdMgrId(p.getTimeCdMgrId());
				fd.setHolidayYn(p.getHolidayYn());
				
				Date planSdate = null;
				if(p.getPlanSdate()!=null && !"".equals(p.getPlanSdate())) {
					planSdate = WtmUtil.toDate(p.getPlanSdate(), "yyyy-MM-dd HH:mm:ss");
					fd.setPlanSdate(planSdate);
				}
				
				Date planEdate = null;
				if(p.getPlanEdate()!=null && !"".equals(p.getPlanEdate())) {
					planEdate = WtmUtil.toDate(p.getPlanEdate(), "yyyy-MM-dd HH:mm:ss");
					fd.setPlanEdate(planEdate);
				}
				
				paramMap.put("ymd", p.getYmd());
				
				if(planSdate!=null && planEdate!=null) {
					paramMap.put("shm", WtmUtil.parseDateStr(planSdate, "HHmm"));
					paramMap.put("ehm", WtmUtil.parseDateStr(planEdate, "HHmm"));
					Map<String, Object> planMinuteMap = flexEmpService.calcElasPlanMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
					fd.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					
					paramMap.put("otType", "OTB");
					paramMap.put("sDate", p.getPlanSdate());
					paramMap.put("eDate", p.getPlanEdate());
					paramMap.put("minute", p.getOtbMinute());
					Map<String, Object> otbMinuteMap = flexEmpService.calcElasOtMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
					
					if(otbMinuteMap!=null) {
						Date otbSdate = WtmUtil.toDate(otbMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
						Date otbEdate = WtmUtil.toDate(otbMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
						
						fd.setOtbSdate(otbSdate);
						fd.setOtbEdate(otbEdate);
						fd.setOtbMinute(Integer.parseInt(otbMinuteMap.get("calcMinute").toString()));
					}	
					
					paramMap.put("otType", "OTA");
					paramMap.put("sDate", p.getPlanSdate());
					paramMap.put("eDate", p.getPlanEdate());
					paramMap.put("minute", p.getOtaMinute());
					Map<String, Object> otaMinuteMap = flexEmpService.calcElasOtMinuteExceptBreaktime(flexibleApplId, paramMap, userId);
					
					if(otaMinuteMap!=null) {
						Date otaSdate = WtmUtil.toDate(otaMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
						Date otaEdate = WtmUtil.toDate(otaMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
						
						fd.setOtaSdate(otaSdate);
						fd.setOtaEdate(otaEdate);
						fd.setOtaMinute(Integer.parseInt(otaMinuteMap.get("calcMinute").toString()));
					}	
				}
				
				fd.setUpdateDate(new Date());
				fd.setUpdateId(userId);
				workList.add(fd);
			}
			
			wtmFlexibleApplDetRepo.saveAll(workList);
		}
		
		return workList;
	}
	
	protected void saveWtmApplLine(Long tenantId, String enterCd, int apprLvl, Long applId, String sabun, String userId) {
		
		//결재라인 저장
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<WtmApplLine> applLines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), null));
		//결재라인 조회 기본으로 3단계까지 가져와서 뽑아  쓰자
		List<WtmApplLineVO> applLineVOs = applMapper.getWtmApplLine(paramMap);
		//기본 결재라인이 없으면 저장도 안됨.
		if(applLineVOs != null && applLineVOs.size() > 0){

			//결재라인 코드는 1,2,3으로 되어있다 이렇게 써야한다!!!! 1:1단계, 2:2단계, 3:3단계
			int applCnt = apprLvl;
			
			//기 저장된 결재라인과 비교
			if(applLines != null && applLines.size() > 0) {
				int whileLoop = 0;
				
				for(WtmApplLine applLine : applLines) {
					
					WtmApplLineVO applLineVO = applLineVOs.get(whileLoop);
					if(whileLoop < applCnt) {
						applLine.setApplId(applId);
						applLine.setApprSeq(applLineVO.getApprSeq());
						applLine.setApprSabun(applLineVO.getSabun());
						applLine.setApprTypeCd(APPL_LINE_S);
						applLine.setUpdateId(userId);
						wtmApplLineRepo.save(applLine);
					}else {
						//기존 결재라인이 더 많으면 지운다. 임시저장이니.. 바뀔수도 있을 것 같아서..
						wtmApplLineRepo.delete(applLine);
					}
					whileLoop++;
				} 
			}else {
				//신규생성
				int lineCnt = 0; 
				for(WtmApplLineVO applLineVO : applLineVOs) {
					if(lineCnt < applCnt) {
						WtmApplLine applLine = new WtmApplLine();
						applLine.setApplId(applId);
						applLine.setApprSeq(applLineVO.getApprSeq());
						applLine.setApprSabun(applLineVO.getSabun());
						applLine.setUpdateId(userId);
						wtmApplLineRepo.save(applLine);
					}
					lineCnt++;
				}
			}
		}
		//결재라인 저장 끝
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
 

}
