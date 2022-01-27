package com.isu.ifw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;


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
	public Map<String, Object> getIfLastDateBefore(Long tenantId, String ifType) throws Exception;

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
	public ReturnParam setTaaApplIf(HashMap reqMap) throws Exception;
	
	/**
	 * 근태정보 이관 수기처리
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setTaaApplParam(HashMap reqMap) throws Exception;
	
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
	 * 근태정보 테이블 내용 전체 돌리기
	 * @return 
	 * @throws Exception 
	 */
	public void setTaaApplBatchIfPostProcess();

	/**
	 * 근태마감
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setWorkTimeCloseIf(HashMap reqMap) throws Exception;
	
	/**
	 * 근태마감 수정본
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setCloseWorkIf(HashMap reqMap) throws Exception;
	
	/**
	 * 근태 타각 미갱신자료 정산
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setCalcDay(Long tenantId) throws Exception;
	
	/**
	 * 근태 타각 미갱신자료 파람받아서 처리하기
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setCalcDayParam(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd) throws Exception;
	
	/**
	 * 근태 타각 미갱신자료 정산(사람별 데이터 생성용)
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public List<Map<String, Object>> getCalcDayLoopEmp(Long tenantId);
	
	/**
	 * 근태 타각 미갱신자료 정산 루프용 파람받아서 처리하기
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setCalcDayLoop(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd) throws Exception;
	
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
	
	/**
	 * 보상휴가 hr전송
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void sendCompCnt(HashMap reqMap) throws Exception;
	
	
	

	public void saveCodeIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveEmpIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveEmpAddrIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveGntIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveHolidayIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveOrgIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveOrgConcIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveOrgChartIntf(Long tenantId, List<Map<String, Object>> dataList);
	public void saveTaaApplIntf(Long tenantId, List<Map<String, Object>> dataList);
	
	/**
	 * 근태 타각 미갱신자료 정산
	 * @param tenantId - 테넌트 아이디
	 * @return 
	 * @throws Exception 
	 */
	public void setTempHj(HashMap reqMap) throws Exception;
	
	
	/**
	 * 근태마감
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public ReturnParam setCloseWorkIfN(HashMap reqMap) throws Exception;
	public void intfTaaAppl(Long tenantId) ;
	public void taaResult(Long tenantId, String enterCd, String Applsabun, String ifApplNo, String status, List<Map<String, Object>> works) throws Exception;
	public void resetTaaResult(Long tenantId, String enterCd, String sabun,String ymd);
	public void resetTaaResultNoFinish(Long tenantId, String enterCd, String sabun,String ymd);

	
	/**
	 * 근무기간 별 근무시간 합계
	 * @param tenantId - 테넌트 아이디
	 * @param enterCd - 엔터 코드
	 * @param sabun - 사번
	 * @param symd - 시작일
	 * @param eymd - 종료일
	 * @return 
	 * @throws Exception 
	 */
	public List<Map<String, Object>> allWorkTimeCheck(Long tenantId, String enterCd, String sabun,String symd, String eymd);


	/**
	 * 마감데이터 전송
	 * @param reqMap
	 * @throws Exception
	 */
	public void sendCloseData(HashMap reqMap) throws Exception;
	
}
