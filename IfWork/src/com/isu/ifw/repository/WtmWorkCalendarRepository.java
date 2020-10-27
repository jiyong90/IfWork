package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkCalendar;

@Repository
public interface WtmWorkCalendarRepository extends JpaRepository<WtmWorkCalendar, Long> {
	public WtmWorkCalendar findByTenantIdAndEnterCdAndSabunAndYmd(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT C FROM WtmWorkCalendar C WHERE C.tenantId=?1 AND C.enterCd=?2 AND C.entryEtypeCd=?3 AND F_WTM_DATE_FORMAT(C.updateDate)=?4 AND C.updateId=?5")
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndEntryEtypeCdAndUpdateDateAndUpdateId(Long tenantId, String enterCd, String entryEtypeCd, String updateDate, String updateId);

	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndSabunAndYmdBetween(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	
	@Query("SELECT C FROM WtmWorkCalendar C WHERE C.tenantId=?1 AND C.enterCd=?2 AND C.ymd=?3 AND C.sabun IN ?4 ")
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndYmdInSabun(Long tenantId, String enterCd, String ymd, List<String> sabuns);

	@Query("SELECT C FROM WtmWorkCalendar C WHERE C.tenantId=?1 AND C.enterCd=?2 AND C.ymd=?3 AND C.sabun IN ?4 ")
	public WtmWorkCalendar findByTenantIdAndEnterCdAndYmdAndSabun(Long tenantId, String enterCd, String ymd, String sabun);

	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndHolidayYn(Long tenantId, String enterCd, String sabun, String symd, String eymd, String holidayYn);

	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndSabunAndYmdGreaterThan(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT C FROM WtmWorkCalendar C JOIN WtmTimeCdMgr T ON C.timeCdMgrId = C.timeCdMgrId WHERE C.tenantId=?1 AND C.enterCd=?2 AND C.ymd=?3 AND C.sabun = ?4 AND T.breakTypeCd = ?5 ")
	public WtmWorkCalendar findByTenantIdAndEnterCdAndSabunAndYmdAndBreakTypeCd(Long tenantId, String enterCd, String sabun, String ymd, String breakTypeCd);
	
	@Query("SELECT C FROM WtmWorkCalendar C JOIN WtmFlexibleEmp F ON C.tenantId = F.tenantId AND C.enterCd = F.enterCd AND C.sabun = F.sabun AND C.ymd BETWEEN F.symd AND F.eymd WHERE C.tenantId = :tenantId AND C.enterCd = :enterCd AND C.ymd BETWEEN :sYmd AND :eYmd Order By C.ymd")
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndYmdBetweenOrderByYmd(@Param("tenantId") Long tenantId,@Param("enterCd") String enterCd,@Param("sYmd") String sYmd,@Param("eYmd")  String eYmd);
	
	@Query("SELECT C FROM WtmWorkCalendar C JOIN WtmFlexibleEmp F ON C.tenantId = F.tenantId AND C.enterCd = F.enterCd AND C.sabun = F.sabun AND C.ymd BETWEEN F.symd AND F.eymd WHERE F.flexibleEmpId = :flexibleEmpId AND F.workTypeCd IN :workTypeCd ")
	public List<WtmWorkCalendar> findByFlexibleEmpIdAndWorkTypeCdIn(@Param("flexibleEmpId") Long flexibleEmpId,@Param("workTypeCd") List<String> workTypeCd);
	
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndYmd(Long tenantId, String enterCd, String ymd);
	
}
