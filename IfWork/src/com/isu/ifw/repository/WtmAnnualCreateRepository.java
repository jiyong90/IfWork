package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmAnnualCreate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 연차내역관리
 */
public interface WtmAnnualCreateRepository extends JpaRepository<WtmAnnualCreate, Long> {

	List<WtmAnnualCreate> findByTenantIdAndEnterCd(Long tenantId, String enterCd);

	@Query(value = "SELECT M FROM WtmAnnualCreate M WHERE M.tenantId = ?1 AND M.enterCd = ?2 AND (?3 BETWEEN M.symd AND M.eymd)")
	List<WtmAnnualCreate> findByTenantIdAndEnterCdAndSymd(Long tenantId, String enterCd, String sYmd);
}
