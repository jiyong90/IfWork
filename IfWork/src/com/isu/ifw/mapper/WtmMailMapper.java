package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmMailMapper {
	
	/**
	 * 메일 테이블에 저장
	 * @param paramMap
	 * @return
	 */
	public void sendMail(Map<String, Object> paramMap);
	
	/**
	 * 메일 내용 수정
	 * @param paramMap
	 */
	public void updateMailContents(Map<String, Object> paramMap);
	
}
