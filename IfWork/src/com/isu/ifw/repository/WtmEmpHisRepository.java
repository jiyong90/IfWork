package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmEmpHis;

@Repository
public interface WtmEmpHisRepository extends JpaRepository<WtmEmpHis, Long> {
	
    //@Query(value="SELECT * FROM WTM_EMP_HIS WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND SABUN = :sabun AND date_format(now(),'%Y%m%d') BETWEEN SYMD AND EYMD ", nativeQuery = true)
	@Query(value="SELECT H FROM WtmEmpHis H WHERE H.tenantId = :tenantId AND H.enterCd = :enterCd AND H.sabun = :sabun AND :ymd BETWEEN H.symd AND H.eymd ")
	public WtmEmpHis findByTenantIdAndEnterCdAndSabunAndYmd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sabun")String sabun, @Param(value="ymd")String ymd);

	public WtmEmpHis findByEmpHisId(Long empHisId);
}