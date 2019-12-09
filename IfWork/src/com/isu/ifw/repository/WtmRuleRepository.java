package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmRule;

@Repository
public interface WtmRuleRepository extends JpaRepository<WtmRule, Long> {
	public WtmRule findByRuleId(Long ruleId);
	
	@Query("SELECT r FROM WtmRule r WHERE r.ruleId IN :ruleIds")
	public List<WtmRule> findByRuleIdsIn(@Param("ruleIds") List<Long> ruleIds);
	
	public List<WtmRule> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmRule r WHERE r.ruleId IN :ruleIds ")
	public void deleteByRuleIdsIn(@Param("ruleIds") List<Long> ruleIds);
}
