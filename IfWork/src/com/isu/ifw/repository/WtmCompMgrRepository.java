package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCompMgr;

@Repository
public interface WtmCompMgrRepository extends JpaRepository<WtmCompMgr, Long> {
	
	//@Query(value="SELECT * FROM WTM_COMP_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd ORDER BY SYMD DESC", nativeQuery = true)
	//public List<WtmCompMgr> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd);
	public List<WtmCompMgr> findByTenantIdAndEnterCdOrderBySymdDesc(Long tenantId,String enterCd);
}
