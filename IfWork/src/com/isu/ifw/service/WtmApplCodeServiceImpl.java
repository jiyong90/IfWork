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

import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.repository.WtmApplCodeRepository;

@Service("applCodeService")
public class WtmApplCodeServiceImpl implements WtmApplCodeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmApplCodeRepository applCodeRepository;

	@Override
	public List<Map<String, Object>> getApplCodeList(Long tenantId, String enterCd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		try {
			List<WtmApplCode> list = applCodeRepository.findByTenantIdAndEnterCd(tenantId, enterCd);
			
			for(WtmApplCode l : list) {
				Map<String, Object> code = new HashMap();
				code.put("applCodeId", l.getApplCodeId());
				code.put("tenantId", l.getTenantId());
				code.put("enterCd", l.getEnterCd());
				code.put("applCd", l.getApplCd());
				code.put("applNm", l.getApplNm());
				code.put("applLevelCd", l.getApplLevelCd());
				code.put("recLevelCd", l.getRecLevelCd());
				code.put("timeUnit", l.getTimeUnit());
				code.put("useMinutes", l.getUseMinutes());
				code.put("inShm", l.getInShm());
				code.put("inEhm", l.getInEhm());
				code.put("holInShm", l.getHolInShm());
				code.put("holInEhm", l.getHolInEhm());
				code.put("holApplTypeCd", l.getHolApplTypeCd());
				code.put("holMaxMinute", l.getHolMaxMinute());
				code.put("subsYn", l.getSubsYn());
				code.put("subsRuleId", l.getSubsRuleId());
				code.put("subsSday", l.getSubsSday());
				code.put("subsEday", l.getSubsEday());
				code.put("note", l.getNote());
				codeList.add(code);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return codeList;
	}
	
	@Override
	public int setApplCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmApplCode> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmApplCode code = new WtmApplCode();
						code.setApplCodeId(l.get("applCodeId").toString().equals("") ? null : Long.parseLong(l.get("applCodeId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setApplCd(l.get("applCd").toString());
						code.setApplNm(l.get("applNm").toString());
						code.setApplLevelCd(l.get("applLevelCd").toString());
						code.setRecLevelCd(l.get("recLevelCd").toString());
						code.setTimeUnit(l.get("timeUnit").toString());
						code.setUseMinutes(l.get("useMinutes").toString());
						code.setInShm(l.get("inShm").toString());
						code.setInEhm(l.get("inEhm").toString());
						code.setSubsYn(l.get("subsYn").toString());
						code.setSubsRuleId(l.get("subsRuleId").toString().equals("") ? null : Long.parseLong(l.get("subsRuleId").toString()));
						code.setSubsSday(Integer.parseInt(l.get("subsSday").toString()));
						code.setSubsEday(Integer.parseInt(l.get("subsEday").toString()));
						code.setHolInShm(l.get("holInShm").toString());
						code.setHolInEhm(l.get("holInEhm").toString());
						code.setHolApplTypeCd(l.get("holApplTypeCd").toString());
						code.setHolMaxMinute(l.get("holMaxMinute").toString().equals("") ? null : Integer.parseInt(l.get("holMaxMinute").toString()));
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						codes.add(code);
					}
					codes = applCodeRepository.saveAll(codes);
					cnt += codes.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmApplCode> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmApplCode code = new WtmApplCode();
						code.setApplCodeId(Long.parseLong(l.get("applCodeId").toString()));
						codes.add(code);
					}
					applCodeRepository.deleteAll(codes);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setApplCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
}