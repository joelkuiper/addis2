package org.drugis.addis2.dao;

import java.util.Collection;

import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectDao extends AbstractHibernateDao {
	@SuppressWarnings("unchecked")
	public Collection<Project> findProjectsByOwner(User user) throws DataAccessException {
	    return template.find("from Project p where p.owner = ?", user);
	}

	@Transactional
	public void save(Project project) {
		template.saveOrUpdate(project);
	}
}