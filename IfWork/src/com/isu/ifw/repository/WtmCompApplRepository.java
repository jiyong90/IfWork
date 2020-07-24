package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCompAppl;

@Repository
public interface WtmCompApplRepository extends JpaRepository<WtmCompAppl, Long> {
	
	@Query("SELECT C FROM WtmAppl A JOIN WtmCompAppl C ON A.applId = C.applId WHERE A.tenantId = :tenantId AND A.enterCd = :enterCd AND (A.applSabun = :sabun OR :sabun IS NULL OR :sabun = '' ) AND C.compSymd <= :compEymd AND C.compEymd >= :compSymd ")
	public List<WtmCompAppl> findByTenantIdAndEnterCdAndSabunOrSabunIsNullAndCompEymdGreaterThanEqualAndCompSymdLessThanEqual(@Param("tenantId") Long tenantId, @Param("enterCd") String enterCd, @Param("sabun") String sabun, @Param("compSymd") String compSymd, @Param("compEymd") String compEymd);
	
}
