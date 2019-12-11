package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmPushSendHis;

@Repository
public interface WtmPushSendHisRepository extends JpaRepository<WtmPushSendHis, Long> {
	public List<WtmPushSendHis> findByTenantIdAndEnterCd(Long tenantId, String enterCd);
}
