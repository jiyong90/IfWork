package com.isu.ifw.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.isu.ifw.vo.WtmWorkTermTimeVO;

public interface WtmFlexibleEmpMapper {
	
	/** 
	 * 해당 월의 근무제 정보 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getFlexibleEmpList(Map<String, Object> paramMap);
	
	/**
	 * 해당 일의 근무 시간 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getWorkDayResult(Map<String, Object> paramMap);
	
	/** 
	 * 선택한 기간의 근무제 정보 조회
	 * @param paramMap
	 * @return
	 */
	public WtmWorkTermTimeVO getWorkTermTime(Map<String, Object> paramMap);
	
	/**
	 * 기존에 신청한 근무제 적용일 가져오기
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getPrevFlexible(Map<String, Object> paramMap);
	
	/**
	 * 부분선근제의 캘린더 생성
	 * @param flexibleEmpId
	 * @param userId
	 */
	public void createWorkCalendarOfSeleC(@Param("flexibleEmpId")Long flexibleEmpId, @Param("userId")String userId);
	
	/**
	 * 탄근제의 캘린더 생성
	 * @param flexibleApplId
	 * @param userId
	 */
	public void createWorkCalendarOfElas(@Param("flexibleApplId")Long flexibleEmpId, @Param("userId")String userId);
	
	public void updateHolidayYnOFWorkCalendar(Map<String, Object> paramMap);
	
	public void updatePlanMinute(@Param("flexibleEmpId") Long flexibleEmpId);
	/**
	 * 인정시간 갱신
	 * @param paramMap
	 */
	public void updateApprDatetimeByYmdAndSabun(Map<String, Object> paramMap);
	
	public void updateEntrySdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(Map<String, Object> paramMap);
	public void updateEntryEdateByTenantIdAndEnterCdAndYmdBetweenAndSabun(Map<String, Object> paramMap);
	public void updateTimeTypePlanToEntryTimeByTenantIdAndEnterCdAndYmdBetweenAndSabun(Map<String, Object> paramMap);
	
	public void createDayResultByTimeTypeAndEntryDateIsNull(Map<String, Object> paramMap);
	/**
	 * 코어타임 필수 여부에 따라 출퇴근 타각정보 둘다 존재할 경우 코어타임 시작시간보다 적을 경우 결근처리를 한다.
	 * @param paramMap
	 */
	public void createDayResultByTimeTypeAndCheckRequireCoreTimeYn(Map<String, Object> paramMap);
	
	public void createDayResultByTimeTypeAndEntrtEdateIsNull(Map<String, Object> paramMap);
	

	public void createDayResultByTimeTypeAndPlanSdateLessThanApprSdate(Map<String, Object> paramMap);
	public void createDayResultByTimeTypeAndApprEdateLessThanPlanEdate(Map<String, Object> paramMap);
	

	/**
	 * 인정시간의 분 계산 - 휴게시간 제외 -
	 * BreakTypeCd = MGR 기준
	 * @param paramMap
	 */
	public void updateApprMinuteByYmdAndSabun(Map<String, Object> paramMap);
	
	/**
	 * BreakTypeCd = TIME 계산
	 * @param paramMap
	 */
	public void updateTimeTypeApprMinuteByYmdAndSabun(Map<String, Object> paramMap);
	/**
	 * BreakTypeCd = TIMEFIX 계산
	 * @param paramMap
	 */
	public void updateTimeFixTypeApprMinuteByYmdAndSabun(Map<String, Object> paramMap);
	
	public Map<String, Object> checkBaseWorktime(@Param("flexibleEmpId") Long flexibleEmpId);
	
