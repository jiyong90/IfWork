package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmTaaListMapper {
	
	/**
	 * 근태신청내역(관리자) 리스트 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getTaaApplDetList(Map<String, Object> paramMap);

	/**
	 * 승인된 근태 신청서 삭제
	 * @author 양동훈
	 * @param paramMap
	 * @return
	 */
	public void delWtmAppl(Map<String, Object> paramMap);
	
	/**
	 * 인터페이스 승인된 근태 신청서 삭제
	 * @author 양동훈
	 * @param paramMap
	 * @return
	 */
	public void delWtmTaaAppl(Map<String, Object> paramMap);
	
	
	/**
	 * 인터페이스 승인된 근태 신청서 상세내용 삭제
	 * @author 양동훈
	 * @param paramMap
	 * @return
	 */
	public void delWtmTaaApplDet(Map<String, Object> paramMap);
	
	
	/**
	 * 근무캘린더 타각 시작, 종료 상태 변경
	 * @author 양동훈
	 * @param paramMap
	 * @return
	 */
	public void saveWtmWorkCaldar(Map<String, Object> paramMap);
	
	/**
	 * 수정된 근태신청내역(관리자) 리스트 조회
	 * @author 양동훈
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getTaaApplUdtList(Map<String, Object> paramMap);
	
}
