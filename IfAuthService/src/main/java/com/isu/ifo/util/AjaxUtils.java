package com.isu.ifo.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class AjaxUtils {

	public static boolean isAjax(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		String ajax = request.getHeader("X-Requested-With");
		
		
		return (accept.indexOf("json") > -1 && !StringUtils.isEmpty(ajax));
	}

	public static boolean isApi(HttpServletRequest request) {
		String accept = request.getHeader("accept");
        String ajax = request.getHeader("X-Requested-With");
        return (accept.indexOf("json") > -1 && StringUtils.isEmpty(ajax));
	}
	
	/**
	 * requestMap을 이용해서 URL을 만들어 반환한다.
	 * @param url
	 * @param requestMap
	 * @return
	 */
	public static String buildUrl(String url, Map<String, Object> requestMap) {

		if(requestMap == null)
			return url;
		
		Set<String>keys = requestMap.keySet();
		Iterator<String>itor = keys.iterator();
		String params = "";
		
		while(itor.hasNext()){
			if(params.equals("") && url.indexOf("?") == -1) {
				params = "?";
			} else { 
				params = params +"&";
			}
			
			String key = itor.next();
			String value = ""+(requestMap.get(key) == null ? "" :  requestMap.get(key));
			
			// URL로 전달될 파라미터들을 URL 인코딩한다.
			/*if(value != null){
				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}*/
			params = params+key+"="+value;
		}
		if(params != null)
			return url+params;
		else
			return url;
		
	}

}
