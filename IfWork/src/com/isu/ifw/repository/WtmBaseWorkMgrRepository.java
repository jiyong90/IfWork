package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmBaseWorkMgr;

@Repository
public interface WtmBaseWorkMgrRepository extends JpaRepository<WtmBaseWorkMgr, Long> {
	
	//@Query(value="SELECT * FROM WTM_BASE_WORK_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND IF(:sYmd='',date_format(now(),'%Y%m%d'),REPLACE(:sYmd, '-', '')) BETWEEN SYMD AND EYMD ", nativeQuery = true)
	@Query("SELECT M FROM WtmBaseWorkMgr M WHERE M.tenantId = ?1 AND M.enterCd = ?2 AND (?3 BETWEEN M.symd AND M.eymd)")
	public List<WtmBaseWorkMgr> findByTenantIdAndEnterCdAndSymd(Long tenantId, String enterCd, String sYmd);
}
