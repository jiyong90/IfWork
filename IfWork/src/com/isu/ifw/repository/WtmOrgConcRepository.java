package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOrgConc;

@Repository
public interface WtmOrgConcRepository extends JpaRepository<WtmOrgConc, Long> {
	
	@Query("SELECT C FROM WtmOrgConc C WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND C.sabun = ?3 AND ?4 BETWEEN C.symd AND C.eymd ")
	public List<WtmOrgConc> findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(Long tenantId, String enterCd, String sabun, String ymd);
	
}
