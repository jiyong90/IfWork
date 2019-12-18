package com.isu.ifa.filter;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
 

public class PreFilter extends ZuulFilter {
	

    private static Logger log = LoggerFactory.getLogger(PreFilter.class);
    
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        System.out.println("PreFilter: " + String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));  
        
        Enumeration<String> params = request.getParameterNames();  
        while(params.hasMoreElements()){  
            String paramName = params.nextElement();  
            System.out.println("Parameter Name: " + paramName + ", Value - "+request.getParameter(paramName));  
        }  
        //ctx.addZuulRequestHeader("Connection", "keep-alive");
        
        /*
         * 	나중에참고 로그아웃
         	HttpSession excistingSession = context.getRequest().getSession(false);
		    if(excistingSession != null){
		        excistingSession.invalidate();  
		        context.setSendZuulResponse(false);
		        context.addZuulResponseHeader("Location", "/abc/def/logout.do");
		        context.setResponseStatusCode(HttpServletResponse.SC_MOVED_TEMPORARILY);
		    }
         */
		return null;
    }
}