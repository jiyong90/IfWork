package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCodeGrp;

@Repository
public interface WtmCodeGrpRepository extends JpaRepository<WtmCodeGrp, Long> {
	
	//@Query(value="SELECT * FROM WTM_CODE_GRP WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd", nativeQuery = true)
	//public List<WtmCodeGrp> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd);
	public List<WtmCodeGrp> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
}
