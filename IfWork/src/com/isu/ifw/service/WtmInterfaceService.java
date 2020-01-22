package com.isu.ifw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 인사자료 이관용 인터페이스
 * @author lhj
 *
 */
public interface WtmInterfaceService {
	/**
	 * 인터페이스 최종시간 조회
	 * @param tenantId - 테넌트 아이디
	 * @param ifType - 인터페이스 타입
	 * @return 
	 * @throws Exception 
	 */
	public Map<String, Object> getIfLastDate(Long tenantId, String ifType) throws Exception;
		
	/**
	 * 인터페이스 서버 호출 
	 * @param url - 호출경로
	 * @return 
	 * @throws Exception 
	 */
	public HashMap getIfRt(String url) throws Exception;
	
	/**
	 * 인터페이스 서버정보 조회 
	 * @param url - 호출경로
	 * @return 
	 * @throws Exception 
	 */
	public String setIfUrl(Long tenantId, String ifaddUrl, String lastDataTime) throws Exception;
	
	
	/**
	 * 공통코드 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getCodeIfResult(Long tenantId) throws Exception;
	
	
	/**
	 * 공휴일정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getHolidayIfResult(Long tenantId) throws Exception;
	
	/**
	 * 근태코드정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getTaaCodeIfResult(Long tenantId) throws Exception;
	
	/**
	 * 조직코드정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getOrgCodeIfResult(Long tenantId) throws Exception;
	
	/**
	 * 조직도정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getOrgChartIfResult(Long tenantId) throws Exception;
		
	/**
	 * 임직원정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getEmpHisIfResult(Long tenantId) throws Exception;
	
	/**
	 * 조직장정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getOrgConcIfResult(Long tenantId) throws Exception;
	
	/**
	 * 임직원정보(수동이관) 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getEmpHisEtcIfResult(Long tenantId) throws Exception;
	
	/**
	 * 임직원정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getEmpAddrIfResult(Long tenantId) throws Exception;
	
	/**
	 * 근태정보 이관
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setTaaApplIf(HashMap reqMap) throws Exception;
	
	/**
	 * 근태정보(배열) 이관
	 * @param reqMap - 파라메터 맵
	 * @return Map<String,Object>
	 * @throws Exception 
	 */
	public Map<String,Object> setTaaApplArrIf(Map reqMap) throws Exception;
	
	
	/**
	 * 근태정보 이력저장
	 * @param ifHisMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setIfHis(Map<String, Object> ifHisMap) throws Exception;
	
	/**
	 * 근태정보 5분간격 이관
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setTaaApplBatchIf(Long tenantId) throws Exception;
	
	/**
	 * 근태마감
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setWorkTimeCloseIf(HashMap reqMap) throws Exception;
	
	/**
	 * 근태 타각 미갱신자료 정산
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setCalcDay(Long tenantId) throws Exception;
	
	/**
	 * 데이터이관용
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void getDataExp(HashMap reqMap) throws Exception;
	
	/**
	 * 일마감(타각)
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setCloseDay(Long tenantId) throws Exception;
	

	public void saveCodeIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveEmpIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveEmpAddrIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveGntIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveHolidayIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveOrgIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveOrgConcIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveTaaApplIntf(Long tenantId, List<Map<String, Object>> dataList);
	
	/**
	 * 근태 타각 미갱신자료 정산
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setTempHj(HashMap reqMap) throws Exception;
}
