package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmOtCanApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEntryApplRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("wtmApplService")
public class WtmApplServiceImpl implements WtmApplService {

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmValidatorService validatorService;
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmOtApplMapper otApplMapper;
	
	@Autowired
	WtmOtCanApplMapper otCanMapper;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	
	@Autowired
	WtmFlexibleApplRepository wtmFlexibleApplRepo;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
	
	@Autowired
	WtmEntryApplRepository entryApplRepo;
	
	@Autowired
	@Qualifier("wtmFlexibleApplService")
	WtmApplService wtmFlexibleApplService;
	
	@Autowired
	@Qualifier("wtmOtApplService")
	WtmApplService wtmOtApplService;
	
	@Autowired
	@Qualifier("wtmOtCanApplService")
	WtmApplService wtmOtCanApplService;
	
	@Autowired
	@Qualifier("wtmEntryApplService")
	WtmApplService entryApplService;
	
	@Autowired
	@Qualifier("wtmOtSubsChgApplService")
	WtmApplService wtmOtSubsChgApplService;

	@Autowired
	@Qualifier("wtmCompApplService")
	WtmApplService wtmCompApplService;
	
	@Autowired
	@Qualifier("wtmCompCanApplService")
	WtmApplService wtmCompCanApplService;
	
	@Autowired
	WtmApplCodeRepository applCodeRepo;
	
	@Autowired
	WtmInboxService inbox;
	
	
	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("empNo", empNo);
		
		String applType = paramMap.get("applType").toString();
		
		//System.out.println("applType::::: " + applType);
		
		List<Map<String, Object>> apprList = null;
		if(applType.equals(APPL_TYPE_REQUEST))
			apprList = applMapper.getApprList01(paramMap);
		else if(applType.equals(APPL_TYPE_PENDING))
			apprList = applMapper.getApprList02(paramMap);
		else if(applType.equals(APPL_TYPE_COMPLETE))
			apprList = applMapper.getApprList03(paramMap);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			logger.debug(">>>>>>>>>>>>>>>>approvalList result: "+ mapper.writeValueAsString(apprList));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(apprList!=null && apprList.size()>0) {
			for(Map<String, Object> appr : apprList) {
				
				if(appr.get("applId")!=null && !"".equals(appr.get("applId")) && appr.get("applCd")!=null && !"".equals(appr.get("applCd"))) {
					Long applId = Long.valueOf(appr.get("applId").toString());
					String applCd = appr.get("applCd").toString();
					String applSabun = appr.get("applSabun").toString();
					Map<String, Object> appl = null;
					
					if("OT".equals(applCd) ) { //연장
						appl = wtmOtApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else if("OT_CAN".equals(applCd)) { //연장 취소
						appl = wtmOtCanApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else if("SUBS_CHG".equals(applCd)) { //대체휴가 정정
						appl = wtmOtSubsChgApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else if("ENTRY_CHG".equals(applCd)) { //근태사유서
						appl = entryApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else if("COMP".equals(applCd)) { //보상휴가
						appl = wtmCompApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else if("COMP_CAN".equals(applCd)) { //보상휴가취소
						appl = wtmCompCanApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else {
						//유연근무제
						appl = wtmFlexibleApplService.getAppl(tenantId, enterCd, applSabun, applId, userId); 
					}
					
					if(appl!=null)
						appl.put("sabun", empNo);
						appr.put("appl", appl);
					
				}
			}
		}
		
		return apprList;
	}

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
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		String apprOpinion = paramMap.get("apprOpinion").toString();
		List<String> emps = new ArrayList();
		
		String applSabun = null;
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() == 1) {
					emps.add(line.getApprSabun());
				}
				if(line.getApprSeq() <= apprSeq) {
					line.setApprStatusCd(APPR_STATUS_REJECT);
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
		
		System.out.println("1111111111111111111 3" + emps.toString());
		inbox.setInbox(tenantId, enterCd, emps, applId, "APPLY", "결재완료", "신청서가  반려되었습니다.", "N");
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", emps);
		
		return rp;
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {
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
