package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmOtAppl;
import com.isu.ifw.entity.WtmOtCanAppl;
import com.isu.ifw.entity.WtmOtSubsAppl;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmOtCanApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmOtApplRepository;
import com.isu.ifw.repository.WtmOtCanApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;

@Service("wtmOtCanApplService")
public class WtmOtCanApplServiceImpl implements WtmApplService {

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
	WtmApplLineRepository wtmApplLineRepo;

	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmOtApplRepository wtmOtApplRepo;
	
	@Autowired
	WtmOtCanApplRepository wtmOtCanApplRepo;
	
	@Autowired
	WtmOtSubsApplRepository wtmOtSubsCanApplRepo;

	@Autowired
	WtmOtSubsApplRepository wtmOtSubsApplRepo;
	
	@Autowired
	WtmWorkCalendarRepository wtmWorkCalendarRepository;
	@Autowired
	WtmFlexibleStdMgrRepository wtmFlexibleStdMgrRepo;
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	WtmOtCanApplMapper wtmOtCanApplMapper;
	
	@Autowired
	WtmInboxService inbox;
	
	@Autowired
	WtmEmpHisRepository wtmEmpHisRepo; 
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Autowired
	private WtmApplAfterService vtmApplAfterService;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("applId", applId);
		paramMap.put("sabun", sabun);
		List<Map<String, Object>> otCanApplList = wtmOtCanApplMapper.otCanApplfindByApplId(paramMap);
		
		Map<String, Object> otCanAppl = null;
		if(otCanApplList!=null && otCanApplList.size()>0) {
			// otACanApplList가 1건 이상이면(연장근무신청 관리자 화면에서 신청한 경우) 대상자 리스트를 보여줌
			List<String> sabuns = new ArrayList<String>();
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			
			if(otCanAppl==null)
				otCanAppl = otCanApplList.get(0);
			
			for(Map<String, Object> o : otCanApplList) {
				//연장근무신청 관리자 화면에서 신청 시 작성한 내용은 똑같으므로 첫번째 사람의 연장근무 신청서만 가져옴
				if(otCanApplList.size()==1 || sabun.equals(o.get("sabun").toString())) {
					otCanAppl = o;
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
				otCanAppl.put("targetList", targetList);
			}
			
			//대체휴일
			if(otCanAppl.get("holidayYn")!=null && "Y".equals(otCanAppl.get("holidayYn")) && otCanAppl.get("subYn")!=null && "Y".equals(otCanAppl.get("subYn"))) {
				List<Map<String, Object>> otSubsAppls = wtmOtApplMapper.otSubsApplfindByOtApplId(Long.valueOf(otCanAppl.get("otApplId").toString()));
				if(otSubsAppls!=null && otSubsAppls.size()>0)
					otCanAppl.put("subs", otSubsAppls);
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
			
			otCanAppl.put("recoveryYn", isRecovery);
			otCanAppl.put("applLine", applLine);
		}
		
		return otCanAppl;
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
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		ReturnParam rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
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
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 연장근무취소신청", "", "Y");
		
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
					line = wtmApplLineRepo.save(line);
					apprSabun = line.getApprSabun();
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
//		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		
		appl = wtmApplRepo.save(appl);
		
		if(lastAppr) {
			rp = vtmApplAfterService.applyCanAfter(tenantId, enterCd, applId, paramMap, sabun, userId);
		}
		
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.addAll(applSabuns);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "연장근무취소 신청서가  승인되었습니다.", "N");
		
			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 연장근무취소신청", "", "N");
		
			rp.put("msgType", "APPR");
		}
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", pushSabun);
		
