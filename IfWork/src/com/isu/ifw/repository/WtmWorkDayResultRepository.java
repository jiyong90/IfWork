package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmWorkDayResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface WtmWorkDayResultRepository extends JpaRepository<WtmWorkDayResult, Long> {
	// @Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmd(Long tenantId, String enterCd, String sabun, String ymd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdOrderByPlanSdateAsc(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE D.timeTypeCd = ?1 AND C.tenantId = ?2 AND C.enterCd = ?3 AND C.sabun = ?4 AND C.ymd = ?5 ORDER BY D.planSdate")
	public List<WtmWorkDayResult> findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(String timeTypeCd, Long tenantId, String enterCd, String sabun, String ymd);

	public List<WtmWorkDayResult> findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(String timeTypeCd, Long tenantId, String enterCd, String sabun, String ymd);
	public List<WtmWorkDayResult> findByTimeTypeCdInAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(List<String> timeTypeCds, Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.workCalendarId = ?1")
	public List<WtmWorkDayResult> findByWorkCalendarId(Long workCalendarId);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(Long tenantId, String enterCd, String sabun, String timeTypeCd, String symd, String eymd);

	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetween(Long tenantId, String enterCd, String sabun, List<String> timeTypeCd, String symd, String eymd);

	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween(Long tenantId, String enterCd, List<String> timeTypeCd, String symd, String eymd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(Long tenantId, String enterCd, String sabun, List<String> timeTypeCd, String symd, String eymd);

	public WtmWorkDayResult findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndPlanSdateAndPlanEdate(Long tenantId, String enterCd, String sabun, String timeTypeCd, Date sdate, Date edate);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndApplId(Long tenantId, String enterCd, String sabun, Long applId);
	
	public WtmWorkDayResult findByWorkDayResultId(Long workDayResultId);
	 
	@Modifying
	@Query("DELETE FROM WtmWorkDayResult D WHERE D.tenantId = :tenantId AND D.enterCd = :enterCd AND D.ymd = :ymd AND D.timeTypeCd = :timeTypeCd AND D.sabun = :sabun ")
	public int deleteByTenantIdAndEnterCdAndYmdAndTimeTypeCdAndSabun(@Param(value="tenantId") Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="ymd") String ymd, @Param(value="timeTypeCd") String timeTypeCd, @Param(value="sabun") String sabun );
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndPlanSdateLessThanEqualAndPlanEdateGreaterThanEqualOrderByPlanSdateAsc(Long tenantId, String enterCd, String ymd, String sabun, Date planSdate, Date planEdate );
	
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdNotAndPlanSdateGreaterThanEqualAndPlanEdateLessThanEqualOrderByPlanSdateAsc(Long tenantId, String enterCd, String sabun, String timeTypeCd, Date planSdate, Date planEdate );
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdGreaterThan(Long tenantId, String enterCd, String sabun, String ymd);
	
	
	/**
	 * 
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param ymd
	 * @param timeTypeCd
	 * @param taaCd
	 * @return
	 */
	public WtmWorkDayResult findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndTaaCd(Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd, String taaCd);
	
	/**
	 * 
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param ymd
	 * @param timeTypeCd NOT IN
	 * @param taaCd
	 * @return
	 */
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndTaaCdNotIn(Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd, List<String> taaCd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndApprEdateAfterAndApprSdateBefore(Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd, Date sDate, Date eDate);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4 AND D.timeTypeCd = ?5 AND C.entrySdate IS NOT NULL AND C.entryEdate IS NOT NULL AND D.planSdate IS NOT NULL AND D.planEdate IS NOT NULL")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull( Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4 AND D.timeTypeCd in (?5) AND C.entrySdate IS NOT NULL AND C.entryEdate IS NOT NULL AND D.planSdate IS NOT NULL AND D.planEdate IS NOT NULL ORDER BY D.planSdate, D.planEdate")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdInAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull( Long tenantId, String enterCd, String sabun, String ymd, List<String> timeTypeCds);

	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4 AND C.entrySdate IS NOT NULL AND C.entryEdate IS NOT NULL AND D.planSdate IS NOT NULL AND D.planEdate IS NOT NULL ORDER BY D.planSdate")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull( Long tenantId, String enterCd, String sabun, String ymd);

	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4 AND D.timeTypeCd not in (?5) AND C.entrySdate IS NOT NULL AND C.entryEdate IS NOT NULL AND D.planSdate IS NOT NULL AND D.planEdate IS NOT NULL ORDER BY D.planSdate")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAnAndTimeTypeCdNotInAndEntrySdateIsNotNullAndEntryEdateIsNotNullAndPlanSdateIsNotNullAndPlanEdateIsNotNull( Long tenantId, String enterCd, String sabun, String ymd, List<String> timeTypeCds);

	@Query("SELECT R FROM WtmWorkDayResult R JOIN WtmWorkCalendar C " + 
			" ON C.tenantId = R.tenantId  AND C.enterCd = R.enterCd AND C.ymd = R.ymd AND C.sabun = R.sabun " +
			" JOIN WtmWorkCalendar C " + 
			"   ON R.tenantId = C.tenantId " + 
			"  AND R.enterCd = C.enterCd " + 
			"  AND R.sabun = C.sabun AND C.ymd = R.ymd " + 
			"WHERE C.tenantId = :tenantId " + 
			"   AND C.enterCd = :enterCd " + 
			"   AND C.ymd = :ymd " + 
			"   AND C.entryEdate > R.planSdate " + 
			"   AND C.entrySdate < R.planEdate " + 
			"   AND C.entrySdate IS NOT NULL AND C.entryEdate IS NOT NULL " +
			"   AND R.apprSdate IS NULL " +
			"   AND R.apprEdate IS NULL " +
			"   AND 1 > (SELECT count(SR)" + 
			"   	   	   FROM WtmWorkDayResult SR WHERE SR.tenantId = R.tenantId " + 
			"   		    AND SR.enterCd = R.enterCd AND SR.sabun = R.sabun " + 
			"   		    AND SR.timeTypeCd = :timeTypeCd " + 
			"   		    AND SR.taaCd = :taaCd AND SR.ymd = R.ymd )  " +
			"   AND (C.sabun =  :sabun OR :sabun = '' OR  :sabun IS NULL) " + 
			"   AND R.timeTypeCd NOT IN ('TAA', 'SUBS') ")
	public List<WtmWorkDayResult> findBytenantIdAndEnterCdAndYmdAndSabunNotInTimeTypeCdAndTaaCd(@Param(value="tenantId") Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="ymd") String ymd, @Param(value="timeTypeCd") String timeTypeCd, @Param(value="taaCd") String taaCd, @Param(value="sabun") String sabun);
	
	/**
	 * 시각 구간 내의 정보들을 조회한다.  
	 * @param tenantId
	 * @param enterCd
	 * @param ymd
	 * @param sabun
	 * @param sDate
	 * @param eDate
	 * @param greaterMinute
	 * @return
	 */
	@Query("SELECT D FROM WtmWorkDayResult D WHERE D.tenantId = :tenantId AND D.enterCd = :enterCd AND D.sabun = :sabun AND D.ymd = :ymd AND D.apprMinute IS NOT NULL AND D.apprMinute > :greaterMinute AND D.apprSdate < :eDate AND D.apprEdate > :sDate ORDER BY planSdate, apprSdate ASC")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndApprEdateAfterAndApprSdateBeforeAndApprMinuteGreaterThenAndApprMinuteIsNotNullOrderByApprSdateAsc(Long tenantId, String enterCd, String ymd, String sabun, Date sDate, Date eDate, int greaterMinute);

	@Query("SELECT D FROM WtmWorkDayResult D WHERE D.tenantId = :tenantId AND D.enterCd = :enterCd AND D.sabun = :sabun AND D.ymd = :ymd AND D.planSdate < :eDate AND D.planEdate > :sDate ORDER BY planSdate, apprSdate ASC")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndplanEdateAfterAndplanSdateBeforeOrderByApprSdateAsc(Long tenantId, String enterCd, String ymd, String sabun, Date sDate, Date eDate);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(Long tenantId, String enterCd, String sabun, String symd, String eymd );

	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndTaaCdOrderByYmdAsc(Long tenantId, String enterCd, String sabun, String symd, String eymd, String taaCd );

	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNull(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdInAndApprMinuteIsNull(Long tenantId, String enterCd, String sabun, String ymd, List<String> timeTypeCds);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNotNull(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	/*
	SUBS를 빼자 APPL_ID 를 찾아 연장근무 일이 현재 근무기간에 속하지 않을 경우 빼야한다. 
    SELECT F_WTM_NVL(SUM(F_WTM_NVL(R.PLAN_MINUTE,0)),0) INTO v_subs_minute
	FROM WTM_FLEXIBLE_EMP E
	JOIN WTM_WORK_DAY_RESULT R
	ON E.TENANT_ID = R.TENANT_ID
	AND E.ENTER_CD = R.ENTER_CD
	AND E.SABUN = R.SABUN
	AND R.YMD BETWEEN E.SYMD AND E.EYMD
	JOIN WTM_OT_APPL O
	ON R.APPL_ID = O.APPL_ID
	WHERE E.FLEXIBLE_EMP_ID = P_SABUN
	AND R.TIME_TYPE_CD = 'SUBS'
	AND O.YMD NOT BETWEEN E.SYMD AND E.EYMD 
	;
    */
	@Query("SELECT r FROM  WtmWorkDayResult r JOIN WtmFlexibleEmp e ON e.tenantId = r.tenantId AND e.enterCd = r.enterCd AND e.sabun = r.sabun AND r.ymd BETWEEN e.symd AND e.eymd  WHERE e.flexibleEmpId = ?1 AND r.timeTypeCd = 'SUBS' AND r.planMinute IS NOT NULL ")
	public List<WtmWorkDayResult> findByFlexibleEmpIdToSubsPlanMinute(Long flexibleEmpId);

	@Transactional
	public int deleteByApplId(Long applId);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndApplIdIsNullAndTimeTypeCdIn(Long tenantId, String enterCd, String ymd, String sabun, List<String> timeTypeCds);
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdBetweenAndSabunAndApplIdIsNullAndTimeTypeCdIn(Long tenantId, String enterCd, String sYmd, String eYmd, String sabun, List<String> timeTypeCds);

	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndApplIdIsNull(Long tenantId, String enterCd, String ymd, String sabun);

	@Query("SELECT MIN(R.planSdate) AS planSdate , MAX(R.planEdate) AS planEdate FROM WtmWorkDayResult R WHERE R.tenantId = ?1 AND R.enterCd = ?2 AND R.sabun = ?3 AND R.ymd = ?4 AND R.timeTypeCd NOT IN (?5) ")
	public Map<String, Object> findByMinMaxPlanDate(Long tenantId, String enterCd, String sabun, String ymd, String timeTypeCd);

	@Query("SELECT MIN(R.planSdate) AS planSdate , MAX(R.planEdate) AS planEdate FROM WtmWorkDayResult R WHERE R.tenantId = ?1 AND R.enterCd = ?2 AND R.sabun = ?3 AND R.ymd = ?4 AND R.timeTypeCd IN ('BASE', 'REGA', 'TAA') ")
	public Map<String, Object> findByMinMaxPlanDate(Long tenantId, String enterCd, String sabun, String ymd);

	public List<WtmWorkDayResult> findByTimeTypeCdInAndTenantIdAndEnterCdAndSabunAndYmdAndApprMinuteIsNullAndAndApplIdIsNull(List<String> timeTypeCds, Long tenantId, String enterCd, String sabun, String ymd);

}
