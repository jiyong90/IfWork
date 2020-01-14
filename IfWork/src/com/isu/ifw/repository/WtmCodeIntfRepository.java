package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCodeIntf;

@Repository
public interface WtmCodeIntfRepository extends JpaRepository<WtmCodeIntf, Long> {
	
}
