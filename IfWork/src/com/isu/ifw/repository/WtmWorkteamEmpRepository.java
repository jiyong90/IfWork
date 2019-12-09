package com.isu.ifw.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmWorkteamEmp;

@Repository
public interface WtmWorkteamEmpRepository extends JpaRepository<WtmWorkteamEmp, Long> {

	/*
	@Query(value="SELECT E.WORKTEAM_EMP_ID as workteamEmpID, E.WORKTEAM_MGR_ID as workteamMgrId, E.SABUN as sabun, E.SYMD as symd, E.EYMD as eymd, E.NOTE as note, C.ORG_CD as orgCd, C.CLASS_CD as classCd, C.EMP_NM as empNm "
			+ "FROM WTM_WORKTEAM_EMP E, WTM_EMP_HIS C "
			+ "WHERE C.TENANT_ID=:tenantId "
			+ "		AND E.SABUN = C.SABUN "
			+ "		AND IF(:sYmd='',date_format(now(),'%Y%m%d'),REPLACE(:sYmd, '-', '')) BETWEEN C.SYMD AND C.EYMD "
			+ "		AND C.ENTER_CD=:enterCd "
			+ "		AND IF(:sYmd='',date_format(now(),'%Y%m%d'),REPLACE(:sYmd, '-', '')) BETWEEN E.SYMD AND E.EYMD "
			+ "		AND (E.SABUN like IF(:sData='', '%', '%'|| :sData ||'%')  OR C.EMP_NM like IF(:sData='', '%', '%'|| :sData ||'%'))", nativeQuery = true)
	public List<Map<String, Object>> findByTenantIdAndEnterCd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sYmd")String sYmd, @Param(value="sData")String sData);
	*/
	public List<WtmWorkteamEmp> findByWorkteamMgrId(Long workteamMgrId);

	/*@Query(value="SELECT A.SABUN, A.SYMD, A.EYMD, B.TENANT_ID, B.ENTER_CD, B.WORKTEAM_MGR_ID, A.WORKTEAM_EMP_ID FROM WTM_WORKTEAM_EMP A "
			+ "JOIN WTM_WORKTEAM_MGR B "
			+ "		ON A.WORKTEAM_MGR_ID = B.WORKTEAM_MGR_ID AND B.TENANT_ID = :tenantId AND B.ENTER_CD = :enterCd "
			+ "WHERE A.SABUN=:sabun AND (:sYmd BETWEEN A.SYMD AND A.EYMD OR :eYmd BETWEEN A.SYMD AND A.EYMD) AND :workteamEmpId != A.WORKTEAM_EMP_ID ", nativeQuery = true)
	public List<Map<String, Object>> dupCheckByYmd(@Param(value="tenantId")Long tenantId, @Param(value="enterCd")String enterCd, @Param(value="sabun")String sabun, @Param(value="workteamEmpId")String workteamEmpId, @Param(value="sYmd")String sYmd, @Param(value="eYmd")String eYmd);
*/
}
