package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmCompAppl;
import com.isu.ifw.entity.WtmCompCreate;
import com.isu.ifw.repository.WtmCompApplRepository;
import com.isu.ifw.repository.WtmCompCreatelRepository;
import com.isu.ifw.vo.ReturnParam;

@Service("wtmCompApplService")
public class WtmCompApplServiceImpl implements WtmApplService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired private WtmCompApplRepository compApplRepo;
	
	@Autowired private WtmCompCreatelRepository compCreateRepo;
	
	public Map<String, Object> getInitInfo(Long tenantId, String enterCd, String sabun, String ymd){
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		List<WtmCompCreate> comps = compCreateRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, sabun, ymd, ymd);
		
		int sumRestMinute = 0;
		if(comps != null && comps.size() > 0) {
			for(WtmCompCreate cre : comps) {
				sumRestMinute += (cre.getRestMinute() != null)?cre.getRestMinute():0;
			}
			
		}
		resMap.put("restMinute", sumRestMinute);
		return resMap;
	}
	
	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
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
		logger.debug("getApprList :: userId=" + userId);
		
		String sabun = paramMap.get("searchKeyword")+"";
		String compYmd = paramMap.get("ymd")+"";
		
		List<WtmCompAppl> appls = compApplRepo.findByTenantIdAndEnterCdAndSabunOrSabunIsNullAndCompEymdGreaterThanEqualAndCompSymdLessThanEqual(tenantId, enterCd, sabun, compYmd, compYmd);
		if(appls != null) {
			ObjectMapper mapper = new ObjectMapper();
			
			return mapper.convertValue(appls, new ArrayList<Map<String, Object>>().getClass());
		}
			
		return null;
	}

	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd,
			Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun,
			String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
