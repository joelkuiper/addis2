package org.drugis.addis2.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

@Service("openIdAuthenticationFailureHandler") 
public class OpenIdAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	public boolean openIdNotFound(AuthenticationException exception) {
		if (exception instanceof UsernameNotFoundException) {
			Authentication auth = exception.getAuthentication();
			if (auth instanceof OpenIDAuthenticationToken) {
				return ((OpenIDAuthenticationToken) auth).getStatus().equals(OpenIDAuthenticationStatus.SUCCESS);
			}
		}
		return false;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
	throws IOException, ServletException {
		if (openIdNotFound(exception)) {
			OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) exception.getAuthentication();
			DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
			request.getSession(true).setAttribute("USER_OPENID_CREDENTIAL", token.getIdentityUrl());
			redirectStrategy.sendRedirect(request, response, "/auth/register?auto=1");
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
