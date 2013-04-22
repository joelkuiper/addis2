package org.drugis.addis2.controller.csrf;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A manager for the CSRF token for a given session. The
 * {@link #getTokenForSession(HttpSession)} should used to obtain the token
 * value for the current session (and this should be the only way to obtain the
 * token value).
 * 
 * @author Eyal Lupu
 */
final class CSRFTokenManager {

	/**
	 * The token parameter name
	 */
	static final String CSRF_PARAM_NAME = "X-CSRFToken";

	/**
	 * The location on the session which stores the token
	 */
	private final static String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class.getName() + ".tokenval";

	static String getTokenForSession(HttpSession session) {
		String token = null;		
		synchronized (session) {
			token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
			if (null == token) {
				token = UUID.randomUUID().toString();
				session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
			}
		}
		return token;
	}

	/**
	 * Extracts the token value from the session
	 * 
	 * @param request
	 * @return
	 */
	static String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getParameter(CSRF_PARAM_NAME);
		if (token == null) { // Check the HEADER
			token = request.getHeader(CSRF_PARAM_NAME);
		}
		return token;
	}

	private CSRFTokenManager() {
	};
}
