package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTimeChgHis;

@Repository
public interface WtmTimeChgHisRepository extends JpaRepository<WtmTimeChgHis, Long> {
	
}
