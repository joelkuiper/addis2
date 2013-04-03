package org.drugis.addis2.dao;

import java.util.Collection;

import org.drugis.addis2.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDao {
	protected HibernateTemplate template = null;

	/**
	 * Sets Hibernate session factory and creates a 
	 * <code>HibernateTemplate</code> from it.
	 */
	@Autowired(required=true)
	public void setSessionFactory(SessionFactory sessionFactory) {
	    template = new HibernateTemplate(sessionFactory);
	}

	/**
	 * Find all persons.
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> findUsers() throws DataAccessException {
	    return template.find("from User");
	}

	/**
	 * Find persons by last name.
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> findUserByOpenId(String openid) throws DataAccessException {
	    return template.find("from User u where u.openid = ?", openid);
	}

	/**
	 * Saves person.
	 */
	@Transactional
	public void save(User user) {
		template.saveOrUpdate(user);
	}
}