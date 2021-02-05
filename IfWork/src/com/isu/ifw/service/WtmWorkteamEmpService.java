package com.isu.ifw.service;

import com.isu.ifw.vo.ReturnParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 로그인 서비스
 *
 * @author ParkMoohun
 *
 */
@Service("WtmWorkteamEmpService")
public interface WtmWorkteamEmpService{

	final static String WTM_WORKTEAM_SAVE_Y = "Y" ;    //  Y : 완료
	final static String WTM_WORKTEAM_SAVE_N = "N" ;    //  N: 실패
	final static String WTM_WORKTEAM_SAVE_I = "I" ;    //  I : 진행중
	final static String WTM_WORKTEAM_SAVE_C = "C" ;    //  C : 취소처리중
	
	public List<Map<String, Object>> getWorkteamList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	public ReturnParam setWorkteamList(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap);

	public void setApply(Long tenantId, String enterCd, String userId, List<Map<String, Object>> convertMap) throws Exception;
	
}