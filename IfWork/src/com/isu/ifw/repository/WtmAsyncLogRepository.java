package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmAsyncLog;

/**
 */
@Repository
public interface WtmAsyncLogRepository extends JpaRepository<WtmAsyncLog, Long> {

	public WtmAsyncLog findByTenantIdAndEnterCdAndAsyncNm(Long tenantId, String enterCd, String asyncNm);
}
