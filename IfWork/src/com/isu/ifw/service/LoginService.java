package com.isu.ifw.service;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmToken;
import com.isu.ifw.mapper.LoginMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmTokenRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.Login;
import com.isu.option.service.TenantConfigManagerService;

/**
 * 로그인 서비스
 *
 * @author ParkMoohun
 *
 */
@Service("LoginService")
public class LoginService{
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	LoginMapper loginMapper;
	
	@Resource
	WtmTokenRepository tokenRepository;
	
	@Autowired
	TenantConfigManagerService tcms;
	
	@Resource
	WtmEmpHisRepository empHisRepository;
	
	public Map<String, String> loginTryCnt(String loginEnterCd, String loginUserId, String loginPassword) throws Exception{
		return loginMapper.loginTryCnt(loginEnterCd, loginUserId, loginPassword);
	}
	
	public Login loginUser(String ssnLocaleCd, String localeCd, String baseLang,String loginEnterCd, String loginUserId, String loginPassword, String ssnSso) throws Exception{
		return loginMapper.loginUser(ssnLocaleCd, localeCd, baseLang, loginEnterCd, loginUserId, loginPassword, ssnSso);
	}
	
	public WtmToken getAccessToken(String accessToken) {
		List<WtmToken> token = tokenRepository.findByAccessToken(accessToken);
		
		if(token != null && token.size() > 0) {
			return token.get(0);
		}
		
		return null;
	}
	
	public void removeTokenCookie(ServletRequest request, ServletResponse response) {
		Cookie[] cookies = ((HttpServletRequest)request).getCookies();

		if(cookies != null){
			for(int i=0; i< cookies.length; i++){
				cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
				((HttpServletResponse)response).addCookie(cookies[i]); 
			}
		}
	}
	
	public WtmToken refreshAccessToken(ServletRequest request, ServletResponse response, WtmToken token, String url, String tokenName) {
		//WtmToken newToken = null;
		
		try {
			Map<String, Object> responseMap = new HashMap();
			ObjectMapper mapper = new ObjectMapper();
			RestTemplate restTemplate = new RestTemplate();

			Map<String, Object> map = new HashMap<String, Object>();
			ResponseEntity<String> responseEntity = null;
			
			
			URI uri = UriComponentsBuilder.fromUriString(url)
			        .queryParam("cmd", "tokenRefresh")
			        .queryParam("accessToken", token.getAccessToken())
			        .queryParam("refreshToken", token.getRefreshToken())
			        .build().toUri();
	        
			responseEntity = restTemplate.postForEntity(uri, "", String.class);
			if(responseEntity.getBody() == null) {
				System.out.println("body is null " + uri + " " + responseEntity.getStatusCodeValue());
				return null;
			}
			responseMap = mapper.readValue(responseEntity.getBody(), new HashMap<>().getClass());
		
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> responseData = null;
				String responseBody = responseEntity.getBody();

				responseData = mapper.readValue(responseBody, new HashMap().getClass());
				if(responseData == null || responseData.equals("")) {
					System.out.println("xxxx responseData is null");
					return null;
				} else if(!responseData.get("message").equals("")) {
					System.out.println("xxxx " + responseData.get("message"));
					return null;
				}
				//newToken = new WtmToken();
				//newToken.setId(token.getId());
				token.setAccessToken(responseData.get("accessToken").toString());
			
				String expiresAt = responseData.get("expiresAt").toString();
				Calendar cal = Calendar.getInstance();
			    cal.add( Calendar.SECOND, Integer.valueOf(expiresAt)); 
	 	        Date date = cal.getTime(); 
				
	 	       token.setExpiresAt(date);
//				newToken.setRefreshToken(responseData.get("refreshToken").toString());
	 	       token.setUpdateId(token.getUserId().toString());
				
	 	      token = tokenRepository.save(token);
			} else if (responseEntity.getStatusCode() != HttpStatus.UNAUTHORIZED) {
				System.out.println("xxxx hr session 만료");
				removeTokenCookie(request, response);
			} else {
				System.out.println("xxxx" + responseEntity.getStatusCode() + " : " + responseEntity.getBody());
				removeTokenCookie(request, response);
			} 

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return token;
	}
	
	public void creatAccessToken(HttpServletRequest request, HttpServletResponse response, WtmToken token) {
		tokenRepository.deleteByTenantIdAndEnterCdAndSabun(token.getTenantId(), token.getEnterCd(), token.getSabun());

		//WtmEmpHis emp = empHisRepository.findByTenantIdAndEnterCdAndSabun(token.getTenantId(), token.getEnterCd(), token.getSabun());
		WtmEmpHis emp = empHisRepository.findByTenantIdAndEnterCdAndSabunAndYmd(token.getTenantId(), token.getEnterCd(), token.getSabun(), WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
		String tokenName = getHrTokenName(token.getTenantId());
		logger.debug("tokenName  " + tokenName);
		if(emp != null) {
			logger.debug("empHis  " + emp.getEmpHisId().toString());
			
			token.setUserId(emp.getEmpHisId().toString());
			token.setUpdateId(emp.getEmpHisId().toString());
			//기존 토큰 다 삭제하고 새로 등록(기존에 다른 곳에서 로그인한 상황이면 그쪽은 튕김)
			tokenRepository.save(token);

//			Cookie cookie = null;
//			cookie = new Cookie(PARAM_NAME_USER_TOKEN, token.getAccessToken());
//			cookie.setPath("/");
//			response.addCookie(cookie);
		} else {
			logger.debug("emp his에 없는 사원정보 " + token.getSabun());
			removeTokenCookie(request, response);
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			//hr을 로그아웃시킬 필요는 없겠지
		}
		//emp his에 업으면 안됨...
	}
	
	public String getHrTokenUrl(Long tenantId) {
		return tcms.getConfigValue(tenantId, "HR.TOKEN_URL", true, "");	
	}

	public String getHrInfoUrl(Long tenantId) {
		return tcms.getConfigValue(tenantId, "HR.INFO_URL", true, "");	
	}

	public String getHrTokenName(Long tenantId) {
		return tcms.getConfigValue(tenantId, "HR.TOKEN_NAME", true, "");	
	}
	
	public void deleteAccessToken(ServletRequest request, ServletResponse response, WtmToken token) {
		tokenRepository.deleteByTenantIdAndEnterCdAndSabun(token.getTenantId(), token.getEnterCd(), token.getSabun());
		removeTokenCookie(request, response);
	}
}