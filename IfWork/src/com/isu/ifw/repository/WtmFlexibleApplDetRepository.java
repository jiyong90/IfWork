package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleApplDet;

@Repository
public interface WtmFlexibleApplDetRepository extends JpaRepository<WtmFlexibleApplDet, Long> {
	
	public List<WtmFlexibleApplDet> findByFlexibleApplId(Long flexibleApplId);
	
	public WtmFlexibleApplDet findByFlexibleApplIdAndYmd(Long flexibleApplId, String ymd);
}
