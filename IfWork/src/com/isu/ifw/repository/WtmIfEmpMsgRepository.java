package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIfEmpMsg;

@Repository
public interface WtmIfEmpMsgRepository extends JpaRepository<WtmIfEmpMsg, Long> {
	
}
