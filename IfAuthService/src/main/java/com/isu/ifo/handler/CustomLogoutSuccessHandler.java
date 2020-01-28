package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.isu.ifo.entity.ClientDetailsImpl;
import com.isu.ifo.service.security.ClientDetailsServiceImpl;

public class CustomLogoutSuccessHandler
  implements LogoutSuccessHandler
{

  @Autowired
  ClientDetailsServiceImpl clientDetailsService;

  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, ServletException
  {
    String clientIdPath = "/logout";

    System.out.println("onLogoutSuccess :::: ");

    SecurityContextHolder.clearContext();
    HttpSession session = request.getSession(false);
    SecurityContextHolder.clearContext();
    session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    String token = "";
    for (Cookie cookie : request.getCookies()) {
      System.out.println("cookie.getName() :: " + cookie.getName());
      System.out.println("cookie.getValue() :: " + cookie.getValue());
      if (cookie.getName().equalsIgnoreCase("Authorization")) {
        token = cookie.getValue();
      }
      cookie.setValue(null);
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
    }

    String requestUri = request.getRequestURI().substring(request.getContextPath().length());
    String clientId = "";

    if (requestUri != null) {
      String truncatedString = null;

      int pathIndex = requestUri.indexOf(clientIdPath);

      if (pathIndex >= 0) {
        truncatedString = requestUri.substring(clientIdPath.length());
        if (truncatedString.length() > 0)
        {
          if (truncatedString.charAt(0) == '/') {
            truncatedString = truncatedString.substring(1);
          }

          if ((truncatedString.length() > 0) && (truncatedString.indexOf("/") > 0)) {
            truncatedString = truncatedString.substring(0, truncatedString.indexOf("/"));
          }
        }

        System.out.println("requested uri:" + requestUri);
        System.out.println("truncatedString:" + truncatedString);

        if (truncatedString.length() > 0) {
          clientId = truncatedString;
        }
      }

    }

    if ((clientId == null) || (clientId.equals(""))) {
      throw new UnauthorizedClientException("Client ID is required after [/login] url.");
    }
    ClientDetailsImpl clientDetails = this.clientDetailsService.findByClientId(clientId);

    String callbackUrl = clientDetails.getLogoutCallbackUrl();

    response.sendRedirect(callbackUrl);
  }
}