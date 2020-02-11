package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleApplDet;

@Repository
public interface WtmFlexibleApplDetRepository extends JpaRepository<WtmFlexibleApplDet, Long> {
	
	public List<WtmFlexibleApplDet> findByFlexibleApplId(Long flexibleApplId);
	
	public WtmFlexibleApplDet findByFlexibleApplIdAndYmd(Long flexibleApplId, String ymd);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmFlexibleApplDet a WHERE a.flexibleApplId = ?1 AND a.ymd NOT BETWEEN ?2 AND ?3 ")
	public void deleteByFlexibleApplIdAndYmdNotBetween(Long flexibleApplId, String sYmd, String eYmd);
}