		return rp;
	}

	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		return null;
	}

	@Override
	@Transactional
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
		//Long flexibleStdMgrId = Long.parseLong(paramMap.get("flexibleStdMgrId").toString());
		//신청서 최상위 테이블이다. 
		//20.05.13 jyp  applId > null 수정. 신청서 아이디를 새로 받아야 한다. 임시저장 개념이 없다.
		//WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, status, sabun, userId);
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, null, workTypeCd, status, sabun, userId);
		
		/**
		 * 20.05.13 jyp 새로 신청서가 저장된 아이디로
		 * List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
		 * 오티 신청 정보를 조회하고 있어서 조회가 안된다. 
		 * 파라메터로 받은 applId 는 otApplId이다 취소할 연장근무신청서 아이디
		 *  
		 */
		//applId = appl.getApplId();
		 
		Long workDayResultId = Long.parseLong(paramMap.get("workDayResultId").toString());
		String reason = paramMap.get("reason").toString();
		
		//WtmWorkDayResult result = wtmWorkDayResultRepo.findByWorkDayResultId(workDayResultId);
		
		/**
		 * 20.05.13 jyp
		 * 취소할 오티 정보 조회수정
		 */
		//List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(applId);
		WtmOtAppl otAppl = wtmOtApplRepo.findById(applId).get();
		
		//if(otApplList!=null && otApplList.size()>0) {
		//	for(WtmOtAppl otAppl: otApplList) {
				otAppl.setCancelYn("Y");
				wtmOtApplRepo.save(otAppl);
				
				List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndApplId(tenantId, enterCd, sabun, otAppl.getApplId());
				for(WtmWorkDayResult result : results) {
					//근무제 신청서 테이블 조회
					//WtmOtCanAppl otCanAppl = 
					saveWtmOtCanAppl(tenantId, enterCd, appl.getApplId(), otAppl.getOtApplId(), result.getWorkDayResultId(), result.getYmd(), result.getTimeTypeCd(), result.getPlanSdate(), result.getPlanEdate(), result.getPlanMinute(), result.getApprSdate(), result.getApprEdate(), result.getApprMinute(), reason, result.getSabun(), userId);
				}
		//	}
		//}
		
		
//		
//		WtmWorkCalendar calendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
//		
//		String subYn = "";
//		if(paramMap.containsKey("subYn")) {
//			subYn = paramMap.get("subYn")+"";
//		}
		//20.05.13 jyp applId 수정
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), appl.getApplId(), workTypeCd, sabun, userId);
		paramMap.put("applId", appl.getApplId());
		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		rp.put("applId", appl.getApplId());
			
		return rp;
	}

	protected WtmOtCanAppl saveWtmOtCanAppl(Long tenantId, String enterCd, Long applId, Long otApplId, Long workDayResultId, String ymd, String timeTypeCd, Date planSdate, Date planEdate, Integer planMinute, Date apprSdate, Date apprEdate, Integer apprMinute,  String reason, String sabun, String userId) {
		 
		/**
		 * 05.14 jyp 
		 * 취소 신청의 경우 임시저장 개념이 없다. 그리고 여러건이 들어오게 되서 무조건 신규 저장이다. 
		 */
		/*
		WtmOtCanAppl otAppl = wtmOtCanApplRepo.findByApplIdAndOtApplId(applId, otApplId);
		if(otAppl == null) {
			otAppl = new WtmOtCanAppl();
		}
		*/
		WtmOtCanAppl otAppl = new WtmOtCanAppl();
		otAppl.setApplId(applId);
		otAppl.setOtApplId(otApplId);
		otAppl.setYmd(ymd);
		otAppl.setSabun(sabun);
		otAppl.setTimeTypeCd(timeTypeCd);
		otAppl.setWorkDayResultId(workDayResultId);
		otAppl.setPlanSdate(planSdate);
		otAppl.setPlanEdate(planEdate);
		otAppl.setPlanMinute(planMinute);
		otAppl.setApprSdate(apprSdate);
		otAppl.setApprEdate(apprEdate);
		otAppl.setApprMinute(apprMinute);
		otAppl.setReason(reason);
		otAppl.setUpdateId(userId);
		
		return wtmOtCanApplRepo.save(otAppl);
	}
	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
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
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		return rp;
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub
		
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

	@Override
	@Transactional
	public void delete(Long applId) {
		
		List<WtmOtCanAppl> wtmOtCanAppls = wtmOtCanApplRepo.findByApplId(applId);
		
		if(wtmOtCanAppls!=null && wtmOtCanAppls.size()>0) {
			//ot신청서의 cancel_yn 다시 돌리기.
			List<WtmOtAppl> otAppls = new ArrayList<WtmOtAppl>();
			for(WtmOtCanAppl otCanAppl : wtmOtCanAppls) {
				WtmOtAppl otAppl = wtmOtApplRepo.findById(otCanAppl.getOtApplId()).get();
				otAppl.setCancelYn(null);
				otAppls.add(otAppl);
			}
			wtmOtApplRepo.saveAll(otAppls);
			
			wtmOtCanApplRepo.deleteAll(wtmOtCanAppls);
		}
		
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

}
