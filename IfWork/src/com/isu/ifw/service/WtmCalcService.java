package com.isu.ifw.service;

import java.util.Date;
import java.util.Map;

import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;

public interface WtmCalcService {

	/**
	 * 고정 OT 일괄 소진에 대한 부분 & no plan 케이스
	 * 기존 P_WTM_WORK_DAY_RESULT_CREATE_N
	 */
	public void P_WTM_WORK_DAY_RESULT_CREATE_N(WtmWorkCalendar calendar, WtmFlexibleStdMgr flexibleStdMgr, WtmTimeCdMgr timeCdMgr, Long tenantId, String enterCd,  String sabun, String ymd, int addSumWorkMinute, String userId);

	public int P_WTM_WORK_DAY_RESULT_CREATE_T(WtmFlexibleStdMgr flexibleStdMgr, Long tenantId, String enterCd, String sabun, String ymd, Long timeCdMgrId, Date entrySdate, Date entryEdate, int unitMinute, String timeTypeCd, String breakTypeCd, int limitMinute, int useMinute, String userId);
	
	public void P_WTM_WORK_DAY_RESULT_CREATE_F(Long tenantId, String enterCd,  String sabun, String ymd, WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr, String userId);
	
	public Date WorkTimeCalcApprDate(Date rDt, Date dt, int unitMinute, String calcType);
		
	public void createWorkDayResultForBreakTime(Long tenantId, String enterCd, String sabun, String ymd, String taaInfoCd, String type, int breakMinute, String userId );
	public int WtmCalcMinute(String shm, String ehm, String limitShm, String limitEhm, Integer unitMinute) ;
	public int getBreakMinuteIfBreakTimeTIME(long timeCdMgrId, int apprMinute);
	public Map<String, Object> getBreakMinuteIfBreakTimeMGR(Date sDate, Date eDate, long timeCdMgrId);
	public int getBreakMinuteIfBreakTimeMGR(Date sDate, Date eDate, long timeCdMgrId, int unitMinute) ;
	
	/**
	 * 인정근무시간 (분) 계산
	 * @param sDate
	 * @param eDate
	 * @param breakTypeCd
	 * @param timeCdMgrId
	 * @param unitMinute
	 * @return
	 */
	public Map<String, Object> calcApprMinute(Date sDate, Date eDate, String breakTypeCd, long timeCdMgrId, int unitMinute);
}
