package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.isu.ifo.entity.ClientDetailsImpl;
import com.isu.ifo.service.security.ClientDetailsServiceImpl;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler{	

	@Autowired ClientDetailsServiceImpl clientDetailsService;
	
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                             Authentication authentication) throws IOException, ServletException {

    	String clientIdPath = "/logout";
    	
    	System.out.println("onLogoutSuccess :::: ");
        
        SecurityContextHolder.clearContext();
        HttpSession session= request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
    	if(session != null) {
    		session.invalidate();
    	}
    	String token = "";
    	for(Cookie cookie : request.getCookies()) {
    		System.out.println("cookie.getName() :: " + cookie.getName()); 
    		System.out.println("cookie.getValue() :: " + cookie.getValue());
    		if(cookie.getName().equalsIgnoreCase("Authorization")) {
    			token = cookie.getValue();
    		}
    		cookie.setValue(null);
    		cookie.setMaxAge(0);
    		cookie.setPath("/");
    		response.addCookie(cookie);
    	}
    	
    	String requestUri = ((HttpServletRequest)request).getRequestURI().substring(((HttpServletRequest)request).getContextPath().length());
    	String clientId = "";
		// tenantKeyPath가 null이 아니라면, 이 값을 이용해서 tenant 서비스를 찾는다.
		// 설정 정보가 tenant id 를 기준으로 설정되기 때문에, 싫더라도 tenent id는 있어야 한다.
		if(requestUri != null){
			String truncatedString = null;
			// 요청 uri 에서 tenantPath 를 뺀 나머지 문자열을 찾음
			int pathIndex = requestUri.indexOf(clientIdPath);
			  
			if(pathIndex>=0){
				truncatedString = requestUri.substring(clientIdPath.length());
				if(truncatedString.length() > 0){
					// tenantKey를 찾는다
					// '/'으로 시작하는 상황이면 /  를 잘라낸다.
					if(truncatedString.charAt(0) == '/')
						truncatedString = truncatedString.substring(1);
					
					// 0번 인덱스부터 가장 처음 만나는 '/'까지를 잘라낸다.
					if(truncatedString.length() > 0 && truncatedString.indexOf("/") > 0){
						truncatedString = truncatedString.substring(0, truncatedString.indexOf("/"));
					}
				}
				
				System.out.println("requested uri:"+requestUri);
				System.out.println("truncatedString:"+truncatedString);
				
				if(truncatedString.length() > 0)
					clientId = truncatedString; // 테넌트 키 식별. 이 값으로 테넌트 id를 획득한다.
			}
		}
			

        //없으면 유효하지 않다. 
        if(clientId == null || clientId.equals(""))
        	throw new UnauthorizedClientException("Client ID is required after [/login] url.");
        
        ClientDetailsImpl clientDetails = clientDetailsService.findByClientId(clientId);
        
        String callbackUrl = clientDetails.getLogoutCallbackUrl();

        response.sendRedirect(callbackUrl);

    	
        	
    }
}