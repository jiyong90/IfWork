package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCompCreate;

@Repository
public interface WtmCompCreatelRepository extends JpaRepository<WtmCompCreate, Long> {
	
	public List<WtmCompCreate> findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(Long tenantId, String enterCd, String sabun, String eymd, String symd);
	
}
