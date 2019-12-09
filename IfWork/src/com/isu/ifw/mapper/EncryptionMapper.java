package com.isu.ifw.mapper;

import java.util.Map;

public interface EncryptionMapper {
	/**
	 * 단방향 암호화
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<?,?> getShaEncrypt(Map<String, Object> paramMap) throws Exception;
}
