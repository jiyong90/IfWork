package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * 로그인 성공시 행동을 재정의할 클래스(추상 클래스가 아닌 인터페이스를 구현해도 된다.)
 * Or Interface - AuthenticationSuccessHandler
 * @author
 *
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements ExceptionProcessor{
    
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
        super.onAuthenticationSuccess(request, response, authentication);
    }
 
    @Override
    public void makeExceptionResponse(HttpServletRequest request, HttpServletResponse response,
            Exception exception) {
    }
    
}



