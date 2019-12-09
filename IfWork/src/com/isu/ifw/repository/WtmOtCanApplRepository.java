package com.isu.ifw.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtCanAppl;

@Repository
public interface WtmOtCanApplRepository extends JpaRepository<WtmOtCanAppl, Long> {
	public WtmOtCanAppl findByApplId(Long applId);
	
	@Query("SELECT r FROM WtmOtCanAppl r WHERE r.otApplId = (SELECT o.otApplId FROM WtmOtAppl o WHERE o.applId = ?1)")
	public WtmOtCanAppl findByOtApplId(Long applId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtCanAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
