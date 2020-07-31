package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCompCanAppl;

@Repository
public interface WtmCompCanApplRepository extends JpaRepository<WtmCompCanAppl, Long> {
	public List<WtmCompCanAppl> findByApplId(Long applId);
	
	public WtmCompCanAppl findByApplIdAndCompApplId(Long applId, Long compApplId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmCompCanAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
