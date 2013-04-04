package org.drugis.addis2.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

public abstract class AbstractHibernateDao {
	protected HibernateTemplate template = null;

	public AbstractHibernateDao() {
		super();
	}

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory sessionFactory) {
	    template = new HibernateTemplate(sessionFactory);
	}
}