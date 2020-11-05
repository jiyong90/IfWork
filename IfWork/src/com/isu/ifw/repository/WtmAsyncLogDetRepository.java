package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmAsyncLogDet;

/**
 */
@Repository
public interface WtmAsyncLogDetRepository extends JpaRepository<WtmAsyncLogDet, Long> {
	
	List<WtmAsyncLogDet> findByAsyncLogId(Long asyncLogId);
	
	//List<WtmAsyncLogDet> findByAsyncLogIdAndAMaxAsyncYmdhis(Long asyncLogId);
}
