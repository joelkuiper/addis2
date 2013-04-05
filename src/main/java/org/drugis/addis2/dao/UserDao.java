package org.drugis.addis2.dao;

import java.util.Collection;

import org.drugis.addis2.model.User;
import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDao extends AbstractHibernateDao {
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public Collection<User> findUsers() throws DataAccessException {
		Query query = getSession().createQuery("from User u");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public Collection<User> findUserByOpenId(String openid) throws DataAccessException {
		Query query = getSession().createQuery("from User u where u.openid = ?").setParameter(0, openid);
		return query.list();
	}

	@Transactional
	public void save(User user) {
		getSession().saveOrUpdate(user);
	}
}