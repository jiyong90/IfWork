package com.isu.ifw.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommAuthRule;

@Repository
public interface WtmCommAuthRuleRepository extends JpaRepository<CommAuthRule, Long> {
	public CommAuthRule findByAuthId(Long authId);
}
