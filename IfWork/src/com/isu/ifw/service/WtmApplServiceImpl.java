package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
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
import com.isu.ifw.vo.WtmApplLineVO;

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
		
		/*ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("apprList : " + mapper.writeValueAsString(apprList));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
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
					} else if("SUBS_CHG".equals(applCd)) { //대체휴가 취소
						
					} else if("ENTRY_CHG".equals(applCd)) { //근태사유서
						appl = entryApplService.getAppl(tenantId, enterCd, applSabun, applId, userId);
					} else {
						//유연근무제
						appl = wtmFlexibleApplService.getAppl(tenantId, enterCd, applSabun, applId, userId); 
					}
					
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
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, String applCd, String userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		paramMap.put("applCd", applCd);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
		
		List<WtmApplLineVO> result = new ArrayList<WtmApplLineVO>();
		List<WtmApplLineVO> applLines = applMapper.getWtmApplLine(paramMap);
		
		WtmApplCode applCode = applCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
		if(applCode!=null && applCode.getApplLevelCd()!=null && !"".equals(applCode.getApplLevelCd())) {
			int applLevel = Integer.parseInt(applCode.getApplLevelCd());
			
			int lineCnt = 0; 
			for(WtmApplLineVO applLine : applLines) {
				if(!APPL_LINE_S.equals(applLine.getApprTypeCd()) || (APPL_LINE_S.equals(applLine.getApprTypeCd()) && lineCnt < applLevel)) {
					result.add(applLine);
				}
				
				if(APPL_LINE_S.equals(applLine.getApprTypeCd()))
					lineCnt++;
			}
		}
		
		return result;
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
		String applSabun = null;
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() == 1) {
					applSabun = line.getApprSabun();
				}
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
		
		inbox.setInbox(tenantId, enterCd, applSabun, applId, "APPLY", "결재완료", "신청서가  반려되었습니다.", "N");
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

}
