package com.isu.ifw.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.isu.ifw.vo.Login;


public interface LoginMapper {
	public Map<String, String> loginTryCnt(@Param("loginEnterCd") String loginEnterCd,@Param("loginUserId") String loginUserId,@Param("loginPassword") String loginPassword) throws Exception;
	
	public Login loginUser(@Param("ssnLocaleCd") String ssnLocaleCd,@Param("localeCd") String localeCd,@Param("baseLang") String baseLang,@Param("loginEnterCd") String loginEnterCd, @Param("loginUserId") String loginUserId,@Param("loginPassword") String loginPassword,@Param("ssnSso") String ssnSso);
	
	
}
