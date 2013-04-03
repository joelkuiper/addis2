package org.drugis.addis2.dao;

import java.util.Arrays;
import java.util.Collection;

import org.drugis.addis2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;    
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("openIdUserDetailsService") 
public class OpenIdUserDetailsService implements UserDetailsService {
	@Autowired(required=true) private UserDao dao;

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String openid) throws UsernameNotFoundException, DataAccessException {
		System.err.println(dao);
		Collection<User> users = dao.findUserByOpenId(openid);
		if (users.isEmpty()) {
			throw new UsernameNotFoundException("OpenID '" + openid + "'not found.");
		}
		User user = users.iterator().next();

		return new org.springframework.security.core.userdetails.User(
				user.openid, "",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
	}
}