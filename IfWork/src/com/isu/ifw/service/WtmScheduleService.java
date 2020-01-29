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
}
