package com.isu.ifw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmUserAuth;

@Repository
public interface WtmUserAuthRepository extends JpaRepository<WtmUserAuth, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM WtmUserAuth r WHERE r.userAuthId IN :userAuthIds ")
	public void deleteByUserAuthIdsIn(@Param("userAuthIds") List<Long> userAuthIds);
}
