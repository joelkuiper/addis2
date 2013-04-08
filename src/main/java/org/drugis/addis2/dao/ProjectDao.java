package org.drugis.addis2.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.drugis.addis2.model.Project;
import org.drugis.addis2.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectDao {
	@PersistenceContext EntityManager d_em;

	@Transactional(readOnly=true)
	public Collection<Project> findProjectsByOwner(User user) throws DataAccessException {
		TypedQuery<Project> query = d_em.createQuery("from Project p where p.owner = :user", Project.class).setParameter("user", user);
		return query.getResultList();
	}

	@Transactional
	public Project save(Project project) {
		return d_em.merge(project);
	}

	@Transactional(readOnly=true)
	public Project get(Long id) {
		return d_em.find(Project.class, id);
	}
}