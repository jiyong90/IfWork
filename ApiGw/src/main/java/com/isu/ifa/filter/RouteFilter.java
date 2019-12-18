package com.isu.ifa.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class RouteFilter extends ZuulFilter {  
    private static Logger log = LoggerFactory.getLogger(RouteFilter.class);  
  
    @Override  
    public String filterType() {  
        return "route";  
    }  
  
    @Override  
    public int filterOrder() {  
        return 1;  
    }  
  
    @Override  
    public boolean shouldFilter() {
    	return true;
    	/*
        RequestContext ctx = RequestContext.getCurrentContext();
        if ( ctx.getRequest().getRequestURI() == null || 
                ! ctx.getRequest().getRequestURI().startsWith("/ifw/certificate"))
        	return false;
        return ctx.getRouteHost() != null && ctx.sendZuulResponse();
        */
    }  
  
    @Override  
    public Object run() {  
        RequestContext ctx = RequestContext.getCurrentContext();  
        HttpServletRequest request = ctx.getRequest();  
 
        System.out.println("RouteFilter: " + String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));  
  
        return null;  
    }  
}