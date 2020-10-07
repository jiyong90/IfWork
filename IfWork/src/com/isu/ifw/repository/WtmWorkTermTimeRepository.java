package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkTermTime;

@Repository
public interface WtmWorkTermTimeRepository extends JpaRepository<WtmWorkTermTime, Long> {
	

	//'20200928' <= WEEK_EDATE AND '20201004' >= WEEK_SDATE
	public List<WtmWorkTermTime> findByTenantIdAndEnterCdAndSabunAndWeekEdateGreaterThanEqualAndWeekSdateLessThanEqual(Long tenantId, String enterCd, String sabun, String symd, String eymd);
		
}
