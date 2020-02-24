package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmApplRecLine;

@Repository
public interface WtmApplRecLineRepository extends JpaRepository<WtmApplRecLine, Long> {
	
	public List<WtmApplRecLine> findByApplCodeIdAndSabun(Long applCodeId, String sabun);
	
	public WtmApplRecLine findByApplCodeIdAndSabunAndSeq(Long applCodeId, String sabun, int seq);
	
}
