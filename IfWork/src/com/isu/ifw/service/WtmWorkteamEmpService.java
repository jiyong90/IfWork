package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.isu.option.vo.ReturnParam;

/**
 * 로그인 서비스
 *
 * @author ParkMoohun
 *
 */
@Service("WtmWorkteamEmpService")
public interface WtmWorkteamEmpService{
	
	public List<Map<String, Object>> getWorkteamList(Long tenantId, String enterCd, Map<String, Object> paramMap);
	public ReturnParam setWorkteamList(Long tenantId, String enterCd, String userId, Map<String, Object> paramMap);
	
}