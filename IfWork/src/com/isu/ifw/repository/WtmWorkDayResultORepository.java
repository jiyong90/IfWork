package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkDayResultO;

@Repository
public interface WtmWorkDayResultORepository extends JpaRepository<WtmWorkDayResultO, Long> {
	
	public List<WtmWorkDayResultO> findByTenantIdAndEnterCdAndSabunAndYmd(Long tenantId, String enterCd, String sabun, String ymd);
	public List<WtmWorkDayResultO> findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCdIn(Long tenantId, String enterCd, String sabun, String ymd, List<String> timeTypeCd);
	public List<WtmWorkDayResultO> findByTenantIdAndEnterCdAndSabunAndTimeTypeCdAndYmdBetween(Long tenantId, String enterCd, String sabun, String timeTypeCd, String symd, String eymd);
}
