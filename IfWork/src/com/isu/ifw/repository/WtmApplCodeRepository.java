package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmApplCode;

@Repository
public interface WtmApplCodeRepository extends JpaRepository<WtmApplCode, Long> {
	public WtmApplCode findByTenantIdAndEnterCdAndApplCd(Long tenantId, String enterCd, String applCd);
	
	//@Query(value="SELECT * FROM WTM_APPL_CODE WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd", nativeQuery = true)
	//public List<WtmApplCode> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd);
	public List<WtmApplCode> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
	
 
}
