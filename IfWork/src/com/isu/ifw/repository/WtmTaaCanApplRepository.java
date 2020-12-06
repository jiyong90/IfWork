package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaCanAppl;

@Repository
public interface WtmTaaCanApplRepository extends JpaRepository<WtmTaaCanAppl, Long> {

	public WtmTaaCanAppl findByApplId(Long applId);
	
	public List<WtmTaaCanAppl> findByCanApplId(Long taaCanApplId);
}
