package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmPushMgr;

@Repository
public interface WtmPushMgrRepository extends JpaRepository<WtmPushMgr, Long> {
	//@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd", nativeQuery = true)
	public List<WtmPushMgr> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
	
	@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE :ymd <= eymd AND :ymd >= symd", nativeQuery = true)
	public List<WtmPushMgr> findBySymdAndEymd(@Param(value="ymd")String ymd);
	
	@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE :ymd <= eymd AND :ymd >= symd AND TENANT_ID = :tenantId AND ENTER_CD = :enterCd ", nativeQuery = true)
	public List<WtmPushMgr> findByTenantIdAndEnterCdAndSymdAndEymd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd);

	@Query("SELECT E FROM WtmPushMgr E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND ?3 BETWEEN E.symd AND F_WTM_NVL(E.eymd, F_WTM_TO_DATE('99991231', 'YMD')) AND E.pushObj = ?4 AND E.stdType = ?5 ")
	public WtmPushMgr findByTenantIdAndEnterCdAndYmdBetweenAndPushObjAndStdType(Long tenantId, String enterCd, String d, String pushObj, String stdType);

}
