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
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkPattDet;
import com.isu.ifw.mapper.WtmFlexibleStdMapper;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmFlexibleStdVO;

@Transactional
@Service
public class WtmFlexibleStdServiceImpl implements WtmFlexibleStdService {

	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmFlexibleStdMapper flexStdMapper;
	
	@Resource
	WtmFlexibleStdMgrRepository flexibleStdRepository; 
	
	@Resource
	WtmWorkPattDetRepository workPattDetRepository;
	
	@Autowired
	WtmTimeCdMgrRepository timeCdMgrRepo;
	
	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Override
	public List<WtmFlexibleStdVO> getFlexibleStd(Long tenantId, String enterCd, String userKey) {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), null));
		
		return flexStdMapper.getWtmFlexibleStd(paramMap);
	}

	@Override
	public void saveFlexibleStd(Long tenantId, String enterCd, Map<String, Object> optionMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Map<String, Object>> getFlexibleStd(Long tenantId, String enterCd) {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		return flexStdMapper.getWtmFlexibleStdList(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getFlexibleStdWorkType(Long tenantId, String enterCd, String workTypeCd) {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("workTypeCd", workTypeCd);
		
		return flexStdMapper.getWtmFlexibleStdWorkTypeList(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getStdListWeb(Long tenantId, String enterCd, String ymd) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("ymd", ymd);
		List<Map<String, Object>> stdList = flexStdMapper.getStdListWeb(paramMap);
				// flexibleStdRepository.findByTenantIdAndEnterCdAndYmd(tenantId, enterCd, ymd);

		return stdList;
	}
	
	@Override
	public int setStdListWeb(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
				List<Map<String, Object>> insertList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("tenantId", tenantId);
						saveMap.put("enterCd", enterCd);
						String workTypeCd = l.get("workTypeCd").toString();
						saveMap.put("workTypeCd", workTypeCd);
						saveMap.put("flexibleNm", l.get("flexibleNm").toString());
						saveMap.put("useSymd", l.get("useSymd").toString());
						saveMap.put("useEymd", l.get("useEymd").toString());
						if("BASE".equals(workTypeCd) || "WORKTEAM".equals(workTypeCd)) {
							saveMap.put("baseWorkYn", "Y");
						} else {
							saveMap.put("baseWorkYn", "N");
						}
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						insertList.add(saveMap);
					}
				}
				
				if(insertList != null && insertList.size() > 0) {
					System.out.println("insertList : " + insertList.size());
					cnt = flexStdMapper.insertFlexibleStd(insertList);
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				List<Map<String, Object>> updateList = new ArrayList(); // 수정용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleStdMgrId", Long.parseLong(l.get("flexibleStdMgrId").toString()));
						saveMap.put("useEymd", l.get("useEymd").toString());
						saveMap.put("holExceptYn", l.get("holExceptYn").toString());
						saveMap.put("fixotUseType", l.get("fixotUseType").toString());
						saveMap.put("fixotUseLimit", l.get("fixotUseLimit").toString().equals("") ? null : Integer.parseInt(l.get("fixotUseLimit").toString()));
						saveMap.put("workShm", l.get("workShm").toString());
						saveMap.put("workEhm", l.get("workEhm").toString());
						saveMap.put("coreShm", l.get("coreShm").toString());
						saveMap.put("coreEhm", l.get("coreEhm").toString());
						saveMap.put("coreChkYn", l.get("coreChkYn").toString());
						saveMap.put("exhaustionYn", l.get("exhaustionYn").toString());
						saveMap.put("usedTermOpt", l.get("usedTermOpt").toString());
						saveMap.put("applTermOpt", l.get("applTermOpt").toString());
						saveMap.put("regardTimeCdId", Integer.parseInt(l.get("regardTimeCdId").toString()));
						saveMap.put("defaultWorkUseYn", l.get("defaultWorkUseYn").toString());
						saveMap.put("unitMinute", l.get("unitMinute").toString().equals("") ? null : Integer.parseInt(l.get("unitMinute").toString()));
						saveMap.put("taaTimeYn", l.get("taaTimeYn").toString());
						saveMap.put("taaWorkYn", l.get("taaWorkYn").toString());
						saveMap.put("dayOpenType", l.get("dayOpenType").toString());
						saveMap.put("dayCloseType", l.get("dayCloseType").toString());
						saveMap.put("unplannedYn", l.get("unplannedYn").toString());
						saveMap.put("applYn", l.get("applYn").toString());
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						updateList.add(saveMap);
					}
				}
				
				if(updateList != null && updateList.size() > 0) {
					System.out.println("updateList : " + updateList.size());
					cnt = flexStdMapper.updateFlexibleStd(updateList);
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setWorkPattList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getWorkPattList(Long flexibleStdMgrId) {
		List<Map<String, Object>> workPattList = new ArrayList();	
		List<WtmWorkPattDet> list = workPattDetRepository.findByFlexibleStdMgrId(flexibleStdMgrId);

		for(WtmWorkPattDet l : list) {
			Map<String, Object> workPatt = new HashMap();
			workPatt.put("workPattDetId", l.getWorkPattDetId());
			workPatt.put("flexibleStdMgrId", l.getFlexibleStdMgrId());
			workPatt.put("seq", l.getSeq());
			workPatt.put("timeCdMgrId", l.getTimeCdMgrId());
			
			WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(l.getTimeCdMgrId()).get();
			
			double subGrp = l.getSeq()%7==0 ? Math.floor(l.getSeq()/7) : Math.floor(l.getSeq()/7)+1;
			workPatt.put("subGrp", subGrp);
			
			workPatt.put("planShm", l.getPlanShm());
			workPatt.put("planEhm", l.getPlanEhm());

			if(timeCdMgr.getHolYn()!=null && !"Y".equals(timeCdMgr.getHolYn()))
				workPatt.put("planMinute", l.getPlanMinute());
			else 
				workPatt.put("planMinute", "");
			
			workPatt.put("otbMinute", l.getOtbMinute());
			workPatt.put("otaMinute", l.getOtaMinute());
			
			int otMinute = 0;
			
			if(timeCdMgr.getHolYn()!=null && "Y".equals(timeCdMgr.getHolYn()) && l.getPlanMinute()!=null) {
				otMinute = l.getPlanMinute();
			} 
			
			if(otMinute!=0) {
				workPatt.put("otMinute", otMinute);
			} else {
				workPatt.put("otMinute", "");
			}
			
			workPatt.put("note", l.getNote());
			workPattList.add(workPatt);

		}
		
		return workPattList;
	}
	
	@Override
	public int setWorkPattList(String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmWorkPattDet> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkPattDet code = new WtmWorkPattDet();
						code.setWorkPattDetId(l.get("workPattDetId").toString().equals("") ? null : Long.parseLong(l.get("workPattDetId").toString()));
						code.setFlexibleStdMgrId(Long.parseLong(l.get("flexibleStdMgrId").toString()));
						code.setSeq(Integer.parseInt(l.get("seq").toString()));
						code.setTimeCdMgrId(Long.parseLong(l.get("timeCdMgrId").toString()));
						
						WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(Long.parseLong(l.get("timeCdMgrId").toString())).get();
						code.setHolidayYn(timeCdMgr.getHolYn());
						
						String planShm = "";
						String planEhm = "";
						if(l.get("planShm")!=null && !"".equals(l.get("planShm"))) {
							planShm = l.get("planShm").toString();
							code.setPlanShm(planShm);
						}
						if(l.get("planEhm")!=null && !"".equals(l.get("planEhm"))) {
							planEhm = l.get("planEhm").toString();
							code.setPlanEhm(planEhm);
						}
							
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("shm", planShm);
						paramMap.put("ehm", planEhm);
						Map<String, Object> cMap = flexibleEmpService.calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), paramMap, userId);
						if(cMap!=null && cMap.get("calcMinute")!=null) {
							code.setPlanMinute(Integer.parseInt(cMap.get("calcMinute").toString()));
						}
						
						if(l.get("otbMinute")!=null && !"".equals(l.get("otbMinute"))) {
							code.setOtbMinute(Integer.parseInt(l.get("otbMinute").toString()));
						}
						
						if(l.get("otaMinute")!=null && !"".equals(l.get("otaMinute"))) {
							code.setOtaMinute(Integer.parseInt(l.get("otaMinute").toString()));
						}
						
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						saveList.add(code);
					}
					saveList = workPattDetRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmWorkPattDet> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmWorkPattDet code = new WtmWorkPattDet();
						code.setWorkPattDetId(Long.parseLong(l.get("workPattDetId").toString()));
						delList.add(code);
					}
					workPattDetRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setWorkPattList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public Map<String, Object> getWeekday(String ymd) {
		return flexStdMapper.getWeekday(ymd);
	}
	
}
