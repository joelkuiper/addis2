package org.drugis.addis2.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao {
	@Autowired(required = true) SessionFactory d_sessionFactory;

	public Session getSession() {
		return d_sessionFactory.getCurrentSession();
	}
}