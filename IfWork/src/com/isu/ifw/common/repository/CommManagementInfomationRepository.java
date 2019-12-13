package com.isu.ifw.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.CommManagementInfomation;

@Repository("WtmManagementInfomationRepository")
public interface CommManagementInfomationRepository extends JpaRepository<CommManagementInfomation, Long>{
	
	/**
	 * 테넌트 id와 infoKey로 테넌트 서비스 설정 값을 반화한다.
	 * @param tenantId
	 * @param infoKey
	 * @return
	 */
	public CommManagementInfomation findByTenantIdAndInfoKey(Long tenantId, String infoKey); 
	
	/**
	 * 해당 테넌트 id에 해당하는 모든 설정 정보를 가져온다.
	 * @param tenantId
	 * @return
	 */
	public List<CommManagementInfomation> findByTenantId(Long tenantId);
	

	/**
	 * 
	 * @param tenantId
	 * @param infoKey
	 * @return
	 */
	public List<CommManagementInfomation> findByTenantIdAndInfoKeyLike(Long tenantId, String infoKey);
	
	@Query("SELECT t "
		 + "  FROM WtmManagementInfomation t "
		 + " WHERE t.tenantId = :tenantId "
		 + "   AND ((:infoKey='' AND :infoName='' AND :infoData='' AND :clobData='')  "
		 + "    OR ( :infoKey<>'' AND t.infoKey LIKE CONCAT('%',:infoKey,'%') "
		 + "    OR :infoName<>'' AND t.infoName LIKE CONCAT('%',:infoName,'%') "
		 + "    OR :infoData<>'' AND t.infoData LIKE CONCAT('%',:infoData,'%') "
		 + "    OR :clobData<>'' AND t.clobData LIKE CONCAT('%',:clobData,'%')))")
	public List<CommManagementInfomation> findByTenantIdAndInfoKeyAndInfoData(@Param("tenantId") Long tenantId, @Param("infoKey") String infoKey, @Param("infoName") String infoName, @Param("infoData") String infoData, @Param("clobData") String clobData);
	

	/**
	 * 테넌트 id로 서비스 설정 값 갯수를 반환한다.
	 * @param tenantId
	 * @param infoKey
	 * @return
	 */
	public int countByTenantId(Long tenantId); 
}
