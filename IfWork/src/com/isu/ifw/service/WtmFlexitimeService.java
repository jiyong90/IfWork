package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.isu.option.vo.ReturnParam;

/**
 * 근무제 관련 service
 * @author 
 *
 */
@Service("WtmFlexitimeService")
public class WtmFlexitimeService {
	
	/**
	 * 적용된 근무제 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getFlexitime(Map<String, Object> paramMap) throws Exception {
		return null;
	}
	
	/**
	 * 사용할 수 있는 근무제 리스트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFlexitimeList(Map<String, Object> paramMap) throws Exception {
		return null;
	}
	
	/**
	 * 근무제 validation 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ReturnParam checkFlexitime(Map<String, Object> paramMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		
		return rp;
	}
	
	/**
	 * 근무제 적용
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public ReturnParam applyFlexitime(Map<String, Object> paramMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		
		return rp;
	}
	
}
