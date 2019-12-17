package com.isu.ifw.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.vo.WtmApplLineVO;


public class MobileUtil {

	public static List parseMobileList(List<Map<String, Object>> list)  { 
		if(list == null || list.size() <= 0) 
			return null;
		List<Map<String, Object>> l = list;
		for(Map<String,Object> temp : list) {
			if(temp.get("key2") != null) {
				temp.put("key", (temp.get("key2")));
			}
//			if(temp.get("CAPTION_LB") != null) {
//				temp.put("caption_lb", (temp.get("CAPTION_LB")));
//			}
//			if(temp.get("CAPTION_RB") != null) {
//				temp.put("caption_rb", (temp.get("CAPTION_RB")));
//			}
//			if(temp.get("KEY2") != null) {
//				temp.put("key", (temp.get("KEY2")));
//			}
//			if(temp.get("TITLE") != null) {
//				temp.put("title", (temp.get("TITLE")));
//			}
//			if(temp.get("STATUS") != null) {
//				temp.put("status", (temp.get("STATUS")));
//			}
		}
		return l;
	}
	
	public static String parseEmpKey(String secret, String empKey, String key) { 
		String data = "";
		try {
			Aes256 aes = new Aes256(secret);
			String emp = aes.decrypt(empKey);
			
			if(key.equals("enterCd")) {
				data = emp.split("@")[0];
			}
			if(key.equals("sabun")) {
				data = emp.split("@")[1];
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
}
