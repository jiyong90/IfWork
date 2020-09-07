package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaAppl;

@Repository
public interface WtmTaaApplRepository extends JpaRepository<WtmTaaAppl, Long> {
	
	public WtmTaaAppl findByApplIdAndSabun(Long applId, String sabun);
	
	public List<WtmTaaAppl> findByTenantIdAndEnterCdAndIfApplNo(Long tenantId, String enterCd, String ifApplNo);
	
	
}
