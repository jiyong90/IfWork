package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmOrgChartMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

/**
 * 근태 달력 관리 service
 * @author 
 *
 */
@Service("WtmCalendarService")
public class WtmCalendarServiceImpl implements WtmCalendarService{
	
	@Autowired
	WtmCalendarMapper wtmCalendarMapper;
	
	@Autowired
	WtmOrgChartMapper wtmOrgChartMapper;
	
	@Autowired
	WtmEmpHisRepository empHisRepo;
	
	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	/**
	 * 달력 조회
	 * @param tenantId
	 * @param enterCd
	 * @param bisinessPlaceCd
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getCalendar(Long tenantId, String enterCd, String bisinessPlaceCd, Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("bisinessPlaceCd", bisinessPlaceCd);
		
		//회사 달력
		result.put("companyCalendar", wtmCalendarMapper.getCalendar(paramMap));
		
		//개인 근태 달력
		//result.put("workCalendar", wtmCalendarMapper.getWorkCalendar(paramMap));
		
		//지난 날은 실적, 아직 도래하지 않은 날은 계획을 보여줌
		//계획
		
		//실적
		
		//권한에 따라 팀원들 근태 보여줌
		
		
		return result;
		
	}
	
	/**
	 * 근태 달력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getWorkTimeCalendar(Map<String, Object> paramMap) throws Exception {
		return wtmCalendarMapper.getWorkTimeCalendar(paramMap);
	}
	
	/**
	 * 근태 달력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getEmpWorkCalendar(Map<String, Object> paramMap) throws Exception {
		

		List<String> auths = empService.getAuth(Long.valueOf(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString()); 

		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(Long.valueOf(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("sYmd").toString()));
		}
		
		return wtmCalendarMapper.getEmpWorkCalendar(paramMap);
	}
	
	/**
	 * 조직원 근태 달력 조회(조직장권한)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrgEmpWorkCalendar(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) throws Exception {
		
		String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("ymd", ymd);
		
		if(paramMap.get("sYmd")!=null && paramMap.get("eYmd")!=null) {
			paramMap.put("sYmd", paramMap.get("sYmd").toString().replaceAll("[-.]", ""));
			paramMap.put("eYmd", paramMap.get("eYmd").toString().replaceAll("[-.]", ""));
		}
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, ymd));
		}
		
		return wtmCalendarMapper.getOrgEmpWorkCalendar(paramMap);
	}
	
	/**
	 * 근태 달력 조회(특정일)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getEmpWorkCalendarDayInfo(Map<String, Object> paramMap) throws Exception {
		return wtmCalendarMapper.getEmpWorkCalendarDayInfo(paramMap);
	}
	
	/**
	 * 근태 달력 조회(특정일) - 타각갱신용
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getEmpWorkCalendarDayInfoEntry(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> stdMap = new HashMap<String, Object>();
		String unplannedYn = "N";
		// 근무제도의 계획없음여부를 확인해야함.
		try {
			stdMap = wtmCalendarMapper.getStdMgrInfo(paramMap);
			if(stdMap != null && stdMap.size() > 0) {
				unplannedYn = stdMap.get("unplannedYn").toString();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		if("Y".equals(unplannedYn)) {
			// 계획이 없어도 괜찮을경우 result를 outer걸어서 조회함.
			result = wtmCalendarMapper.getEmpWorkCalendarDayInfoSele(paramMap);
		} else {
			result = wtmCalendarMapper.getEmpWorkCalendarDayInfo(paramMap);
		}
		return result;
	}
	
	@Override
	public ReturnParam getHolidayYn(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> sabuns = new ArrayList<String>();
		sabuns.add(sabun);
		
		Map<String, Object> applSabuns = null;
		try {
			if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
				applSabuns = mapper.readValue(paramMap.get("applSabuns").toString(), new HashMap<String, Object>().getClass());
				
				if(applSabuns!=null && applSabuns.keySet().size()>0) {
					sabuns = new ArrayList<String>();
					for(String k : applSabuns.keySet()) {
						sabuns.add(k);
					}
				}
			} 
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("ymd")!=null && !"".equals(paramMap.get("ymd")))
				ymd = paramMap.get("ymd").toString();
			
			List<WtmWorkCalendar> calendars =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdInSabun(tenantId, enterCd, ymd, sabuns);
			
			if(calendars!=null && calendars.size()>0) {
				String holidayYn = null;
				int i = 0;
				List<String> checkTargets = null;
				for(WtmWorkCalendar c : calendars) {
					if(i == 0) {
						holidayYn = c.getHolidayYn();
						rp.put("holidayYn", holidayYn);
					}
					
					if(!holidayYn.equals(c.getHolidayYn())) {
						if(checkTargets == null)
							checkTargets = new ArrayList<String>();
						
						if(applSabuns.get(c.getSabun())!=null ) {
							Map<String, Object> applSabunInfo = (Map<String, Object>)applSabuns.get(c.getSabun());
							checkTargets.add(applSabunInfo.get("empNm").toString());
						}
					}
				
					i++;
				}
				
				if(checkTargets!=null && checkTargets.size()>0) {
					rp.put("checkTarget", checkTargets);
					rp.setFail("근무일이 다른 대상자가 있습니다. 대상자를 확인해 주세요.");
					return rp;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("휴일 조회 시 오류가 발생했습니다.");
			return rp;
		}
		
		return rp;
	}
	
	@Override
	public List<Map<String, Object>> getTaaCodeList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		
		List<Map<String, Object>> searchList = new ArrayList();

		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);

		searchList = wtmCalendarMapper.getTaaCodeList(paramMap);
		
		return searchList;

	}
}
