package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmPropertie;

@Repository
public interface WtmPropertieRepository extends JpaRepository<WtmPropertie, Long> {
	
	public WtmPropertie findByTenantIdAndEnterCdAndInfoKey(Long tenantId, String enterCd, String infoKey);
	
	public List<WtmPropertie> findByTenantIdAndEnterCdAndInfoKeyLike(Long tenantId, String enterCd, String infoKey);
	
}
