package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmMobileToken;

@Repository
public interface WtmMobileTokenRepository extends JpaRepository<WtmMobileToken, Long> {
	
	@Query(value="SELECT H FROM WtmMobileToken H WHERE H.tenantId = ?1 AND H.empKey = ?2 ")
	public WtmMobileToken findByTenantIdAndEmpKey(Long tenantId, String empKey);
}
