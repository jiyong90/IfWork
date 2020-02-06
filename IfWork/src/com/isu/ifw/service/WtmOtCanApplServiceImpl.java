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
				for(WtmApplLineVO l : applLine) {
					if(APPL_LINE_S.equals(l.getApprTypeCd())) {
						if(seq==1 && APPR_STATUS_REQUEST.equals(l.getApprStatusCd()) && sabuns.indexOf(sabun)!=-1 )
							isRecovery = true;
						
						seq++;
					}
				}
			}
			
			otCanAppl.put("recoveryYn", isRecovery);
			otCanAppl.put("applLine", applLine);
		}
		
		return otCanAppl;
	}

	@Override
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, String applCd,
			String userId) {
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
	public void request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
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
						line.setApprDate(WtmUtil.parseDateStr(new Date(), "yyyyMMddHHmmss"));
						line = wtmApplLineRepo.save(line);
					} else if(APPL_LINE_S.equals(line.getApprTypeCd())) { //결재
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
					line.setApprDate(WtmUtil.parseDateStr(new Date(), null));
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
					}
					lastAppr = false;
					apprSabun = line.getApprSabun();
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
			//취소하는 근무시간 정보를 지운다.
			List<WtmOtCanAppl> otCanApplList = wtmOtCanApplRepo.findByApplId(applId);
			
			if(otCanApplList!=null && otCanApplList.size()>0) {
				for(WtmOtCanAppl otCanAppl : otCanApplList) {
					Long deletedApplId = null;
					
					WtmWorkDayResult dayResult = wtmWorkDayResultRepo.findById(otCanAppl.getWorkDayResultId()).get();
					//지우려는 정보의 신청정보가 있다면 관련된 정보도 같이 지워준다 대체휴일과 같은 정보..
					if(dayResult.getApplId() != null) {
						deletedApplId = dayResult.getApplId(); 
					}
					
					wtmWorkDayResultRepo.delete(dayResult);
					 
					rp.put("sabun", dayResult.getSabun());
					rp.put("symd", dayResult.getYmd());
					rp.put("eymd", dayResult.getYmd());
					
					
					if(deletedApplId != null) {
						//대체 휴일 정보를 찾자
						List<WtmOtSubsAppl> otSubsAppls = wtmOtSubsApplRepo.findByApplId(deletedApplId);
						if(otSubsAppls != null && otSubsAppls.size() > 0) {
							String currYmd = null;
							paramMap.put("tenantId", tenantId);
							paramMap.put("enterCd", enterCd);
							Map<String, Map<String, Date>> resetBaseTime = new HashMap<String, Map<String, Date>>();
							for(WtmOtSubsAppl otSubsAppl : otSubsAppls) {
								wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(tenantId, enterCd, otSubsAppl.getSubYmd(), otCanAppl.getSabun(), WtmApplService.TIME_TYPE_SUBS, "", otSubsAppl.getSubsSdate(), otSubsAppl.getSubsEdate(), deletedApplId, userId);
							}
						
						}
					}
				}
			}
			 
		}
		
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.add(sabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "연장근무취소 신청서가  승인되었습니다.", "N");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 연장근무취소신청", "", "N");
		}
		
		return rp;
	}

	@Override
	public void reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		if(paramMap == null || !paramMap.containsKey("apprOpinion") && paramMap.get("apprOpinion").equals("")) {
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
					line.setApprDate("");
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
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, status, sabun, userId);
		
		applId = appl.getApplId();
		 
		Long workDayResultId = Long.parseLong(paramMap.get("workDayResultId").toString());
		String reason = paramMap.get("reason").toString();
		
		WtmWorkDayResult result = wtmWorkDayResultRepo.findByWorkDayResultId(workDayResultId);
		
		List<WtmOtAppl> otApplList = wtmOtApplRepo.findByApplId(result.getApplId());
		if(otApplList!=null && otApplList.size()>0) {
			for(WtmOtAppl otAppl: otApplList) {
				otAppl.setCancelYn("Y");
				wtmOtApplRepo.save(otAppl);
				
				//근무제 신청서 테이블 조회
				WtmOtCanAppl otCanAppl = saveWtmOtCanAppl(tenantId, enterCd, applId, otAppl.getOtApplId(), workDayResultId, result.getYmd(), result.getTimeTypeCd(), result.getPlanSdate(), result.getPlanEdate(), result.getPlanMinute(), result.getApprSdate(), result.getApprEdate(), result.getApprMinute(), reason, result.getSabun(), userId);
			}
		}
		
		
