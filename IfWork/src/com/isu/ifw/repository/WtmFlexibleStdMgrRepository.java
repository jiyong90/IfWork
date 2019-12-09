package com.isu.ifw.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleStdMgr;

@Repository
public interface WtmFlexibleStdMgrRepository extends JpaRepository<WtmFlexibleStdMgr, Long> {

	public WtmFlexibleStdMgr findByTenantIdAndEnterCdAndWorkTypeCd(Long tenantId, String enterCd, String workTypeCd);
	
	//@Query(value="SELECT * FROM WTM_FLEXIBLE_STD_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND REPLACE(:ymd, '-', '') BETWEEN USE_SYMD AND F_WTM_NVL(USE_EYMD, '99991231') ORDER BY WORK_TYPE_CD, USE_SYMD DESC", nativeQuery = true)
	@Query(value="SELECT M FROM WtmFlexibleStdMgr M WHERE M.tenantId = :tenantId AND M.enterCd = :enterCd AND :ymd BETWEEN M.useSymd AND CASE WHEN M.useEymd IS NULL THEN '99991231' ELSE M.useEymd END ORDER BY M.workTypeCd, M.useSymd DESC")
	public List<Map<String, Object>> findByTenantIdAndEnterCdAndBetweenUseSymdAndUseEymd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd);

	@Query("SELECT M FROM WtmFlexibleStdMgr M JOIN WtmFlexibleEmp E ON M.flexibleStdMgrId = E.flexibleStdMgrId WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND ?4 BETWEEN E.symd AND E.eymd")
	public WtmFlexibleStdMgr findByTenantIdAndEnterCdAndSabunAndYmdBetween(Long tenantId, String enterCd, String sabun, String d);
}