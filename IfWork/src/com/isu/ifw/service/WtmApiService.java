package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WtmApiService {
	
	
	/**
	 * @author yukpan
	 * 
	 * @explain
	 * 			갱신일 기준 사원 리스트를 반환한다.	   
	 *  
	 * @param datetime 		  
	 * @param updateType
	 * @param enterCd
	 * 
	 * @return
	 * 
	 */
	
	public List<Map<String, Object>> getEmpList(String datetime, String updateType, Long tenentId) throws Exception;
}
