package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.WtmFlexibleApplDetVO;


public interface WtmFlexibleApplMapper {
	
	/**
	 * 임시저장중인 근무제 마지막 신청정보를 가지고 오자/ 근무제 신청은 중복으로 신청할 수 없다
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getLastAppl(Map<String, Object> paramMap);
	
	public Map<String, Object> findByApplId(Long applId);
	
	/**
	 * 나의 근무기간의 총 소정근로시간을 계산한다.
	 * @param flexibleEmpId
	 */
	public void updateWorkMinuteOfWtmFlexibleEmp(Map<String, Object> paramMap);
	
	public List<WtmFlexibleApplDetVO> getWorkPattern(Map<String, Object> paramMap);
}
