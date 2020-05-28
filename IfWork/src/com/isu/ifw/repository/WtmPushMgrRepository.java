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
	
	public List<WtmPushMgr> findByTenantIdAndEnterCdAndPushObjNot(Long tenantId, String enterCd, String pushObj);
	
	@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE :ymd <= eymd AND :ymd >= symd AND PUSH_OBJ IN('LEADER', 'EMP')", nativeQuery = true)
	public List<WtmPushMgr> findBySymdAndEymd(@Param(value="ymd")String ymd);
	
	@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE :ymd <= eymd AND :ymd >= symd AND TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND PUSH_OBJ IN('LEADER', 'EMP') AND MOBILE_YN='Y'", nativeQuery = true)
	public List<WtmPushMgr> findByTenantIdAndEnterCdAndSymdAndEymd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd);

	@Query("SELECT E FROM WtmPushMgr E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.businessPlaceCd = ?3 AND ?4 BETWEEN E.symd AND F_WTM_NVL(E.eymd, F_WTM_TO_DATE('99991231', 'YMD')) AND E.pushObj = ?5 AND E.stdType = ?6 ")
	public WtmPushMgr findByTenantIdAndEnterCdAndBusinessPlaceCdAndYmdBetweenAndPushObjAndStdType(Long tenantId, String enterCd, String businessPlaceCd, String d, String pushObj, String stdType);

}
