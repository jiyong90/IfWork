package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmFlexibleApplyEmpTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WtmFlexibleApplyEmpTempRepository extends JpaRepository<WtmFlexibleApplyEmpTemp, Long>{

	@Transactional
	@Modifying
	@Query(value="UPDATE WtmFlexibleApplyEmpTemp t SET t.applyYn = :#{#empTemp.applyYn} WHERE t.tenantId = :#{#empTemp.tenantId} AND t.enterCd = :#{#empTemp.enterCd} AND t.flexibleApplyId = :#{#empTemp.flexibleApplyId}")
	public Integer updateApplyYnByFlexibleApplId(@Param("empTemp") WtmFlexibleApplyEmpTemp empTemp);


}
