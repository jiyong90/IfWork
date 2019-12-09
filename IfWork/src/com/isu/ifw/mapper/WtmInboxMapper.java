package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmInboxMapper {
	
	/**
	 * 근무 계획 잔여 일 수(0보다 크면 근무 계획 작성 알림)
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getToDoPlanDays(Map<String, Object> paramMap);
	
}
