package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmIuerpInterfaceMapper {
	 
	/**
	 * 근태 테이블에는 있고, 인터페이스 테이블에는 없는 데이터 종료일 변경
	 * @param paramMap
	 * tenantId
	 * ymdhis
	 * updateId
	 */
	public List<Map<String, Object>> getExpireWtmCode(Map<String, Object> paramMap);
	public int expireWtmCode(Map<String, Object> paramMap);
	/**
	 * 신규 반영할 시작/종료일(인터페이스 될 데이터)이 기존의 시작/종료일을 포함할 경우 기존 데이터 삭제
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getDeleteWtmCode(Map<String, Object> paramMap);
	public int deleteWtmCode(Map<String, Object> paramMap);
	/**
	 * 기존의 시작/종료일(인터페이스 될 데이터)이 신규 반영할 시작/종료일을 포함할 경우 
	 * 신규 종료일+1 ~ 기존 종료일 데이터 삽입
	 * @param paramMap
	 * tenantId
	 * ymdhis
	 * updateId
	 */
	public int insertWtmCodeForBetween(Map<String, Object> paramMap);
	/**
	 * 시작/종료일 수정
	 * @param paramMap
	 * tenantId
	 * ymdhis
	 * updateId
	 */
	public int updateWtmCode(Map<String, Object> paramMap);
	/**
	 * 신규 추가된 데이터 삽입
	 * @param paramMap
	 * tenantId
	 * ymdhis
	 * updateId
	 */
	public int insertWtmCode(Map<String, Object> paramMap);
	
	
	public int expireWtmHolidayMgr(Map<String, Object> paramMap);
	public int updateWtmHolidayMgr(Map<String, Object> paramMap);
	public int insertWtmHolidayMgr(Map<String, Object> paramMap);
	
	
	public int expireWtmTaaCode(Map<String, Object> paramMap);
	public int updateWtmTaaCode(Map<String, Object> paramMap);
    //reuqest type cd가 안맞는 경우 근태에서 조정하고 업데이트 할 때는 반영하지 않는다.
	public int updateWtmTaaCode2(Map<String, Object> paramMap);
	public int insertWtmTaaCode(Map<String, Object> paramMap);
	
	
	public List<Map<String, Object>> getExpireWtmEmpHis(Map<String, Object> paramMap);
	public int expireWtmEmpHis(Map<String, Object> paramMap);
	public List<Map<String, Object>> getUpdateWtmEmpHis(Map<String, Object> paramMap);
	public int updateWtmEmpHis(Map<String, Object> paramMap);
	public List<Map<String, Object>> getInsertWtmEmpHis(Map<String, Object> paramMap);
	public int insertWtmEmpHis(Map<String, Object> paramMap);
	public int insertCommUser(Map<String, Object> paramMap);
	public List<Map<String, Object>> getExpireFlexibleEmp(Map<String, Object> paramMap);
	
	
	public int expireWtmOrgConc(Map<String, Object> paramMap);
	public int updateWtmOrgConc(Map<String, Object> paramMap);
	public int insertWtmOrgConc(Map<String, Object> paramMap);
	
	public int expireWtmOrgChart(Map<String, Object> paramMap);
	public int updateWtmOrgChart(Map<String, Object> paramMap);
	public int insertWtmOrgChart(Map<String, Object> paramMap);

	
	public int expireWtmEmpAddr(Map<String, Object> paramMap);
	public int updateWtmEmpAddr(Map<String, Object> paramMap);
	public int insertWtmEmpAddr(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getExpireWtmOrgCode(Map<String, Object> paramMap);
	public int expireWtmOrgCode(Map<String, Object> paramMap);
	public List<Map<String, Object>> getDeleteWtmOrgCode(Map<String, Object> paramMap);
	public int deleteWtmOrgCode(Map<String, Object> paramMap);
	public int insertWtmOrgCodeForBetween(Map<String, Object> paramMap);
	public int insertWtmOrgCode(Map<String, Object> paramMap);
	public int updateWtmOrgCode(Map<String, Object> paramMap);
	public int updateWtmOrgChartDet(Map<String, Object> paramMap);
	public int insertWtmOrgChartDet(Map<String, Object> paramMap);
	
}
