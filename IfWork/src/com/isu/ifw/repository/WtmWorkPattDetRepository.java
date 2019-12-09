package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkPattDet;

@Repository
public interface WtmWorkPattDetRepository extends JpaRepository<WtmWorkPattDet, Long> {
	
	public WtmWorkPattDet findTopByFlexibleStdMgrIdOrderBySeqDesc(Long flexibleStdMgrId);
	
	@Query(value="SELECT * FROM WTM_WORK_PATT_DET WHERE FLEXIBLE_STD_MGR_ID = :flexibleStdMgrId", nativeQuery = true)
	public List<WtmWorkPattDet> findByFlexibleStdMgrId(@Param(value="flexibleStdMgrId")Long flexibleStdMgrId);
}
