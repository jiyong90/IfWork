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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmWorkteamEmp;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorkteamEmpMapper;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.option.vo.ReturnParam;

@Service
public class WtmWorkteamEmpServiceImpl implements WtmWorkteamEmpService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	
	@Resource
	WtmWorkteamEmpRepository workteamRepository;

	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmWorkteamEmpMapper workteamEmpMapper;
	
	@Override
	public List<Map<String, Object>> getWorkteamList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> timeList = new ArrayList();	
		try {
			/*List<Map<String, Object>> list = workteamRepository.findByTenantIdAndEnterCd(tenantId, enterCd, paramMap.get("sYmd").toString(), paramMap.get("searchKeyword").toString());
			
			for(Map<String, Object> l : list) {
				Map<String, Object> time = new HashMap();
				time.put("workteamEmpId", l.get("workteamEmpId"));
				time.put("workteamMgrId", l.get("workteamMgrId"));
				time.put("sabun", l.get("sabun"));
				time.put("symd", l.get("symd"));
				time.put("eymd", l.get("eymd"));
				time.put("note", l.get("note"));
				time.put("orgCd", l.get("orgCd"));
				time.put("classCd", l.get("classCd"));
				time.put("empNm", l.get("empNm"));
				timeList.add(time);
			}*/
			
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			
			String ymd = null;
			if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
				ymd = paramMap.get("sYmd").toString().replaceAll("-", "");
			} else {
				ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			}
			paramMap.put("ymd", ymd);
			
			timeList = workteamEmpMapper.getWorkteamEmpList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		} finally {
			MDC.clear();
			logger.debug("getWorkteamList Controller End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));

		}
		
		return timeList;
	}
	
	@Override
	public ReturnParam setWorkteamList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		ReturnParam rp = new ReturnParam();
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
 
		rp.setSuccess("저장에 성공하였습니다.");
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				//List<WtmWorkteamEmp> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkteamEmp workteam = new WtmWorkteamEmp();
						workteam.setUpdateId(userId);
						workteam.setSabun(l.get("sabun").toString());
						workteam.setWorkteamEmpId(l.get("workteamEmpId").toString().equals("") ? null : Long.parseLong(l.get("workteamEmpId").toString()));
						workteam.setWorkteamMgrId(Long.parseLong(l.get("workteamMgrId").toString()));
						workteam.setEymd(l.get("eymd").toString());
						workteam.setNote(l.get("note").toString());
						workteam.setSymd(l.get("symd").toString());
						
						//List<Map<String, Object>> dup = workteamRepository.dupCheckByYmd(tenantId, enterCd, l.get("sabun").toString(), l.get("workteamEmpId").toString(), l.get("symd").toString(), l.get("eymd").toString());
						
						paramMap.put("sabun", l.get("sabun").toString());
						paramMap.put("workteamEmpId", l.get("workteamEmpId").toString());
						paramMap.put("sYmd", l.get("symd").toString());
						paramMap.put("eYmd", l.get("eymd").toString());
						List<Map<String, Object>> dup = workteamEmpMapper.dupCheckByYmd(paramMap);
						if(dup != null && dup.size() > 0) {
							rp.setFail("중복된 기간이 존재합니다. (sabun : " + l.get("sabun").toString() + ")");
							return rp;
						}
						//saveList.add(workteam);
						workteam = workteamRepository.save(workteam);
						
						paramMap = new HashMap();
						paramMap.put("workteamMgrId", workteam.getWorkteamMgrId());
						paramMap.put("sabun", workteam.getSabun());
						paramMap.put("pId", userId);
						
						flexEmpMapper.resetWtmWorkteamOfWtmWorkDayResult(paramMap);
						cnt++;
					}
					//saveList = workteamRepository.saveAll(saveList);
					//cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
			cnt = 0;
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				//List<WtmWorkteamEmp> deleteList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkteamEmp workteam = new WtmWorkteamEmp();
						workteam.setWorkteamEmpId(Long.parseLong(l.get("workteamEmpId").toString()));
						//deleteList.add(workteam);
						workteamRepository.delete(workteam);
						paramMap.put("workteamMgrId", l.get("workteamMgrId").toString());
						paramMap.put("sabun", l.get("sabun").toString());
						paramMap.put("pId", userId);
						
						flexEmpMapper.resetWtmWorkteamOfWtmWorkDayResult(paramMap);
						cnt++;
					}
				}
				
				MDC.put("delete cnt", "" + cnt);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
			rp.setFail("저장에 실패하였습니다.");
		} finally {
			logger.debug("setTaaCodeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return rp;
	}

}