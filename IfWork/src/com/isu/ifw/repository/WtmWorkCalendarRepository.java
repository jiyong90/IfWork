package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkCalendar;

@Repository
public interface WtmWorkCalendarRepository extends JpaRepository<WtmWorkCalendar, Long> {
	public WtmWorkCalendar findByTenantIdAndEnterCdAndSabunAndYmd(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query("SELECT C FROM WtmWorkCalendar C WHERE C.tenantId=?1 AND C.enterCd=?2 AND C.entryEtypeCd=?3 AND F_WTM_DATE_FORMAT(C.updateDate)=?4 AND C.updateId=?5")
	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndEntryEtypeCdAndUpdateDateAndUpdateId(Long tenantId, String enterCd, String entryEtypeCd, String updateDate, String updateId);

	public List<WtmWorkCalendar> findByTenantIdAndEnterCdAndSabunAndYmdBetween(Long tenantId, String enterCd, String sabun, String symd, String eymd);
}
