package com.isu.ifw.mapper;

import com.isu.ifw.vo.WtmApplLineVO;

import java.util.List;
import java.util.Map;

public interface WtmApplMapper {
	public List<WtmApplLineVO> getWtmApplLine(Map<String, Object> paramMap);
	public List<WtmApplLineVO> getWtmApplLineHS(Map<String, Object> paramMap);

	public List<WtmApplLineVO> getWtmApplLineByApplId(Long applId);
	
	public Map<String, Object> calcWorkDay(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getApprList01(Map<String, Object> paramMap);
	public List<Map<String, Object>> getApprList02(Map<String, Object> paramMap);
	public List<Map<String, Object>> getApprList03(Map<String, Object> paramMap);

	public List<Map<String, Object>> getMobileApprList01(Map<String, Object> paramMap);
	public List<Map<String, Object>> getMobileApprList02(Map<String, Object> paramMap);
	public List<Map<String, Object>> getMobileApprList03(Map<String, Object> paramMap);

	public int countByApprList01(Map<String, Object> paramMap);
	public int countByApprList02(Map<String, Object> paramMap);
	
	//모바일에서 신청서 기본 validation체크 (날짜, 시간, 기간 등)
	public Map<String, Object> getApplValidation(Map<String, Object> paramMap);
	//모바일에서 사용할 신청서 갯수
	public Map<String, Object> getEdocCountForMobile(Map<String, Object> paramMap);
	
	/**
	 * 신청서별 수신처 조회
	 * @param paramMap
	 * tenantId
	 * enterCd
	 * d : 오늘 날짜
	 * applCodeId : 신청서 id
	 * @return
	 */
	public List<Map<String, Object>> getRecLine(Map<String, Object> paramMap);

	/**
	 * 결재상태리스트
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getApprovalApplList01(Map<String, Object> paramMap);
	public List<Map<String, Object>> getApprovalApplList02(Map<String, Object> paramMap);
	public List<Map<String, Object>> getApprovalApplList03(Map<String, Object> paramMap);
	
	/**
	 * nvg근태리스트조회
	 * @return 
	 **/
	public List<Map<String, Object>> getTaaListNgv(Map<String, Object> paramMap);
}
