package com.isu.ifw.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.StdManagementMapper;
import com.isu.ifw.vo.StdManagement;

/**
 * 시스템 기준관리 service
 * @author 
 *
 */
@Service("StdManagementService")
public class StdManagementService {
	
	@Autowired
	StdManagementMapper stdManagementMapper;
	
	/**
	 * 직원 조회 전 키 값 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public StdManagement getKeyCheckResult(Map<String, Object> paramMap) throws Exception {
		return stdManagementMapper.getKeyCheckResult(paramMap);
	}
	
}
