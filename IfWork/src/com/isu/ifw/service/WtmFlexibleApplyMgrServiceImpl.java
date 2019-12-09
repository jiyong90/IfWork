package com.isu.ifw.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmFlexibleApplyMgr;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplyMgrMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleApplyMgrRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.option.vo.ReturnParam;

@Service("flexibleApplyMgrService")
public class WtmFlexibleApplyMgrServiceImpl implements WtmFlexibleApplyMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmFlexibleApplyMgrRepository flexibleApplyRepository;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
//	@Resource
//	WtmFlexibleApplyGrpRepository flexibleApplyGrpRepository;
//	@Resource
//	WtmFlexibleApplyEmpRepository flexibleApplyEmpRepository;
	
	@Autowired
	WtmFlexibleApplyMgrMapper wtmFlexibleApplyMgrMapper;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmFlexibleApplMapper flexApplMapper;
	
	@Autowired
	@Qualifier("wtmFlexibleApplService")
	WtmApplService wtmApplService;

	@Override
	public List<Map<String, Object>> getApplyList(Long tenantId, String enterCd, String sYmd) {
		List<Map<String, Object>> searchList = new ArrayList();	
		try {
			Map<String, Object> paramMap = new HashMap();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sYmd", sYmd);
			searchList =  wtmFlexibleApplyMgrMapper.getApplyList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplyList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	
	@Override
	public int setApplyList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				List<WtmFlexibleApplyMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmFlexibleApplyMgr code = new WtmFlexibleApplyMgr();
						code.setFlexibleApplyId(l.get("flexibleApplyId").toString().equals("") ? null : Long.parseLong(l.get("flexibleApplyId").toString()));
						code.setFlexibleStdMgrId(Long.parseLong(l.get("flexibleStdMgrId").toString()));
						code.setTenantId(tenantId);
						code.setEnterCd(enterCd);
						code.setApplyNm(l.get("applyNm").toString());
						code.setUseSymd(l.get("useSymd").toString());
						code.setUseEymd(l.get("useEymd").toString());
						code.setRepeatTypeCd(l.get("repeatTypeCd").toString());
						code.setRepeatCnt(l.get("repeatCnt").toString().equals("") ? null : Integer.parseInt(l.get("repeatCnt").toString()));
						code.setWorkMinute(l.get("workMinute").toString().equals("") ? null : Integer.parseInt(l.get("workMinute").toString()));
						code.setOtMinute(l.get("otMinute").toString().equals("") ? null : Integer.parseInt(l.get("otMinute").toString()));
						code.setApplyYn(l.get("applyYn").toString());
						code.setNote(l.get("note").toString());
						code.setUpdateId(userId);
						codes.add(code);
					}
					codes = flexibleApplyRepository.saveAll(codes);
					cnt += codes.size();
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<WtmFlexibleApplyMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmFlexibleApplyMgr code = new WtmFlexibleApplyMgr();
						code.setFlexibleApplyId(l.get("flexibleApplyId").toString().equals("") ? null : Long.parseLong(l.get("flexibleApplyId").toString()));
						codes.add(code);
					}
					flexibleApplyRepository.deleteAll(codes);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setApplyList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public Map<String, Object> getEymd(Map<String, Object> paramMap) {
		Map<String, Object> searchMap = new HashMap();	
		try {
			searchMap =  wtmFlexibleApplyMgrMapper.getEymd(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getEymd Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchMap;
	}
	
	@Override
	public List<Map<String, Object>> getworkTypeList(Long flexibleStdMgrId) {
		List<Map<String, Object>> searchList = new ArrayList();	
		Map<String, Object> paramMap = new HashMap();	
		paramMap.put("flexibleStdMgrId", flexibleStdMgrId);
		try {
			searchList =  wtmFlexibleApplyMgrMapper.getworkTypeList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getworkTypeList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		
		return searchList;
	}
	
	@Override
	public ReturnParam setApply(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		List<Map<String, Object>> searchList = new ArrayList(); // 확정대상자 조회
		List<Map<String, Object>> repeatList = new ArrayList();	// 반복기준조회
		List<Map<String, Object>> repeatForList = new ArrayList();	// 반복기준 data 리스트
		Long tenantId = (Long) paramMap.get("tenantId");
		String enterCd = paramMap.get("enterCd").toString();
		String userId = paramMap.get("userId").toString();
		String symd = "";
		String eymd = "";
		
		int cnt = 0;
		try {
			// 반복기준 조회
			repeatList = wtmFlexibleApplyMgrMapper.getApplyRepeatList(paramMap);
			int repeatCnt = 0;
			if(repeatList.get(0).get("repeatCnt") != null) {
				repeatCnt = Integer.parseInt(repeatList.get(0).get("repeatCnt").toString());
			}
			
			String repeatTypeCd = repeatList.get(0).get("repeatTypeCd").toString();
			paramMap.put("repeatTypeCd", repeatTypeCd);
			if(repeatCnt == 0) {
				// 반복횟수가 없으면 무조건 1로 처리함(한번은 생성해야하니깐)
				repeatCnt = 1;
			}
			for(int repeat = 0; repeat < repeatCnt; repeat++) {
				System.out.println("repeat : " + repeat);
				Map<String, Object> eymdMap = new HashMap();
				Map<String, Object> listMap = new HashMap();
				if(repeat == 0) {
					symd = repeatList.get(0).get("useSymd").toString();
				} else {
					// 직전종료일 +1일을 해줘야함
					DateFormat df = new SimpleDateFormat("yyyyMMdd");
					Date date = df.parse(eymd);
			         
			        // 날짜 더하기
			        Calendar cal = Calendar.getInstance();
			        cal.setTime(date);
			        cal.add(Calendar.DATE, 1);
			        symd = df.format(cal.getTime());
				}
				paramMap.put("symd", symd);
				paramMap.put("repeatCnt", 1);
				
				eymdMap = wtmFlexibleApplyMgrMapper.getEymd(paramMap);
				eymd = eymdMap.get("eymd").toString();
				
				listMap.put("symd", symd);
				listMap.put("eymd", eymd);
				
				repeatForList.add(listMap);
			}
			
			// 확정대상자 조회
			searchList =  wtmFlexibleApplyMgrMapper.getApplyConfirmList(paramMap);
			// 오류체크가 필요함.
			if(searchList != null && searchList.size() > 0) {
				for(int i=0; i < searchList.size(); i++) {
					Map<String, Object> validateMap = new HashMap<>();
					validateMap = searchList.get(i);
					String sabun = validateMap.get("sabun").toString();
					String workTypeCd = validateMap.get("workTypeCd").toString();
					
					for(int j=0; j < repeatForList.size(); j++) {
						// 반복 구간별 밸리데이션 체크 및 유연근무기간 입력
						paramMap.put("sYmd", repeatForList.get(j).get("symd"));
						paramMap.put("eYmd", repeatForList.get(j).get("eymd"));
						
						System.out.println("validate sabun : " + sabun);
						System.out.println("validate workTypeCd : " + workTypeCd);
						rp = wtmApplService.validate(tenantId, enterCd, sabun, workTypeCd, paramMap);
						if(rp.getStatus().equals("FAIL")) {
							break;
						}
					}
				}

				if(rp.getStatus().equals("FAIL")) {
					return rp;
				}
				
				// 오류검증이 없으면 저장하고 갱신해야함.
				// 검증 오류가 없으면 flexible_emp에 저장하고 갱신용로직을 불러야함
				for(int i=0; i< searchList.size(); i++) {
					Map<String, Object> saveMap = new HashMap<>();
					Map<String, Object> calendarMap = new HashMap<>();
					saveMap = searchList.get(i);
					calendarMap = searchList.get(i);
					saveMap.put("userId", userId);
					for(int j=0; j < repeatForList.size(); j++) {
						saveMap.put("symd", repeatForList.get(j).get("symd"));
						saveMap.put("eymd", repeatForList.get(j).get("eymd"));
						cnt = wtmFlexibleApplyMgrMapper.insertApplyEmp(saveMap);
						System.out.println("insert cnt : " + cnt);
						Map<String, Object> searchMap = new HashMap<>();
						searchMap = wtmFlexibleApplyMgrMapper.setApplyEmpId(saveMap);
						saveMap.put("flexibleEmpId", Long.parseLong(searchMap.get("flexibleEmpId").toString()));
	//					Long flexibleStdMgrId = Long.parseLong(saveMap.get("flexibleStdMgrId").toString());
	//					System.out.println("flexibleStdMgrId : " + flexibleStdMgrId);
	//					WtmFlexibleStdMgr stdMgr = flexStdMgrRepo.findById(flexibleStdMgrId).get();
	//					saveMap.putAll(stdMgr.getWorkDaysOpt());
						//근무제 기간의 총 소정근로 시간을 업데이트 한다.
						flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(saveMap);
					
					}
					System.out.println("updateWorkMinuteOfWtmFlexibleEmp");
					
					calendarMap.put("symd", searchList.get(i).get("useSymd"));
					calendarMap.put("eymd", searchList.get(i).get("useEymd"));
					calendarMap.put("userId", userId);
					for ( String key : calendarMap.keySet() ) {
		    		    System.out.println("key : " + key +" / value : " + calendarMap.get(key));
		    		}
						
					flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(calendarMap);
					flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(calendarMap);
					System.out.println("initWtmFlexibleEmpOfWtmWorkDayResult");
				}
			}
			
			// 여기까지 잘 오면....성공인데
			cnt = wtmFlexibleApplyMgrMapper.updateApplyEmp(paramMap);
			rp.setSuccess("");
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setApply Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return rp;
	}
	
	@Override
	public List<Map<String, Object>> getApplyGrpList(Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = null;
		try {
			searchList =  wtmFlexibleApplyMgrMapper.getApplyGrpList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplyGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return searchList;
	}
	
	@Override
	public int setApplyGrpList(String userId, Long flexibleApplyId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyId", Long.parseLong(l.get("flexibleApplyId").toString()));
						saveMap.put("orgCd", l.get("orgCd").toString());
						saveMap.put("jobCd", l.get("jobCd").toString());
						saveMap.put("dutyCd", l.get("dutyCd").toString());
						saveMap.put("posCd", l.get("posCd").toString());
						saveMap.put("classCd", l.get("classCd").toString());
						saveMap.put("workteamCd", l.get("workteamCd").toString());
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						saveList.add(saveMap);
					}
				}
				
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.insertGrp(saveList);
				}
				
				MDC.put("insert cnt", "" + cnt); 
			}
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyGroupId", Long.parseLong(l.get("flexibleApplyGroupId").toString()));
						System.out.println("update group id : " + l.get("flexibleApplyGroupId").toString());
						saveMap.put("flexibleApplyId", Long.parseLong(l.get("flexibleApplyId").toString()));
						saveMap.put("orgCd", l.get("orgCd").toString());
						saveMap.put("jobCd", l.get("jobCd").toString());
						saveMap.put("dutyCd", l.get("dutyCd").toString());
						saveMap.put("posCd", l.get("posCd").toString());
						saveMap.put("classCd", l.get("classCd").toString());
						saveMap.put("workteamCd", l.get("workteamCd").toString());
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						saveList.add(saveMap);
					}
				}
				
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.updateGrp(saveList);
				}
				
				MDC.put("insert cnt", "" + cnt);
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyGroupId", Long.parseLong(l.get("flexibleApplyGroupId").toString()));
						System.out.println("update group id : " + l.get("flexibleApplyGroupId").toString());
						saveList.add(saveMap);
					}
				}
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.deleteGrp(saveList);
				}
				
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			// 임시대상자 재생성하기
			Map<String, Object> saveMap = new HashMap();
			saveMap.put("flexibleApplyId", flexibleApplyId);
			saveMap.put("userId", userId);
			int rtn = wtmFlexibleApplyMgrMapper.deleteApplyEmpTemp(saveMap);
			rtn = wtmFlexibleApplyMgrMapper.insertApplyEmpTemp(saveMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setApplyGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getApplyEmpList(Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = null;
		try {
			searchList =  wtmFlexibleApplyMgrMapper.getApplyEmpList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplyGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return searchList;
	}
	
	@Override
	public int setApplyEmpList(String userId, Long flexibleApplyId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyId", Long.parseLong(l.get("flexibleApplyId").toString()));
						saveMap.put("sabun", l.get("sabun").toString());
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						saveList.add(saveMap);
					}
				}
				
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.insertEmp(saveList);
				}
				MDC.put("insert cnt", "" + cnt);
			}
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyEmpId", Long.parseLong(l.get("flexibleApplyEmpId").toString()));
						saveMap.put("flexibleApplyId", Long.parseLong(l.get("flexibleApplyId").toString()));
						saveMap.put("sabun", l.get("sabun").toString());
						saveMap.put("note", l.get("note").toString());
						saveMap.put("userId", userId);
						saveList.add(saveMap);
					}
				}
				
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.updateEmp(saveList);
				}
				MDC.put("insert cnt", "" + cnt);
			}
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						Map<String, Object> saveMap = new HashMap();
						saveMap.put("flexibleApplyEmpId", Long.parseLong(l.get("flexibleApplyEmpId").toString()));
						saveList.add(saveMap);
					}
				}
				if(saveList != null && saveList.size() > 0) {
					cnt += wtmFlexibleApplyMgrMapper.deleteEmp(saveList);
				}
				MDC.put("delete cnt", "" + iList.size());
				cnt += iList.size();
			}
			// 임시대상자 재생성하기
			Map<String, Object> saveMap = new HashMap();
			saveMap.put("flexibleApplyId", flexibleApplyId);
			saveMap.put("userId", userId);
			int rtn = wtmFlexibleApplyMgrMapper.deleteApplyEmpTemp(saveMap);
			rtn = wtmFlexibleApplyMgrMapper.insertApplyEmpTemp(saveMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("setApplyGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public List<Map<String, Object>> getApplyEmpPopList(Map<String, Object> paramMap) {
		List<Map<String, Object>> searchList = null;
		try {
			searchList =  wtmFlexibleApplyMgrMapper.getApplyEmpPopList(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("getApplyGrpList Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return searchList;
	}
}