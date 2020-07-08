package com.isu.ifw.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmFlexibleStdVO;

public interface WtmScheduleMapper {
		
	/**
	 * 일 퇴근마감
	 **/
	public List<Map<String, Object>> getWtmCloseDay(Map<String, Object> paramMap);
	/**
	 * 현지출퇴근 코드 매핑여부(근태코드 매핑없으면 현출, 현퇴기능없음)
	 * @return 
	 **/
	public Map<String, Object> getTaaLocalCode(Map<String, Object> paramMap);
	/**
	 * 현퇴 대상 퇴근시간 갱신
	 * @return 
	 **/
	public int setUpdateLocalOut(Map<String, Object> paramMap);
	
	/**
	 * 현출 대상 출근시간 갱신
	 * @return 
	 **/
	public int setUpdateLocalIn(Map<String, Object> paramMap);
	
	/**
	 * 근무계획없음 일 퇴근마감
	 **/
	public List<Map<String, Object>> getWtmCloseDayUnplaned(Map<String, Object> paramMap);
	
	/**
	 * 근무계획없음 일 퇴근마감
	 **/
	public List<Map<String, Object>> setCalcGobackList(Map<String, Object> paramMap);
	
	
	
	/**
	 * intf전송할 ot내역
	 **/
	public List<Map<String, Object>> getIntfOtList(Map<String, Object> paramMap);

	/**
	 * intf전송할 보상휴가내역
	 **/
	public List<Map<String, Object>> getIntfCompList(Map<String, Object> paramMap);

	
	/**
	 * 연장근무시간 조회
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getOtList(Map<String, Object> paramMap);
	
	/**
	 * 연장근무시간 조회2
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getOtList2(Map<String, Object> paramMap);

	/**
	 * 전체근무시간 조회
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getTotList(Map<String, Object> paramMap);

	/**
	 * 출퇴근 미타각자 조회
	 * @param paramMap
	 * @return 
	 */
	public List<Map<String, Object>> getInoutCheckList(Map<String, Object> paramMap);
	
	/**
	 * 근태갱신대상
	 * @return 
	 **/
	public List<Map<String, Object>> getTaaReset();
	
	/**
	 * 근태갱신대상
	 * @return 
	 **/
	public int setDeleteTaaOld(Map<String, Object> paramMap);
	
	
}
