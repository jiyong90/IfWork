package com.isu.ifw.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmFlexibleAppl;

@Repository
public interface WtmFlexibleApplRepository extends JpaRepository<WtmFlexibleAppl, Long> {
	public WtmFlexibleAppl findByApplId(Long applId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmFlexibleAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
