package org.drugis.addis2.dao;

import java.util.Collection;

import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectDao extends AbstractHibernateDao {
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public Collection<Project> findProjectsByOwner(User user) throws DataAccessException {
		Query query = getSession().createQuery("from Project p where p.owner = ?").setParameter(0, user);
		return query.list();
	}

	@Transactional
	public void save(Project project) {
		getSession().saveOrUpdate(project);
	}

	@Transactional(readOnly=true)
	public Project get(Long id) {
		return (Project) getSession().get(Project.class, id);
	}
}