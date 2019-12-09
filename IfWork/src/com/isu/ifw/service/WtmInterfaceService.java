package com.isu.ifw.service;

import java.util.HashMap;
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
	 * 사업장, 근무조 정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getOrgMapCodeIfResult(Long tenantId) throws Exception;
	
	/**
	 * 임직원정보 이관
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void getEmpHisIfResult(Long tenantId) throws Exception;
	
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
}
