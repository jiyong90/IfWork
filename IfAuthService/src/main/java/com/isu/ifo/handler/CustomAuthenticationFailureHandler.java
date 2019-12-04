package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * 로그인 실패시 행동을 재정의할 클래스(추상 클래스가 아닌 인터페이스를 구현해도 된다.)
 * Or Interface - AuthenticationFailureHandler
 * @author yun-yeoseong
 *
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler implements ExceptionProcessor{
	//@Resource(name="userSer")
    //private UserService userSer;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
    	System.out.println("CustomAuthenticationFailureHandler ::: ");
    	//String username = request.getParameter(loginidname);
        //String password = request.getParameter(loginpwdname);
        String errormsg = exception.getMessage();
        System.out.println("errormsg : " + errormsg);

        super.onAuthenticationFailure(request, response, exception);
        /*
        String username = request.getParameter("");
        String password = request.getParameter("");
        String errormsg = null;
        
        if(exception instanceof BadCredentialsException) {
        	loginFailureCount(username);
            errormsg = MessageUtils.getMessage("error.BadCredentials");
        } else if(exception instanceof InternalAuthenticationServiceException) {
            errormsg = MessageUtils.getMessage("error.BadCredentials");
        } else if(exception instanceof DisabledException) {
            errormsg = MessageUtils.getMessage("error.Disaled");
        } else if(exception instanceof CredentialsExpiredException) {
            errormsg = MessageUtils.getMessage("error.CredentialsExpired");
        }
        
        request.setAttribute(loginidname, username);
        request.setAttribute(loginpwdname, password);
        request.setAttribute(errormsgname, errormsg);
 
        request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
		*/

    }
 
    @Override
    public void makeExceptionResponse(HttpServletRequest request, HttpServletResponse response,
            Exception exception) {
    }
    
    protected void loginFailureCount(String username) {
        /*
		userSer.countFailure(username);
        int cnt = userSer.checkFailureCount(username);
        if(cnt==3) {
            userSer.disabledUsername(username);
        }
        */
    }


	
}
 