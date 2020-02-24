package com.isu.ifw.service;

/**
 * @author KSJ
 * WTM INTERFACE TABLE DATA(인터페이스된 데이터)를 WTM TABLE로 데이터 이관
 */
public interface WtmIuerpInterfaceService {
	
	//인터페이스 대상 테이블 구분자(WTM_IF_HIS테이블의 IF_ITEM)
	// 공통코드
	final static String IF_IUERP_WTM_CODE = "WTM_INTF_CODE";
	// 공휴일
	final static String IF_IUERP_WTM_HOLIDAY_MGR = "WTM_INTF_HOLIDAY";	
	// 근태코드
	final static String IF_IUERP_WTM_TAA_CODE = "WTM_INTF_GNT";
	// 조직코드
	final static String IF_IUERP_WTM_ORG_CODE = "WTM_INTF_ORG";
	// 직원정보
	final static String IF_IUERP_WTM_EMP_HIS = "WTM_INTF_EMP";
	// 겸직정보
	final static String IF_IUERP_WTM_ORG_CONC = "WTM_INTF_ORG_CONC";
	// 직원 연락처
	final static String IF_IUERP_WTM_EMP_ADDR = "WTM_INTF_EMP_ADDR";
	// 근태 신청
	final static String IF_IUERP_WTM_TAA_APPL = "WTM_INTF_TAA_APPL";
	
	/**
	 * 인터페이스
	 * @param tenantId
	 */
	public void applyIntf(Long tenantId, String gubun);
	
}
