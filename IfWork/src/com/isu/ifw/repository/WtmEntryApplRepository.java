package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmEntryAppl;

@Repository
public interface WtmEntryApplRepository extends JpaRepository<WtmEntryAppl, Long> {
	public WtmEntryAppl findByApplId(Long applId);
}
