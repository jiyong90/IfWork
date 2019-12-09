package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.mapper.WtmTaaCodeMapper;
import com.isu.ifw.repository.WtmTaaCodeRepository;

@Service("taaCodeService")
public class WtmTaaCodeServiceImpl implements WtmTaaCodeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmTaaCodeRepository taaCodeRepository;

	@Resource
	WtmTaaCodeMapper taaCodeMapper;

	@Override
	public List<Map<String, Object>> getTaaCodeList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> codeList = new ArrayList();	
		try {
			List<WtmTaaCode> list = taaCodeRepository.findByTenantIdAndEnterCd(tenantId, enterCd, paramMap.containsKey("searchKeyword")?paramMap.get("searchKeyword").toString():"");
			
			for(WtmTaaCode l : list) {
				Map<String, Object> code = new HashMap();
				code.put("tenantId", l.getTenantId());
				code.put("taaCodeId", l.getTaaCodeId());
				code.put("enterCd", l.getEnterCd());
				code.put("taaCd", l.getTaaCd());
				code.put("taaNm", l.getTaaNm());
				code.put("code", l.getTaaCd());
				code.put("codeNm", l.getTaaNm());
				code.put("requestTypeCd", l.getRequestTypeCd());
				code.put("holInclYn", l.getHolInclYn());
				code.put("workYn", l.getWorkYn());
				code.put("workApprHour", l.getWorkApprHour());
				code.put("note", l.getNote());
				code.put("taaTypeCd", l.getTaaTypeCd());
				codeList.add(code);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getTaaCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return codeList;
	}
	
	@Override
	public int setTaaCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmTaaCode> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTaaCode code = new WtmTaaCode();
						code.setEnterCd(enterCd);
						code.setTaaCodeId(l.get("taaCodeId").toString().equals("") ? null : Long.parseLong(l.get("taaCodeId").toString()));
						code.setHolInclYn(l.get("holInclYn").toString());
						code.setRequestTypeCd(l.get("requestTypeCd").toString());
						code.setTaaCd(l.get("taaCd").toString());
						code.setTaaNm(l.get("taaNm").toString());
						code.setTaaTypeCd(l.get("taaTypeCd").toString());
						code.setRequestTypeCd(l.get("requestTypeCd").toString());
						code.setTenantId(tenantId);
						code.setUpdateId(userId);
						code.setNote(l.get("note").toString());
						code.setWorkApprHour(l.get("workApprHour").toString().equals("") ? null : Integer.parseInt(l.get("workApprHour").toString()));
						code.setWorkYn(l.get("workYn").toString());
						codes.add(code);
					}
					codes = taaCodeRepository.saveAll(codes);
					cnt += codes.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmTaaCode> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTaaCode code = new WtmTaaCode();
						code.setTaaCodeId(Long.parseLong(l.get("taaCodeId").toString()));
						codes.add(code);
					}
					taaCodeRepository.deleteAll(codes);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setTaaCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
}