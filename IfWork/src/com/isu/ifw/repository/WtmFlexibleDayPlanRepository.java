package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleDayPlan;

@Repository
public interface WtmFlexibleDayPlanRepository extends JpaRepository<WtmFlexibleDayPlan, Long> {
	public List<WtmFlexibleDayPlan> findByFlexibleApplId(Long flexibleApplId);
}
