package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;



/**
 * 
 * @author 
 *
 */
public interface WtmEmpMgrService {
	//사원 이력 조회
	public List<Map<String, Object>> getEmpHisList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	//사원 조회
	public Map<String, Object> getEmpHis(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap);
	//사원변경이력조회
	public List<Map<String, Object>> getEmpIfMsgList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	
	public int saveEmpHis(Long tenantId, String enterCd, Map<String, Object> paramMap, String userId);
	
	//비밀번호 인증 정보(이메일 또는 핸드폰번효) 있는지 확인
	public boolean checkPasswordCertificate(Long tenantId, String enterCd, String userInfo);
	
	/**
	 * otp code 체크 비밀번호 변경
	 * @param tenantId
	 * @param enterCd
	 * @param otp
	 * @param userInfo
	 * @return
	 */
	public ReturnParam codeCheck(Long tenantId, String enterCd, String otp, String userInfo);
	
	/**
	 * 비밀번호 변경
	 * @param tenantId
	 * @param tsId
	 * @param enterCd
	 * @param paramMap
	 */
	public void changePw(Long tenantId, String tsId, String enterCd, Map<String, Object> paramMap);
	
}
