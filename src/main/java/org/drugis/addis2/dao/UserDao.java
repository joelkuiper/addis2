package org.drugis.addis2.dao;

import java.util.Collection;

import org.drugis.addis2.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDao extends AbstractHibernateDao {
	@SuppressWarnings("unchecked")
	public Collection<User> findUsers() throws DataAccessException {
	    return template.find("from User");
	}

	@SuppressWarnings("unchecked")
	public Collection<User> findUserByOpenId(String openid) throws DataAccessException {
	    return template.find("from User u where u.openid = ?", openid);
	}

	@Transactional
	public void save(User user) {
		template.saveOrUpdate(user);
	}
}