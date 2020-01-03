package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmMobileToken;

@Repository
public interface WtmMobileTokenRepository extends JpaRepository<WtmMobileToken, Long> {
	public WtmMobileToken findByTenantIdAndEmpKey(Long tenantId, String empKey);
}
