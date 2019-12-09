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

import com.isu.ifw.entity.WtmTimeBreakMgr;
import com.isu.ifw.entity.WtmTimeBreakTime;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.mapper.WtmTimeCdMgrMapper;
import com.isu.ifw.repository.WtmTimeBreakMgrRepository;
import com.isu.ifw.repository.WtmTimeBreakTimeRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.util.WtmUtil;

@Transactional
@Service
public class WtmTimeCdMgrServiceImpl implements WtmTimeCdMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmTimeCdMgrMapper timeCdMgrMapper;
	@Resource
	WtmTimeCdMgrRepository timeCdMgrRepository;
	@Resource
	WtmTimeBreakMgrRepository timeBreakMgrRepository; 
	@Resource
	WtmTimeBreakTimeRepository timeBreakTimeRepository;
	
	

	@Override
	public List<Map<String, Object>> getTimeCdMgrList(Long tenantId, String enterCd,  Map<String, Object> paramMap) {
		/*List<Map<String, Object>> timeList = new ArrayList();	
		List<WtmTimeCdMgr> list = timeCdMgrRepository.findByTenantIdAndEnterCd(tenantId, enterCd,paramMap.containsKey("sYmd")?paramMap.get("sYmd").toString():"");
		
		for(WtmTimeCdMgr l : list) {
			Map<String, Object> time = new HashMap();
			time.put("timeCdMgrId", l.getTimeCdMgrId());
			time.put("code", l.getTimeCdMgrId());
			time.put("codeNm", l.getTimeNm());
			time.put("timeNm", l.getTimeNm());
			time.put("timeCd", l.getTimeCd());
			time.put("symd", l.getSymd());
			time.put("eymd", l.getEymd());
			time.put("workShm", l.getWorkShm());
			time.put("workEhm", l.getWorkEhm());
			time.put("holYn", l.getHolYn());
			time.put("holTimeCdMgrId", l.getHolTimeCdMgrId());
			time.put("lateChkYn", l.getLateChkYn());
			time.put("leaveChkYn", l.getLeaveChkYn());
			time.put("absenceChkYn", l.getAbsenceChkYn());
			time.put("note", l.getNote());
			timeList.add(time);
		}
		return timeList;*/
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		String ymd = null;
		if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
			ymd = paramMap.get("sYmd").toString().replaceAll("-", "");
		} else {
			ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		}
		paramMap.put("ymd", ymd);
		
		return timeCdMgrMapper.getTimeCdMgrList(paramMap);
	}
	
	@Override
	public int setTimeCodeMgrList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmTimeCdMgr> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeCdMgr code = new WtmTimeCdMgr();
						code.setTimeCdMgrId(l.get("timeCdMgrId").toString().equals("") ? null : Long.parseLong(l.get("timeCdMgrId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setTimeCd(l.get("timeCd").toString());
						code.setTimeNm(l.get("timeNm").toString());
						code.setSymd(l.get("symd").toString());
						code.setEymd(l.get("eymd").toString());
						code.setWorkShm(l.get("workShm").toString());
						code.setWorkEhm(l.get("workEhm").toString());
						code.setHolYn(l.get("holYn").toString());
						code.setBreakTypeCd(l.get("breakTypeCd").toString());
						code.setHolTimeCdMgrId(l.get("holTimeCdMgrId").toString().equals("") ? null : Long.parseLong(l.get("holTimeCdMgrId").toString()));
						code.setLateChkYn(l.get("lateChkYn").toString());
						code.setLeaveChkYn(l.get("leaveChkYn").toString());
						code.setAbsenceChkYn(l.get("absenceChkYn").toString());
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						saveList.add(code);
					}
					saveList = timeCdMgrRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmTimeCdMgr> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeCdMgr code = new WtmTimeCdMgr();
						code.setTimeCdMgrId(Long.parseLong(l.get("timeCdMgrId").toString()));
						delList.add(code);
					}
					timeCdMgrRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setTimeCodeMgrList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getTimeBreakMgrList(Long tenantId, String enterCd,  Map<String, Object> paramMap) {
		List<Map<String, Object>> timeBreakList = new ArrayList();	
		System.out.println("timecdmgrid : " + paramMap.get("timeCdMgrId").toString());
		List<WtmTimeBreakMgr> list = timeBreakMgrRepository.findByTimeCdMgrId(paramMap.get("timeCdMgrId").toString());
		
		for(WtmTimeBreakMgr l : list) {
			Map<String, Object> timeBreak = new HashMap();
			timeBreak.put("timeBreakMgrId", l.getTimeBreakMgrId());
			timeBreak.put("timeCdMgrId", l.getTimeCdMgrId());
			timeBreak.put("breakTimeCd", l.getBreakTimeCd());
			timeBreak.put("shm", l.getShm());
			timeBreak.put("ehm", l.getEhm());
			timeBreak.put("seq", l.getSeq());
			timeBreak.put("note", l.getNote());
			timeBreakList.add(timeBreak);
		}
		return timeBreakList;
	}
	
	@Override
	public int setTimeBreakMgrList(String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmTimeBreakMgr> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeBreakMgr code = new WtmTimeBreakMgr();
						code.setTimeBreakMgrId(l.get("timeBreakMgrId").toString().equals("") ? null : Long.parseLong(l.get("timeBreakMgrId").toString()));
						code.setTimeCdMgrId(Long.parseLong(l.get("timeCdMgrId").toString()));
						code.setBreakTimeCd(l.get("breakTimeCd").toString());
						code.setShm(l.get("shm").toString());
						code.setEhm(l.get("ehm").toString());
						code.setSeq(Integer.parseInt(l.get("seq").toString()));
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						saveList.add(code);
					}
					saveList = timeBreakMgrRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmTimeBreakMgr> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeBreakMgr code = new WtmTimeBreakMgr();
						code.setTimeBreakMgrId(Long.parseLong(l.get("timeBreakMgrId").toString()));
						delList.add(code);
					}
					timeBreakMgrRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setTimeCodeMgrList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getTimeBreakTimeList(Long tenantId, String enterCd,  Map<String, Object> paramMap) {
		List<Map<String, Object>> timeBreakList = new ArrayList();	
		System.out.println("timecdmgrid : " + paramMap.get("timeCdMgrId").toString());
		List<WtmTimeBreakTime> list = timeBreakTimeRepository.findByTimeCdMgrId(paramMap.get("timeCdMgrId").toString());
		
		for(WtmTimeBreakTime l : list) {
			Map<String, Object> timeBreak = new HashMap();
			timeBreak.put("timeBreakTimeId", l.getTimeBreakTimeId());
			timeBreak.put("timeCdMgrId", l.getTimeCdMgrId());
			timeBreak.put("workMinute", l.getWorkMinute());
			timeBreak.put("breakMinute", l.getBreakMinute());
			timeBreak.put("note", l.getNote());
			timeBreakList.add(timeBreak);
		}
		return timeBreakList;
	}
	
	@Override
	public int setTimeBreakTimeList(String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmTimeBreakTime> saveList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeBreakTime code = new WtmTimeBreakTime();
						code.setTimeBreakTimeId(l.get("timeBreakTimeId").toString().equals("") ? null : Long.parseLong(l.get("timeBreakTimeId").toString()));
						code.setTimeCdMgrId(Long.parseLong(l.get("timeCdMgrId").toString()));
						code.setWorkMinute(l.get("workMinute").toString());
						code.setBreakMinute(l.get("breakMinute").toString());
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						saveList.add(code);
					}
					saveList = timeBreakTimeRepository.saveAll(saveList);
					cnt += saveList.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmTimeBreakTime> delList = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmTimeBreakTime code = new WtmTimeBreakTime();
						code.setTimeBreakTimeId(Long.parseLong(l.get("timeBreakTimeId").toString()));
						delList.add(code);
					}
					timeBreakTimeRepository.deleteAll(delList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setTimeBreakTimeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getTimeCodeList(Long tenantId, String enterCd, String holYn) {
		List<Map<String, Object>> timeList = new ArrayList();	
				List<WtmTimeCdMgr> list = timeCdMgrRepository.findByTenantIdAndEnterCdAndHolYnAndYmd(tenantId, enterCd, holYn, WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
		
		for(WtmTimeCdMgr l : list) {
			Map<String, Object> time = new HashMap();
			time.put("timeCdMgrId", l.getTimeCdMgrId());
			time.put("code", l.getTimeCdMgrId());
			time.put("codeNm", l.getTimeNm());
			time.put("timeNm", l.getTimeNm());
			time.put("timeCd", l.getTimeCd());
			time.put("symd", l.getSymd());
			time.put("eymd", l.getEymd());
			time.put("workShm", l.getWorkShm());
			time.put("workEhm", l.getWorkEhm());
			time.put("holYn", l.getHolYn());
			time.put("lateChkYn", l.getLateChkYn());
			time.put("leaveChkYn", l.getLeaveChkYn());
			time.put("absenceChkYn", l.getAbsenceChkYn());
			time.put("note", l.getNote());
			timeList.add(time);
		}
		return timeList;
	}

}