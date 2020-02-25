package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfTaaAppl;

@Repository
public interface WtmIntfTaaApplRepository extends JpaRepository<WtmIntfTaaAppl, Long> {
	
	List<WtmIntfTaaAppl> findByYyyymmddhhmissGreaterThanAndTenantId(String yyyymmddhhmiss, Long tenantId);
	
}
