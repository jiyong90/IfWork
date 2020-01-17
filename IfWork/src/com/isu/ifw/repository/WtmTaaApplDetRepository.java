package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTaaApplDet;

@Repository
public interface WtmTaaApplDetRepository extends JpaRepository<WtmTaaApplDet, Long> {
	
}
