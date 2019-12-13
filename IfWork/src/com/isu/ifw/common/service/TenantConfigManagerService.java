package com.isu.ifw.common.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.common.entity.CommManagementInfomation;
import com.isu.ifw.vo.ReturnParam;

/**
 * 테넌트 설정 정보를 불러오고 저장하는 서비스
 * @author tykim
 *
 */
public interface TenantConfigManagerService {

	/**
	 * 어떤 테넌트의 특정 키 값에 대한 설정 값을 가져온다.
	 * reloadConfig 값이 true 이면, 캐쉬 없이 변경된 내용을 즉시 가져온다.
	 * 만일 특정 키 값이 없으면, null 대신에 defaultValue 값을 반환한다.
	 * @param tenantId
	 * @param key
	 * @param reloadConfig 
	 * @param defaultValue
	 * @return
	 */
	public String getConfigValue(Long tenantId, String key, boolean reloadConfig, String defaultValue);
	
	/**
	 * 어떤 테넌트의 특정 키 값에 대한 설정 값을 세팅한다.
	 * @param tenantId
	 * @param key 검색 키
	 * @param value 값
	 * @param desc 설명(200바이트)
	 * @return 저장되고 난 후의 CommManagementInfomation 객체
	 */
	public CommManagementInfomation setConfigValue(Long tenantId, String key, String value, String desc);
	
	/**
	 * 어떤 테넌트의 특정 키 값을 삭제한다.
	 * @param tenantId
	 * @param key
	 */
	public void deleteConfigValue(Long tenantId, String key);
	
	public List getManagementList(Long tenantId, String userKey) throws Exception;

	public List getManagementList(Long tenantId, String userKey, String searchKey,
			String searchData) throws Exception;

	public ReturnParam deleteInfo(Map manageVo) throws Exception;

	public ReturnParam saveInfo(Long tenantId, Map manageVo) throws Exception;
	
	/**
	 * 공고정보 관리 수정
	 * @param manageVo
	 * @return
	 * @throws Exception
	 */
	public ReturnParam updateInfo(Map manageVo, Long tenantId, String userKey) throws Exception;
	
	/**
	 * 공고정보 관리 수정(백업 컬럼 추가)
	 * @param manageVo
	 * @return
	 * @throws Exception
	 */
	public ReturnParam updateInfo(Map manageVo, Long tenantId, String userKey, String columns) throws Exception;
	

	public Map<?, ?> getInformationsList(Long tenantId, String searchKey, String searchData, int startPg, int pgCount) throws Exception;
	
	/**
	 * 공고정보 리스트 소트(페이징)
	 * @param tenantId
	 * @param searchData 
	 * @param searchKey 
	 * @param startPg
	 * @param pgCount
	 * @param userKey
	 * @param userToken
	 * @param sortColumn
	 * @param sortDir
	 * @return
	 */
	public Map<?, ?> getInformationsList(Long tenantId, String searchKey, String searchData, int startPg, int pgCount, String userKey,
			String userToken, String sortColumn, String sortDir) throws Exception;

	ReturnParam updateUrlInfo(Map manageVo, Long tenantId, String userKey) throws Exception;

	
	/**
	 * 전체 tenant에서 infokey, clobdata로 검색
	 * @param tenantId
	 * @param searchData 
	 * @return
	 */
	List<Map> getManagementListByInfoKeyLikeClobData(String infoKey, String searchKey, String userKey) throws Exception;

	public int countByTenantId(Long tenantId);

}
