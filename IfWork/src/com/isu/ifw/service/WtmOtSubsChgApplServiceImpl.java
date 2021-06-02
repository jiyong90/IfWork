package com.isu.ifw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("wtmOtSubsChgApplService")
public class WtmOtSubsChgApplServiceImpl implements WtmApplService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;
	
	@Autowired
	WtmOtApplRepository wtmOtApplRepo;
	
	@Autowired
	WtmOtSubsApplRepository wtmOtSubsApplRepo;
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;
	
	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmInboxService inbox;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("applId", applId);
			paramMap.put("sabun", sabun);
			List<Map<String, Object>> otApplList = wtmOtApplMapper.otApplfindBySubsChgApplId(paramMap);
			
			//대체휴가 정정 신청서는 무조건 1건이라고 본다.
			Map<String, Object> otAppl = null;
			if(otApplList!=null && otApplList.size()>0) {
				
				List<String> sabuns = new ArrayList<String>();
				
				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
				for(Map<String, Object> o : otApplList) {
					otAppl = o;
					ymd = o.get("ymd").toString();
					sabuns.add(o.get("sabun").toString());
				}
				
				//대체휴일
				if(otAppl.get("holidayYn")!=null && "Y".equals(otAppl.get("holidayYn")) && otAppl.get("subYn")!=null && "Y".equals(otAppl.get("subYn"))) {
					//이전 대체휴일
					List<Map<String, Object>> otSubsAppls = wtmOtApplMapper.prvOtSubsApplFindByApplId(applId);
					if(otSubsAppls!=null && otSubsAppls.size()>0)
						otAppl.put("oldSubs", otSubsAppls);
					
					//정정하고자 하는 데이터
					List<Map<String, Object>> otSubsChgAppls = wtmOtApplMapper.otSubsChgApplfindByApplId(applId);
					if(otSubsChgAppls!=null && otSubsChgAppls.size()>0)
						otAppl.put("subs", otSubsChgAppls);
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
		
		rp = imsi(tenantId, enterCd, null, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
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
		
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 대체휴가정정신청", "", "Y");
		
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
		
		String applSabun = "";
		if(lastAppr) {
			List<WtmOtAppl> otApplList = new ArrayList<WtmOtAppl>();
			List<WtmOtSubsAppl> otSubs = wtmOtSubsApplRepo.findByApplId(applId);
			
			System.out.println("applId : " + applId);
			
			if(otSubs==null || otSubs.size()==0) {
				rp.setFail("대체휴일 정보가 없습니다.");
				return rp;
			}
			
			int i = 0;
			for(WtmOtSubsAppl sub : otSubs) {
				if(sub.getOldSubsApplId()!=null) {
					
					System.out.println("otApplId : " + sub.getOtApplId());
					
					WtmOtAppl otAppl = wtmOtApplRepo.findById(sub.getOtApplId()).get();
					if(i==0)
						applSabun = otAppl.getSabun();
					
					//변경 전 대체휴일
					WtmOtSubsAppl oldSubs = wtmOtSubsApplRepo.findById(sub.getOldSubsApplId()).get();
					if(oldSubs==null) {
						rp.setFail("변경할 대체휴일이 없습니다.");
						return rp;
					}
					
					System.out.println("result 삭제 > ");
					System.out.println("chg subsSdate : " + WtmUtil.parseDateStr(oldSubs.getSubsSdate(), "yyyy-MM-dd HH:mm") + "/ subsEdate : " + WtmUtil.parseDateStr(oldSubs.getSubsEdate(), "yyyy-MM-dd HH:mm"));
					
					//변경 전 대체휴일 result 에서 삭제
					wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(tenantId, enterCd, oldSubs.getSubYmd(), otAppl.getSabun(), WtmApplService.TIME_TYPE_SUBS, "", oldSubs.getSubsSdate(), oldSubs.getSubsEdate(), oldSubs.getApplId(), userId);
					
					//cancelYn 변경
					oldSubs.setCancelYn("Y");
					wtmOtSubsApplRepo.save(oldSubs);
					
					//대체휴가 정정을 위한 연장근무 신청서 데이터
					WtmOtAppl newOtAppl = new WtmOtAppl();
					newOtAppl.setApplId(applId);
					newOtAppl.setOtApplId(otAppl.getOtApplId());
					newOtAppl.setYmd(otAppl.getYmd());
					newOtAppl.setSabun(otAppl.getSabun());
					newOtAppl.setOtSdate(otAppl.getOtSdate());
					newOtAppl.setOtEdate(otAppl.getOtEdate());
					otApplList.add(newOtAppl);
					
				}
			}
			
			if(otApplList.size()>0) {
				rp.put("otApplList", otApplList);
				ObjectMapper mapper = new ObjectMapper();
				System.out.println("otApplList : " + mapper.writeValueAsString(otApplList));
			}
		}
		
		//신청서 메인 상태값 업데이트
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_APPR:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		
		appl = wtmApplRepo.save(appl);
		
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.add(applSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "대체휴일정정 신청서가  승인되었습니다.", "N");
		
			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 대체휴일정정신청", "", "N");
		
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
		return null;
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
		
		List<Map<String, Object>> subs = (List<Map<String, Object>>) paramMap.get("subs");
		if(subs != null && subs.size() > 0) {
			Map<String, Object> resultMap = new HashMap<>();
			Map<String, Object> pMap = new HashMap<>();
			pMap.put("tenantId", tenantId);
			pMap.put("enterCd", enterCd);
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("subs : " + mapper.writeValueAsString(subs));
			
			for(Map<String, Object> sub : subs) {
				String subYmd = sub.get("subYmd").toString();
				String subsSdate = sub.get("subsSdate").toString();
				String subsEdate = sub.get("subsEdate").toString();
				
				Date sd = WtmUtil.toDate(subsSdate, "yyyyMMddHHmm");
				Date ed = WtmUtil.toDate(subsEdate, "yyyyMMddHHmm");
				
				WtmOtSubsAppl otSub = new WtmOtSubsAppl();
				otSub.setApplId(applId);
				otSub.setOtApplId(Long.parseLong(paramMap.get("otApplId").toString()));
				otSub.setSubYmd(subYmd);
				otSub.setSubsSdate(sd);
				otSub.setSubsEdate(ed);
				
				if(sub.get("oldSubsApplId")!=null && !"".equals(sub.get("oldSubsApplId")))
					otSub.setOldSubsApplId(Long.parseLong(sub.get("oldSubsApplId").toString()));
				
				String sHm = WtmUtil.parseDateStr(sd, "HHmm");
				String eHm = WtmUtil.parseDateStr(ed, "HHmm");
				pMap.put("ymd", subYmd);
				pMap.put("shm", sHm);
				pMap.put("ehm", eHm);
				pMap.put("sabun", appl.getApplSabun());
				
				//현재 신청할 연장근무 시간 계산
				resultMap.putAll(wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, applSabun, pMap, userId));
				
				otSub.setSubsMinute(resultMap.get("calcMinute").toString());
				otSub.setUpdateId(userId);
				wtmOtSubsApplRepo.save(otSub);
			}
		}
		
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, applSabun, userId);
		
		rp.put("applId", appl.getApplId());
			
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

	@Transactional
	@Override
	public void delete(Long applId) {
		
		wtmOtSubsApplRepo.deleteByApplId(applId);
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
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprovalApplList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		return null;
	}

}
