package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfOrgConc;

@Repository
public interface WtmIntfOrgConcRepository extends JpaRepository<WtmIntfOrgConc, Long> {
	
}
