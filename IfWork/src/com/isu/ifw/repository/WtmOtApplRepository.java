package com.isu.ifw.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtAppl;

@Repository
public interface WtmOtApplRepository extends JpaRepository<WtmOtAppl, Long> {
	public WtmOtAppl findByApplId(Long applId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
