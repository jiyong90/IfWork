package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmFlexibleStdVO;

public interface WtmFlexibleStdMapper {
	/*
	 * 신청가능한 근무제 조회
	 */
	public List<WtmFlexibleStdVO> getWtmFlexibleStd(Map<String, Object> paramMap);
	
	/**
	 * 기본근무제를 제외한 유연근무 신청 중 또는 이미 등록된 정보가 있는지 확인한다
	 * @param applId
	 * @return
	 */
	public Map<String, Object> checkRequestDate(Map<String, Object> paramMap);
	
	//근무제 전체 조회
	public List<Map<String, Object>> getWtmFlexibleStdList(Map<String, Object> paramMap);
	
	//근무제 work_type_cd별 조회
	public List<Map<String, Object>> getWtmFlexibleStdWorkTypeList(Map<String, Object> paramMap);
	
	/**
	 * 특정일 기준으로 주의 시작일과 종료일을 가지고 온다. 
	 * 주의 시작 요일은 WTM_PROPERTIE OPTION_FIRSTDAY_OF_WEEK 
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getRangeWeekDay(Map<String, Object> paramMap);
	
	/**
	 * 근무제도기본 입력
	 * @param list
	 * @return
	 */
	public int insertFlexibleStd(List<Map<String, Object>> list);
	
	/**
	 * 근무제도기본 수정
	 * @param list
	 * @return
	 */
	public int updateFlexibleStd(List<Map<String, Object>> list);
	
	// 근무제도 관리자 화면 조회
	public List<Map<String, Object>> getStdListWeb(Map<String, Object> paramMap);
	
	/**
	 * 해당 일의 요일 가져오기
	 * @param weekday
	 * @return
	 */
	public Map<String, Object> getWeekday(String weekday);
}
