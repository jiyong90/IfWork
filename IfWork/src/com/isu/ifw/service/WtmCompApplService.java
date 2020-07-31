package com.isu.ifw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.vo.WtmApplLineVO;
import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmCompApplService {
	
	//임시저장
	final static String APPL_STATUS_IMSI = "11";
	//결재처리중
	final static String APPL_STATUS_APPLY_ING = "21";
	//결재반려
	final static String APPL_STATUS_APPLY_REJECT = "22";
	//승인처리중
	final static String APPL_STATUS_APPR_ING = "31";
	//승인반려
	final static String APPL_STATUS_APPR_REJECT = "32";
	//취소처리완료
	final static String APPL_STATUS_CANCEL = "44";
	//처리완료
	final static String APPL_STATUS_APPR = "99";
	

	//결재처리 코드
	final static String APPR_STATUS_REQUEST = "10";	//결재요청
	final static String APPR_STATUS_APPLY = "20";	//결재완료
	final static String APPR_STATUS_REJECT = "30";	//반려
	
	final static String APPL_LINE_I = "1"; //기안
	final static String APPL_LINE_S = "2"; //발신결재
	final static String APPL_LINE_R = "3"; //수신결재
	
	final static String APPL_TAACD_D = "COMP_D"; //보상휴가
	final static String APPL_TAACD_A = "COMP_A"; //보상휴가 오전반차
	final static String APPL_TAACD_P = "COMP_P"; //보상휴가 오후반차
	
	final static int DAY_HOUR = 8;
	final static int HARF_DAY_HOUR = 4;
	
	/**
	 * 보상휴가 신청내역서 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
	/**
	 * 보상휴가 신청
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ReturnParam saveApplRequest(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception;
	
	/**
	 * 보상휴가 사용가능 시간 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getPossibleUseTime(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
	/**
	 * 보상휴가 사용가능 시간 조회
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getWorkDay(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception;
	
	
//	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId);

}