	public Map<String, Object> checkBaseWorktimeMgr(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getWorktimePlan(@Param("flexibleEmpId") Long flexibleEmpId);
	
	public List<Map<String, Object>> getWorktimePlanByYmdBetween(Map<String, Object> paramMap);
	
	/**
	 * 근무제 기간에서 특정일 포함 이전 근무시간 합(분) - 인정 분 이 없을 경우 계획 분으로   
	 * @param paramMap - tenantId, enterCd, sabun, ymd
	 * @return { totalApprMinute : 22 }
	 */
	public Map<String, Object> getTotalApprMinute(Map<String, Object> paramMap);
	
	/**
	 * 근무제 기간에서 특정일 이후부터 근무제 적용 종료기간 까지의 코어시간의 합을 가지고 온다.
	 * 사용처 : 연장근무 신청 시 소정근로 선 소진사용여부에 따라 사용할 수 있다. 
	 * @param paramMap - tenantId, enterCd, sabun,  ymd
	 * @return { coreHm : 22 }
	 */
	public Map<String, Object> getTotalCoretime(Map<String, Object> paramMap);
	/**
	 * 근무제 기간에서 연장근무 시간을 가지고 오자
	 * @param paramMap
	 * @return { otMinute : 22 }
	 */
	public Map<String, Object> getSumOtMinute(Map<String, Object> paramMap);
	
	/**
	 * WTM_PROPETIE OPTION_BREAKTIME_INCLUDED_YN_OF_OT_APPL 옵션여부에 따라 휴게시간 계산 여부 Y면 휴게시간을 인정시간으로 본다.즉근무시간 전체가 인정
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> calcMinuteAsBreaktimeOption(Map<String, Object> paramMap);
	/**
	 * 휴게시간을 제외한 시간 계산
	 * @param paramMap { timeCdMgrId : 휴게시간아이디,shm : '2200', ehm : '0200'}
	 * @return { calcMinute : 22 } 
	 */
	public Map<String, Object> calcMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeApprMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeFixMinuteExceptBreaktime(Map<String, Object> paramMap);
	
	/**
	 * 탄력 근무제의 휴게시간을 제외한 계획 시간 계산
	 * @param paramMap { flexibleApplId: 111 , ymd: '20191120', shm: , ehm:}
	 * @return
	 */
	public Map<String, Object> calcElasPlanMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeElasPlanMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeFixElasPlanMinuteExceptBreaktime(Map<String, Object> paramMap);
	
	/**
	 * 탄력 근무제의 휴게시간을 제외한 연장 시간 계산
	 * @param paramMap { flexibleApplId: 111 , ymd: '20191120', minute: }
	 * @return
	 */
	public Map<String, Object> calcElasOtMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeElasOtMinuteExceptBreaktime(Map<String, Object> paramMap);
	public Map<String, Object> calcTimeTypeFixElasOtMinuteExceptBreaktime(Map<String, Object> paramMap);
	
	/**
	 * 근무제 기간에 대한 정보
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getFlexibleRangeInfo(Map<String, Object> paramMap);
	
	/**
	 * 근무일에 대한 정보
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getFlexibleDayInfo(Map<String, Object> paramMap);
	
	/**
	 * 선택한 날의 근무시간에 대한 정보
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getFlexibleWorkTimeInfo(Map<String, Object> paramMap);
	
	/**
	 * 오늘의 근무제 정보
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getFlexibleEmp(Map<String, Object> paramMap);
	
	/**
	 * 이미 신청중이거나 등록된 근무정보가 있는지 확인하자
	 * @param paramMap { tenantId, sabun, enterCd, sYmd, eYmd, otSdate(DATE), otEdate(DATE)
	 * @return { workCnt : 2 }
	 */
	public Map<String, Object> checkDuplicateWorktime(Map<String, Object> paramMap);
	public Map<String, Object> checkDuplicateSubsWorktime(Map<String, Object> paramMap);
	
	/**
	 * 근무조에 등록된 대상자를 WTM_FLEXIBLE_EMP 테이블에 생성한다.
	 * 단 근무조 기간에 중복된 데이터가 있는 대상자는 제외하고 등록한다.
	 * 근무조, 유연근무제 모두 중복된 데이터로 한다. 근무조는 근무조 관리에서 시작종료를 관리하기 때문에 선 수정 작업 
	 * 기본근무제는 체크하지 않고 중복데이터 삽입 - 이후에 FLEXIBLE_EMP의 시작종료일 정리하는 업데이트문이 필요하다
	 * @param paramMap
	 */
	public void createWorkteamOfWtmFlexibleEmp(Map<String, Object> paramMap);
	
	/**
	 * 중복된 데이터들 중에 같은 근무제의 중복은 기간 업데이트를 하자. 
	 * 예를 들어 근무조의 종료일만 변경했을 경우 - 종료일 변경 시 사전에 중복되는 지 여부를판단해야한다
	 * @param paramMap
	 */
	public void updateWorkteamOfWtmFlexibleEmp(Map<String, Object> paramMap);
	
	/**
	 * 기본근무제에 대한 정보를 갱신한다. 
	 * 유연근무제 > 근무조 > 기본근무 순으로 기본근무로 구분되는 근무조와 기본근무에 대한 정보를 다시 셋팅한다.
	 * @param paramMap
	 */
	public void initWtmFlexibleEmpOfWtmWorkDayResult(Map<String, Object> paramMap);
	
	/**
	 * 고정 OT 일괄소진 계산 : flexibleEmpId별 근무제 기간 내 : 날짜 구간이 겹치면 앞단에서 LOOP 돌아야한당
	 * 
	 * @param paramMap
	 */
	public void resetFixOtWtmWorkDayResultByFlexibleEmpId(Map<String, Object> paramMap);
	public void resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(Map<String, Object> paramMap);
	
	public void createWtmWorkteamOfWtmWorkDayResult(Map<String, Object> paramMap);
	
	/**
	 * 근무조, 근무조대상자 변경 시 호출
	 * @param paramMap
	 * @return
	 */
	public void resetWtmWorkteamOfWtmWorkDayResult(Map<String, Object> paramMap);

	/**
	 * 계획을 작성 해야 하는 유연근무제 리스트
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getFlexibleEmpListForPlan(Map<String, Object> paramMap);
	
	/**
	 * calendar Id로 일근무 데이터 전체 조회(관리자용)
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getWorkDayResultByCalendarId(Map<String, Object> paramMap);
	
	/**
	 * 근무 시간 조회
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getWorkHour(Map<String, Object> paramMap);
	
	/**
	 * 개인별 근무제도조회 관리자 화면
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getFlexibleEmpWebList(Map<String, Object> paramMap);
	
	/**
	 * 특정 사원의
	 * 특정 기간의
	 * 통계데이터를 (재)생성한다
	 * @param paramMap { tenantId, enterCd, sabun, symd, eymd, pId }
	 */
	public void createWorkTermBySabunAndSymdAndEymd(Map<String, Object> paramMap);
	
	public Map<String, Object> findWorkDayResultMinMaxByYmdAndTimeTypeCdBySabun(Map<String, Object> paramMap);
	
	
	
	/**
	 * 탄력근무제의 계획 생성
	 * @param flexibleEmpId
	 * @param userId
	 */
	public void createFlexibleApplDet(@Param("flexibleApplId")Long flexibleApplId, @Param("userId")String userId);
	
	public void deleteByApplId(@Param("applId") Long applId);
	
	/**
	 * 계획을 작성 해야 하는 유연근무제 리스트
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getFlexibleListForPlan(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getPlanByFlexibleEmpId(Map<String, Object> paramMap);
	
	/**
	 * 그날의 TimeTypeCd 의 마지막 시분을 가지고 온다
	 * @param paramMap
	 * @return
	 */
	public Date getMaxPlanEdate(Map<String, Object> paramMap);
	/**
	 * 
	 * @param paramMap
	 *  { yyyyMMddHHmmss : "20190101180000"  -- 퇴근시간이라면
	 *    intervalMinute : yyyyMMddHHmmss 이후 "60" -- 60분에 대한 시간이 언제인지 가지고 오기 위해. (휴게시간을 제외한 시간이다.)
	 *    tenantId
	 *    enterCd
	 *    sabun
	 *    ymd
	 * @return
	 */
	public Date getIntervalDateTime(Map<String, Object> paramMap);
	
	/**
	 * 계획을 작성해야 하는 탄근제 조회
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getElasForPlan(Map<String, Object> paramMap);
	
	/**
	 * 탄근제 근무 계획 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getElasPlanByFlexibleApplId(Map<String, Object> paramMap);
	
	/** 
	 * 탄근제 근무 계획 조회 - work_day_result 테이블에 저장 하기 위함
	 * 하나의 ROW를 BASE와 OT로 분리
	 * @param flexibleApplId
	 * @return
	 */
	public List<Map<String, Object>> getElasWorkDayResult(@Param("flexibleApplId")Long flexibleApplId);
	
	/**
	 * 탄근제 평균 근무 시간 조회
	 * @param paramMap flexibleApplId
	 * @return
	 */
	public Map<String, Object> getElasAvgHour(Map<String, Object> paramMap);
	
	/**
	 * 탄근제 주별 근무 시간 조회
	 * @param flexibleApplId
	 * @return
	 */
	public List<Map<String, Object>> getElasWeekHour(@Param("flexibleApplId")Long flexibleApplId);
	

	/**
	 * 유연근무 변경/취소 확인
	 */
	public Map<String, Object> getChangeChk(Map<String, Object> paramMap);
	
	/**
	 * 유연근무 적용내용 삭제
	 */
	public void deleteByflexibleEmpId(Map<String, Object> paramMap);
	
	/**
	 * 유연근무 적용내용 수정
	 */
	public void updateByflexibleEmpId(Map<String, Object> paramMap);
	
	/**
	 * 유연근무 오류메시지 반영
	 */
	public void setChangeErrMsg(Map<String, Object> paramMap);
	/**
	 * 유연근무 완료메시지 반영
	 */
	public void setChangeEndMsg(Map<String, Object> paramMap);
}
