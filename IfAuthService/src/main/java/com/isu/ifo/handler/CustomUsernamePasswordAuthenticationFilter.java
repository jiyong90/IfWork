package com.isu.ifo.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifo.entity.ClientDetailsImpl;
import com.isu.ifo.entity.UserDetailsImpl;
import com.isu.ifo.entity.UserRole;
import com.isu.ifo.service.security.ClientDetailsServiceImpl;
import com.isu.ifo.service.security.UserDetailServiceImpl;

/**
 * Form 로그인 인증을 담당하는 Filter이다.
 * @author yun-yeoseong
 *
 */
//@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	final static String clientIdPath = "/login";
	
	@Autowired 
    private PasswordEncoder encoder;
	
	@Autowired RestTemplate restTemplate;
	
	
	@Autowired UserDetailServiceImpl userDetailsService;
	@Autowired ClientDetailsServiceImpl clientDetailsService;
	//@Autowired UserDetailsServiceImpl userDetailsService;
	//@Autowired UserServiceImpl userService;
	
    private boolean postOnly = true;
    
    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
    
    /*
     * 해당 필터에서 인증 프로세스 이전에 요청에서 사용자 정보를 가져와서
     * Authentication 객체를 인증 프로세스 객체에게 전달하는 역할
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        //log.info("JwtAuthentication.attemptAuthentication ::::");
        System.out.println("attemptAuthentication ::::");
        ObjectMapper mapper = new ObjectMapper();
        
       
        //request에서 client id 정보를 가지고 오자
        String clientId = getClientIdFromRequest(request);
        //없으면 유효하지 않다. 
        if(clientId == null || clientId.equals(""))
        	throw new UnauthorizedClientException("Client ID is required after [/login] url.");
        
        ClientDetailsImpl clientDetails = clientDetailsService.findByClientId(clientId);
        
        Enumeration<String> k = request.getHeaderNames();
        System.out.println("buildHttpsRedirectUrlForRequest header start");
        while(k.hasMoreElements()) {
        	String key = k.nextElement();
        	System.out.println(key + " : " + request.getHeader(key));
        }
        System.out.println("buildHttpsRedirectUrlForRequest header end");
        
        Map<String, Object> queryMap = new HashMap<>();
        Enumeration<String> p = request.getParameterNames();
        System.out.println("buildHttpsRedirectUrlForRequest param start");
        while(p.hasMoreElements()) {
        	String key = p.nextElement();
        	queryMap.put(key,request.getParameter(key));
        }
        System.out.println("buildHttpsRedirectUrlForRequest param end");
        
        String loginPageUrl = clientDetails.getLoginPageUrl();
        String loginMethod = clientDetails.getLoginEndpointMethod();
        String loginUrl = clientDetails.getLoginEndpointUrl();
        
        Map<String, Object> resultMap = null;
        
        boolean isValid =  true;
        String errorMsg = "";
        if(loginMethod.equalsIgnoreCase("GET")) {
        	loginUrl = buildUrl(loginUrl, queryMap);
        	
        	resultMap = restTemplate.getForEntity(loginUrl, new HashMap<String, Object>().getClass()).getBody();
        }else {
        	
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            //headers.add("Authorization", "Basic " + encodedCredentials);
            //MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            //params.add("code", code);
            //params.add("grant_type", "authorization_code");
            //params.add("redirect_uri", "http://localhost:8081/oauth2/callback"); 
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(queryMap, headers);
        	System.out.println("loginUrl : " + loginUrl);
        	try {
            	resultMap = restTemplate.postForEntity(loginUrl, req, new HashMap().getClass()).getBody();
        	}catch (Exception e) {
				e.printStackTrace();
				isValid =  false;
				errorMsg = e.getMessage();
			}
        }
        
        if(resultMap == null)
        	isValid =  false;
        if(resultMap != null && resultMap.containsKey("status")) {
        	if(resultMap.get("status").equals("FAIL")) {
        		isValid =  false;
        		if(resultMap.containsKey("message") && resultMap.get("message") != null) {
        			errorMsg = resultMap.get("message").toString();
        		}
        	}
        }else { 
        	isValid =  false;
        }
        
        if(!isValid) {
        	try {
				response.sendRedirect(loginPageUrl + "#err=" + URLEncoder.encode(String.format("%s", errorMsg)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        /*
        String username = request.getParameter("username");
        String enterCd = request.getParameter("enterCd");
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        
        String urls = "http://203.231.40.60/MobileLogin.do?loginEnterCd="+enterCd+"&loginUserId="+loginId+"&loginPassword="+password+"&localeCd=ko_KR";
        */
        

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
			System.out.println("resultMap : " + mapper.writeValueAsString(resultMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        try {
        	UserDetails details = userDetailsService.loadUserByUsername(username);
        }catch(UsernameNotFoundException ne) {
        	//System.out.println(encoder.encode(password));
        	UserDetailsImpl newDetails = new UserDetailsImpl();
    		newDetails.setUsername(username);
    		newDetails.setPassword(encoder.encode(password));
    		//newDetails.setPassword(password);
    		newDetails.setRoles(UserRole.ROLE_USER);
    		
    		userDetailsService.save(newDetails);
        }
    	
    	
        /*
         * POST로 넘어왔는지 체크
         */
        /*
        if(postOnly && !request.getMethod().equals("POST")) {
        	System.out.println("aslkjaslkjdklasjkldjlkasjlkdasjkld");
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
         	*/
        /*
        System.out.println("obtainUsername : "); 
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        
        if(StringUtils.isEmpty(username)) {
            username = "";
        }
        if(StringUtils.isEmpty(password)) {
            password = "";
        }
        
        username = username.trim();
        System.out.println("username : " + username);
        System.out.println("password : " + password);
        */
        

        

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, authorities);
        System.out.println("authRequest.getName() : " + authRequest.getName());
        System.out.println(this.getFilterName());
        setDetails(request, authRequest);
        System.out.println(authRequest.getAuthorities());
        authRequest.setDetails(resultMap);
        
        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
    protected String getClientIdFromRequest(HttpServletRequest request) {
    	 //로그인 요청 시 clientId에 대한 정보가 없다. /login/{tsId} 로판단해야한다. tsId = clientId 이다.
    	String requestUri = ((HttpServletRequest)request).getRequestURI().substring(((HttpServletRequest)request).getContextPath().length());
    			
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
					return truncatedString; // 테넌트 키 식별. 이 값으로 테넌트 id를 획득한다.
			}
		}
		return null;
    }
    /**
	 * requestMap을 이용해서 URL을 만들어 반환한다.
	 * @param url
	 * @param requestMap
	 * @return
	 */
	private String buildUrl(String url, Map<String, Object> requestMap) {

		if(requestMap == null)
			return url;
		
		Set<String>keys = requestMap.keySet();
		Iterator<String>itor = keys.iterator();
		String params = "";
		
		while(itor.hasNext()){
			if(params.equals("") && url.indexOf("?") == -1) {
				params = "?";
			} else { 
				params = params +"&";
			}
			
			String key = itor.next();
			String value = ""+(requestMap.get(key) == null ? "" :  requestMap.get(key));
			
			// URL로 전달될 파라미터들을 URL 인코딩한다.
			/*if(value != null){
				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}*/
			params = params+key+"="+value;
		}
		if(params != null)
			return url+params;
		else
			return url;
		
	}



}
 
 