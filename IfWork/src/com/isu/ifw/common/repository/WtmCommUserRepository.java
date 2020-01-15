package com.isu.ifw.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommUser;

@Repository("WtmCommUserRepository")
public interface WtmCommUserRepository extends JpaRepository<CommUser, Long> {
	
	@Query("SELECT u FROM WtmUser u WHERE u.tenantId = :tenantId AND u.enterCd = F_WTM_AES_ENCRYPT(:enterCd, :encKey) AND u.loginId = F_WTM_AES_ENCRYPT(:loginId, :encKey) ")
	public CommUser findByTenantIdAndEnterCdAndLoginIdAndEncKey(@Param("tenantId")Long tenantId, @Param("enterCd")String enterCd, @Param("loginId")String loginId, @Param("encKey")String encKey);
 
}
