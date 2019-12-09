package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtSubsAppl;

@Repository
public interface WtmOtSubsApplRepository extends JpaRepository<WtmOtSubsAppl, Long> {
	public List<WtmOtSubsAppl> findByOtApplId(Long otApplId);
	
	public List<WtmOtSubsAppl> findByApplId(Long applId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtSubsAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
	
}
