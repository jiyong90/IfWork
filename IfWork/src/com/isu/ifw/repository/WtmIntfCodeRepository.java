package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfCode;

@Repository
public interface WtmIntfCodeRepository extends JpaRepository<WtmIntfCode, Long> {
	
}
