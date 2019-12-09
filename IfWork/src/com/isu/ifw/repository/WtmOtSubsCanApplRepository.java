package com.isu.ifw.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtSubsCanAppl;

@Repository
public interface WtmOtSubsCanApplRepository extends JpaRepository<WtmOtSubsCanAppl, Long> {
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtSubsCanAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
	
}
