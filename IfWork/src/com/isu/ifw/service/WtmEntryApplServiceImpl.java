package com.isu.ifw.service;

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
import com.isu.ifw.entity.WtmEntryAppl;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmEntryApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEntryApplRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmApplLineVO;
import com.isu.option.vo.ReturnParam;

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
	
	
	@Override
	public Map<String, Object> getAppl(Long applId) {
		return null;
	}

	@Override
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap,
			String userId) {
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

	@Transactional
	@Override
	public void request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		
		rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
		if(rp!=null && rp.getStatus()!=null && "OK".equals(rp.getStatus())) {
			applId = Long.valueOf(rp.get("applId").toString());
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
			//출퇴근 타각 저장
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", applSabun);
			paramMap.put("typeCd", "APPL");
			paramMap.put("userId", userId);
			List<Map<String, Object>> insertRows = new ArrayList<Map<String, Object>>();
			insertRows.add(paramMap);
			paramMap.put("insertRows", insertRows);
			calendarMapper.updateEntryDateByAdm(paramMap);
			
			//출퇴근 타각이 둘 다 있으면 타각 정보로 인정시간 계산
			WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, applSabun, ymd);
			if(calendar!=null && calendar.getEntrySdate()!=null && calendar.getEntryEdate()!=null) {
				System.out.println("calcApprDayInfo>>>");
				flexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, applSabun);
			}
			
		}
		
		return rp;
	}

	@Override
	public void reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		
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
		
		saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, sabun, userId);
		
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
		
		if(planSdate!=null && !"".equals(planSdate))
			entryAppl.setPlanSdate(WtmUtil.toDate(planSdate, "yyyyMMddHHmm"));
		if(planEdate!=null && !"".equals(planEdate))
			entryAppl.setPlanEdate(WtmUtil.toDate(planEdate, "yyyyMMddHHmm"));
		if(entrySdate!=null && !"".equals(entrySdate))
			entryAppl.setEntrySdate(WtmUtil.toDate(entrySdate, "yyyyMMddHHmm"));
		if(entryEdate!=null && !"".equals(entryEdate))
			entryAppl.setEntryEdate(WtmUtil.toDate(entryEdate, "yyyyMMddHHmm"));
		if(chgSdate!=null && !"".equals(chgSdate))
			entryAppl.setChgSdate(WtmUtil.toDate(chgSdate, "yyyyMMddHHmm"));
		if(chgEdate!=null && !"".equals(chgEdate)) {
			Date edate = WtmUtil.toDate(chgEdate, "yyyyMMddHHmm");
			
			if(chgSdate!=null && !"".equals(chgSdate)) {
				Date sdate = WtmUtil.toDate(chgSdate, "yyyyMMddHHmm");
				
				//종료일이 시작일보다 작을 때
				if(sdate.compareTo(edate)>0) {
					edate = WtmUtil.addDate(edate,1);
				}
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
						applLine.setApprTypeCd(APPL_LINE_S);
						applLine.setUpdateId(userId);
						wtmApplLineRepo.save(applLine);
					}
					lineCnt++;
				}
			}
		}
		//결재라인 저장 끝
	}
	
	
}
