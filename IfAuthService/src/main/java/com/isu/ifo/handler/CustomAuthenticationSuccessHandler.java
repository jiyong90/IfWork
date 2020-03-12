package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Reducing;

/**
 * 로그인 성공시 행동을 재정의할 클래스(추상 클래스가 아닌 인터페이스를 구현해도 된다.)
 * Or Interface - AuthenticationSuccessHandler
 * @author
 *
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements ExceptionProcessor{
    private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private String targetUrlParameter;
	private boolean useReferer;
	
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	System.out.println("CustomAuthenticationSuccessHandler :: onAuthenticationSuccess ");
    	
        /*
         * 쿠키에 인증 토큰을 넣어준다.
         */
        //this.setDefaultTargetUrl("http://10.30.30.188/ifw/api/callback");
        //this.setAlwaysUseDefaultTargetUrl(false);
//        
//        OAuth2AccessToken oauth2Token = authTokenServices.createAccessToken(convertAuthentication(authentication)); //Automatically checks validity
//        String redirectUrl = new StringBuilder(REDIRECT_PATH_BASE)
//            .append("?").append(FIELD_TOKEN).append("=")
//            .append(encode(oauth2Token.getValue()))
//            .append("&").append(FIELD_EXPIRATION_SECS).append("=")
//            .append(oauth2Token.getExpiresIn())
//            .toString();
//        log.debug("Sending redirection to " + redirectUrl);
//        response.sendRedirect(redirectUrl);
//        
    	//this.setDefaultTargetUrl("http://10.30.30.188/ifw/api/callback");
    	System.out.println("authentication : " + authentication.toString());
        //super.onAuthenticationSuccess(request, response, authentication);
    	
    	clearAuthenticationAttributes(request);
    	
    	int intRedirectStrategy = decideRedirectStrategy(request, response);
    	//logger.debug("intRedirectStrategy : " + intRedirectStrategy);
    	System.out.println("intRedirectStrategy : " + intRedirectStrategy);
    	switch (intRedirectStrategy) {
		case 1:
			useTargetUrl(request, response);
			break;
		case 2:
			useSessionUrl(request, response);
			break;
		case 3:
			useRefererUrl(request, response);
			break;
		default:
			useDefaultUrl(request, response);
		}
    }
 
    @Override
    public void makeExceptionResponse(HttpServletRequest request, HttpServletResponse response,
            Exception exception) {
    }
    
    private void useTargetUrl(HttpServletRequest request, HttpServletResponse response) throws IOException{

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if(savedRequest != null) {
			requestCache.removeRequest(request, response);
		}
		String targetUrl = request.getParameter(targetUrlParameter);
		redirectStrategy.sendRedirect(request, response, targetUrl);
    }
    
    private void useSessionUrl(HttpServletRequest request, HttpServletResponse response) throws IOException{

		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String targetUrl = savedRequest.getRedirectUrl(); 
		redirectStrategy.sendRedirect(request, response, targetUrl);
    }
    
    private void useRefererUrl(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String targetUrl = request.getHeader("REFERER"); 
		redirectStrategy.sendRedirect(request, response, targetUrl);
    }
    
    private void useDefaultUrl(HttpServletRequest request, HttpServletResponse response) throws IOException{
		redirectStrategy.sendRedirect(request, response, this.getDefaultTargetUrl());
    }
    /**
     * 인증 성공후 어떤 URL로 redirect 할지를 경정한다.
     * 판단 기준은 targetUrlParameter 값을 읽은 URL이 존재할 경우 그것을 1순위
     * 1순위 URL이 없을 경우 Sptring Security가 세션에 저장한 URL을 2순위
     * 2순위 URL이 없을 경우 Request의 REFERER를 사용하고 그 REFERER URL이 존재할 경우 그 URL을 3순위
     * 3순위 URL이 없을 경우 Default URL을 4순위로 한다.
     * @param request
     * @param response
     * @return 
     */
    private int decideRedirectStrategy(HttpServletRequest request, HttpServletResponse response) {
    	int result = 0;
    	SavedRequest savedRequest = requestCache.getRequest(request, response);
    	if(!"".equals(targetUrlParameter)) {
    		String targetUrl = request.getParameter(targetUrlParameter);
    		if(StringUtils.hasText(targetUrl)) {
    			result = 1;
    		}else {
    			if(savedRequest != null) {
    				result = 2;
    			}else {
    				String refererUrl = request.getHeader("REFERER");
    				if(useReferer && StringUtils.hasText(refererUrl)) {
    					result = 3;
    				}else {
    					result = 0;
    				}
    			}
    		}
    		return result;
    	}
    	
    	if(savedRequest != null) {
    		result = 2;
    		return result;
    	}
    	
    	String refererUrl = request.getHeader("REFERER");
		if(useReferer && StringUtils.hasText(refererUrl)) {
			result = 3;
		}else {
			result = 0;
		}
		
    	return result;
    }
    
}



