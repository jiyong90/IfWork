package com.isu.ifw.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkDayResult;

@Repository
public interface WtmWorkDayResultRepository extends JpaRepository<WtmWorkDayResult, Long> {
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND C.ymd = ?4")
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndYmd(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE D.timeTypeCd = ?1 AND C.tenantId = ?2 AND C.enterCd = ?3 AND C.sabun = ?4 AND C.ymd = ?5 ORDER BY D.planSdate")
	public List<WtmWorkDayResult> findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(String timeTypeCd, Long tenantId, String enterCd, String sabun, String ymd);

	public List<WtmWorkDayResult> findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmdAndApprSdateIsNotNullOrderByApprSdateAsc(String timeTypeCd, Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.workCalendarId = ?1")
	public List<WtmWorkDayResult> findByWorkCalendarId(Long workCalendarId);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(Long tenantId, String enterCd, String sabun, String timeTypeCd, String symd, String eymd);
	
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
	
}
