package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmTimeBreakTime;

@Repository
public interface WtmTimeBreakTimeRepository extends JpaRepository<WtmTimeBreakTime, String> {
	@Query(value="SELECT * FROM WTM_TIME_BREAK_TIME WHERE TIME_CD_MGR_ID = :timeCdMgrId ORDER BY WORK_MINUTE", nativeQuery = true)
	public List<WtmTimeBreakTime> findByTimeCdMgrId(@Param(value="timeCdMgrId")String timeCdMgrId);
	
}
