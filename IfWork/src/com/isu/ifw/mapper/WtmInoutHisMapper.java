package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmInoutHisMapper {
	
	/**
	 * 오늘 출퇴근 상태 가져오기
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getInoutStatus(Map<String, Object> paramMap) throws Exception;

	/**
	 * 메뉴 상태 가져오기
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getContext(Map<String, Object> paramMap) throws Exception;

	
	/**
	 * 타각 테이블에 출퇴근 기록
	 * @param paramMap
	 * @return
	 */
	public int saveWtmInoutHis(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 캘린더 출퇴근 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getMyInoutList(Map<String, Object> paramMap) throws Exception;

	/**
	 * 일별 타각데이터 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getMyInoutHistory(Map<String, Object> paramMap) throws Exception;

	
	/**
	 * 출퇴근 상세 조회
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getMyInoutDetail(Map<String, Object> paramMap) throws Exception;
}
