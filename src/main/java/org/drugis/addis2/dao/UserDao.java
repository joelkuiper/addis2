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
public class UserDao extends GenericDao<User, Long> {
	
	public UserDao() {
		super(User.class);
	}

	@Transactional(readOnly=true)
	public Collection<User> findUserByOpenId(String openid) throws DataAccessException {
		TypedQuery<User> queryTpl = getEntityManager().createQuery("from User u where u.openid = :openid", User.class);
		TypedQuery<User> query = queryTpl.setParameter("openid", openid);
		return query.getResultList();
	}

}