package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaApplDet;

@Repository
public interface WtmTaaApplDetRepository extends JpaRepository<WtmTaaApplDet, Long> {
	
	@Query(value="SELECT D.TAA_APPL_DET_ID, D.TAA_APPL_ID , D.TAA_CD, D.SYMD, D.EYMD, D.SHM, D.EHM, D.NOTE, D.UPDATE_DATE, D.UPDATE_ID, D.TAA_MINUTE FROM WTM_TAA_APPL A  JOIN WYM_TAA_APPL_DET D ON D.TAA_APPL_ID = A.TAA_APPL_ID  WHERE A.TENANT_ID = :tenantId AND A.ENTER_CD = :enterCd AND A.SABUN = :sabun AND  :ymd BETWEEN D.SYMD AND D.EYMD AND A.IF_APPL_NO in (SELECT MAX(W.IF_APPL_NO)  FROM WtmTaaAppl W WHERE W.TENANT_ID = W.TENANT_ID AND W.ENTER_CD = A.ENTER_CD AND W.SABUN = A.SABUN AND W.SYMD = A.SYMD AND W.EYMD = A.EYMD GROUP BY W.TENANT_ID, W.ENTER_CD, W.SABUN, W.TAA_CD,  W.SYMD,  W.EYMD, W.SHM, W.EHM ) ", nativeQuery = true)
	public List<WtmTaaApplDet> findByMaxApplInfo(@Param(value="tenantId") Long tenantId, @Param(value="enterCd") String enterCd, @Param(value="sabun") String sabun, @Param(value="ymd") String ymd);
}
 