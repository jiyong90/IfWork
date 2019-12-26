package com.isu.ifw.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.vo.WtmApplLineVO;


public class MobileUtil {

	public static List parseMobileList(List<Map<String, Object>> list) throws Exception { 
		if(list == null || list.size() <= 0) 
			throw new Exception("조회 결과가 없습니다.");
		List<Map<String, Object>> l = list;
		for(Map<String,Object> temp : list) {
			if(temp.get("key2") != null) {
				temp.put("key", (temp.get("key2")));
			}
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
	
	public static String parseEmpKey(String empKey, String key) { 
		String data = "";
		try {
			if(empKey != null && empKey.indexOf("@") >=0 ){
				String separator = "@";
				String[] arrEmpKey = empKey.split(separator);
				if(arrEmpKey.length == 2) {
					if(key.equals("enterCd"))
						data = arrEmpKey[0];
					if(key.equals("sabun"))
						data = arrEmpKey[1];
				}
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	
	public static List makeApprLines(List<WtmApplLineVO> applLineVOs, WtmApplCode applCode) {
		List<Map<String, Object>> apprLines = new ArrayList();
		
		List<Map<String, Object>> lines = new ArrayList();
		List<Map<String, Object>> apprTypes = new ArrayList();

		int lineCnt = 0;
		
		Map<String, Object> line = new HashMap();
		line.put("name", "결재자");
		line.put("type", "");
		line.put("useAdd", "false");
		line.put("useStatusChange", "false");
		line.put("apprTypes", apprTypes);
		line.put("line", lines);

		apprLines.add(line);
		
		Map<String, Object> type = new HashMap();
		type.put("applTypeCd", "10");
		type.put("typeNm", "결재");
		
		apprTypes.add(type);

		for(WtmApplLineVO applLineVO : applLineVOs) {
			if(lineCnt < Integer.parseInt(applCode.getApplLevelCd())) {
				Map<String, Object> temp = new HashMap();
				temp.put("type", "emp");
				temp.put("typeNm", "결재");
				temp.put("key", applCode.getEnterCd() + "@" + applLineVO.getSabun());
				temp.put("name", applLineVO.getEmpNm());
				lines.add(temp);
			}
			lineCnt++;
		}
		return apprLines;
	}
	
	public static List makeApprLines(List<WtmApplLineVO> applLineVOs) {
		List<Map<String, Object>> apprLines = new ArrayList();
		
		List<Map<String, Object>> lines1 = new ArrayList();
		List<Map<String, Object>> lines2 = new ArrayList();

		int lineCnt = 0;
		
		Map<String, Object> line1 = new HashMap();
		line1.put("name", "기안");
		line1.put("type", "");
		line1.put("useAdd", "false");
		line1.put("useStatusChange", "false");
		line1.put("apprTypes", null);
		line1.put("line", lines1);

		Map<String, Object> line2 = new HashMap();
		line2.put("name", "결재");
		line2.put("type", "");
		line2.put("useAdd", "false");
		line2.put("useStatusChange", "false");
		line2.put("apprTypes", null);
		line2.put("line", lines2);
		
		apprLines.add(line1);
		apprLines.add(line2);
		
		for(WtmApplLineVO applLineVO : applLineVOs) {
			Map<String, Object> temp = new HashMap();
			temp.put("type", "emp");
			temp.put("typeNm", applLineVO.getApprTypeNm());
			temp.put("name", applLineVO.getEmpNm());

			if(applLineVO.getApprTypeCd().equals("1"))
				lines1.add(temp);
			else if(applLineVO.getApprTypeCd().equals("2"))
				lines2.add(temp);
		}
		return apprLines;
	}
}
