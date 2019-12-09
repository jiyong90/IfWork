package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmAppl;

@Repository
public interface WtmApplRepository extends JpaRepository<WtmAppl, Long> {

}
