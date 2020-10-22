package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmAppl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WtmApplRepository extends JpaRepository<WtmAppl, Long> {

	public WtmAppl findByApplId(Long applId);

	public WtmAppl findByIfApplNo(Long ifApplNo);

	public WtmAppl findByTenantIdAndEnterCdAndIfApplNoAndApplCdIn(Long tenantId, String enterCd, String ifApplNo, List<String> applCds);
	
	public WtmAppl findByTenantIdAndEnterCdAndIfApplNo(Long tenantId, String enterCd, String ifApplNo);
}
