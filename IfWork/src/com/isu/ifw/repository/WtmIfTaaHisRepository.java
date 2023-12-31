package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmIfTaaHis;

@Repository
public interface WtmIfTaaHisRepository extends JpaRepository<WtmIfTaaHis, Long> {
	public List<WtmIfTaaHis> findByIfStatusNotIn(List<String> ifStatus);

	public List<WtmIfTaaHis> findByIfStatusNotInOrIfStatusNull(List<String> ifStatus);

	public List<WtmIfTaaHis> findByTenantIdAndIfStatusNotInAndIfStatusNull(Long tenantId, List<String> ifStatus);
	
	public List<WtmIfTaaHis> findByTenantIdAndIfStatusNotIn(Long tenantId, List<String> ifStatus);
	
	public List<WtmIfTaaHis> findByTenantIdAndIfStatusNotInAndStatusNotIn(Long tenantId, List<String> ifStatus,String status);

	public List<WtmIfTaaHis> findByTenantIdAndEnterCdAndApplNoAndIfStatusNotIn(Long tenantId, String enterCd, String applNo, String ifStatus);
	
	public List<WtmIfTaaHis> findByTenantIdAndEnterCdAndIfStatusNotIn(Long tenantId, String enterCd, String ifStatus);

	public List<WtmIfTaaHis> findByTenantIdAndApplNo(Long tenantId, String applNo);

	public List<WtmIfTaaHis> findByTenantIdAndIfStatusNotInOrIfStatusNull(Long tenantId, List<String> ifStatus);

	public List<WtmIfTaaHis> findByTenantIdAndEnterCdAndApplNoAndIfStatusNotInOrIfStatusIsNull(Long tenantId, String enterCd, String applNo, String ifStatus);

	public List<WtmIfTaaHis> findAllByTenantIdAndEnterCdAndApplNoAndSabunAndWorkTimeCodeAndStatusAndStartYmdAndEndYmd(Long tenantId, String enterCd, String applNo,String sabun, String workTimeCode, String status, String startYmd, String endYmd);
}
