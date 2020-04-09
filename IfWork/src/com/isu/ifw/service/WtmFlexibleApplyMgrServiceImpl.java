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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmFlexibleApplyDet;
import com.isu.ifw.entity.WtmFlexibleApplyMgr;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkPattDet;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplyMgrMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleApplyDetRepository;
import com.isu.ifw.repository.WtmFlexibleApplyMgrRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmFlexibleApplDetVO;

@Service("flexibleApplyMgrService")
public class WtmFlexibleApplyMgrServiceImpl implements WtmFlexibleApplyMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Resource
	WtmFlexibleApplyMgrRepository flexibleApplyRepository;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
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
	
	@Autowired
	WtmWorkPattDetRepository workPattDetRepo;
	
	@Autowired
	WtmFlexibleApplyDetRepository flexibleApplyDetRepo;
	
	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;

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
			logger.debug(e.toString());
		}
		return searchList;
	}
	
	@Transactional
	@Override
	public int setApplyList(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {
		int cnt = 0;
		try {
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				logger.debug("[setApplyList] iList" + iList.size());

				List<WtmFlexibleApplyMgr> codes = new ArrayList();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmFlexibleStdMgr mgr = flexStdMgrRepo.findByFlexibleStdMgrId(Long.parseLong(l.get("flexibleStdMgrId").toString()));
						String mgrSymd = mgr.getUseSymd();
						String mgrEymd = mgr.getUseEymd();
						
						if(Long.parseLong(l.get("useSymd").toString()) < Long.parseLong(mgrSymd) 
								|| Long.parseLong(l.get("useSymd").toString()) > Long.parseLong(mgrEymd)
								|| Long.parseLong(l.get("useEymd").toString()) < Long.parseLong(mgrSymd) 								
								|| Long.parseLong(l.get("useEymd").toString()) > Long.parseLong(mgrEymd)) {
							throw new Exception("근무제의 사용기간은" + 
									mgrSymd.substring(0,4) +"/" + mgrSymd.substring(4,6) +"/"+ mgrSymd.substring(6,8) + " ~ " + 
									mgrEymd.substring(0,4) +"/" + mgrEymd.substring(4,6) +"/"+ mgrEymd.substring(6,8) + "입니다.");
						} 
						
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
						
						if(l.get("workTypeCd")!=null && "ELAS".equals(l.get("workTypeCd").toString())) {
							WtmFlexibleApplyMgr flexibleApply = flexibleApplyRepository.save(code);
							
							createElasPlan(tenantId, enterCd, flexibleApply.getFlexibleStdMgrId(), flexibleApply.getFlexibleApplyId(), flexibleApply.getUseSymd(), flexibleApply.getUseEymd(), userId);
							
							cnt += 1;
						} else {
							codes.add(code);
						}
						
					}
					codes = flexibleApplyRepository.saveAll(codes);
					cnt += codes.size();
				}
			}
		
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");

				logger.debug("[setApplyList]2 iList" + iList.size());

				List<WtmFlexibleApplyMgr> codes = new ArrayList();
				List<WtmFlexibleApplyDet> dets = new ArrayList<WtmFlexibleApplyDet>();
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						if(l.get("flexibleApplyId")!=null && !"".equals(l.get("flexibleApplyId").toString())) {
							Long flexibleApplyId = Long.parseLong(l.get("flexibleApplyId").toString());
							WtmFlexibleApplyMgr code = new WtmFlexibleApplyMgr();
							code.setFlexibleApplyId(flexibleApplyId);
							codes.add(code);
						
							//탄근제 삭제 시 근무 계획도 삭제
							flexibleApplyDetRepo.deleteByFlexibleApplyId(flexibleApplyId);
						}
					}
					flexibleApplyRepository.deleteAll(codes);
				}
				cnt += iList.size();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString());
			return 0;
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
			logger.debug(e.toString());
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
			logger.debug(e.toString());
		} 
		
		return searchList;
	}

	@Override
	public List<Map<String, Object>> getApplyYmdList(Map<String, Object> paramMap) throws Exception {
		int repeatCnt = 1;
		// 반복기준 조회
		List<Map<String, Object>> repeatList = wtmFlexibleApplyMgrMapper.getApplyRepeatList(paramMap);
		List<Map<String, Object>> repeatForList = wtmFlexibleApplyMgrMapper.getApplyRepeatList(paramMap);

		List<Map<String, Object>> result = new ArrayList();

		if(repeatList != null && repeatList.get(0).get("repeatCnt") != null) {
			repeatCnt = Integer.parseInt(repeatList.get(0).get("repeatCnt").toString());
		}
		String repeatTypeCd = repeatList.get(0).get("repeatTypeCd").toString();
		paramMap.put("repeatTypeCd", repeatTypeCd);
//		paramMap.put("repeatCnt", repeatCnt);

		String symd = "";
		String eymd = "";
		
		for(int i = 0; i < repeatCnt; i++) {
			System.out.println("repeat : " + i);
			
			Map<String, Object> listMap = new HashMap();
			
			if(i == 0) {
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
			
			if("NO".equals(repeatTypeCd)) {
				eymd = repeatList.get(0).get("useEymd").toString();
			} else {
				Map<String, Object> eymdMap = wtmFlexibleApplyMgrMapper.getEymd(paramMap);
				eymd = eymdMap.get("eymd").toString();
			}
			
			listMap.put("symd", symd);
			listMap.put("eymd", eymd);
		
			result.add(listMap);
		}
		return result;
	}
	
	@Override
	@Async("threadPoolTaskExecutor")
	public void setApplyAsync(List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList) {
	
		int cnt = 0;
		long flexibleApplyId = 0L;
		// 오류검증이 없으면 저장하고 갱신해야함.
		// 검증 오류가 없으면 flexible_emp에 저장하고 갱신용로직을 불러야함
		for(int i=0; i< searchList.size(); i++) {
			int rs = flexibleEmpService.setApplyForOne(searchList.get(i), ymdList);
			if(rs == 1) {
				//이사람 성공하면 Y
				long flexibleApplyTempId = Long.parseLong(searchList.get(i).get("flexibleApplyTempId").toString());
				flexibleApplyId = Long.parseLong(searchList.get(i).get("flexibleApplyId").toString());
				wtmFlexibleApplyMgrMapper.updateFlexibleEmpTemp(flexibleApplyTempId);
				logger.debug("[setApply] 확정성공 대상" + searchList.get(i).toString());
				 
				cnt++;
			}
		}
		
		//전체성공
		if(cnt == searchList.size()) {
			wtmFlexibleApplyMgrMapper.updateFlexibleApplyAll(flexibleApplyId);
		}
	}
	
	@Override
	public int setApply(List<Map<String, Object>> searchList, List<Map<String, Object>> ymdList) {
	
		int cnt = 0;
		long flexibleApplyId = 0L;
		// 오류검증이 없으면 저장하고 갱신해야함.
		// 검증 오류가 없으면 flexible_emp에 저장하고 갱신용로직을 불러야함
		for(int i=0; i< searchList.size(); i++) {
			int rs = flexibleEmpService.setApplyForOne(searchList.get(i), ymdList);
			if(rs == 1) {
				//이사람 성공하면 Y
				long flexibleApplyTempId = Long.parseLong(searchList.get(i).get("flexibleApplyTempId").toString());
				flexibleApplyId = Long.parseLong(searchList.get(i).get("flexibleApplyId").toString());
				wtmFlexibleApplyMgrMapper.updateFlexibleEmpTemp(flexibleApplyTempId);
				logger.debug("[setApply] 확정성공 대상" + searchList.get(i).toString());
				 
				cnt++;
			}
		}
		
		//전체성공
		if(cnt == searchList.size()) {
			wtmFlexibleApplyMgrMapper.updateFlexibleApplyAll(flexibleApplyId);
			
		}
		return cnt;
	}
	
	public ReturnParam setApply_backup(Map<String, Object> paramMap) {
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
				
				//반복없음이면, 입력한 종료일로 
				if("NO".equals(repeatTypeCd)) {
					eymd = repeatList.get(0).get("useEymd").toString();
				} else {
					eymdMap = wtmFlexibleApplyMgrMapper.getEymd(paramMap);
					eymd = eymdMap.get("eymd").toString();
				}
				
				listMap.put("symd", symd);
				listMap.put("eymd", eymd);
				
				repeatForList.add(listMap);
			}
			
			String workTypeCd = paramMap.get("workTypeCd").toString();
			Long flexibleApplyId = Long.valueOf(paramMap.get("flexibleApplyId").toString());
			
			// 확정대상자 조회
			searchList =  wtmFlexibleApplyMgrMapper.getApplyConfirmList(paramMap);
			
			if(searchList==null || searchList.size()==0) {
				rp.setFail("대상자가 없습니다.");
				return rp;
			}
			
			// 오류체크가 필요함.
			for(int i=0; i < searchList.size(); i++) {
				Map<String, Object> validateMap = new HashMap<>();
				validateMap = searchList.get(i);
				String sabun = validateMap.get("sabun").toString();
				
				for(int j=0; j < repeatForList.size(); j++) {
					// 반복 구간별 밸리데이션 체크 및 유연근무기간 입력
					paramMap.put("sYmd", repeatForList.get(j).get("symd"));
					paramMap.put("eYmd", repeatForList.get(j).get("eymd"));
					
					//탄근제 validation 체크를 위한 param
					paramMap.put("adminYn", "Y");
					paramMap.put("flexibleApplyId", flexibleApplyId);
					
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
					
					String sd = repeatForList.get(j).get("symd").toString();
					String ed = repeatForList.get(j).get("eymd").toString();
					String sabun = saveMap.get("sabun").toString();
					System.out.println("sd : " + sd);
					System.out.println("ed : " + ed);
					WtmFlexibleEmp emp = new WtmFlexibleEmp();
					List<WtmFlexibleEmp> empList = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCd(tenantId, enterCd, sabun, sd, ed, "BASE");
					if(empList != null) {
						for(WtmFlexibleEmp e : empList) {
							//신청기간내에 시작 종료가 포함되어있을 경우
							if(Integer.parseInt(sd) <= Integer.parseInt(e.getSymd()) && Integer.parseInt(ed) >= Integer.parseInt(e.getEymd())) {
								wtmFlexibleEmpRepo.delete(e);
							//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
							}else if(Integer.parseInt(sd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(ed) < Integer.parseInt(e.getEymd())) {
								String meymd = e.getEymd();
								
								e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sd, ""), -1),null));
								wtmFlexibleEmpRepo.save(e);
								WtmFlexibleEmp newEmp = new WtmFlexibleEmp();
								newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
								newEmp.setTenantId(e.getTenantId());
								newEmp.setEnterCd(e.getEnterCd());
								newEmp.setSabun(e.getSabun());
								newEmp.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(ed, ""), 1),null));
								newEmp.setEymd(meymd);
								newEmp.setUpdateId(userId);
								newEmp.setWorkTypeCd(e.getWorkTypeCd());
								newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
								wtmFlexibleEmpRepo.save(newEmp);

							//시작일만 포함되어있을 경우 
							}else if(Integer.parseInt(sd) >= Integer.parseInt(e.getSymd()) && Integer.parseInt(ed) < Integer.parseInt(e.getEymd())) {
								//시작일을 신청종료일 다음날로 업데이트 해주자
								e.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(ed, ""), 1),null));
								wtmFlexibleEmpRepo.save(e);
							//종료일만 포함되어있을 경우
							}else if(Integer.parseInt(sd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(ed) <= Integer.parseInt(e.getEymd())) {
								//종료일을 신청시작일 전날로 업데이트 해주자
								e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sd, ""), -1),null));
								wtmFlexibleEmpRepo.save(e);
								
							}
						}
					}
					
					//탄근제의 경우 근무 계획까지 작성하여 신청을 하기 때문에
					//calendar, result 만들어준다.
					if(workTypeCd.equals("ELAS")) {
						
						//calendar 있으면 삭제하고 다시 만들어주자.
						//initWtmFlexibleEmpOfWtmWorkDayResult 프로시저에서 calendar 만들어주기 때문에 생략
						/*List<WtmWorkCalendar> calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, sd, ed);
						
						if(calendar!=null && calendar.size()>0) {
							workCalendarRepo.deleteAll(calendar);
							workCalendarRepo.flush();
						}
						flexEmpMapper.createWorkCalendarOfElasApply(flexibleApplyId, sabun, userId);*/
						
						//List<WtmWorkCalendar> calendar2 = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, appl.getApplSabun(), flexibleAppl.getSymd(), flexibleAppl.getEymd());
						
						//result 만들어주자.
						List<WtmWorkDayResult> result = new ArrayList<WtmWorkDayResult>();
						Map<String, Object> pMap = new HashMap<String, Object>();
						pMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
						pMap.put("key", "FLEXIBLE_APPLY_ID");
						pMap.put("value", flexibleApplyId);
						List<Map<String, Object>> dets = flexEmpMapper.getElasWorkDayResult(pMap);
						if(dets!=null && dets.size()>0) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							
							//result 에 base와 ot, fixot 있으면 삭제하고 다시 만들어주자.
							List<String> timeTypCds = new ArrayList<String>();
							timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
							timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
							timeTypCds.add(WtmApplService.TIME_TYPE_OT);
							
							List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sd, ed);
							if(results!=null && results.size()>0) {
								wtmWorkDayResultRepo.deleteAll(results);
								wtmWorkDayResultRepo.flush();
							}
							
							for(Map<String, Object> det : dets) {
								Date s = null;
								Date e = null;
								
								WtmWorkDayResult r = new WtmWorkDayResult();
								r.setTenantId(tenantId);
								r.setEnterCd(enterCd);
								r.setYmd(det.get("ymd").toString());
								r.setSabun(sabun);
								//r.setApplId(applId);
								r.setTimeTypeCd(det.get("timeTypeCd").toString());
								r.setTaaCd(null);
								
								if(det.get("planSdate")!=null && !"".equals(det.get("planSdate"))) {
									s = sdf.parse(det.get("planSdate").toString());
									r.setPlanSdate(s);
								}
								
								if(det.get("planEdate")!=null && !"".equals(det.get("planEdate"))) {
									e = sdf.parse(det.get("planEdate").toString());
									r.setPlanEdate(e);
								}
								
								if(det.get("planMinute")!=null && !"".equals(det.get("planMinute"))) {
									r.setPlanMinute(Integer.parseInt(det.get("planMinute").toString()));
								}
								
								r.setUpdateDate(new Date());
								r.setUpdateId(userId);
								
								result.add(r);
							}
							
							if(result.size()>0)
								wtmWorkDayResultRepo.saveAll(result);
						}
						
					} else {
						// 근무제도 시행시 시행할 기간의 근무제도가 기본근무의 정보는 지워야함.
						//유연근무 승인 시 해당 구간 내의 result는 지워야 한다. //리셋 프로시져에서 지우지 않음.  
						//result 에 base와 ot, fixot 있으면 삭제
						List<String> timeTypCds = new ArrayList<String>();
						timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
						timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
						timeTypCds.add(WtmApplService.TIME_TYPE_OT);
						
						List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sd, ed);
						if(results!=null && results.size()>0) {
							wtmWorkDayResultRepo.deleteAll(results);
							wtmWorkDayResultRepo.flush();
						}
					}
					
