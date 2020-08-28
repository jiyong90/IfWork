package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIfTaaHis;

@Repository
public interface WtmIfTaaHisRepository extends JpaRepository<WtmIfTaaHis, Long> {
	public List<WtmIfTaaHis> findByIfStatusNotIn(String ifStatus);
	
	public List<WtmIfTaaHis> findByTenantIdAndEnterCdAndApplNoAndIfStatusNotIn(Long tenantId, String enterCd, String applNo, String ifStatus);
	
	public List<WtmIfTaaHis> findByTenantIdAndEnterCdAndIfStatusNotIn(Long tenantId, String enterCd, String ifStatus);
}
