package com.isu.ifw.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 배치 스케줄러
 * @author lhj
 *
 */
public interface WtmScheduleService {
	
	/**
	 * 일마감(타각)
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void setCloseDay(Long tenantId) throws Exception;

	/**
	 * 인터페이스로 자료 전송
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void sendIntfData(Long tenantId, String ifType) throws Exception;

	/**
	 * push 전송
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void sendPushMessageMin(Long tenantId, String enterCd);

	/**
	 * push 전송
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void sendPushMessageDay(Long tenantId, String enterCd);

	/**
	 * push 전송
	 * @param reqMap - 파라메터 맵
	 * @return 
	 * @throws Exception 
	 */
	public void sendPushMessageDay2(Long tenantId, String enterCd);

	
	/**
	 * 근태재갱신 - 근태상태코드가 00인것들을 99 상태로 갱신해야함
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public void setTaaReset() throws Exception;
	
}
