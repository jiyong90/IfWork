package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmAnnualMgr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 연차기준 관리
 */
@Repository
public interface WtmAnnualMgrRepository extends JpaRepository<WtmAnnualMgr, Long> {

	public List<WtmAnnualMgr> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
}
