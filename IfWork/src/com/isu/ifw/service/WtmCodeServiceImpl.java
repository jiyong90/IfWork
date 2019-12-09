package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmCode;
import com.isu.ifw.entity.WtmCodeGrp;
import com.isu.ifw.repository.WtmCodeGrpRepository;
import com.isu.ifw.repository.WtmCodeRepository;
import com.isu.ifw.util.WtmUtil;

@Service("codeService")
public class WtmCodeServiceImpl implements WtmCodeService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmCodeRepository codeRepository;
	
	@Resource
	WtmCodeGrpRepository codeGrpRepository; 

	@Override
	public List<Map<String, Object>> getCodeList(Long tenantId, String enterCd, String grpCodeCd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		List<WtmCode> list = codeRepository.findByTenantIdAndEnterCdAndYmdAndGrpCodeCd(tenantId, enterCd, WtmUtil.parseDateStr(new Date(), "yyyyMMdd"), grpCodeCd);
		
		for(WtmCode l : list) {
			Map<String, Object> code = new HashMap();
			code.put("codeId", l.getCodeId());
			code.put("grpCodeCd", l.getGrpCodeCd());
			code.put("code", l.getCodeCd());
			code.put("codeCd", l.getCodeCd());
			code.put("codeNm", l.getCodeNm());
			code.put("symd", l.getSymd());
			code.put("eymd", l.getEymd());
			code.put("seq", l.getSeq());
			code.put("note", l.getNote());
			code.put("tenantId", l.getTenantId());
			code.put("enterCd", l.getEnterCd());
			codeList.add(code);
		}
		return codeList;
	}
	
	@Override
	public List<Map<String, Object>> getCodeListWeb(Long tenantId, String enterCd, String grpCodeCd, String ymd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		List<WtmCode> list = codeRepository.findByTenantIdAndEnterCdAndYmdAndGrpCodeCd(tenantId, enterCd, ymd.replaceAll("-", ""), grpCodeCd);
		
		for(WtmCode l : list) {
			Map<String, Object> code = new HashMap();
			code.put("codeId", l.getCodeId());
			code.put("grpCodeCd", l.getGrpCodeCd());
			code.put("code", l.getCodeCd());
			code.put("codeCd", l.getCodeCd());
			code.put("codeNm", l.getCodeNm());
			code.put("symd", l.getSymd());
			code.put("eymd", l.getEymd());
			code.put("seq", l.getSeq());
			code.put("note", l.getNote());
			code.put("tenantId", l.getTenantId());
			code.put("enterCd", l.getEnterCd());
			codeList.add(code);
		}
		return codeList;
	}
	
	@Override
	public int setCodeList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmCode> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCode code = new WtmCode();
						code.setCodeId(l.get("codeId").toString().equals("") ? null : Long.parseLong(l.get("codeId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setGrpCodeCd(l.get("grpCodeCd").toString());
						code.setCodeCd(l.get("codeCd").toString());
						code.setCodeNm(l.get("codeNm").toString());
						code.setSymd(l.get("symd").toString());
						code.setEymd(l.get("eymd").toString());
						code.setSeq(Integer.parseInt(l.get("seq").toString()));
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						saveList.add(code);
					}
					saveList = codeRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmCode> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCode code = new WtmCode();
						code.setCodeId(Long.parseLong(l.get("codeId").toString()));
						delList.add(code);
					}
					codeRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getCodeGrpList(Long tenantId, String enterCd) {
		List<Map<String, Object>> codeList = new ArrayList();	
		List<WtmCodeGrp> list = codeGrpRepository.findByTenantIdAndEnterCd(tenantId, enterCd);
		
		for(WtmCodeGrp l : list) {
			Map<String, Object> codeGrp = new HashMap();
			codeGrp.put("codeGrpId", l.getCodeGrpId());
			codeGrp.put("tenantId", l.getTenantId());
			codeGrp.put("enterCd", l.getEnterCd());
			codeGrp.put("grpCodeCd", l.getGrpCodeCd());
			codeGrp.put("grpCodeNm", l.getGrpCodeNm());
			codeGrp.put("editYn", l.getEditYn());
			codeGrp.put("usedYn", l.getUsedYn());
			codeGrp.put("note", l.getNote());
			codeList.add(codeGrp);
		}
		return codeList;
	}
	
	@Override
	public int setCodeGrpList(Long tenantId, String enterCd,String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmCodeGrp> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCodeGrp codeGrp = new WtmCodeGrp();
						codeGrp.setCodeGrpId(l.get("codeGrpId").toString().equals("") ? null : Long.parseLong(l.get("codeGrpId").toString()));
						codeGrp.setTenantId(tenantId);
						codeGrp.setEnterCd(enterCd);
						codeGrp.setGrpCodeCd(l.get("grpCodeCd").toString());
						codeGrp.setGrpCodeNm(l.get("grpCodeNm").toString());
						codeGrp.setEditYn(l.get("editYn").toString());
						codeGrp.setUsedYn(l.get("usedYn").toString());
						codeGrp.setNote(l.get("note").toString());
						codeGrp.setUpdateId(userId);
						saveList.add(codeGrp);
					}
					saveList = codeGrpRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmCodeGrp> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmCodeGrp codeGrp = new WtmCodeGrp();
						codeGrp.setTenantId(tenantId);
						codeGrp.setEnterCd(enterCd);
						codeGrp.setGrpCodeCd(l.get("grpCodeCd").toString());
						delList.add(codeGrp);
					}
					codeGrpRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setCodeGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
}