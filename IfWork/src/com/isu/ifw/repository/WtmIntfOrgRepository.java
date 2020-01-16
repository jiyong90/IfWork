package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfOrg;

@Repository
public interface WtmIntfOrgRepository extends JpaRepository<WtmIntfOrg, Long> {
	
}
