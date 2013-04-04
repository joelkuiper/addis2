package org.drugis.addis2.auth;

import java.util.Arrays;
import java.util.Collection;

import org.drugis.addis2.dao.UserDao;
import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;    
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

@Service("openIdUserDetailsService") 
public class OpenIdUserDetailsService implements UserDetailsService {
	@Autowired(required=true) private UserDao d_dao;

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String openid) throws UsernameNotFoundException, DataAccessException {
		Collection<User> users = d_dao.findUserByOpenId(openid);
		if (users.isEmpty()) {
			throw new UsernameNotFoundException("OpenID '" + openid + "'not found.");
		}
		User user = users.iterator().next();

		return new org.springframework.security.core.userdetails.User(
				user.openid, "",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
	}

	public User getActiveUser(WebRequest request) {
		Object principal2 = ((Authentication) request.getUserPrincipal()).getPrincipal();
		String openid = ((org.springframework.security.core.userdetails.User) principal2).getUsername();
		User user = d_dao.findUserByOpenId(openid).iterator().next();
		return user;
	}
}