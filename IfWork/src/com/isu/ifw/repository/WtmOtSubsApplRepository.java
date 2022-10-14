package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtSubsAppl;

@Repository
public interface WtmOtSubsApplRepository extends JpaRepository<WtmOtSubsAppl, Long> {
	public List<WtmOtSubsAppl> findByOtApplId(Long otApplId);
	
	public List<WtmOtSubsAppl> findByApplId(Long applId);
	
	@Query("SELECT s FROM WtmOtSubsAppl s JOIN WtmOtAppl o ON s.otApplId = o.otApplId WHERE o.applId = ?1 AND s.cancelYn = ?2")
	public List<WtmOtSubsAppl> findByApplIdAndCancelYn(Long applId, String cancelYn);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtSubsAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
	
	public List<WtmOtSubsAppl> findByOtApplIdAndCancelYnIsNullOrCancelYnNot(Long applId, String cancelYn);

	@Query("SELECT s FROM WtmOtSubsAppl s WHERE s.applId = ?1 AND (s.cancelYn = ?2 or s.cancelYn is null) ")
	public List<WtmOtSubsAppl> findByApplIdAndCancelYnIsNullOrCancelYnNot(Long applId, String cancelYn);
	
	@Query("SELECT s FROM WtmOtSubsAppl s WHERE s.otApplId = ?1 AND s.cancelYn is null ")
	public List<WtmOtSubsAppl> findByApplIdAndCancelYnIsNull(Long otApplId);

}
