package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmFlexibleApplyDet;

@Repository
public interface WtmFlexibleApplyDetRepository extends JpaRepository<WtmFlexibleApplyDet, Long> {
	
	public List<WtmFlexibleApplyDet> findByFlexibleApplyId(Long flexibleApplyId);
	
	public WtmFlexibleApplyDet findByFlexibleApplyIdAndYmd(Long flexibleApplyId, String ymd);
	
	@Modifying
	@Transactional
	public void deleteByFlexibleApplyId(Long flexibleApplyId);
}
