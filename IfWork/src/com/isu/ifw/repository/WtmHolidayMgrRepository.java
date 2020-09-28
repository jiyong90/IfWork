package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmHolidayMgr;
import com.isu.ifw.entity.WtmHolidayMgrPK;

@Repository
public interface WtmHolidayMgrRepository extends JpaRepository<WtmHolidayMgr, WtmHolidayMgrPK> {
	
	@Query("SELECT H FROM WtmHolidayMgr H WHERE H.id.tenantId = :tenantId AND H.id.enterCd = :enterCd AND H.id.holidayYmd BETWEEN :symd AND :eymd")
	public List<WtmHolidayMgr> findByTenantIdAndEnterCdAndHolidayYmdBetween(@Param(value = "tenantId") Long tenantId
																		  , @Param(value = "enterCd") String enterCd
																		  , @Param(value = "symd") String symd
																		  , @Param(value = "eymd") String eymd);
}
