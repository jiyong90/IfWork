package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmTaaCanAppl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WtmTaaCanApplRepository extends JpaRepository<WtmTaaCanAppl, Long> {

	public WtmTaaCanAppl findByApplId(Long applId);
}
