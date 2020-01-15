package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmEmpAddr;

@Repository
public interface WtmEmpAddrRepository extends JpaRepository<WtmEmpAddr, Long> {
	
	public WtmEmpAddr findByTenantIdAndEnterCdAndEmail(Long tenantId, String enterCd, String email);

}