//					Long flexibleStdMgrId = Long.parseLong(saveMap.get("flexibleStdMgrId").toString());
//					System.out.println("flexibleStdMgrId : " + flexibleStdMgrId);
//					WtmFlexibleStdMgr stdMgr = flexStdMgrRepo.findById(flexibleStdMgrId).get();
//					saveMap.putAll(stdMgr.getWorkDaysOpt());
					//근무제 기간의 총 소정근로 시간을 업데이트 한다.
					//20200102jyp P_WTM_WORK_CALENDAR_RESET procedure에서 한다.
					//flexApplMapper.updateWorkMinuteOfWtmFlexibleEmp(saveMap);
				
				}
				System.out.println("updateWorkMinuteOfWtmFlexibleEmp");
				
				calendarMap.put("symd", searchList.get(i).get("useSymd"));
				calendarMap.put("eymd", searchList.get(i).get("useEymd"));
				calendarMap.put("userId", userId);
				calendarMap.put("pId", userId);
				for ( String key : calendarMap.keySet() ) {
	    		    System.out.println("key : " + key +" / value : " + calendarMap.get(key));
	    		}
					
				flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(calendarMap);
				flexEmpMapper.createWorkTermBySabunAndSymdAndEymd(calendarMap);
				System.out.println("initWtmFlexibleEmpOfWtmWorkDayResult");
			}
			
			// 여기까지 잘 오면....성공인데
			cnt = wtmFlexibleApplyMgrMapper.updateApplyEmp(paramMap);
			rp.setSuccess("");
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString());
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
			logger.debug(e.toString());
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
			logger.debug(e.toString());
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
			logger.debug(e.toString());
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
			logger.debug(e.toString());
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
			logger.debug(e.toString());
		} 
		
		return searchList;
	}
	
	@Override
	public List<Map<String, Object>> getElasDetail(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId) {
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("tableName", "WTM_FLEXIBLE_APPLY_DET");
		paramMap.put("key", "FLEXIBLE_APPLY_ID");
		paramMap.put("value", Long.valueOf(paramMap.get("flexibleApplyId").toString()));
		paramMap.put("totalYn", "Y");
		
		List<Map<String, Object>> elasDetails = flexApplMapper.getElasApplDetail(paramMap);
		
		if(elasDetails!=null && elasDetails.size()>0) {
			paramMap.put("totalYn", "N");
			for(Map<String, Object> t : elasDetails) {
				paramMap.put("symd", t.get("startYmd").toString());
				List<Map<String, Object>> details = flexApplMapper.getElasApplDetail(paramMap);
				t.put("details", details);
			}
		}
		
		return elasDetails; 
	}
	
	protected List<WtmFlexibleApplyDet> saveWtmFlexibleApplyDet(Long tenantId, String enterCd, Long flexibleApplyId, Long flexibleStdMgrId, String sYmd, String eYmd, String sabun, String userId) {
		
		WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findById(flexibleStdMgrId).get();
		
		// 공휴일 제외 여부
		String holExceptYn = "N";
		if(flexibleStdMgr!=null && flexibleStdMgr.getHolExceptYn()!=null && !"".equals(flexibleStdMgr.getHolExceptYn())) 
			holExceptYn = flexibleStdMgr.getHolExceptYn();

		// 근무제 패턴으로 정해놓은 일 수  
		int maxPattDet = 0;
		WtmWorkPattDet workPattDet = workPattDetRepo.findTopByFlexibleStdMgrIdOrderBySeqDesc(flexibleStdMgrId);
		if(workPattDet!=null && workPattDet.getSeq()!=null) 
			maxPattDet = workPattDet.getSeq();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		paramMap.put("flexibleStdMgrId", flexibleStdMgrId);
		paramMap.put("holExceptYn", holExceptYn);
		paramMap.put("maxPattDet", maxPattDet);
		
		List<WtmFlexibleApplyDet> workList = new ArrayList<WtmFlexibleApplyDet>();
		List<WtmFlexibleApplDetVO> patterns = flexApplMapper.getWorkPattern(paramMap);
		if(patterns!=null && patterns.size()>0) {
			for(WtmFlexibleApplDetVO p : patterns) {
				WtmFlexibleApplyDet fd = new WtmFlexibleApplyDet();
				fd.setFlexibleApplyId(flexibleApplyId);
				fd.setYmd(p.getYmd());
				fd.setTimeCdMgrId(p.getTimeCdMgrId());
				fd.setHolidayYn(p.getHolidayYn());
				
				Date planSdate = null;
				if(p.getPlanSdate()!=null && !"".equals(p.getPlanSdate())) {
					planSdate = WtmUtil.toDate(p.getPlanSdate(), "yyyyMMddHHmm");
					fd.setPlanSdate(planSdate);
				}
				
				Date planEdate = null;
				if(p.getPlanEdate()!=null && !"".equals(p.getPlanEdate())) {
					planEdate = WtmUtil.toDate(p.getPlanEdate(), "yyyyMMddHHmm");
					fd.setPlanEdate(planEdate);
				}
				
				fd.setOtbMinute(p.getOtbMinute());
				fd.setOtaMinute(p.getOtaMinute());
				
				fd.setUpdateDate(new Date());
				fd.setUpdateId(userId);
				workList.add(fd);
			}
			
			flexibleApplyDetRepo.saveAll(workList);
		}
		
		return workList;
	}
	
	protected void updateWtmFlexibleApplyDet(List<WtmFlexibleApplyDet> applyDets, String userId) {
		
		if(applyDets!=null && applyDets.size()>0) {
			for(WtmFlexibleApplyDet d : applyDets) {
				Date planSdate = d.getPlanSdate();
				Date planEdate = d.getPlanEdate();
				
				if(planSdate!=null && planEdate!=null) {
					String pSdate = WtmUtil.parseDateStr(planSdate, "yyyyMMddHHmm");
					String pEdate = WtmUtil.parseDateStr(planEdate, "yyyyMMddHHmm");
					
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("ymd", d.getYmd());
					
					paramMap.put("shm", WtmUtil.parseDateStr(planSdate, "HHmm"));
					paramMap.put("ehm", WtmUtil.parseDateStr(planEdate, "HHmm"));
					Map<String, Object> planMinuteMap = flexibleEmpService.calcMinuteExceptBreaktimeForElas(true, d.getFlexibleApplyId(), paramMap, userId);
					d.setPlanMinute(Integer.parseInt(planMinuteMap.get("calcMinute")+""));
					
					if(d.getOtbMinute()!=0) {
						Map<String, Object> otbMinuteMap = flexibleEmpService.calcOtMinuteExceptBreaktimeForElas(true, d.getFlexibleApplyId(), d.getYmd(), pSdate, pEdate, "OTB", d.getOtbMinute(), userId);
						
						if(otbMinuteMap!=null) {
							Date otbSdate = WtmUtil.toDate(otbMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
							Date otbEdate = WtmUtil.toDate(otbMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
							
							d.setOtbSdate(otbSdate);
							d.setOtbEdate(otbEdate);
							d.setOtbMinute(Integer.parseInt(otbMinuteMap.get("calcMinute").toString()));
						}	
					}
					
					if(d.getOtaMinute()!=0) {
						Map<String, Object> otaMinuteMap = flexibleEmpService.calcOtMinuteExceptBreaktimeForElas(true, d.getFlexibleApplyId(), d.getYmd(), pSdate, pEdate, "OTA", d.getOtaMinute(), userId);
						
						if(otaMinuteMap!=null) {
							Date otaSdate = WtmUtil.toDate(otaMinuteMap.get("sDate").toString(), "yyyyMMddHHmmss");
							Date otaEdate = WtmUtil.toDate(otaMinuteMap.get("eDate").toString(), "yyyyMMddHHmmss");
							
							d.setOtaSdate(otaSdate);
							d.setOtaEdate(otaEdate);
							d.setOtaMinute(Integer.parseInt(otaMinuteMap.get("calcMinute").toString()));
						}
					}
						
				}
				
			}	
			
			flexibleApplyDetRepo.saveAll(applyDets);
		}
		
	}
	
	protected void createElasPlan(Long tenantId, String enterCd, Long flexibleStdMgrId, Long flexibleApplyId, String sYmd, String eYmd, String userId) {
		//탄근제 계획 삭제하고 다시 생성
		flexibleApplyDetRepo.deleteByFlexibleApplyId(flexibleApplyId);
		flexibleApplyDetRepo.flush();
		
		//계획 생성
		List<WtmFlexibleApplyDet> applyDets = saveWtmFlexibleApplyDet(tenantId, enterCd, flexibleApplyId, flexibleStdMgrId, sYmd, eYmd, null, userId);
		updateWtmFlexibleApplyDet(applyDets, userId);
	}
	
	@Transactional
	@Override
	public ReturnParam createElasPlan(Long tenantId, String enterCd, Long flexibleApplyId, String userId) {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		try {
			WtmFlexibleApplyMgr flexibleApply = flexibleApplyRepository.findById(flexibleApplyId).get();
			createElasPlan(tenantId, enterCd, flexibleApply.getFlexibleStdMgrId(), flexibleApply.getFlexibleApplyId(), flexibleApply.getUseSymd(), flexibleApply.getUseEymd(), userId);
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("탄력근무제 계획 생성 시 오류가 발생했습니다.");
		}
		
		return rp;
		
	}
	
}