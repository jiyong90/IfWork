package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaAppl;

@Repository
public interface WtmTaaApplRepository extends JpaRepository<WtmTaaAppl, Long> {
	
	public WtmTaaAppl findByApplIdAndSabun(Long applId, String sabun);
	
}
