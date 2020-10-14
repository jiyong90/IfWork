package com.isu.ifw.service;

import java.util.List;

import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmWorkPattDet;

public interface WtmFlexibleEmpResetService {
	
	//근무조
	final static String WORK_TYPE_BASE_WORKTEAM = "WORKTEAM";
	final static String WORK_TYPE_BASE = "BASE";
	final static String WORK_TYPE_FLEX = "FLEX";
	
	public void P_WTM_FLEXIBLE_EMP_RESET(Long tenantId, String enterCd, String sabun, String year, String userId) throws Exception;
	public void P_WTM_WORK_CALENDAR_RESET(WtmFlexibleStdMgr flexStdMgr, List<WtmWorkPattDet> pattDets, String sabun, String sYmd, String eYmd, String workType, Long workteamMgrId, String userId) throws Exception;
	public void P_WTM_WORK_DAY_RESULT_RESET(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd, String userId);
	public void P_WTM_WORK_DAY_RESULT_TIME_C(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd, String userId);
}
