package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmEntryAppl;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmEntryApplMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("wtmEntryApplService")
public class WtmEntryApplServiceImpl implements WtmApplService {
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmEntryApplMapper entryApplMapper;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	WtmEntryApplRepository wtmEntryApplRepo;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmCalendarMapper calendarMapper;
	
	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmValidatorService validatorService;
	
	@Autowired
	WtmInoutHisMapper inoutHisMapper;
	
	@Autowired
	WtmInboxService inbox;

	@Autowired
	WtmApplLineService applLineService;
	
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		Map<String, Object> appl = entryApplMapper.findByApplId(applId);
		appl.put("applLine", applMapper.getWtmApplLineByApplId(applId));		
				
		return appl;
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

	@Transactional
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		
		rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		List<String> emps = new ArrayList();
//		String apprSabun = null;
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
						emps.add(line.getApprSabun());
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						break;
					}
					 
				}
			}
		}
		inbox.setInbox(tenantId, enterCd, emps, applId, "APPR", "결재요청 : 근태사유서", "", "Y");
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", emps);
		
		return rp;
	}
	
	//모바일용 동기처리
	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("신청되었습니다.");
		
		Long applId = null;
		rp = imsi(tenantId, enterCd, null, paramMap.get("applCd").toString(), paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		List<String> emps = new ArrayList();
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
						emps.add(line.getApprSabun());
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						break;
					}
					 
				}
			}
		}
		
		inbox.setInbox(tenantId, enterCd, emps, applId, "APPR", "결재요청 : 근태사유서", "", "Y");

		return rp;
		//push전송 추가
	}

	@Transactional
	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		ReturnParam rp = new ReturnParam();
		String applSabun = paramMap.get("applSabun").toString();
		
		String ymd = paramMap.get("ymd").toString();
		rp = validatorService.checkDuplicateEntryAppl(tenantId, enterCd, applSabun, ymd, applId);
		
		if(rp.getStatus().equals("FAIL")) {
			return rp;
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
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_APPR:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		
		appl = wtmApplRepo.save(appl);
		
		if(lastAppr) {
			//출퇴근 타각 저장
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", applSabun);
			paramMap.put("typeCd", "APPL");
			paramMap.put("userId", userId);
			paramMap.put("stdYmd", ymd);
			paramMap.put("paramSdate", ymd);
			paramMap.put("paramEdate", ymd);
			List<Map<String, Object>> insertRows = new ArrayList<Map<String, Object>>();
			insertRows.add(paramMap);
			paramMap.put("insertRows", insertRows);
			calendarMapper.updateEntryDateByAdm(paramMap);
			
			//계획 여부에 따라 result 생성
			rp.put("sabun", applSabun);
			rp.put("stdYmd", ymd);
			Map<String, Object> unplannedYn = inoutHisMapper.getMyUnplannedYn(paramMap);
			paramMap.put("unplannedYn", "N");
			if(unplannedYn!=null && unplannedYn.get("unplannedYn")!=null) {
				rp.put("unplannedYn", unplannedYn.get("unplannedYn").toString());
			}


		}
		
		List<String> emps = new ArrayList();
		if(lastAppr) {
			emps.add(applSabun);
			System.out.println("1111111111111111111 1" + emps.toString());
			inbox.setInbox(tenantId, enterCd, emps, applId, "APPLY", "결재완료", "근태사유서가  승인되었습니다.", "N");
			
			rp.put("msgType", "APPLY");
		} else {
			emps.add(apprSabun);
			System.out.println("1111111111111111111 2" + emps.toString());
			inbox.setInbox(tenantId, enterCd, emps, applId, "APPR", "결재요청 : 근태사유서", "", "N");
			
			rp.put("msgType", "APPR");
		}
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", emps);

		return rp;
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

	@Transactional
	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
		
		//신청서 최상위 테이블이다. 
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, applId, workTypeCd, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		applId = appl.getApplId();
		
		String ymd = paramMap.get("ymd").toString();
		String planSdate = null;
		if(paramMap.containsKey("planSdate") && paramMap.get("planSdate")!=null && !"".equals(paramMap.get("planSdate")))
			planSdate = paramMap.get("planSdate").toString();
		String planEdate = null;
		if(paramMap.containsKey("planEdate") && paramMap.get("planEdate")!=null && !"".equals(paramMap.get("planEdate")))
			planEdate = paramMap.get("planEdate").toString();
		String entrySdate = null;
		if(paramMap.containsKey("entrySdate") && paramMap.get("entrySdate")!=null && !"".equals(paramMap.get("entrySdate")))
			entrySdate = paramMap.get("entrySdate").toString();
		String entryEdate = null;
		if(paramMap.containsKey("entryEdate") && paramMap.get("entryEdate")!=null && !"".equals(paramMap.get("entryEdate")))
			entryEdate = paramMap.get("entryEdate").toString();
		String chgSdate = null;
		if(paramMap.containsKey("chgSdate") && paramMap.get("chgSdate")!=null && !"".equals(paramMap.get("chgSdate")))
			chgSdate = paramMap.get("chgSdate").toString();
		String chgEdate = null;
		if(paramMap.containsKey("chgEdate") && paramMap.get("chgEdate")!=null && !"".equals(paramMap.get("chgEdate")))
			chgEdate = paramMap.get("chgEdate").toString();
		String reason = paramMap.get("reason").toString();
		
		//근태사유서 신청서 저장
		saveInOutChangeAppl(tenantId, enterCd, applId, ymd, planSdate, planEdate, entrySdate, entryEdate, chgSdate, chgEdate, reason, sabun, userId);
		
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, sabun, userId);
		
		rp.put("applId", applId);
		

		if(rp.getStatus().equals("FAIL")) {
			throw new RuntimeException(rp.get("message").toString());
		}
		
		return rp;
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
		
		String ymd = paramMap.get("ymd").toString();
		Long applId = null;
		if(paramMap.containsKey("applId") && paramMap.get("applId")!=null && !"".equals(paramMap.get("applId")))
			applId = Long.valueOf(paramMap.get("applId").toString());
		
		return validatorService.checkDuplicateEntryAppl(tenantId, enterCd, sabun, ymd, applId);
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub
		
	}
	
	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}
	
	protected WtmEntryAppl saveInOutChangeAppl(Long tenantId, String enterCd, Long applId, String ymd, String planSdate, String planEdate, String entrySdate, String entryEdate, String chgSdate, String chgEdate, String reason, String sabun, String userId) {
		WtmEntryAppl entryAppl = wtmEntryApplRepo.findByApplId(applId);
		if(entryAppl == null) {
			entryAppl = new WtmEntryAppl();
		}
		entryAppl.setApplId(applId);
		entryAppl.setYmd(ymd);
		
		Date sdate = null;
		if(planSdate!=null && !"".equals(planSdate))
			entryAppl.setPlanSdate(WtmUtil.toDate(planSdate, "yyyyMMddHHmm"));
		if(planEdate!=null && !"".equals(planEdate))
			entryAppl.setPlanEdate(WtmUtil.toDate(planEdate, "yyyyMMddHHmm"));
		if(entrySdate!=null && !"".equals(entrySdate)) {
			entryAppl.setEntrySdate(WtmUtil.toDate(entrySdate, "yyyyMMddHHmm"));
			sdate = WtmUtil.toDate(entrySdate, "yyyyMMddHHmm");
		}
		if(entryEdate!=null && !"".equals(entryEdate))
			entryAppl.setEntryEdate(WtmUtil.toDate(entryEdate, "yyyyMMddHHmm"));
		if(chgSdate!=null && !"".equals(chgSdate))
			entryAppl.setChgSdate(WtmUtil.toDate(chgSdate, "yyyyMMddHHmm"));
		if(chgEdate!=null && !"".equals(chgEdate)) {
			Date edate = WtmUtil.toDate(chgEdate, "yyyyMMddHHmm");
			
			if(chgSdate!=null && !"".equals(chgSdate)) {
				sdate = WtmUtil.toDate(chgSdate, "yyyyMMddHHmm");
			} 
			
			//종료일이 시작일보다 작을 때
			if(sdate.compareTo(edate)>0) {
				edate = WtmUtil.addDate(edate,1);
			}
			entryAppl.setChgEdate(edate);
		}
		entryAppl.setReason(reason);
		entryAppl.setUpdateId(userId);
		
		return wtmEntryApplRepo.save(entryAppl);
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
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
