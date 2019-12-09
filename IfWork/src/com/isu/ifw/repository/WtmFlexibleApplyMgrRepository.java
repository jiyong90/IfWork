package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleApplyMgr;

@Repository
public interface WtmFlexibleApplyMgrRepository extends JpaRepository<WtmFlexibleApplyMgr, Long> {
	
	//@Query(value="SELECT * FROM WTM_FLEXIBLE_APPLY WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND REPLACE(:sYmd, '-', '') BETWEEN USE_SYMD AND USE_EYMD", nativeQuery = true)
	//@Query(value="SELECT A FROM WtmFlexibleApplyMgr A WHERE A.tenantId = :tenantId AND A.enterCd = :enterCd AND :ymd BETWEEN A.useSymd AND A.useEymd")
	//public List<WtmFlexibleApplyMgr> findByTenantIdAndEnterCdAndBetweenUseSymdAndUseEymd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd);
}
