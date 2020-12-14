package com.isu.ifw.service;

import com.isu.ifw.vo.ReturnParam;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public interface WtmApplService {
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
	
	

	//기본근무시간
	final static String TIME_TYPE_BASE = "BASE";
	//고정 OT
	final static String TIME_TYPE_FIXOT = "FIXOT";
	//연장근무
	final static String TIME_TYPE_EARLY_OT = "EARLYOT";
	final static String TIME_TYPE_OT = "OT";
	//연장근무
	final static String TIME_TYPE_EARLY_NIGHT = "EARLYNIGHT";
	final static String TIME_TYPE_NIGHT = "NIGHT";
	//대체휴가
	final static String TIME_TYPE_SUBS = "SUBS";
	//간주근무
	final static String TIME_TYPE_REGA = "REGA";
	
	final static String TIME_TYPE_REGA_OT = "REGA_OT";
	final static String TIME_TYPE_REGA_NIGHT = "REGA_NIGHT";
	//간주근무
	final static String TIME_TYPE_REGA_CAN = "REGA_CAN";
	//휴가
	final static String TIME_TYPE_TAA = "TAA";
	//휴가 취소
	final static String TIME_TYPE_TAA_CAN = "TAA_CAN";
	// 지각/조퇴
	final static String TIME_TYPE_LLA = "LLA";
	//외출
	final static String TIME_TYPE_GOBACK = "GOBACK";
	//  근태사유서
	final static String TIME_TYPE_ENTRY_CHG = "ENTRY_CHG";
	//  유연근무제
	final static String TIME_TYPE_SELE_F = "SELE_F";
	//  연장 취소
	final static String TIME_TYPE_OT_CAN = "OT_CAN";
	//  대체휴가 정정
	final static String TIME_TYPE_SUBS_CHG = "SUBS_CHG";
	//  보상휴가취소
	final static String TIME_TYPE_COMP = "COMP";
	//  보상휴가취소
	final static String TIME_TYPE_COMP_CAN = "COMP_CAN";

	final static String TIME_TYPE_EXCEPT = "EXCEPT";

	final static String TIME_TYPE_ANNUAL = "ANNUAL";
	final static String TIME_TYPE_ANNUAL_CAN = "ANNUAL_CAN";


	//현출 현퇴
	final static String TIME_TYPE_ETC = "ETC";
	
	//결재
	final static String APPL_TYPE_REQUEST = "01"; //신청
	final static String APPL_TYPE_PENDING = "02"; //미결
	final static String APPL_TYPE_COMPLETE = "03"; //기결
	
	//휴게시간
	final static String BREAK_TYPE_MGR = "MGR";
	//휴게시간
	final static String BREAK_TYPE_TIME = "TIME";
	//휴게시간
	final static String BREAK_TYPE_TIMEFIX = "TIMEFIX";
	
	final static String APPL_TAACD_D = "COMP_D"; //보상휴가
	final static String APPL_TAACD_A = "COMP_A"; //보상휴가 오전반차
	final static String APPL_TAACD_P = "COMP_P"; //보상휴가 오후반차


	final static String WTM_FLEXIBLE_APPLY_Y = "Y" ;    //  Y : 완료
	final static String WTM_FLEXIBLE_APPLY_N = "N" ;    //  N: 실패
	final static String WTM_FLEXIBLE_APPLY_I = "I" ;    //  I : 진행중
	final static String WTM_FLEXIBLE_APPLY_C = "C" ;    //  C : 취소처리중

	final static int DAY_HOUR = 8;
	final static int HARF_DAY_HOUR = 4;
	
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId);
	
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId);
	
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId);
	/**
	 * 승인/반려 신청서 리스트(결재함)
	 * @param tenantId
	 * @param enterCd
	 * @param empNo
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId);
	
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception;
	//모바일용 동기처리, 임시저장이 없어서 applId가 없음
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception;
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception;
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception;
	public void delete(Long applId);
	
	/**
	 * 
	 * @param tenantId
	 * @param enterCd
	 * @param applId - 
	 * @param targetApplId -WTM_APPL이 부모 신청서다 하위에 근테 근무 등의 신청테이블이 붙는다.
	 * @param workTypeCd - WTM_APPL_CODE의 신청서 코드이다
	 * @param paramMap - 신청서별 필요한 추가 파라메터들을 담는다.
	 * @param sabun
	 */
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String status, String sabun, String userId) throws Exception;
	
	/**
	 * 사전점검
	 * @param tenantId
	 * @param enterCd
	 * @param applId
	 * @param workTypeCd
	 * @param paramMap
	 * @return
	 */
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap);
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap) throws ParseException;
	
	public void sendPush();
	
	
	/**
	 * 연장/휴일근무 신청내역조회 결재상태 변경
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param userId
	 * @param convertMap
	 * @return
	 */
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId, Map<String, Object> convertMap);
	
}
