package com.isu.ifw.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity(name = "OauthClientToken")
@Table(name="oauth_client_token")
public class OauthClientToken {
	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="authentication_id")
	private String authenticationId;

	@Column(name="token_id")
	private String tokenId;

	@Lob
	@Column(name="token")
	private String token;

	@Column(name="user_name")
	private String userName;
	
	@Column(name="client_id")
	private String clientId;

	public OauthClientToken() {
		
	}

	public String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	


}