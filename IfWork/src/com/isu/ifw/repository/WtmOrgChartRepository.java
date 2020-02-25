package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOrgChart;

@Repository
public interface WtmOrgChartRepository extends JpaRepository<WtmOrgChart, Long> {
	
	@Query("SELECT C FROM WtmOrgChart C WHERE C.tenantId = ?1 AND C.enterCd = ?2 AND ?3 BETWEEN C.symd AND C.eymd ")
	public WtmOrgChart findByTenantIdAndEnterCdAndBetweenSymdAndEymd(Long tenantId, String enterCd, String ymd);
	
}
