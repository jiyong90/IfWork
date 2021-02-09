package com.isu.ifw.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

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

	@Autowired WtmFlexibleEmpResetService flexibleEmpResetService;
	@Autowired WtmCalcService calcService;
	
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
			
			String symd = null;
			String eymd = null;
			if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
				symd = paramMap.get("sYmd").toString().replaceAll("[-.]", "");
			} else {
				symd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			}
			if(paramMap.get("eYmd")!=null && !"".equals(paramMap.get("eYmd"))) {
				eymd = paramMap.get("eYmd").toString().replaceAll("[-.]", "");
			} else {
				eymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			}
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			
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
		List<Map<String, Object>> returnParamMap = new ArrayList<>();

		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
 
		rp.setSuccess("저장에 성공하였습니다.");
		int cnt = 0;
		try {
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
			
			//수정
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {				
				List<Map<String, Object>> updateMap = (List<Map<String, Object>>) convertMap.get("updateRows");
				rp = setWorkteamListSave(tenantId, enterCd, userId, updateMap);
				returnParamMap.addAll(updateMap);
			}
			
			//신규
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {				
				List<Map<String, Object>> updateMap = (List<Map<String, Object>>) convertMap.get("insertRows");
				rp = setWorkteamListSave(tenantId, enterCd, userId, updateMap);
				returnParamMap.addAll(updateMap);
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
			rp.setFail(e.toString());
		}
		rp.put("returnParamMap", returnParamMap);
		return rp;
	}


	/**
	 * 근무조 변경 처리(수정, 신규)
	 * @param tenantId
	 * @param enterCd
	 * @param userId
	 * @param convertMap
	 * @return
	 * @throws Exception
	 */

	@Transactional
	public ReturnParam setWorkteamListSave(Long tenantId, String enterCd, String userId, List<Map<String, Object>> convertMap) throws Exception{
		ReturnParam rp = new ReturnParam();
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
 
		rp.setSuccess("근무조 생성을 요청하였습니다.");
		
			
		//List<WtmWorkteamEmp> saveList = new ArrayList();
		if(convertMap != null && convertMap.size() > 0) {
			for(Map<String, Object> l : convertMap) {
				String sabun = l.get("sabun").toString();
				String sYmd = l.get("symd").toString();
				String eYmd = l.get("eymd").toString();
				Long workTeamMgrId =Long.parseLong(l.get("workteamMgrId").toString());
				
				logger.debug("setWorkteamList for " + sabun + ", "+sYmd + ", " +eYmd);

				WtmWorkteamEmp workteamEmp =  workteamRepository.findByTenantIdAndEnterCdAndSabunAndSymdAndEymd(tenantId, enterCd, sabun, sYmd, eYmd);
				if(workteamEmp != null) {
					if("WTM_WORKTEAM_SAVE_I".equals(workteamEmp.getStatus())) {
						throw new Exception("사번 : " + sabun + "는 근무조 생성 진행 중 입니다.");
					}
				}

				
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
				workteam.setStatus(WTM_WORKTEAM_SAVE_I);

				paramMap.put("sabun", sabun);
				paramMap.put("workteamEmpId", l.get("workteamEmpId").toString());
				paramMap.put("sYmd", sYmd);
				paramMap.put("eYmd", eYmd);

				List<Map<String, Object>> dup = workteamEmpMapper.dupCheckByYmd(paramMap);
				if(dup != null && dup.size() > 0) {
					throw new Exception("근무조가 중복된 기간이 존재합니다. (sabun : " + sabun + ")");
				}
				List<Map<String, Object>> dup2 = workteamEmpMapper.dupCheckFlexibleByYmd(paramMap);
				if(dup2 != null && dup2.size() > 0) {
					throw new Exception("해당 기간에 적용된 유연근무제가 존재합니다. (sabun : " + sabun + ")");
				}

				//saveList.add(workteam);
				workteam = workteamRepository.save(workteam);


				workteamRepository.flush();


			}
//			setApply(tenantId, enterCd, userId, convertMap);
				//saveList = workteamRepository.saveAll(saveList);
				//cnt += saveList.size();
		}
		
		return rp;
	}


	@Async
	@Override
	public void setApply(Long tenantId, String enterCd, String userId, List<Map<String, Object>> convertMap) throws Exception{
		Map<String, Object> paramMap = new HashMap();

		if(convertMap != null && convertMap.size() > 0) {
			for(Map<String, Object> l : convertMap) {
				try {

					String sabun = l.get("sabun").toString();
					String sYmd = l.get("symd").toString();
					String eYmd = l.get("eymd").toString();


					paramMap.put("symd", sYmd);
					paramMap.put("eymd", eYmd);
					paramMap.put("sabun", sabun);
					paramMap.put("userId", userId);
					logger.debug("setWorkteamList workteamRepository.save " + paramMap.toString());

					Long workTeamMgrId =Long.parseLong(l.get("workteamMgrId").toString());
					WtmWorkteamMgr mgr = workteamMgrRepository.findByWorkteamMgrId(workTeamMgrId);

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

					List<String> workTypeCds = new ArrayList<String>();
					workTypeCds.add("BASE");
					workTypeCds.add("WORKTEAM");
					List<WtmFlexibleEmp> empList = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCds(tenantId, enterCd, sabun, l.get("symd").toString(), l.get("eymd").toString(), workTypeCds);
					if(empList != null) {
						for(WtmFlexibleEmp e : empList) {
							//신규로 넣은 값을 제외한 다른 값들은 비교하여 처리한다.
							if(!flexibleEmp.getFlexibleEmpId().equals(e.getFlexibleEmpId())) {
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
					}

					List<String> timeTypCds = new ArrayList<String>();
					timeTypCds.add(WtmApplService.TIME_TYPE_BASE);
					timeTypCds.add(WtmApplService.TIME_TYPE_FIXOT);
					timeTypCds.add(WtmApplService.TIME_TYPE_EARLY_OT);
					timeTypCds.add(WtmApplService.TIME_TYPE_OT);

					List<WtmWorkDayResult> results = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypCds, sYmd, eYmd);
					if(results!=null && results.size()>0) {
						workDayResultRepo.deleteAll(results);
						workDayResultRepo.flush();
					}

					//flexEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
					//try {

					String s = sYmd.substring(0,4);
					String e = eYmd.substring(0,4);

//					flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, s+"0101", s+"1231", "ADMIN");
					flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, sYmd, eYmd, "ADMIN");
					calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, s+"0101", e+"1231");
//					if(!s.equals(e)) {
//						flexibleEmpResetService.P_WTM_FLEXIBLE_EMP_RESET(tenantId, enterCd, sabun, e+"0101", e+"1231", "ADMIN");
//						calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, e+"0101", e+"1231");
//					}

					WtmWorkteamEmp workteamEmp =  workteamRepository.findByTenantIdAndEnterCdAndSabunAndSymdAndEymd(tenantId, enterCd, sabun, sYmd, eYmd);

					if(workteamEmp != null) {
						workteamEmp.setStatus(WTM_WORKTEAM_SAVE_Y);

						workteamRepository.save(workteamEmp);
					}

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}