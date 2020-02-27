package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.mapper.WtmEntryApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.mapper.WtmValidatorMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service
public class WtmValidatorServiceImpl implements WtmValidatorService  {

	@Autowired
	WtmFlexibleStdMapper flexStdMapper;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmValidatorMapper validatorMapper;
	
	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;

	@Autowired
	WtmTaaCodeRepository taaCodeRepo;
	
	@Autowired
	WtmEntryApplMapper entryApplMapper;
	
	@Override
	public ReturnParam validTaa(Long tenantId, String enterCd, String sabun,
			String timeTypeCd, String taaCd,
			String symd, String shm, String eymd, String ehm, Long applId, String locale) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		paramMap.put("applId", applId);
		paramMap.put("sabun", sabun); 
		if(taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_H)) {
			//근태가 시간단위 일때 시분 정보가 누락되지 않았는지 체크한다.
			if(shm.equals("") || ehm.equals("")) {
				rp.setFail("시간단위 근태의 경우 시간 정보가 필요합니다.");
				return rp;
			}
			paramMap.put("timeTypeCd", timeTypeCd);
			paramMap.put("requestTypeCd", taaCode.getRequestTypeCd());
			paramMap.put("sdate", symd + shm);
			paramMap.put("edate", eymd + ehm);
			Map<String, Object> m = validatorMapper.checkDuplicateTaaByTaaTypeH(paramMap);
			int cnt = Integer.parseInt(m.get("cnt").toString());
			
			if(cnt > 0) {
				rp.setFail("이미 해당일에 중복된 근태 정보가 존재합니다.");
				return rp;
			}
			
			
		}else {
			//신청하려는근태 코드와 타입 종일 반일(오전/오후) 등 해당 건은 같은날 중복해서 들어갈 수 없다 체케럽
			paramMap.put("timeTypeCd", timeTypeCd);
			paramMap.put("requestTypeCd", taaCode.getRequestTypeCd());
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			Map<String, Object> m = validatorMapper.checkDuplicateTaa(paramMap);
			int cnt = Integer.parseInt(m.get("cnt").toString());
			
			if(cnt > 0) {
				rp.setFail("이미 해당일에 중복된 근태 정보가 존재합니다.");
				return rp;
			}
		} 
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		paramMap.put("sabun", sabun); 
		int workCnt = validatorMapper.getWorkCnt(paramMap);
		
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("workdayCnt", workCnt);
		rp.put("result", resultMap);
		
		return rp;
	}	
	
	
	@Override
	public ReturnParam checkDuplicateFlexibleWork(Long tenantId, String enterCd, String sabun, String symd, String eymd, Long applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		paramMap.put("applId", applId);
		
		Map<String, Object> m = flexStdMapper.checkRequestDate(paramMap);
		int cnt = Integer.parseInt(m.get("cnt").toString());
		if(cnt > 0) {
			rp.setFail("신청중인 또는 이미 적용된 근무정보가 있습니다.");
			return rp;
		}
		return rp;
	}

	@Override
	public ReturnParam checkDuplicateWorktime(Long tenantId, String enterCd, String sabun, String sdate, String edate, Long applId){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sdate", sdate);
		paramMap.put("edate", edate);
		paramMap.put("applId", applId);

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> m = flexEmpMapper.checkDuplicateWorktime(paramMap);
		int cnt = Integer.parseInt(m.get("workCnt").toString());
		if(cnt > 0) {
			rp.setFail("신청중인 또는 이미 적용된 근무정보가 있습니다.");
			return rp;
		}
		return rp;
		 
	}
	
	@Override
	public ReturnParam checkDuplicateEntryAppl(Long tenantId, String enterCd, String sabun, String ymd, Long applId){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", ymd);
		paramMap.put("applId", applId);

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		Map<String, Object> m = entryApplMapper.checkDuplicateEntryAppl(paramMap);
		int cnt = Integer.parseInt(m.get("chgCnt").toString());
		if(cnt > 0) {
			rp.setFail("이미 신청중인 근태사유서가 존재합니다.");
			return rp;
		}
		
		return rp;
	}
	
	@Override
	public ReturnParam worktimeValid(Long tenantId, String enterCd, List<Map<String, Object>> workList, Long applId) {
		ReturnParam rp = new ReturnParam();
		
		if(workList!=null && workList.size()>0) {
			for(Map<String, Object> workMap : workList) {
				
				if(workMap.get("sabun")!=null && !"".equals(workMap.get("sabun")) && workMap.get("works")!=null && !"".equals(workMap.get("works"))) {
					String sabun = workMap.get("sabun").toString();
					List<Map<String, Object>> works = (List<Map<String, Object>>)workMap.get("works");
					
					if(works!=null && works.size()>0) {
						for(Map<String, Object> work : works) {
							if(work.get("symd")==null || "".equals(work.get("symd")) ||work.get("eymd")==null || "".equals(work.get("eymd"))) {
								rp.setFail(sabun+"의 일자 정보가 없습니다.");
								return rp;
							}
							
							String symd = work.get("symd").toString();
							String eymd = work.get("eymd").toString();
							
							if(symd.length() != eymd.length()) {
								rp.setFail("시작일과 종료일의 날짜 포맷이 맞지 않습니다.");
								return rp;
							}
							
							if(symd.length()!=8) {
								rp.setFail("일자는 8자리 입니다.");
								return rp;
							} 
							
							Date sd = WtmUtil.toDate(symd, "yyyyMMdd");
							Date ed = WtmUtil.toDate(eymd, "yyyyMMdd");
							
							if(sd.compareTo(ed) > 0) {
								rp.setFail("시작일자가 종료일자보다 큽니다.");
								return rp;
							}
							
							String shm = "";
							String ehm = "";
							if(work.get("shm")!=null && !"".equals(work.get("shm"))) 
								shm = work.get("shm").toString();
							if(work.get("ehm")!=null && !"".equals(work.get("ehm"))) 
								shm = work.get("ehm").toString();
							
							//신청서 중복 체크
							rp = checkDuplicateTaaAppl(tenantId, enterCd, sabun, symd, eymd, shm, ehm, applId);
							
							if(rp.getStatus()!=null && !"OK".equals(rp.getStatus()))
								return rp;
							
							//근무시간 체크
							rp = checkWorktimeTaaAppl(tenantId, enterCd, sabun, symd, eymd, shm, ehm, applId);
						}
					}
				}
				
			}
		}
	
		return rp;
	}
	
	protected ReturnParam checkDuplicateTaaAppl(Long tenantId, String enterCd, String sabun, String symd, String eymd, String shm, String ehm, Long applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("applId", applId);
		
		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);
		
		paramMap.put("shm", shm);
		paramMap.put("ehm", ehm);
		
		Map<String, Object> m = validatorMapper.checkDuplicateTaaAppl(paramMap);
		if(m!=null && m.get("cnt")!=null) {
			int cnt = Integer.parseInt(m.get("cnt").toString());
			if(cnt > 0) {
				rp.setFail(m.get("empNm").toString()+"("+m.get("sabun").toString()+")의 신청중인 또는 이미 적용된 근무정보가 있습니다.");
				return rp;
			}
		}
		
		return rp;
	}
	
	protected ReturnParam checkWorktimeTaaAppl(Long tenantId, String enterCd, String sabun, String symd, String eymd, String shm, String ehm, Long applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		//1.근무 계획 시간보다 근태 신청 시간이 더 큰지 체크
		
		//2.근로시간 넘는지 체크
		
		return rp;
	}
	
}
