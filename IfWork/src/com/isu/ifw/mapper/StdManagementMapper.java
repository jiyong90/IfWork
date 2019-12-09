package com.isu.ifw.mapper;

import java.util.Map;

import com.isu.ifw.vo.StdManagement;

public interface StdManagementMapper {
	/**
	 * 직원조회 전 키 값 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public StdManagement getKeyCheckResult(Map<String, Object> paramMap) throws Exception;
}
