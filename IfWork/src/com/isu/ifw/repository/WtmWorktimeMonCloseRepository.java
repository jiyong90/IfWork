package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorktimeMonClose;
import com.isu.ifw.entity.WtmWorktimeMonClosePK;

@Repository
public interface WtmWorktimeMonCloseRepository extends JpaRepository<WtmWorktimeMonClose, WtmWorktimeMonClosePK> {
	/*
	@Query("SELECT M FROM WtmWorktimeMonClose M JOIN WtmWorktimeClose C ON M.id.worktimeCloseId = C.worktimeCloseId WHERE C.tenantId = :tenantId AND C.enterCd = :enterCd AND C.symd <= :eymd AND C.eymd >= :symd ")
	public List<WtmWorktimeMonClose> findByTenantIdAndEnterCdAndSymdAndEYmd(@Param(value = "tenantId") Long tenantId
																		  , @Param(value = "enterCd") String enterCd
																		  , @Param(value = "symd") String symd
																		  , @Param(value = "eymd") String eymd);
	 */
																		  
}
