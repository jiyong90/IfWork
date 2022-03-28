package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmEntryAppl;

@Repository
public interface WtmEntryApplRepository extends JpaRepository<WtmEntryAppl, Long> {
	public WtmEntryAppl findByApplId(Long applId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmEntryAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
	
}
