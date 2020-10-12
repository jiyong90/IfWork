package com.isu.ifo.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import lombok.ToString;

@ToString
@Entity(name="oauth_client_details")
public class ClientDetailsImpl implements ClientDetails{

	public static enum ClientType {
		PUBLIC,CONFIDENTIAL
	}

	@Id
	@Column(name="client_id")
	String clientId;
	
	@Transient
	@Column(name="resource_ids")
	Set<String> resourceIds;
	@Column(name="secret_required")
	boolean secretRequired;
	@Column(name="client_secret")
	String clientSecret;

	@Transient
	boolean scoped;
	
	@Transient
	Set<String> scope;
	
	@Transient
	@Column(name="authorized_grant_types")
	Set<String> authorizedGrantTypes;
	
	@Transient
	@Column(name="registered_redirect_uri")
	Set<String> registeredRedirectUri;
	
	@Transient
	@Column(name="authorities")
	Collection<GrantedAuthority> authorities;
	
	@Column(name="access_token_validity")
	Integer accessTokenValiditySeconds;
	
	@Column(name="refresh_token_validity")
	Integer refreshTokenValiditySeconds;

	@Transient
	@Column(name="auto_pprove")
	boolean autoApprove;
	
	@Transient
	@Column(name="additional_information")
	Map<String, Object> additionalInformation;
	
	
	@Column(name = "login_page_url")
	String loginPageUrl;

	@Column(name="logout_callback_url")
	String logoutCallbackUrl;
	  
	@Column(name = "login_endpoint_url")
	String loginEndpointUrl;

	@Column(name = "login_endpoint_method")
	String loginEndpointMethod;
	
	@Column(name = "web_server_redirect_uri")
	String webServerRedirectUri;
	
	@Override
	public String getClientId() {
		return this.clientId;
	}

	@Override
	public Set<String> getResourceIds() {
		return this.resourceIds;
	}

	@Override
	public boolean isSecretRequired() {
		return this.secretRequired;
	}

	@Override
	public String getClientSecret() {
		return this.clientSecret;
	}

	@Override
	public boolean isScoped() {
		return this.scoped;
	}

	@Override
	public Set<String> getScope() {
		return this.scope;
	}
	 
	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return this.authorizedGrantTypes;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return this.registeredRedirectUri;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.authorities;
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		return this.accessTokenValiditySeconds;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return this.refreshTokenValiditySeconds;
	}

	@Override
	public boolean isAutoApprove(String scope) {
		return this.autoApprove;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return this.additionalInformation;
	}

	public String getLoginPageUrl() {
		return loginPageUrl;
	}

	public void setLoginPageUrl(String loginPageUrl) {
		this.loginPageUrl = loginPageUrl;
	}

	public String getLoginEndpointUrl() {
		return loginEndpointUrl;
	}

	public void setLoginEndpointUrl(String loginEndpointUrl) {
		this.loginEndpointUrl = loginEndpointUrl;
	}

	public String getLoginEndpointMethod() {
		return loginEndpointMethod;
	}

	public void setLoginEndpointMethod(String loginEndpointMethod) {
		this.loginEndpointMethod = loginEndpointMethod;
	}

	public String getLogoutCallbackUrl() {
		return logoutCallbackUrl;
	}

	public void setLogoutCallbackUrl(String logoutCallbackUrl) {
		this.logoutCallbackUrl = logoutCallbackUrl;
	}

	public String getWebServerRedirectUri() {
		return webServerRedirectUri;
	}
	
}
