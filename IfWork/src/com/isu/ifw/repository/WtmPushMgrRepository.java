package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmPushMgr;

@Repository
public interface WtmPushMgrRepository extends JpaRepository<WtmPushMgr, Long> {
	//@Query(value="SELECT * FROM WTM_PUSH_MGR WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd", nativeQuery = true)
	public List<WtmPushMgr> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
}
