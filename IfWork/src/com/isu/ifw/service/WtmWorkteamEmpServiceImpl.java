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

import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkteamEmp;
import com.isu.ifw.entity.WtmWorkteamMgr;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmWorkteamEmpMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.repository.WtmWorkteamMgrRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service
public class WtmWorkteamEmpServiceImpl implements WtmWorkteamEmpService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Resource
	WtmWorkteamMgrRepository workteamMgrRepository;
	
	@Resource
	WtmWorkteamEmpRepository workteamRepository;

	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmWorkteamEmpMapper workteamEmpMapper;
	
	@Autowired
	WtmFlexibleEmpRepository flexEmpRepo;

	@Autowired
	WtmWorkDayResultRepository workDayResultRepo;

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
						String sabun = l.get("sabun").toString();
						String sYmd = l.get("symd").toString();
						String eYmd = l.get("eymd").toString();
						Long workTeamMgrId =Long.parseLong(l.get("workteamMgrId").toString());
						
						logger.debug("setWorkteamList for " + sabun + ", "+sYmd + ", " +eYmd);
						
						WtmWorkteamMgr mgr = workteamMgrRepository.findByWorkteamMgrId(workTeamMgrId);
						//근무조 기간이 신청한 기간 안에 포함 안되면 
						String tSymd = mgr.getSymd();
						String tEymd = mgr.getEymd();
						
						if(Long.parseLong(sYmd) < Long.parseLong(tSymd) 
								|| Long.parseLong(sYmd) > Long.parseLong(tEymd) 
								|| Long.parseLong(eYmd) > Long.parseLong(tEymd)
								|| Long.parseLong(eYmd) < Long.parseLong(tSymd)) {
							throw new Exception("근무조의 사용기간은" + 
									tSymd.substring(0,4) +"/" + tSymd.substring(4,6) +"/"+ tSymd.substring(6,8) + " ~ " + 
									tEymd.substring(0,4) +"/" + tEymd.substring(4,6) +"/"+ tEymd.substring(6,8) + "입니다.");
						} 
						
						WtmWorkteamEmp workteam = new WtmWorkteamEmp();
						workteam.setUpdateId(userId);
						workteam.setSabun(sabun);
						workteam.setWorkteamEmpId(l.get("workteamEmpId").toString().equals("") ? null : Long.parseLong(l.get("workteamEmpId").toString()));
						workteam.setWorkteamMgrId(workTeamMgrId);
						workteam.setEymd(eYmd);
						workteam.setNote(l.get("note").toString());
						workteam.setSymd(sYmd);
						
						//List<Map<String, Object>> dup = workteamRepository.dupCheckByYmd(tenantId, enterCd, l.get("sabun").toString(), l.get("workteamEmpId").toString(), l.get("symd").toString(), l.get("eymd").toString());
						
						paramMap.put("sabun", sabun);
						paramMap.put("workteamEmpId", l.get("workteamEmpId").toString());
						paramMap.put("sYmd", sYmd);
						paramMap.put("eYmd", eYmd);
						
						List<Map<String, Object>> dup = workteamEmpMapper.dupCheckByYmd(paramMap);
						if(dup != null && dup.size() > 0) {
							rp.setFail("중복된 기간이 존재합니다. (sabun : " + sabun + ")");
							return rp;
						}
						//saveList.add(workteam);
						workteam = workteamRepository.save(workteam);
						
						paramMap.put("symd", sYmd);
						paramMap.put("eymd", eYmd);
						paramMap.put("sabun", sabun);
						paramMap.put("userId", userId);
						logger.debug("setWorkteamList workteamRepository.save " + paramMap.toString());
						
						//
						WtmFlexibleEmp flexibleEmp = new WtmFlexibleEmp();
						flexibleEmp.setEnterCd(enterCd);
						flexibleEmp.setEymd(eYmd);
						flexibleEmp.setFlexibleStdMgrId(mgr.getFlexibleStdMgrId());
						flexibleEmp.setSabun(sabun);
						flexibleEmp.setSymd(sYmd);
						flexibleEmp.setTenantId(tenantId);
						flexibleEmp.setUpdateId(sabun);
						flexibleEmp.setWorkTypeCd("WORKTEAM");
						flexibleEmp.setNote(l.get("note").toString());
						flexEmpRepo.save(flexibleEmp);
						flexEmpRepo.flush();
						//
						WtmFlexibleEmp emp = new WtmFlexibleEmp();
						List<WtmFlexibleEmp> empList = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCd(tenantId, enterCd, workteam.getSabun(), l.get("symd").toString(), l.get("eymd").toString(), "BASE");
						if(empList != null) {
							for(WtmFlexibleEmp e : empList) {
								//신청기간내에 시작 종료가 포함되어있을 경우
								if(Integer.parseInt(sYmd) <= Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) >= Integer.parseInt(e.getEymd())) {
									flexEmpRepo.delete(e);
									flexEmpRepo.flush();
								//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
								} else if(Integer.parseInt(sYmd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) < Integer.parseInt(e.getEymd())) {
									String meymd = e.getEymd();
									
									e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sYmd, ""), -1),null));
									// System.out.println("save 1 : " + e.toString());
									flexEmpRepo.save(e);
									flexEmpRepo.flush();
									
									WtmFlexibleEmp newEmp = new WtmFlexibleEmp();
									newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
									newEmp.setTenantId(e.getTenantId());
									newEmp.setEnterCd(e.getEnterCd());
									newEmp.setSabun(e.getSabun());
									newEmp.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eYmd, ""), 1),null));
									newEmp.setEymd(meymd);
									newEmp.setUpdateId(sabun);
									newEmp.setWorkTypeCd(e.getWorkTypeCd());
									newEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
									// System.out.println("save 2 : " + newEmp.toString());
									flexEmpRepo.save(newEmp);
									flexEmpRepo.flush();
									

								//시작일만 포함되어있을 경우 
								}else if(Integer.parseInt(sYmd) >= Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) < Integer.parseInt(e.getEymd())) {
									//시작일을 신청종료일 다음날로 업데이트 해주자
									e.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eYmd, ""), 1),null));
									// System.out.println("save 3 : " + e.toString());
									flexEmpRepo.save(e);
									flexEmpRepo.flush();
									
								//종료일만 포함되어있을 경우
								}else if(Integer.parseInt(sYmd) > Integer.parseInt(e.getSymd()) && Integer.parseInt(eYmd) <= Integer.parseInt(e.getEymd())) {
									//종료일을 신청시작일 전날로 업데이트 해주자
									e.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(sYmd, ""), -1),null));
									// System.out.println("save 4 : " + e.toString());
									flexEmpRepo.save(e);
									flexEmpRepo.flush();
								}
							}
						}
						
						List<String> timeTypCds = new ArrayList<String>();
						timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
						timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
						timeTypCds.add(WtmApplService.TIME_TYPE_OT);
						
						List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sYmd, eYmd);
						if(results!=null && results.size()>0) {
							workDayResultRepo.deleteAll(results);
							workDayResultRepo.flush();
						}						
						
						flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
						cnt++;
					}
					//saveList = workteamRepository.saveAll(saveList);
					//cnt += saveList.size();
				}
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
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
			rp.setFail(e.toString());
		} 
		return rp;
	}

}