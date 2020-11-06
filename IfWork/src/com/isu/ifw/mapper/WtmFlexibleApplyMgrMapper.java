package com.isu.ifw.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WtmFlexibleApplyMgrMapper {
	
	public List<Map<String, Object>> getApplyList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyRepeatList(Map<String, Object> paramMap);
	
	public Map<String, Object> getEymd(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getworkTypeList(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyConfirmList(Map<String, Object> paramMap);
	
	public int insertApplyEmp(Map<String, Object> paramMap);
	
	public int updateApplyEmp(Map<String, Object> paramMap);
	
	public Map<String, Object> setApplyEmpId(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyGrpList(Map<String, Object> paramMap);
	
	public int insertGrp(List<Map<String, Object>> list);
	
	public int updateGrp(List<Map<String, Object>> list);
	
	public int deleteGrp(List<Map<String, Object>> list);
	
	public List<Map<String, Object>> getApplyEmpList(Map<String, Object> paramMap);
	
	public int insertEmp(List<Map<String, Object>> list);
	
	public int updateEmp(List<Map<String, Object>> list);
	
	public int deleteEmp(List<Map<String, Object>> list);
	
	public int deleteApplyEmpTemp(Map<String, Object> paramMap);
	
	public int insertApplyEmpTemp(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApplyEmpPopList(Map<String, Object> paramMap);
	//확정 시 개인별 성공여부 업데이트
	public void updateFlexibleEmpTemp(@Param("flexibleApplyTempId") Long flexibleApplyTempId);
	//확정 시 전체 성공여부 업데이트
	public void updateFlexibleApplyAll(@Param("flexibleApplyId") Long flexibleApplyId);
	
	//확정 시 선반영된 근태건이 있으면 재갱신 대상으로 변경해야함.
	public void updateFlexibleTaaReset(@Param("flexibleApplyId") Long flexibleApplyId);
	//복사 저장 시 Wtm_Flexible_apply_emp_temp 복사 대상 조회
	public List<Map<String, Object>> getApplyEmpTempList(Map<String, Object> paramMap);
	//복사 건 저장 이후 auto_increment flexibleApplyId 조회
	public List<Map<String, Object>> getFlexibleApplyId(Map<String, Object> paramMap);
	
	//근무제 복사
	public int copyWtmApplyGroup(Map<String, Object> paramMap);
	public int copyWtmApplyEmp(Map<String, Object> paramMap);
	public int copyWtmApplyEmpTemp(Map<String, Object> paramMap);

	//  확정취소 대상자 조회
	List<Map<String, Object>> getApplyConfirmCancelList(Map<String, Object> paramMap);

	//  확정대상자 존재여부 체크
	int getExistCountBySymdAndEymd(Map<String, Object> paramMap);
}
