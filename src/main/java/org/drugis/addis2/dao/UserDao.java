package org.drugis.addis2.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.drugis.addis2.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDao {
	@PersistenceContext EntityManager d_em;

	@Transactional(readOnly=true)
	public Collection<User> findUserByOpenId(String openid) throws DataAccessException {
		TypedQuery<User> query = d_em.createQuery("from User u where u.openid = :openid", User.class).setParameter("openid", openid);
		return query.getResultList();
	}

	@Transactional
	public User save(User user) {
		return d_em.merge(user);
	}
}