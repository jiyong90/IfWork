package com.isu.ifw.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommTenantModule;

@Repository
public interface CommTenantModuleRepository extends JpaRepository<CommTenantModule, Long> {

	public CommTenantModule findBytenantId(Long tenantId);

	public CommTenantModule findByApiKey(String apiKey);

	@Query("select t from CommTenantModule t where t.commModule.moduleId = :moduleId and t.tenantKey = :tenantKey ")
	public CommTenantModule findByModuleIdAndtenantKey(@Param(value="moduleId") Long moduleId,@Param(value="tenantKey") String tenantKey);

	public CommTenantModule findByTenantKey(String tenantKey);
	
	@Query("select t from CommTenantModule t where t.commModule.moduleId = :moduleId ")
	public List<CommTenantModule> findByModuleId(Long moduleId);

}
