package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfEmp;

@Repository
public interface WtmIntfEmpRepository extends JpaRepository<WtmIntfEmp, Long> {
	
}
