package com.isu.ifo.util;

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

}
