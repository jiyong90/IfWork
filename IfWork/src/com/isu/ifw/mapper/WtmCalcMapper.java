package com.isu.ifw.mapper;

import java.util.Map;

import com.isu.ifw.vo.WtmFlexibleInfoVO;

public interface WtmCalcMapper {
	/**
	 * 특정일의 캘린더 정보를 가져온다. 
	 * entry s/e Date is not null
	 * holidayYn = 'N' 
	 * @param paramMap { tenantId, enterCd, sabun, ymd}
	 * @return
	 */
	public WtmFlexibleInfoVO getCalendarInfoByYmdAndEntryIsNotNullAndisNotHoliday(Map<String, Object> paramMap);
	
	/**
	 * 근무제 기간 내의 총 근무일과 실제 근무한 시간을 구한다.
	 * @param paramMap { tenantId, enterCd, sabun, ymd }
	 * @return
	 */
	public WtmFlexibleInfoVO getTotalWorkMinuteAndRealWorkMinute(Map<String, Object> paramMap);
	
	/**
	 * FIXOT 소진 시간 과 현재까지 FIXOT가 생성된 시간을 가지고 온다. 
	 * FIXOT 일괄 소진때 사용하는 기능이다. 
	 * @param paramMap
	 * @return
	 */
	public WtmFlexibleInfoVO getTotalFixOtMinuteAndRealFixOtkMinute(Map<String, Object> paramMap);
	
	
	/**
	 * 기간내 apprMinute is not null 의 apprMinute 합을 구한다. 
	 * @param paramMap { tenantId, enterCd, sabun, sYmd, eYmd, timeTypeCd }
	 * @return
	 */
	public WtmFlexibleInfoVO calcSumApprMinuteForWorkDayResultByApprMinuteIsNotNull(Map<String, Object> paramMap);
	
	
}
