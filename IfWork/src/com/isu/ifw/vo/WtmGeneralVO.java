package com.isu.ifw.vo;

import java.util.HashMap;

import org.springframework.jdbc.support.JdbcUtils;

public class WtmGeneralVO extends HashMap {

	@Override
	public Object put(Object key, Object value) {
		
		String k = (String)key;
		
		try {
			// _ to camelCase
			if(k.contains("_") || k.equals(k.toUpperCase())) {
				
				k = JdbcUtils.convertUnderscoreNameToPropertyName(k);
				
				//S_YMD 인 경우 convertUnderscoreNameToPropertyName 로 변환하면 sYmd 가 아니라 SYmd로 변환된다
				if(!k.equals(k.substring(0, 1).toLowerCase())) {
					String c = k.substring(0, 1).toLowerCase();
					k = c + k.substring(1);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			k = (String)key;
		}
		
		return super.put(k, value);
	}
}
