package com.isu.ifw.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmEntryAppl;
import com.isu.ifw.entity.WtmFlexibleAppl;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmOtCanApplMapper;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmEntryApplRepository;
import com.isu.ifw.repository.WtmFlexibleApplRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmApplLineVO;
import com.isu.option.vo.ReturnParam;

@Service("wtmApplService")
public class WtmApplServiceImpl implements WtmApplService {

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
	
	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("empNo", empNo);
		
		String applType = paramMap.get("applType").toString();
		
		System.out.println("applType::::: " + applType);
		
		List<Map<String, Object>> apprList = null;
		if(applType.equals(APPL_TYPE_REQUEST))
			apprList = applMapper.getApprList01(paramMap);
		else if(applType.equals(APPL_TYPE_PENDING))
			apprList = applMapper.getApprList02(paramMap);
		else if(applType.equals(APPL_TYPE_COMPLETE))
			apprList = applMapper.getApprList03(paramMap);
		
		if(apprList!=null && apprList.size()>0) {
			for(Map<String, Object> appr : apprList) {
				
				if(appr.get("applId")!=null && !"".equals(appr.get("applId")) && appr.get("applCd")!=null && !"".equals(appr.get("applCd"))) {
					Long applId = Long.valueOf(appr.get("applId").toString());
					String applCd = appr.get("applCd").toString();
					
					if("OT".equals(applCd) || "OT_CAN".equals(applCd)) { //연장, 휴일연장, 연장취소, 휴일연장 취소
						Map<String, Object> otAppl = null;
						
						if("OT".equals(applCd))
							otAppl = otApplMapper.otApplfindByApplId(applId);
						else if("OT_CAN".equals(applCd))
							otAppl = otCanMapper.otApplAndOtCanApplfindByApplId(applId);
						
						if(otAppl!=null && otAppl.containsKey("subYn") && otAppl.get("subYn")!=null && "Y".equals(otAppl.get("subYn"))) {
							otAppl.put("subs", otApplMapper.otSubsApplfindByOtApplId(Long.valueOf(otAppl.get("otApplId").toString())));
						}
						appr.put("appl", otAppl);
					} else if("SUBS_CHG".equals(applCd)) { //대체휴가 취소
						
					} else if("ENTRY_CHG".equals(applCd)) { //근태사유서
						WtmEntryAppl entryAppl = entryApplRepo.findByApplId(applId);
						appr.put("appl", entryAppl);
					} else {
						//유연근무제
						WtmFlexibleAppl flexibleAppl = wtmFlexibleApplRepo.findByApplId(applId);
						appr.put("appl", flexibleAppl);
					}
					
				}
			}
		}
		
		return apprList;
	}

	@Override
	public Map<String, Object> getAppl(Long applId) {
		// TODO Auto-generated method stub
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
	public void request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public void reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		
		if(paramMap == null || !paramMap.containsKey("apprOpinion") && paramMap.get("apprOpinion").equals("")) {
			throw new Exception("사유를 입력하세요.");
		}
		String apprOpinion = paramMap.get("apprOpinion").toString();
		
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() <= apprSeq) {
					line.setApprStatusCd(APPR_STATUS_REJECT);
					line.setApprDate(WtmUtil.parseDateStr(new Date(), null));
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

}
