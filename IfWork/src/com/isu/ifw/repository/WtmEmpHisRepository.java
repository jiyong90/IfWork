package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmEmpHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface WtmEmpHisRepository extends JpaRepository<WtmEmpHis, Long> {
	
    //@Query(value="SELECT * FROM WTM_EMP_HIS WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND SABUN = :sabun AND date_format(now(),'%Y%m%d') BETWEEN SYMD AND EYMD ", nativeQuery = true)
	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = :tenantId AND H.enterCd = :enterCd AND H.sabun = :sabun AND :ymd BETWEEN H.symd AND H.eymd ")
	public WtmEmpHis findByTenantIdAndEnterCdAndSabunAndYmd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sabun")String sabun, @Param(value="ymd")String ymd);

	public WtmEmpHis findByEmpHisId(Long empHisId);
	
	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = ?1 AND H.enterCd = ?2 AND ?3 BETWEEN H.symd AND H.eymd AND H.sabun IN ?4 ")
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndYmdAndSabuns(Long tenantId, String enterCd, String ymd, List<String> sabuns);

	@Query("SELECT E FROM WtmEmpHis E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND (?4 BETWEEN E.symd AND E.eymd OR  ?5 BETWEEN E.symd AND E.eymd) ")
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(Long tenantId, String enterCd, String sabun, String symd, String eymd);

	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = :tenantId AND H.enterCd = :enterCd AND :ymd BETWEEN H.symd AND H.eymd ")
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndYmd(Long tenantId, String enterCd, String ymd);

	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = ?1 AND H.enterCd = ?2 AND H.sabun = ?3 AND H.statusCd IN ?4 ")
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndSabunAndStatusCdIn(Long tenantId, String enterCd, String sabun, List<String> statusCds);

	@Query(value="SELECT * FROM WTM_EMP_HIS WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND SABUN NOT IN (SELECT SABUN FROM WTM_FLEXIBLE_EMP WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND :ymd BETWEEN SYMD AND EYMD) ", nativeQuery = true)
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndYmdNotExistWtmFlexibleEmp(@Param(value="tenantId")Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="ymd") String ymd);

	@Modifying
	@Transactional
	@Query("DELETE FROM WtmEmpHis E WHERE E.empHisId IN :empHisIds ")
	public void deleteByEmpHisIdsIn(@Param("empHisIds")List<Long> empHisIds);

	@Query(nativeQuery = true, value = "SELECT f_wtm_get_emp_org_nm( :tenantId, :enterCd , :sabun, :ymd) FROM Dual")
	public String getFWtmGetEmpOrgNm(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sabun")String sabun, @Param(value="ymd")String ymd);
	
	@Query("SELECT MAX(e.empYmd) FROM WtmEmpHis e WHERE e.tenantId = :tenantId AND e.enterCd = :enterCd AND e.sabun = :sabun ")
	public String findMaxEmpYmdByTenantIdAndEnterCdAndSabun(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sabun")String sabun);
	
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndEmpIdOrderBySymdDesc(Long tenantId, String enterCd, String empId);
	
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndNoteOrderBySymdDesc(Long tenantId, String enterCd, String note);
	
	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = :tenantId AND H.enterCd = :enterCd AND :ymd BETWEEN H.symd AND H.eymd  And H.statusCd IN (:statusCds) AND H.empYmd is not null ")
	public List<WtmEmpHis> findByTenantIdAndEnterCdAndYmdAndStatusCdInAndEmpYmdIsNotNull(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd, @Param(value="statusCds") List<String> statusCds);
}