package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmEmpHisMapper;
import com.isu.ifw.mapper.WtmIfEmpMsgMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmIfEmpMsgRepository;
import com.isu.ifw.util.WtmUtil;


@Service("empMgrService")
public class WtmEmpMgrServiceImpl implements WtmEmpMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmEmpHisRepository empHisRepository;
	
	@Resource
	WtmIfEmpMsgRepository empMsgRepository;
	
	@Autowired
	WtmIfEmpMsgMapper ifEmpMsgMapper;
	
	@Autowired
	WtmEmpHisMapper wtmEmpHisMapper;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Override
	public List<Map<String, Object>> getEmpHisList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> empList = new ArrayList();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		if(!paramMap.containsKey("sYmd")) {
			paramMap.put("sYmd", "");
		} else {
			sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
		}
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
		}
		
		empList =  wtmEmpHisMapper.getEmpHisList(paramMap);
		
//		List<WtmEmpHis> list = empHisRepository.findByTenantIdAndEnterCd(tenantId, enterCd, paramMap.containsKey("sYmd")?paramMap.get("sYmd").toString():"", paramMap.get("searchKeyword").toString());
//		
//		for(WtmEmpHis l : list) {
//			Map<String, Object> emp = new HashMap();
//			emp.put("empHisId", l.getEmpHisId());
//			emp.put("sabun", l.getSabun());
//			emp.put("empNm", l.getEmpNm());
//			emp.put("empEngNm", l.getEmpEngNm());
//			emp.put("symd", l.getSymd());
//			emp.put("eymd", l.getEymd());
//			emp.put("statusCd", l.getStatusCd());
//			emp.put("orgCd", l.getOrgCd());
//			emp.put("businessPlaceCd", l.getBusinessPlaceCd());
//			emp.put("dutyCd", l.getDutyCd());
//			emp.put("posCd", l.getPosCd());
//			emp.put("classCd", l.getClassCd());
//			emp.put("jobGroupCd", l.getJobGroupCd());
//			emp.put("jobCd", l.getJobCd());
//			emp.put("payTypeCd", l.getPayTypeCd());
//			emp.put("leaderYn", l.getLeaderYn());
//			emp.put("note", l.getNote());
//			empList.add(emp);
//		}
		return empList;
	}

	@Override
	public Map<String, Object> getEmpHis(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		
		Map<String, Object> emp = new HashMap();
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, WtmUtil.parseDateStr(new Date(), "yyyyMMdd")));
		}
		
		emp = wtmEmpHisMapper.getEmpHis(paramMap);
//		WtmEmpHis l = empHisRepository.findByEmpHisId(Long.parseLong(paramMap.get("empHisId").toString()));
//		emp.put("sabun", l.getSabun());
//		emp.put("empNm", l.getEmpNm());
//		emp.put("empEngNm", l.getEmpEngNm());
//		emp.put("symd", l.getSymd());
//		emp.put("eymd", l.getEymd());
//		emp.put("statusCd", l.getStatusCd());
//		emp.put("orgCd", l.getOrgCd());
//		emp.put("businessPlaceCd", l.getBusinessPlaceCd());
//		emp.put("dutyCd", l.getDutyCd());
//		emp.put("posCd", l.getPosCd());
//		emp.put("classCd", l.getClassCd());
//		emp.put("jobGroupCd", l.getJobGroupCd());
//		emp.put("jobCd", l.getJobCd());
//		emp.put("payTypeCd", l.getPayTypeCd());
//		emp.put("leaderYn", l.getLeaderYn());
//		emp.put("note", l.getNote());
		return emp;
	}
	
	@Override
	public List<Map<String, Object>> getEmpIfMsgList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> empList = new ArrayList();
		
		String ymd = null;
		if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
			ymd = paramMap.get("sYmd").toString().replaceAll("-", "");
		} else {
			ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		}
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("ymd", ymd);
		
		/*List<WtmIfEmpMsg> list = empMsgRepository.findByTenantIdAndEnterCd(tenantId, enterCd, s, paramMap.get("searchKeyword").toString());
		
		for(WtmIfEmpMsg l : list) {
			Map<String, Object> emp = new HashMap();
			emp.put("sabun", l.getSabun());
			emp.put("chgYmd", l.getChgYmd());
			emp.put("chgTypeCd", l.getChgTypeCd());
			emp.put("oldValue", l.getOldValue());
			emp.put("newValue", l.getNewValue());
			emp.put("note", l.getNote());
			empList.add(emp);
		}
		return empList; */
		
		return ifEmpMsgMapper.getIfEmpMsg(paramMap);
		
	}
}