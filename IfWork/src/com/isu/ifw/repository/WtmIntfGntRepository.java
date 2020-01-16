package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIntfGnt;

@Repository
public interface WtmIntfGntRepository extends JpaRepository<WtmIntfGnt, Long> {
	
}
