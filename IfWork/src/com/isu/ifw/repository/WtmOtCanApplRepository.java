package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmOtCanAppl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface WtmOtCanApplRepository extends JpaRepository<WtmOtCanAppl, Long> {
	public List<WtmOtCanAppl> findByApplId(Long applId);
	
	public WtmOtCanAppl findByApplIdAndOtApplId(Long applId, Long otApplId);

	public WtmOtCanAppl findByApplIdAndSabun(Long applId, String sabun);

	@Modifying
	@Transactional
	@Query("DELETE FROM WtmOtCanAppl a WHERE a.applId = ?1 ")
	public void deleteByApplId(Long applId);
}
