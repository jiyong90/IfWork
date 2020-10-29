package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorktimeDayClose;
import com.isu.ifw.entity.WtmWorktimeDayClosePK;
import com.isu.ifw.entity.WtmWorktimeMonClose;

@Repository
public interface WtmWorktimeDayCloseRepository extends JpaRepository<WtmWorktimeDayClose, WtmWorktimeDayClosePK> {
	/*
	@Query("SELECT M FROM WtmWorktimeDayClose M JOIN WtmWorktimeClose C ON M.id.worktimeCloseId = C.worktimeCloseId WHERE C.tenantId = :tenantId AND C.enterCd = :enterCd AND C.symd <= :eymd AND C.eymd >= :symd ")
	public List<WtmWorktimeDayClose> findByTenantIdAndEnterCdAndHolidayYmdBetween(@Param(value = "tenantId") Long tenantId
																		  , @Param(value = "enterCd") String enterCd
																		  , @Param(value = "symd") String symd
																		  , @Param(value = "eymd") String eymd);
																		  */
	
	@Query("SELECT D FROM WtmWorktimeDayClose D WHERE D.id.worktimeCloseId = :worktimeCloseId")
	public List<WtmWorktimeDayClose> findByWorktimeCloseId(@Param(value = "worktimeCloseId") Long worktimeCloseId);
	
	@Query("SELECT D FROM WtmWorktimeDayClose D WHERE D.id.worktimeCloseId = :worktimeCloseId AND D.id.sabun = :sabun")
	public List<WtmWorktimeDayClose> findByWorktimeCloseIdAndSabun(@Param(value = "worktimeCloseId") Long worktimeCloseId, @Param(value = "sabun") String sabun);
	
}
