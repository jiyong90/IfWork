package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOrgCode;

@Repository
public interface WtmOrgCodeRepository extends JpaRepository<WtmOrgCode, Long> {
	
	@Query("SELECT E FROM WtmOrgCode E WHERE E.tenantId = ?1 AND E.enterCd = ?2 AND E.orgCd = ?3 AND (?4 BETWEEN E.symd AND E.eymd OR  ?5 BETWEEN E.symd AND E.eymd) ")
	public List<WtmOrgCode> findByTenantIdAndEnterCdAndOrgCdAndBetweenSymdAndEymd(Long tenantId, String enterCd, String orgCd, String symd, String eymd);

	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOrgCode E WHERE E.orgCodeId IN :orgCodeIds ")
	public void deleteByOrgCodeIdsIn(@Param("orgCodeIds")List<Long> orgCodeIds);
	
}
