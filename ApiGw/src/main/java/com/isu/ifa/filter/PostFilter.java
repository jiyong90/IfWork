package com.isu.ifa.filter;

import javax.servlet.http.HttpServletResponse;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class PostFilter extends ZuulFilter {
    @Override
    public boolean shouldFilter() {
        return true;
    }
 
    @Override
    public String filterType() {
        return "post";
    }
 
    @Override
    public int filterOrder() {
        return 2;
    }
 
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        int statusCode = response.getStatus();
        System.out.println("statusCode="+statusCode);
        return null;
    }
} 