package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmGpsMgrMapper {
	
	/**
	 * GPS 타각 리스트 가져오기
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getGpsList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * GPS 타각 테이블에 출퇴근 기록
	 * @param paramMap
	 * @return
	 */
	public int saveWtmGpsInoutHis(Map<String, Object> paramMap) throws Exception;
	
}
