package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmTaaApplDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WtmTaaApplDetRepository extends JpaRepository<WtmTaaApplDet, Long> {
	
	//@Query("SELECT D FROM WtmTaaAppl A JOIN WtmTaaApplDet D ON D.taaApplId = A.taaApplId WHERE A.tenantId = :tenantId AND A.enterCd = :enterCd AND A.sabun = :sabun AND  :ymd BETWEEN D.symd AND D.eymd AND A.ifApplNo in (SELECT MAX(W.ifApplNo)  FROM WtmTaaAppl W JOIN WtmTaaApplDet T ON W.taaApplId = T.taaApplId WHERE W.tenantId = A.tenantId AND W.enterCd = A.enterCd AND W.sabun = A.sabun AND T.taaCd = D.taaCd AND  T.symd = D.symd AND T.eymd = D.eymd AND T.shm = D.shm AND T.ehm = D.ehm  GROUP BY W.tenantId, W.enterCd, W.sabun, T.taaCd,  T.symd,  T.eymd, T.shm, T.ehm ) ")
	//@Query(value="SELECT D.TAA_APPL_DET_ID as \"taaApplDetId\" , D.TAA_APPL_ID as taaApplId , D.TAA_CD as taaCd, D.SYMD as symd, D.EYMD as eymd, D.SHM as shm, D.EHM as ehm, D.NOTE as note, D.UPDATE_DATE as updateDate, D.UPDATE_ID as updateId, D.TAA_MINUTE as taaMinute FROM WTM_TAA_APPL A  JOIN WTM_TAA_APPL_DET D ON D.TAA_APPL_ID = A.TAA_APPL_ID  WHERE A.TENANT_ID = :tenantId AND A.ENTER_CD = :enterCd AND A.SABUN = :sabun AND  :ymd BETWEEN D.SYMD AND D.EYMD AND A.IF_APPL_NO in (SELECT MAX(W.IF_APPL_NO)  FROM WTM_TAA_APPL W JOIN WTM_TAA_APPL_DET T ON W.TAA_APPL_ID = T.TAA_APPL_ID    WHERE W.TENANT_ID = A.TENANT_ID AND W.ENTER_CD = A.ENTER_CD AND W.SABUN = A.SABUN AND T.TAA_CD = D.TAA_CD AND  T.SYMD = D.SYMD AND T.EYMD = D.EYMD AND T.SHM = D.SHM AND T.EHM = D.EHM  GROUP BY W.TENANT_ID, W.ENTER_CD, W.SABUN, T.TAA_CD,  T.SYMD,  T.EYMD, T.SHM, T.EHM ) ", nativeQuery = true)
	/**
	 * 기존 데이터 문제로 인해 조건절 제거.. 2020831 JYP
	 */
	@Query("SELECT D FROM WtmTaaAppl A JOIN WtmTaaApplDet D ON D.taaApplId = A.taaApplId WHERE A.tenantId = :tenantId AND A.enterCd = :enterCd AND A.sabun = :sabun AND  :ymd BETWEEN D.symd AND D.eymd AND A.ifApplNo in (SELECT MAX(W.ifApplNo)  FROM WtmTaaAppl W JOIN WtmTaaApplDet T ON W.taaApplId = T.taaApplId WHERE W.tenantId = A.tenantId AND W.enterCd = A.enterCd AND W.sabun = A.sabun AND T.taaCd = D.taaCd AND  T.symd = D.symd GROUP BY W.tenantId, W.enterCd, W.sabun, T.taaCd,  T.symd ) ")
	public List<WtmTaaApplDet> findByMaxApplInfo(@Param(value="tenantId") Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="sabun") String sabun, @Param(value="ymd") String ymd);

	public List<WtmTaaApplDet> findByTaaApplId(Long taaApplId);

	@Query("SELECT D FROM WtmTaaAppl A JOIN WtmTaaApplDet D ON D.taaApplId = A.taaApplId WHERE A.applId = :applId ")
	public List<WtmTaaApplDet> findByApplId(@Param(value="applId") Long applId);
	 
}
 