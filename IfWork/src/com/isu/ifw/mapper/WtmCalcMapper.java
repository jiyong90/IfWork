package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmCalcMapper {
	/**
	 * 특정일의 캘린더 정보를 가져온다. 
	 * entry s/e Date is not null
	 * holidayYn = 'N' 
	 * @param paramMap { tenantId, enterCd, sabun, ymd }
	 * @return
	 */
	public Map<String, Object> getCalendarInfoByYmdAndEntryIsNotNullAndisNotHoliday(Map<String, Object> paramMap);
	
	/**
	 * 근무제 기간 내의 총 근무일과 실제 근무한 시간을 구한다.
	 * @param paramMap { tenantId, enterCd, sabun, ymd }
	 * @return
	 */
	public Map<String, Object> getTotalWorkMinuteAndRealWorkMinute(Map<String, Object> paramMap);
	
	
}
