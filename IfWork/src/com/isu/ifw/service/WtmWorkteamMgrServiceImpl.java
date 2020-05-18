package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmWorkteamEmp;
import com.isu.ifw.entity.WtmWorkteamMgr;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorkteamEmpMapper;
import com.isu.ifw.mapper.WtmWorkteamMgrMapper;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.repository.WtmWorkteamMgrRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("workteamMgrService")
public class WtmWorkteamMgrServiceImpl implements WtmWorkteamMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Resource
	WtmWorkteamMgrRepository workteamMgrRepository;

	@Resource
	WtmWorkteamEmpRepository workteamEmpRepository;
	
	@Autowired
	WtmWorkteamMgrMapper workteamMgrMapper;
	
	@Autowired
	WtmWorkteamEmpMapper workteamEmpMapper;

	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	
	@Override
	public List<Map<String, Object>> getWorkteamMgrList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = new ArrayList();	
		//List<WtmWorkteamMgr> list = workteamMgrRepository.findByTenantIdAndEnterCd(tenantId, enterCd, paramMap.get("sYmd").toString());
		
		String sYmd = null;
		if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
			sYmd = paramMap.get("sYmd").toString().replaceAll("-", "");
		} else {
			sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		}
		List<WtmWorkteamMgr> list = workteamMgrRepository.findByTenantIdAndEnterCdAndSymd(tenantId, enterCd, sYmd);
		
		for(WtmWorkteamMgr l : list) {
			Map<String, Object> workteam = new HashMap();
			workteam.put("workteamMgrId", l.getWorkteamMgrId());
			workteam.put("workteamNm", l.getWorkteamNm());
			workteam.put("flexibleStdMgrId", l.getFlexibleStdMgrId());
			workteam.put("symd", l.getSymd());
			workteam.put("eymd", l.getEymd());
			workteam.put("note", l.getNote());
			searchList.add(workteam);
		}
		return searchList;
	}
	
	@Override
	public ReturnParam setWorkteamMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("저장에 성공하였습니다.");
		
		int cnt = 0;
		Map<String, Object> paramMap = new HashMap();
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				//List<WtmWorkteamMgr> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkteamMgr workteam = null;
						if(!l.get("workteamMgrId").equals("")) {
							workteam = workteamMgrRepository.findByWorkteamMgrId(Long.parseLong(l.get("workteamMgrId").toString()));
						}
						
						if(workteam != null) { //update는 종료일 변경, 비고만 가능
							workteam.setNote(l.get("note").toString());
							workteam.setEymd(l.get("eymd").toString());
							//근무조 대상자 중 해당 근무조를사용중인 직원의 종료일이 더 크면 변경 불가
							int emp = workteamEmpMapper.getWorkteamMgrIdAndEymd(l);
							if(emp > 0) {
								throw new Exception(workteam.getWorkteamNm() +" 가 할당된 직원중 종료일이 남은 직원이 종료합니다. 먼저 해당직원의 근무조 정보를  변경해주세요.");
							}
						} else {               //insert
							workteam = new WtmWorkteamMgr();
							workteam.setEnterCd(enterCd);
							workteam.setTenantId(tenantId);
							workteam.setUpdateId(userId);
							workteam.setWorkteamMgrId(l.get("workteamMgrId").toString().equals("") ? null : Long.parseLong(l.get("workteamMgrId").toString()));
							workteam.setWorkteamNm(l.get("workteamNm").toString());
							workteam.setEymd(l.get("eymd").toString());
							workteam.setNote(l.get("note").toString());
							workteam.setSymd(l.get("symd").toString());
							workteam.setFlexibleStdMgrId(Long.parseLong(l.get("flexibleStdMgrId").toString()));
						}
						
						workteam = workteamMgrRepository.save(workteam);

//						paramMap.put("workteamMgrId", workteam.getWorkteamMgrId());
//						paramMap.put("sabun", "");
//						paramMap.put("pId", userId);
//						
//						flexEmpMapper.resetWtmWorkteamOfWtmWorkDayResult(paramMap);
						cnt++;
					}
					//saveList = workteamMgrRepository.saveAll(saveList);
					//cnt += saveList.size();
				}
				
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmWorkteamMgr> deleteList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkteamMgr workteam = workteamMgrRepository.findByWorkteamMgrId(Long.parseLong(l.get("workteamMgrId").toString()));
						if(workteam == null) {
							throw new Exception("근무조 정보가 존재하지 않습니다. 화면 갱신 후 다시 작업해주세요.");
						}
						//deleteList.add(workteam);
						//workteamMgrId가 걸려있는 데이터가있는지 확인
						List<WtmWorkteamEmp> empList = workteamEmpRepository.findByWorkteamMgrId(workteam.getWorkteamMgrId());
						if(empList !=null && empList.size() > 0) {
							throw new Exception(workteam.getWorkteamNm() +" 가 할당된 직원 정보가 존재합니다. 할당된 적이 없는 근무조만 삭제 가능합니다.");
						}
						
						workteamMgrRepository.delete(workteam);
						
//						paramMap.put("workteamMgrId", l.get("workteamMgrId").toString());
//						paramMap.put("sabun", "");
//						paramMap.put("pId", userId);
//						
//						flexEmpMapper.resetWtmWorkteamOfWtmWorkDayResult(paramMap);
						cnt++;
					}
//					workteamMgrRepository.deleteAll(deleteList);
				}
				
				cnt += iList.size();
			}
			logger.debug("setWorkteamMgrList cnt " + cnt);
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			rp.setFail(e.getMessage());
		} 
		return rp;
	}
	
	@Override
	public List<Map<String, Object>> getWorkteamCdList(Long tenantId, String enterCd) {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		return workteamMgrMapper.getWorkteamCdList(paramMap);
	}
}