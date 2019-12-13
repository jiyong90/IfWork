package com.isu.ifo.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;

import com.isu.ifo.entity.ClientDetailsImpl;
import com.isu.ifo.service.security.ClientDetailsServiceImpl;
import com.isu.ifo.util.AjaxUtils;
import com.isu.ifo.util.JsonUtils;

/*
* Spring Security에서 로그인 페이지로 리다이렉트 시켜줄 Entrypoint객체이다.
* 만약 권한이 없는 사용자가 페이지에 접근하였을 때, 해당 객체가 로그인 페이지로
* 리다이렉트 시켜주는 역할을 담당한다.
*/
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	//private static final Logger log = getLogger(CustomAuthenticationEntryPoint.class);
	
	private PortMapper portMapper = new PortMapperImpl();
    private PortResolver portResolver = new PortResolverImpl();
    private String loginFormUrl;
    private boolean forceHttps = false;
    private boolean useForward = false;
    
    protected RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired ClientDetailsServiceImpl clientService;
    /*
    @Override
    public void afterPropertiesSet() throws Exception {
        isTrue(StringUtils.hasText(loginFormUrl) && UrlUtils.isValidRedirectUrl(loginFormUrl),
                "loginFormUrl must be specified and must be a valid redirect URL");
        if (useForward && isAbsoluteUrl(loginFormUrl)) {
            throw new IllegalArgumentException("useForward must be false if using an absolute loginFormURL");
        }
        notNull(portMapper, "portMapper must be specified");
        notNull(portResolver, "portResolver must be specified");
    }
*/
    /**
     * Allows subclasses to modify the login form URL that should be applicable for a given request.
     *
     * @param request   the request
     * @param response  the response
     * @param exception the exception
     * @return the URL (cannot be null or empty; defaults to {@link #getLoginFormUrl()})
     */
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) { // NOSONAR : used
    	if(request.getParameter("client_id") != null) {
        	String clientId = request.getParameter("client_id");
        	ClientDetailsImpl details= clientService.findByClientId(clientId);
        	this.loginFormUrl = details.getLoginPageUrl();
        }
        return getLoginFormUrl();
    }

    /**
     * Performs the redirect (or forward) to the login form URL.
     */
    @SuppressWarnings("deprecation")
	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
    	System.out.println("==================CustomAuthenticationEntryPoint==================");
        String redirectUrl = null;

        if (useForward) {
            if (forceHttps && "http".equals(request.getScheme())) {
                // First redirect the current request to HTTPS.
                // When that request is received, the forward to the login page will be used.
                redirectUrl = buildHttpsRedirectUrlForRequest(request);
            }

            if (redirectUrl == null) {
                String loginForm = determineUrlToUseForThisRequest(request, response, authException);
                //log.debug("Server side forward to: {}", loginForm);

                RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);
                dispatcher.forward(request, response);
                return;
            }
        } else {
            // redirect to login page. Use https if forceHttps true
            redirectUrl = buildRedirectUrlToLoginPage(request, response, authException);
        }

        Enumeration<String> p = request.getParameterNames();
    	System.out.println("buildHttpsRedirectUrlForRequest param start");
    	Map<String, Object> queryMap = new HashMap<>();
        while(p.hasMoreElements()) {
        	String key = p.nextElement();
        	System.out.println(key + " : " + request.getParameter(key));
        	queryMap.put(key, request.getParameter(key));
        }
        System.out.println("buildHttpsRedirectUrlForRequest param end");
        redirectUrl = AjaxUtils.buildUrl(redirectUrl, queryMap);
        System.out.println("redirectUrl ::::: " + redirectUrl);
        if(AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("redirect", redirectUrl);
			
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
	        response.getWriter().close();
	        
        }else {
	        //log.info("Send Redirect");
	        redirectStrategy.sendRedirect(request, response, redirectUrl);
        }
    }

    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) {

        
        
        Enumeration<String> k = request.getHeaderNames();
        System.out.println("buildHttpsRedirectUrlForRequest header start");
        while(k.hasMoreElements()) {
        	String key = k.nextElement();
        	System.out.println(key + " : " + request.getHeader(key));
        }
        System.out.println("buildHttpsRedirectUrlForRequest header end");
        
    	
        if(request.getParameter("client_id") != null) {
        	String clientId = request.getParameter("client_id");
        	//this.loginFormUrl = "http://10.30.30.56/ifw/login/isu";
        	//ClientDetails details = clientService.loadClientByClientId(clientId);
        	ClientDetailsImpl details= clientService.findByClientId(clientId);
        	this.loginFormUrl = details.getLoginPageUrl();
//        	Set<String> redirectUri = details.getRegisteredRedirectUri();
//        	Iterator<String> it = redirectUri.iterator();
//        	while(it.hasNext()) {
//        		this.loginFormUrl = it.next();
//        	}
        	//System.out.println("this.loginFormUrl : " + this.loginFormUrl);
        	/*
        	if(clientId.equals("zhege") || clientId.equals("isu")) {
        		this.loginFormUrl = "http://10.30.30.56/ifw/login/isu";
        	}else {
        		System.out.println("aslkjdlkasjdklsajdlkjsakldjaskldjklsajdklasjdklasjdkljaskldjaskldjklsajdklasjdklsajdkljsakldjaskljdklasjdllkasjdklasjkldjkalsdjklas");
        		this.loginFormUrl = "http://10.30.30.188/ifw/login/isu";
        	}
        	*/
        }
        
        

        String loginForm = determineUrlToUseForThisRequest(request, response, authException);
        
        if (UrlUtils.isAbsoluteUrl(loginForm)) {
            return loginForm;
        }

        int serverPort = portResolver.getServerPort(request);
        String scheme = request.getScheme();

        RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();

        urlBuilder.setScheme(scheme);
        urlBuilder.setServerName(request.getServerName());
        //urlBuilder.setPort(serverPort);
        //urlBuilder.setContextPath(request.getContextPath());
        urlBuilder.setPathInfo(loginForm);

        
        if (forceHttps && "http".equals(scheme)) {
            Integer httpsPort = portMapper.lookupHttpsPort(Integer.valueOf(serverPort));

            if (httpsPort != null) {
                // Overwrite scheme and port in the redirect URL
                urlBuilder.setScheme("https");
                urlBuilder.setPort(httpsPort.intValue());
            } else {
                //log.warn("Unable to redirect to HTTPS as no port mapping found for HTTP port {}", serverPort);
            }
        }
        System.out.println("buildRedirectUrlToLoginPage .getUrl() : " + urlBuilder.getUrl());
         
        
        return urlBuilder.getUrl();
    }

    /**
     * Builds a URL to redirect the supplied request to HTTPS. Used to redirect the current request
     * to HTTPS, before doing a forward to the login page.
     */
    protected String buildHttpsRedirectUrlForRequest(HttpServletRequest request)
            throws IOException, ServletException {

        int serverPort = portResolver.getServerPort(request);
        Integer httpsPort = portMapper.lookupHttpsPort(Integer.valueOf(serverPort));

        if (httpsPort != null) {
            RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
            urlBuilder.setScheme("https");
            urlBuilder.setServerName(request.getServerName());
            urlBuilder.setPort(httpsPort.intValue());
            urlBuilder.setContextPath(request.getContextPath());
            urlBuilder.setServletPath(request.getServletPath());
            urlBuilder.setPathInfo(request.getPathInfo());
            urlBuilder.setQuery(request.getQueryString());
            System.out.println("buildHttpsRedirectUrlForRequest .getUrl() : " + urlBuilder.getUrl());
            
            return urlBuilder.getUrl();
        }

        // Fall through to server-side forward with warning message
        //log.warn("Unable to redirect to HTTPS as no port mapping found for HTTP port {}", serverPort);

        return null;
    }

    /**
     * Set to true to force login form access to be via https. If this value is true (the default is false),
     * and the incoming request for the protected resource which triggered the interceptor was not already
     * <code>https</code>, then the client will first be redirected to an https URL, even if <tt>serverSideRedirect</tt>
     * is set to <tt>true</tt>.
     */
    public void setForceHttps(boolean forceHttps) {
        this.forceHttps = forceHttps;
    }

    protected boolean isForceHttps() {
        return forceHttps;
    }

    /**
     * The URL where the <code>UsernamePasswordAuthenticationFilter</code> login
     * page can be found. Should either be relative to the web-app context path
     * (include a leading {@code /}) or an absolute URL.
     */
    public void setLoginFormUrl(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    public String getLoginFormUrl() {
        return loginFormUrl;
    }

    public void setPortMapper(PortMapper portMapper) {
        this.portMapper = portMapper;
    }

    protected PortMapper getPortMapper() {
        return portMapper;
    }

    public void setPortResolver(PortResolver portResolver) {
        this.portResolver = portResolver;
    }

    protected PortResolver getPortResolver() {
        return portResolver;
    }

    /**
     * Tells if we are to do a forward to the {@code loginFormUrl} using the {@code RequestDispatcher},
     * instead of a 302 redirect.
     *
     * @param useForward true if a forward to the login page should be used. Must be false (the default) if
     * {@code loginFormUrl} is set to an absolute value.
     */
    public void setUseForward(boolean useForward) {
        this.useForward = useForward;
    }

    protected boolean isUseForward() {
        return useForward;
    }

    public org.springframework.security.web.RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(org.springframework.security.web.RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
