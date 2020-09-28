package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmDayMgr;

@Repository
public interface WtmDayMgrRepository extends JpaRepository<WtmDayMgr, String> {

	public List<WtmDayMgr> findBySunYmdBetween(String symd, String eymd);
}
