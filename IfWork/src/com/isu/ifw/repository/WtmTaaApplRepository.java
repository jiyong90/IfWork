package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaAppl;

@Repository
public interface WtmTaaApplRepository extends JpaRepository<WtmTaaAppl, Long> {
	
	public WtmTaaAppl findByApplIdAndSabun(Long applId, String sabun);
	
	public List<WtmTaaAppl> findByTenantIdAndEnterCdAndIfApplNo(Long tenantId, String enterCd, String ifApplNo);

	@Query("SELECT A FROM WtmTaaAppl A WHERE A.tenantId =:tenantId AND A.enterCd = :enterCd AND A.applId = (SELECT MAX(B.applId) FROM WtmTaaAppl B WHERE B.tenantId = :tenantId AND B.enterCd = :enterCd AND B.ifApplNo = :ifApplNo) ")
	public List<WtmTaaAppl> findByMaxTenantIdAndEnterCdAndIfApplNo(Long tenantId, String enterCd, String ifApplNo);

	public List<WtmTaaAppl> findByApplId(Long applId);
	
	
}
