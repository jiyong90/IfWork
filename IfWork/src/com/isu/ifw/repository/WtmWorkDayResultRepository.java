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
	
	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE D.timeTypeCd = ?1 AND C.tenantId = ?2 AND C.enterCd = ?3 AND C.sabun = ?4 AND C.ymd = ?5")
	public WtmWorkDayResult findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(String timeTypeCd, Long tenantId, String enterCd, String sabun, String ymd);

	@Query("SELECT D FROM WtmWorkDayResult D JOIN WtmWorkCalendar C ON D.tenantId = C.tenantId AND D.enterCd = C.enterCd AND D.ymd = C.ymd AND D.sabun = C.sabun WHERE C.workCalendarId = ?1")
	public List<WtmWorkDayResult> findByWorkCalendarId(Long workCalendarId);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(Long tenantId, String enterCd, String sabun, String timeTypeCd, String symd, String eymd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndTimeTypeCdInAndYmdBetween(Long tenantId, String enterCd, List<String> timeTypeCd, String symd, String eymd);
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(Long tenantId, String enterCd, String sabun, List<String> timeTypeCd, String symd, String eymd);

	public WtmWorkDayResult findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndPlanSdateAndPlanEdate(Long tenantId, String enterCd, String sabun, String timeTypeCd, Date sdate, Date edate);
	
	public WtmWorkDayResult findByWorkDayResultId(Long workDayResultId);
	 
	@Modifying
	@Query("DELETE FROM WtmWorkDayResult D WHERE D.tenantId = :tenantId AND D.enterCd = :enterCd AND D.ymd = :ymd AND D.timeTypeCd = :timeTypeCd AND D.sabun = :sabun ")
	public int deleteByTenantIdAndEnterCdAndYmdAndTimeTypeCdAndSabun(@Param(value="tenantId") Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="ymd") String ymd, @Param(value="timeTypeCd") String timeTypeCd, @Param(value="sabun") String sabun );
	
	public List<WtmWorkDayResult> findByTenantIdAndEnterCdAndYmdAndSabunAndPlanSdateLessThanEqualAndPlanEdateGreaterThanEqualOrderByPlanSdateAsc(Long tenantId, String enterCd, String ymd, String sabun, Date planSdate, Date planEdate );
	
}
