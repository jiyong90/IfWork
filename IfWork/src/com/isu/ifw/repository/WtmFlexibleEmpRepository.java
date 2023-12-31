package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleEmpCalc;

@Repository
public interface WtmFlexibleEmpRepository extends JpaRepository<WtmFlexibleEmp, Long> {
	
	/**
	 * 특정일에 속한 근무제 정보를 가져오기 위함.
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param d
	 * @return
	 */
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND ?4 BETWEEN E.symd AND E.eymd")
	public WtmFlexibleEmp findByTenantIdAndEnterCdAndSabunAndYmdBetween(Long tenantId, String enterCd, String sabun, String d);
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.symd = (SELECT MAX(EE.symd) FROM WtmFlexibleEmp EE WHERE E.tenantId = EE.tenantId AND E.enterCd = EE.enterCd AND E.sabun = EE.sabun AND EE.symd < :d ) AND E.tenantId = :tenantId AND E.enterCd = :enterCd  AND E.sabun = :sabun")
	public WtmFlexibleEmp findByPrevFlexibleEmp(@Param("tenantId") Long tenantId,@Param("enterCd") String enterCd,@Param("sabun") String sabun,@Param("d") String d);
	
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND (?4 BETWEEN E.symd AND E.eymd OR  ?5 BETWEEN E.symd AND E.eymd)")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(Long tenantId, String enterCd, String sabun, String symd, String eymd);

	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndSymdAndEymd(Long tenantId, String enterCd, String sabun, String symd, String eymd);

	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND (?4 BETWEEN E.symd AND E.eymd OR  ?5 BETWEEN E.symd AND E.eymd) AND E.workTypeCd = ?6 ")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCd(Long tenantId, String enterCd, String sabun, String symd, String eymd, String workTypeCd);
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND ?3 BETWEEN E.symd AND E.eymd")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndYmdBetween(Long tenantId, String enterCd, String d);
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND ?3 <= E.eymd AND  ?4 >= E.symd")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSymdAndEymd(Long tenantId, String enterCd, String symd, String eymd);

	@Query("SELECT E FROM WtmFlexibleEmp E JOIN WtmFlexibleStdMgr M ON E.flexibleStdMgrId = M.flexibleStdMgrId WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND ?3 <= E.eymd AND  ?4 >= E.symd AND M.defaultWorkUseYn = 'Y' AND M.fixotUseType = 'ALL' ")
	public List<WtmFlexibleEmp> findAllTypeFixotByTenantIdAndEnterCdAndSymdAndEymdAnd(Long tenantId, String enterCd, String symd, String eymd);
	
	@Query("SELECT E FROM WtmFlexibleEmp E JOIN WtmFlexibleStdMgr M ON E.flexibleStdMgrId = M.flexibleStdMgrId WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND ?4 <= E.eymd AND  ?5 >= E.symd AND M.defaultWorkUseYn = 'Y' AND M.fixotUseType = 'ALL' ")
	public List<WtmFlexibleEmp> findAllTypeFixotByTenantIdAndEnterCdAndSabunAndSymdAndEymdAnd(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	
	public List<WtmFlexibleEmp> findByFlexibleStdMgrId(Long flexibleStdMgrId);
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.flexibleEmpId IN ?1")
	public List<WtmFlexibleEmp> findByFlexibleEmpIds(List<Long> flexibleEmpIds);
	
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndSymdGreaterThan(Long tenantId, String enterCd, String sabun, String ymd);
	
	@Query(nativeQuery = true)
	public WtmFlexibleEmpCalc getTotalWorkMinuteAndRealWorkMinute(@Param("tenantId") Long tenantId, @Param("enterCd") String enterCd, @Param("sabun") String sabun, @Param("symd") String symd);
	
	@Query("SELECT E FROM WtmFlexibleEmp E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.sabun = ?3 AND (?4 BETWEEN E.symd AND E.eymd OR  ?5 BETWEEN E.symd AND E.eymd) AND E.workTypeCd IN ?6 ")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAndWorkTypeCds(Long tenantId, String enterCd, String sabun, String symd, String eymd, List<String> workTypeCd);

	@Query("SELECT e FROM WtmFlexibleEmp e JOIN WtmFlexibleStdMgr m ON e.flexibleStdMgrId = m.flexibleStdMgrId WHERE e.tenantId = :tenantId AND e.enterCd = :enterCd AND e.sabun = :sabun AND e.symd <= :eYmd AND e.eymd >= :sYmd ")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymdAll(@Param("tenantId") Long tenantId, @Param("enterCd") String enterCd, @Param("sabun") String sabun, @Param("sYmd") String sYmd, @Param("eYmd") String eYmd);
	
	//'20200928' <= EYMD AND '20201004' >= SYMD
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(Long tenantId, String enterCd, String sabun, String symd, String eymd);
	 
	@Query("SELECT e FROM WtmFlexibleEmp e JOIN WtmFlexibleStdMgr m ON e.flexibleStdMgrId = m.flexibleStdMgrId WHERE e.tenantId = :tenantId AND e.enterCd = :enterCd AND e.sabun = :sabun AND e.symd <= :eYmd AND e.eymd >= :sYmd AND m.baseWorkYn = 'Y' AND e.workTypeCd = 'BASE' ")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndEymdGreaterThanEqualAndSymdLessThanEqualAndBaseWorkYnIsYAndWorkTypeCdIsBASE(@Param("tenantId") Long tenantId, @Param("enterCd") String enterCd, @Param("sabun") String sabun, @Param("sYmd") String sYmd, @Param("eYmd") String eYmd);

	@Query("SELECT e FROM WtmFlexibleEmp e JOIN WtmFlexibleStdMgr m ON e.flexibleStdMgrId = m.flexibleStdMgrId WHERE e.tenantId = :tenantId AND e.enterCd = :enterCd AND e.sabun = :sabun AND e.symd <= :eYmd AND e.eymd >= :sYmd AND e.workTypeCd IN ('BASE', 'WORKTEAM') ")
	public List<WtmFlexibleEmp> findByTenantIdAndEnterCdAndEymdGreaterThanEqualAndSymdLessThanEqualAndBaseWorkYnIsYAndWorkTypeCdIsBASEWORKTEAM(@Param("tenantId") Long tenantId, @Param("enterCd") String enterCd, @Param("sabun") String sabun, @Param("sYmd") String sYmd, @Param("eYmd") String eYmd);
}
