package com.isu.ifo.dto;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.isu.ifo.entity.ClientType;

/*
* OAuth2.0에서 클라이언트 인증에 사용되는 객체이다.
* ClientDetailsService에서 load하면 반환하는 객체이다.
* Spring Security를 다루어보았다면, UserDetails와 동일한 역할을 하는 객체이다.
*/
public class ClientDetailsImpl extends BaseClientDetails {
 
    private static final long serialVersionUID = -8263549600098155096L;
    
    private ClientType clientType;
    
    private String loginUrl;
 
    public ClientType getClientType() {
        return clientType;
    }
 
    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
    
    
    
}


