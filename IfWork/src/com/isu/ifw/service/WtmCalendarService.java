package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

public interface WtmCalendarService {

	public Map<String, Object> getCalendar(Long tenantId, String enterCd, String bisinessPlaceCd, Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> getWorkTimeCalendar(Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> getEmpWorkCalendar(Map<String, Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> getOrgEmpWorkCalendar(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) throws Exception;
	
	public Map<String, Object> getEmpWorkCalendarDayInfo(Map<String, Object> paramMap) throws Exception;
}
