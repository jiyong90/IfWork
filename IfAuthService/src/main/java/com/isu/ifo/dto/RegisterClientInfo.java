package com.isu.ifo.dto;

public class RegisterClientInfo {
	private String name;
    private String redirectUri;
    private String clientType;
    private String clientId;
    private String secret;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRedirectUri() {
        return redirectUri;
    }
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    public String getClientType() {
        return clientType;
    }
    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
    
}
