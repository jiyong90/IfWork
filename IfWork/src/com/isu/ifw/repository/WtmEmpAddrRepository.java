package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmEmpAddr;

@Repository
public interface WtmEmpAddrRepository extends JpaRepository<WtmEmpAddr, Long> {
	
	public WtmEmpAddr findByTenantIdAndEnterCdAndEmail(Long tenantId, String enterCd, String email);
	
	public WtmEmpAddr findByTenantIdAndEnterCdAndSabun(Long tenantId, String enterCd, String sabun);
	
	@Query("SELECT A FROM WtmEmpAddr A WHERE A.tenantId = ?1 AND A.enterCd = ?2 AND A.sabun IN ?3 ")
	public List<WtmEmpAddr> findByTenantIdAndEnterCdAndSabuns(Long tenantId, String enterCd, List<String> sabuns);

}
