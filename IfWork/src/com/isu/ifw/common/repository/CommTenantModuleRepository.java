package com.isu.ifw.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommTenantModule;

@Repository("WtmTenantModuleRepository")
public interface CommTenantModuleRepository extends JpaRepository<CommTenantModule, Long> {

	@Query("select t from WtmTenantModule t where t.tenantId = :tenantId ")
	public CommTenantModule findBytenantId(@Param(value="tenantId") Long tenantId);

	@Query("select t from WtmTenantModule t where t.apiKey = :apiKey ")
	public CommTenantModule findByApiKey(@Param(value="apiKey") String apiKey);

	@Query("select t from WtmTenantModule t where t.commModule.moduleId = :moduleId and t.tenantKey = :tenantKey ")
	public CommTenantModule findByModuleIdAndtenantKey(@Param(value="moduleId") Long moduleId,@Param(value="tenantKey") String tenantKey);

	@Query("select t from WtmTenantModule t where t.tenantKey = :tenantKey ")
	public CommTenantModule findByTenantKey(@Param(value="tenantKey") String tenantKey);
	
	@Query("select t from WtmTenantModule t where t.commModule.moduleId = :moduleId ")
	public List<CommTenantModule> findByModuleId(Long moduleId);

}
