package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTimeCdMgr;

@Repository
public interface WtmTimeCdMgrRepository extends JpaRepository<WtmTimeCdMgr, Long> {
	//@Query(value="SELECT * FROM WTM_TIME_CD_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND (IF(:sYmd='',date_format(now(),'%Y%m%d'),REPLACE(:sYmd, '-', '')) BETWEEN SYMD AND F_WTM_NVL(EYMD, '99991231')) ORDER BY SYMD, TIME_CD", nativeQuery = true)
	//public List<WtmTimeCdMgr> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sYmd")String sYmd);
	
	//@Query(value="SELECT * FROM WTM_TIME_CD_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND HOL_YN = :holYn AND date_format(now(),'%Y%m%d') BETWEEN SYMD AND F_WTM_NVL(EYMD, '99991231') ORDER BY SYMD, TIME_CD", nativeQuery = true)
	@Query(value="SELECT M FROM WtmTimeCdMgr M WHERE M.tenantId = :tenantId AND M.enterCd = :enterCd AND M.holYn like F_WTM_NVL(:holYn, '%') AND :ymd BETWEEN M.symd AND CASE WHEN M.eymd IS NULL THEN '99991231' ELSE M.eymd END ORDER BY M.symd, M.timeCd")
	public List<WtmTimeCdMgr> findByTenantIdAndEnterCdAndHolYnAndYmd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="holYn")String holYn, @Param(value="ymd")String ymd);
	
}
