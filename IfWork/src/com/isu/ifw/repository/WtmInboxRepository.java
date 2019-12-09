package com.isu.ifw.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmInbox;

@Repository
public interface WtmInboxRepository extends JpaRepository<WtmInbox, Long> {
	public int countByTenantIdAndEnterCdAndSabunAndCheckYn(Long tenantId, String enterCd, String sabun, String checkYn);
	public List<WtmInbox> findByTenantIdAndEnterCdAndSabunAndCheckYn(Long tenantId, String enterCd, String sabun, String checkYn);
	public WtmInbox findById(long id);
}
