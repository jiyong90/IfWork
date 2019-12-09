package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaCode;

@Repository
public interface WtmTaaCodeRepository extends JpaRepository<WtmTaaCode, Long> {
	
	@Query(value="SELECT * FROM WTM_TAA_CODE WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND (TAA_CD like %:searchKeyword% OR TAA_NM like %:searchKeyword%)", nativeQuery = true)
	public List<WtmTaaCode> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="searchKeyword")String searchKeyword);
	
	public WtmTaaCode findByTenantIdAndEnterCdAndTaaCd(Long tenantId, String enterCd, String taaCd);
	
	public WtmTaaCode findByTenantIdAndEnterCdAndTaaInfoCd(Long tenantId, String enterCd, String taaInfoCd);
}
