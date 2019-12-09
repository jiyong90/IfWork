package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmApplLine;

@Repository
public interface WtmApplLineRepository extends JpaRepository<WtmApplLine, Long> {
	public List<WtmApplLine> findByApplIdOrderByApprSeqAsc(Long applId);
	
	public WtmApplLine findByApplIdAndApprSeq(Long applId, int apprSeq);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmApplLine l WHERE l.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
