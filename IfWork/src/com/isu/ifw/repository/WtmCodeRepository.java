package com.isu.ifw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmCode;

@Repository
public interface WtmCodeRepository extends JpaRepository<WtmCode, Long> {
	
	//@Query(value="SELECT * FROM WTM_CODE WHERE TENANT_ID = :tenantId AND ENTER_CD = :enterCd AND date_format(now(),'%Y%m%d') BETWEEN SYMD AND EYMD AND GRP_CODE_CD = :grpCodeCd ORDER BY SEQ", nativeQuery = true)
	@Query(value="SELECT C FROM WtmCode C WHERE C.tenantId = :tenantId AND C.enterCd = :enterCd AND :ymd BETWEEN C.symd AND C.eymd AND C.grpCodeCd = :grpCodeCd ORDER BY seq")
	public List<WtmCode> findByTenantIdAndEnterCdAndYmdAndGrpCodeCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="ymd")String ymd, @Param(value="grpCodeCd")String grpCodeCd);
}
