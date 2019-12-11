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
			paramMap.put("sYmd", paramMap.get("sYmd").toString().replaceAll("-", ""));
			paramMap.put("eYmd", paramMap.get("eYmd").toString().replaceAll("-", ""));
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
	
	@Override
	public ReturnParam getEmpsCalendar(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		ObjectMapper mapper = new ObjectMapper();
		List<String> sabuns = new ArrayList<String>();
		Map<String, Object> applSabuns = null;
		
		try {
			if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
				applSabuns = mapper.readValue(paramMap.get("applSabuns").toString(), new HashMap<String, Object>().getClass());
				
				if(applSabuns!=null) {
					for(String k : applSabuns.keySet()) {
						sabuns.add(k);
					}
				}
				
			} else {
				sabuns.add(sabun);
			}
			
			String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
			if(paramMap.get("ymd")!=null && !"".equals(paramMap.get("ymd")))
				ymd = paramMap.get("ymd").toString();
			
			List<WtmWorkCalendar> calendars =  workCalendarRepo.findByTenantIdAndEnterCdAndYmdInSabun(tenantId, enterCd, ymd, sabuns);
			
			if(calendars!=null && calendars.size()>0) {
				String holidayYn = null;
				int i = 0;
				List<String> diffTargets = null;
				for(WtmWorkCalendar c : calendars) {
					if(i == 0) {
						holidayYn = c.getHolidayYn();
						rp.put("holidayYn", holidayYn);
					}
					
					if(!holidayYn.equals(c.getHolidayYn())) {
						if(diffTargets == null)
							diffTargets = new ArrayList<String>();
						
						if(applSabuns.get(c.getSabun())!=null ) {
							Map<String, Object> applSabunInfo = (Map<String, Object>)applSabuns.get(c.getSabun());
							diffTargets.add(applSabunInfo.get("empNm").toString());
						}
					}
				
					i++;
				}
				
				if(diffTargets!=null && diffTargets.size()>0) {
					rp.setFail("근무일이 다른 대상자가 있습니다. "+diffTargets.toString()+" 대상자를 확인해 주세요.");
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
}
