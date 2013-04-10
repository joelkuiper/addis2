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
public class ProjectDao extends GenericDao<Project, Long> {
	
	public ProjectDao() {
		super(Project.class);
	}

	@Transactional(readOnly=true)
	public Collection<Project> findProjectsByOwner(User user) throws DataAccessException {
		TypedQuery<Project> queryTpl= getEntityManager().createQuery("from Project p where p.owner = :user", Project.class);
		TypedQuery<Project> query = queryTpl.setParameter("user", user);
		return query.getResultList();
	}

}