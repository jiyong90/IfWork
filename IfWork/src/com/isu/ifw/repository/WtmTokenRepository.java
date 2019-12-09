package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmToken;

@Repository
public interface WtmTokenRepository extends JpaRepository<WtmToken, Long> {
	public WtmToken findByTenantIdAndEnterCdAndSabun(Long tenantId, String enterCd, String sabun);
	public List<WtmToken> findByAccessToken(String accessToken);
	@Transactional
	public void deleteByTenantIdAndEnterCdAndSabun(Long tenantId, String enterCd, String sabun);
}
