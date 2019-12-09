package com.isu.ifw.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.EncryptionMapper;

/**
 * 암호화 service
 * @author 
 *
 */
@Service("EncryptionService")
public class EncryptionService {
	
	@Autowired
	EncryptionMapper encryptionMapper;
	
	/**
	 * 단방향 암호화
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<?,?> getShaEncrypt(Map<String, Object> paramMap) throws Exception {
		return encryptionMapper.getShaEncrypt(paramMap);
	}
	
}
