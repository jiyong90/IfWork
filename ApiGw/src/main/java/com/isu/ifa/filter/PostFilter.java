package com.isu.ifa.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class PostFilter extends ZuulFilter {
	
	private static final String REQUEST_PATH = "/ifa";
    private static final String TARGET_SERVICE = "second-service";
    private static final String HTTP_METHOD = "POST";

    //private final DiscoveryClient discoveryClient;
/*
    public PostFilter(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }
  */  
    @Override
    public boolean shouldFilter() {
        //int status = RequestContext.getCurrentContext().getResponseStatusCode();
        //return status >= 300 && status < 400;
    	
    	RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        return HTTP_METHOD.equalsIgnoreCase(method) && requestURI.startsWith(REQUEST_PATH);
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
        
/*
        RequestContext context = RequestContext.getCurrentContext();
        for(String k : discoveryClient.getServices()) {
        	System.out.println("discoveryClient service : " + k);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(TARGET_SERVICE);
        try {
            if (instances != null && instances.size() > 0) {
                context.setRouteHost(instances.get(0).getUri().toURL());
            } else {
                throw new IllegalStateException("Target service instance not found!");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't get service URL!", e);
        }
        */
        return null;
        
            /*
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();
        Optional<Pair<String, String>> locationHeader = ctx.getZuulResponseHeaders()
                    .stream()
                    .filter(stringStringPair -> "Location".equals(stringStringPair.first()))
                    .findFirst();
        
        	System.out.println("locationHeader : " + locationHeader);
            if (locationHeader.isPresent()) {
                String oldLocation = locationHeader.get().second();
            	System.out.println("oldLocation : " + oldLocation);
                String newLocation = "";
                locationHeader.get().setSecond(oldLocation);
            }
            */
        //return null;
    }
} 