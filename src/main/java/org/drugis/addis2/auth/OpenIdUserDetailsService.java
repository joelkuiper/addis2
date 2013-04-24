package org.drugis.addis2.auth;

import java.util.Arrays;

import org.drugis.addis2.model.User;
import org.drugis.addis2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("openIdUserDetailsService")
@Transactional(readOnly = true)
public class OpenIdUserDetailsService implements UserDetailsService {
	@Autowired private UserRepository d_users;

	public UserDetails loadUserByUsername(final String openid) throws UsernameNotFoundException, DataAccessException {
		final User user = d_users.findByOpenid(openid);
		if (user == null) {
			throw new UsernameNotFoundException("OpenID '" + openid + "'not found.");
		}
		return new org.springframework.security.core.userdetails.User(
				user.openid, "",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
