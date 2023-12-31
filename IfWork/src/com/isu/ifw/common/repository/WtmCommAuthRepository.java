package com.isu.ifw.common.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommAuth;

@Repository
public interface WtmCommAuthRepository extends JpaRepository<CommAuth, Long> {
	
	public List<CommAuth> findByTenantId(Long tenantId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmAuth r WHERE r.authId IN :authIds ")
	public void deleteByAuthIdsIn(@Param("authIds") List<Long> authIds);
 
}