//		
//		WtmWorkCalendar calendar = wtmWorkCalendarRepository.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
//		
//		String subYn = "";
//		if(paramMap.containsKey("subYn")) {
//			subYn = paramMap.get("subYn")+"";
//		}
				
		saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, sabun, userId);
		paramMap.put("applId", appl.getApplId());
		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());
		rp.put("applId", appl.getApplId());
			
		return rp;
	}

	protected WtmOtCanAppl saveWtmOtCanAppl(Long tenantId, String enterCd, Long applId, Long otApplId, Long workDayResultId, String ymd, String timeTypeCd, Date planSdate, Date planEdate, Integer planMinute, Date apprSdate, Date apprEdate, Integer apprMinute,  String reason, String sabun, String userId) {
		 
		WtmOtCanAppl otAppl = wtmOtCanApplRepo.findByApplIdAndOtApplId(applId, otApplId);
		if(otAppl == null) {
			otAppl = new WtmOtCanAppl();
		}
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

	protected void saveWtmApplLine(Long tenantId, String enterCd, int apprLvl, Long applId, String applCd, String sabun, String userId) {
		
		//결재라인 저장
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<WtmApplLine> applLines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), null));
		paramMap.put("applCd", applCd);
		//결재라인 조회 기본으로 3단계까지 가져와서 뽑아  쓰자
		List<WtmApplLineVO> applLineVOs = applMapper.getWtmApplLine(paramMap);
		//기본 결재라인이 없으면 저장도 안됨.
		if(applLineVOs != null && applLineVOs.size() > 0){

			//결재라인 코드는 1,2,3으로 되어있다 이렇게 써야한다!!!! 1:1단계, 2:2단계, 3:3단계
			int applCnt = apprLvl; 
			
			//기 저장된 결재라인과 비교
			if(applLines != null && applLines.size() > 0) {
				int i=1; // apprSeq
				int whileLoop = 0;
				int lineCnt = 0;
				for(WtmApplLine applLine : applLines) {
					
					WtmApplLineVO applLineVO = applLineVOs.get(whileLoop);
					
					if(whileLoop < applLineVOs.size()) {
						
						if(!APPL_LINE_S.equals(applLineVO.getApprTypeCd()) || (APPL_LINE_S.equals(applLineVO.getApprTypeCd()) && lineCnt < applCnt)) {
							applLine.setApplId(applId);
							applLine.setApprSeq(i);
							applLine.setApprSabun(applLineVO.getSabun());
							applLine.setApprTypeCd(applLineVO.getApprTypeCd());
							applLine.setUpdateId(userId);
							wtmApplLineRepo.save(applLine);
							i++;
						}
						if(APPL_LINE_S.equals(applLineVO.getApprTypeCd()))
							lineCnt++;
						
					}else {
						//기존 결재라인이 더 많으면 지운다. 임시저장이니.. 바뀔수도 있을 것 같아서..
						wtmApplLineRepo.delete(applLine);
					}
					whileLoop++;
				} 
			}else {
				//신규생성
				int i=1; // apprSeq
				int lineCnt = 0; 
				for(WtmApplLineVO applLineVO : applLineVOs) {
					//발신결재 결재레벨 체크
					if(!APPL_LINE_S.equals(applLineVO.getApprTypeCd()) || (APPL_LINE_S.equals(applLineVO.getApprTypeCd()) && lineCnt < applCnt)) {
						WtmApplLine applLine = new WtmApplLine();
						applLine.setApplId(applId);
						applLine.setApprSeq(i);
						applLine.setApprSabun(applLineVO.getSabun());
						applLine.setApprTypeCd(applLineVO.getApprTypeCd());
						applLine.setUpdateId(userId);
						wtmApplLineRepo.save(applLine);
						i++;
					}
					
					if(APPL_LINE_S.equals(applLineVO.getApprTypeCd()))
						lineCnt++;
				}
			}
		}
		//결재라인 저장 끝
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


}
