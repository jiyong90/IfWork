package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmAnnualUsed;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 연차사용내역 관리
 */
public interface WtmAnnualUsedRepository extends JpaRepository<WtmAnnualUsed, Long> {
	
	public WtmAnnualUsed findByTenantIdAndEnterCdAndTaaTypeCdAndSabunAndSymdAndEymd(Long tenantId, String enterCd, String taaTypeCd, String sabun, String symd, String eymd);
	
}
