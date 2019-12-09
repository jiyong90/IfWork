package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTimeBreakMgr;

@Repository
public interface WtmTimeBreakMgrRepository extends JpaRepository<WtmTimeBreakMgr, String> {
	@Query(value="SELECT * FROM WTM_TIME_BREAK_MGR WHERE TIME_CD_MGR_ID = :timeCdMgrId ORDER BY SEQ", nativeQuery = true)
	public List<WtmTimeBreakMgr> findByTimeCdMgrId(@Param(value="timeCdMgrId")String timeCdMgrId);
	
}
