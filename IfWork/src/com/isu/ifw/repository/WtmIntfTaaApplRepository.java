package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmIntfTaaAppl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WtmIntfTaaApplRepository extends JpaRepository<WtmIntfTaaAppl, Long> {
	
	List<WtmIntfTaaAppl> findByYyyymmddhhmissGreaterThanAndTenantIdOrderByIntfId(String yyyymmddhhmiss, Long tenantId);

	List<WtmIntfTaaAppl> findByYyyymmddhhmissGreaterThanAndTenantIdAndSabunOrderByIntfId(String yyyymmddhhmiss, Long tenantId, String sabun);

}
