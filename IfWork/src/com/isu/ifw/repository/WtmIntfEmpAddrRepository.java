package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfEmpAddr;

@Repository
public interface WtmIntfEmpAddrRepository extends JpaRepository<WtmIntfEmpAddr, Long> {
	
}
