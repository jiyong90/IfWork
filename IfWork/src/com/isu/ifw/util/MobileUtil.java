package com.isu.ifw.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.vo.WtmApplLineVO;


public class MobileUtil {

	public static List parseMobileList(List<Map<String, Object>> list) throws Exception { 
		if(list == null || list.size() <= 0) 
			throw new Exception("조회 결과가 없습니다.");
		List<Map<String, Object>> l = list;
		List<Map<String, Object>> l2 = new ArrayList();
		for(Map<String,Object> temp : list) {
			Map<String, Object> temp2 = new HashMap();
			temp2.putAll(temp);
			
			if(temp.get("key2") != null) {
				temp2.put("key", (temp.get("key2")));
			}
			if(temp.containsKey("captionLb")) {
				temp2.put("caption_lb", (temp.get("captionLb")));
//				temp.put("cc", "remappingFunction");
//				temp2.put("caption_lb", (temp.get("captionLb")));
//				temp3.put("caption_lb", (temp.get("captionLb")));
//				System.out.println("00000000000001 " + temp.toString());
//				System.out.println("00000000000002 " + temp2.toString());
//				System.out.println("00000000000003 " + temp3.toString());
			}
			if(temp.containsKey("captionRb")) {
				temp2.put("caption_rb", (temp.get("captionRb")));
			}
			l2.add(temp2);
		}
		
//		for(Map<String,Object> temp : list) {
//			System.out.println("00000000000002 " + temp.toString());
//		}
		return l2;
	}
	
	public static List<Map<String, Object>> parseApprList(List<Map<String, Object>> l) {
		for(Map<String, Object> m : l) {
			m.put("key", m.get("applKey"));
			m.put("title", m.get("applNm"));
			m.put("caption_lb", m.get("empNm"));
			m.put("caption_rb", m.get("applYmd"));
			m.put("status", m.get("applStatNm"));
		}
		return l;
		//[{applId=15153, applKey=ISU_ST@ENTRY_CHG@15153@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=ENTRY_CHG, applEmpNm=김수정, applStatusCd=21, applNm=근태사유서신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=0, urlLink=view://W04View}, {applId=15152, applKey=ISU_ST@ENTRY_CHG@15152@01@18014@WTM, empNm=김수정, applStatNm=처리완료, applCd=ENTRY_CHG, applEmpNm=김수정, applStatusCd=99, applNm=근태사유서신청, applInSabun=18014, applStatusNm=처리완료, applYmd=2020.01.09, applSabun=18014, rowSeq=1, urlLink=view://W04View}, {applId=15151, applKey=ISU_ST@ENTRY_CHG@15151@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=ENTRY_CHG, applEmpNm=김수정, applStatusCd=21, applNm=근태사유서신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=2, urlLink=view://W04View}, {applId=15150, applKey=ISU_ST@ENTRY_CHG@15150@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=ENTRY_CHG, applEmpNm=김수정, applStatusCd=21, applNm=근태사유서신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=3, urlLink=view://W04View}, {applId=15148, applKey=ISU_ST@ENTRY_CHG@15148@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=ENTRY_CHG, applEmpNm=김수정, applStatusCd=21, applNm=근태사유서신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=4, urlLink=view://W04View}, {applId=934, applKey=ISU_ST@OT@934@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=OT, applEmpNm=김수정, applStatusCd=21, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=5, urlLink=view://W02View}, {applId=933, applKey=ISU_ST@OT@933@01@18014@WTM, empNm=김수정, applStatNm=처리완료, applCd=OT, applEmpNm=김수정, applStatusCd=99, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=처리완료, applYmd=2020.01.09, applSabun=18014, rowSeq=6, urlLink=view://W02View}, {applId=932, applKey=ISU_ST@OT@932@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=OT, applEmpNm=김수정, applStatusCd=21, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=7, urlLink=view://W02View}, {applId=931, applKey=ISU_ST@OT@931@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=OT, applEmpNm=김수정, applStatusCd=21, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=8, urlLink=view://W02View}, {applId=929, applKey=ISU_ST@OT@929@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=OT, applEmpNm=김수정, applStatusCd=21, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=9, urlLink=view://W02View}, {applId=927, applKey=ISU_ST@OT@927@01@18014@WTM, empNm=김수정, applStatNm=결재처리중, applCd=OT, applEmpNm=김수정, applStatusCd=21, applNm=연장/휴일근로신청, applInSabun=18014, applStatusNm=결재처리중, applYmd=2020.01.09, applSabun=18014, rowSeq=10, urlLink=view://W02View}]
	}
	
	public static Map<String,Object> getStatusMap(String icon, String title, String value, String total, String unit, String link){
		Map<String,Object>m = new HashMap<String,Object>();
		m.put("icon", icon);
		m.put("title", title);
		m.put("value", value);
		m.put("total",total);
		m.put("unit",unit);
		m.put("link",link);
		return m;
	}
	
	public static Map<String,Object> getStatusMap(String icon, String title, String value, String total, String unit){
		Map<String,Object>m = new HashMap<String,Object>();
		m.put("icon", icon);
		m.put("title", title);
		m.put("value", value);
		m.put("total",total);
		m.put("unit",unit);
		return m;
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
	
	public static String parseDEmpKey(String secret, String empKey, String key) { 
		String data = "";
		try {
			System.out.println("00000000000000000 0 " + empKey);
			empKey = URLDecoder.decode(empKey);
			System.out.println("00000000000000000 1 " + empKey);
			empKey = empKey.replace(" ", "+");
			System.out.println("00000000000000000 2 " + empKey);
			Aes256 aes = new Aes256(secret);
			String emp = aes.decrypt(empKey);
			System.out.println("00000000000000000 3 " + emp);
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
	
	public static String encEmpKey(String secret, String empKey) { 
		String data = "";
		try {
			Aes256 aes = new Aes256(secret);
			data = aes.encrypt(empKey);
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
		
		List<Map<String, Object>> lines1 = new ArrayList();
		List<Map<String, Object>> lines2 = new ArrayList();
		List<Map<String, Object>> apprTypes = new ArrayList();

		int lineCnt = 0;
		
		Map<String, Object> line1 = new HashMap();
		line1.put("name", "발신");
		line1.put("type", "");
		line1.put("useAdd", "false");
		line1.put("useStatusChange", "false");
		line1.put("apprTypes", apprTypes);
		line1.put("line", lines1);

		Map<String, Object> line2 = new HashMap();
		line2.put("name", "수신");
		line2.put("type", "");
		line2.put("useAdd", "false");
		line2.put("useStatusChange", "false");
		line2.put("apprTypes", apprTypes);
		line2.put("line", lines2);

		apprLines.add(line1);
		apprLines.add(line2);
		
		Map<String, Object> type1 = new HashMap();
		type1.put("applTypeCd", "1");
		type1.put("typeNm", "기안");
		
		Map<String, Object> type2 = new HashMap();
		type2.put("applTypeCd", "2");
		type2.put("typeNm", "발신결재");

		Map<String, Object> type3 = new HashMap();
		type3.put("applTypeCd", "3");
		type3.put("typeNm", "수신결재");

		apprTypes.add(type1);
		apprTypes.add(type2);
		apprTypes.add(type3);

		for(WtmApplLineVO applLineVO : applLineVOs) {
			if(("2".equals(applLineVO.getApprTypeCd()) ||"1".equals(applLineVO.getApprTypeCd())) && lineCnt < Integer.parseInt(applCode.getApplLevelCd())) {
				String apprTypeNm = applLineVO.getApprTypeCd().equals("1")?"기안":applLineVO.getApprTypeCd().equals("2")?"결재":"결재";
				
				Map<String, Object> temp = new HashMap();
				temp.put("type", "emp");
				temp.put("typeNm", apprTypeNm);
				temp.put("key", applCode.getEnterCd() + "@" + applLineVO.getSabun());
				temp.put("name", applLineVO.getEmpNm());
				lines1.add(temp);
			}
			
			if("2".equals(applLineVO.getApprTypeCd()))
				lineCnt++;
			
			if("3".equals(applLineVO.getApprTypeCd())) {
				Map<String, Object> temp = new HashMap();
				temp.put("type", "emp");
				temp.put("typeNm", "결재");
				temp.put("key", applCode.getEnterCd() + "@" + applLineVO.getSabun());
				temp.put("name", applLineVO.getEmpNm());
				lines2.add(temp);
			}
		}
		
		return apprLines;
	}
	
	public static List makeApprLines(List<WtmApplLineVO> applLineVOs) {
		List<Map<String, Object>> apprLines = new ArrayList();
		
//		List<Map<String, Object>> lines1 = new ArrayList();
		List<Map<String, Object>> lines2 = new ArrayList();
		List<Map<String, Object>> lines3 = new ArrayList();

		int lineCnt = 0;
		
//		Map<String, Object> line1 = new HashMap();
//		line1.put("name", "기안");
//		line1.put("type", "");
//		line1.put("useAdd", "false");
//		line1.put("useStatusChange", "false");
//		line1.put("apprTypes", null);
//		line1.put("line", lines1);

		Map<String, Object> line2 = new HashMap();
		line2.put("name", "발신결재");
		line2.put("type", "");
		line2.put("useAdd", "false");
		line2.put("useStatusChange", "false");
		line2.put("apprTypes", null);
		line2.put("line", lines2);
		
		Map<String, Object> line3 = new HashMap();
		line3.put("name", "수신결재");
		line3.put("type", "");
		line3.put("useAdd", "false");
		line3.put("useStatusChange", "false");
		line3.put("apprTypes", null);
		line3.put("line", lines3);

//		apprLines.add(line1);
		apprLines.add(line2);
		apprLines.add(line3);
		
		for(WtmApplLineVO applLineVO : applLineVOs) {
			Map<String, Object> temp = new HashMap();
			temp.put("type", "emp");
			temp.put("typeNm", applLineVO.getApprTypeNm());
			temp.put("name", applLineVO.getEmpNm());
			temp.put("statNm", applLineVO.getApprStatusNm());
			
			if(applLineVO.getApprTypeCd().equals("1"))
				lines2.add(temp);
			else if(applLineVO.getApprTypeCd().equals("2"))
				lines2.add(temp);
			else if(applLineVO.getApprTypeCd().equals("3"))
				lines3.add(temp);
		}
		return apprLines;
	}
}
